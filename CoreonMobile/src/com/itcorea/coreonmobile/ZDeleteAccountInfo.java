package com.itcorea.coreonmobile;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import com.itcorea.coreonmobile.R;

public class ZDeleteAccountInfo extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// add animation for startup of activity
		overridePendingTransition(R.anim.righttomain, R.anim.maintoleft);
		setContentView(R.layout.activity_account_info);
		
		
		//comment made sa bahay
		//haha
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_info, menu);
		return true;
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		// add animation for back button
		overridePendingTransition(R.anim.lefttomain, R.anim.maintoright);
		
		//bahay
	}
}
