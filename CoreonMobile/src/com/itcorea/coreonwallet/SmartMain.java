package com.itcorea.coreonwallet;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class SmartMain extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.righttomain, R.anim.maintoleft);
		setContentView(R.layout.activity_smart_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//hehe test commit
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smart_main, menu);
		return true;
	}

}
