package com.itcorea.coreonwallet;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

public class SplashScreen extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

//		Intent newIntent = new Intent(SplashScreen.this, LogIn.class);
//		startActivity(newIntent);
//		((Activity) SplashScreen.this).finish();
		
		new LogInExecute().execute();
	}

	private class LogInExecute extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute()
		{

		}

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			Intent newIntent = new Intent(SplashScreen.this, LogIn.class);
			startActivity(newIntent);
			((Activity) SplashScreen.this).finish();
		}

		@Override
		protected void onProgressUpdate(Void... values)
		{
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}
}
