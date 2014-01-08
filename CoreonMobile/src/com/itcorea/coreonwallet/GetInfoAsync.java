package com.itcorea.coreonwallet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

class GetInfoAsync extends AsyncTask<String, Integer, Long>
{
	boolean				RetrieveSuccess	= false;
	String				useremail		= "";
	boolean				network			= true;
	boolean				timeout			= false;
	private Context		mContext;
	private Activity	mActivity;
	ProgressDialog		mDialog;

	public GetInfoAsync(Context context, Activity activity)
	{
		mContext = context;
		mActivity = activity;
	}

	private boolean isNetworkAvailable()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private String sendPost(String httpAddress)
	{
		String result = "";
		StringBuilder sb = null;
		InputStream is = null;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try
		{
			int timeoutsec = 20000; // 20 second timeout
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutsec);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutsec);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost(httpAddress);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}
		catch (ConnectTimeoutException e)
		{
			// timeout connection
			timeout = true;
			return "";
		}
		catch (Exception e)
		{
			// Log.e("log_tag", "Error in http connection " + e.toString());
		}

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");

			String line = "0";
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		}
		catch (Exception e)
		{
			// Log.e("log_tag", "Error converting result " + e.toString());
		}
		return result;
	}

	@Override
	protected Long doInBackground(String... params)
	{
		timeout = false;

		if (!isNetworkAvailable())
		{
			network = false;
			return null;
		}
		else
		{
			network = true;
		}

		// desktop set to static ip 192.168.123.111
		String ipAdd = "192.168.123.111";
		String httpAddress = "http://" + ipAdd + "/android/androidsql.php?email='" + params[0] + "'&pw='" + params[1] + "'&request=" + params[2] + "";

		Log.i("urlPost", httpAddress.toString());
		String result = sendPost(httpAddress);

		JSONArray jArray = null;
		String name = null;
		String fname = null;
		String lname = null;
		String points = null;

		try
		{
			jArray = new JSONArray(result);
			JSONObject json_data = null;

			List<String[]> rowList = new ArrayList<String[]>();

			rowList.add(new String[] { "title", "content", "image", "date", "url" });
			rowList.add(new String[] { "title", "content", "image", "date", "url" });
			rowList.add(new String[] { "title", "content", "image", "date", "url" });

			for (int i = 0; i < jArray.length(); i++)
			{
				json_data = jArray.getJSONObject(i);
				name = json_data.getString("mpin");// column name in the database
				fname = json_data.getString("fname");
				lname = json_data.getString("lname");
				points = json_data.getString("points");
			}
		}
		catch (JSONException e1)
		{
			useremail = "No data found";
			Log.e("Exception", e1.toString());
		}
		catch (ParseException e1)
		{
			Log.e("Exception", e1.toString());
			e1.printStackTrace();
		}

		if (useremail.equals("No data found"))
		{
			RetrieveSuccess = false;
			// logIn = false;
		}
		else
		{
			RetrieveSuccess = true;
			// logIn = true;
		}

		useremail = "mpin: " + name;

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = preferences.edit();
		// Boolean tr = true;
		editor.putString("fname", fname); // value to store
		editor.putString("lname", lname); // value to store
		editor.putString("points", points); // value to store
		editor.commit();

		return null;
	}

	protected void onProgressUpdate(Integer... progress)
	{
		// setProgressPercent(progress[0]);
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();

		mDialog = new ProgressDialog(mActivity);
		mDialog.setMessage("Loggin in..");
		mDialog.show();
	}

	protected void onPostExecute(Long result)
	{
		mDialog.dismiss();
		if (RetrieveSuccess)
		{
			
			Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
		}
		else
		{
			if (network)
			{
				if (timeout)
				{
					Toast.makeText(mContext, "Network time out, server maybe down", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(mContext, "Wrong password or username", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(mContext, "No internet connection", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
