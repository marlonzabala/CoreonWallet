package com.itcorea.coreonmobile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_log_in);

		// Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();

		// check if user is logged in
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean LoggedIn = prefs.getBoolean("LoggedIn", false);

		// for dev
		// test for errors
		LoggedIn = true;
		EditText ep = (EditText) findViewById(R.id.editPassword);
		EditText eu = (EditText) findViewById(R.id.editUsername);
		ep.setText("admin");
		eu.setText("admin");

		if (LoggedIn == true)
		{
			// Toast.makeText(getApplicationContext(), useremail, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getApplicationContext(), CoreonMain.class);
			// Intent intent = new Intent(mContext, MainSliderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(intent);

			finish();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Not Logged In", Toast.LENGTH_SHORT).show();
		}

		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}

	public void CheckLogIn(View view)
	{
		// get text values
		EditText editUsername = (EditText) findViewById(R.id.editUsername);
		EditText editPassword = (EditText) findViewById(R.id.editPassword);

		// check if incomplete
		if (editUsername.getText().toString().equals("") || editPassword.getText().toString().equals(""))
		{
			Toast.makeText(getBaseContext(), "Please complete the fields", Toast.LENGTH_SHORT).show();
		}
		else
		{
			// Check login credentials then proceed
			// execute in asynchronous task
			new CheckCredentials(getApplicationContext(), LogIn.this).execute(editUsername.getText().toString(), editPassword.getText().toString());

			// to be removed code
			// used for early testing
			if (editUsername.getText().toString().equals("admin") && editPassword.getText().toString().equals("admin"))
			{
				// Intent intent = new Intent(this, MainSliderActivity.class);
				// startActivity(intent);
			}
			else
			{
				// Toast.makeText(getBaseContext(),"Incorrect username and/or password",
				// Toast.LENGTH_SHORT).show();
			}
		}
		// String message = editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
	}
}

class CheckCredentials extends AsyncTask<String, Integer, Long>
{

	String				useremail	= "";
	boolean				logIn		= false;
	boolean				network		= true;
	boolean				timeout		= false;
	private Context		mContext;
	private Activity	mActivity;
	ProgressDialog		mDialog;

	public CheckCredentials(Context context, Activity activity)
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

			// Log.e("info_tag", "response: " + response.getStatusLine().getStatusCode());
		}
		catch (ConnectTimeoutException e)
		{
			// Log.e("info_tag", "timeout!!!!!");
			timeout = true;
			return "";
		}
		catch (Exception e)
		{
			// Log.e("log_tag", "Error in http connection " + e.toString());
		}
		// convert response to string
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
			// Log.e("info_tag", "No network Available");
			network = false;
			return null;
		}
		else
		{
			// Log.e("info_tag", "Network Available");
			network = true;
		}

		// desktop set to static ip 192.168.123.111
		String ipAdd = "192.168.123.111";
		String httpAddress = "http://" + ipAdd + "/android/androidsql.php?email='" + params[0] + "'&pw='" + params[1] + "'";
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
			logIn = false;
		}
		else
		{
			logIn = true;
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
		// remove progress dialog
		mDialog.dismiss();

		if (logIn)
		{
			Toast.makeText(mContext, useremail, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(mContext, CoreonMain.class);
			// Intent intent = new Intent(mContext, LogIn.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
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
