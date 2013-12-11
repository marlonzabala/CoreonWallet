package com.itcorea.coreonmobile;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.itcorea.coreonmobile.R;

public class GlobeSendMoney extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_money);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_money, menu);
		return true;
	}

}
