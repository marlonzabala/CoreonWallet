package com.itcorea.coreonwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;

public class MainSliderActivity extends FragmentActivity
{

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_slider, menu);
		return true;
	}

	private static final int	NUM_PAGES	= 3;
	private ViewPager			mPager;
	private PagerAdapter		mPagerAdapter;
	int							margin;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.righttomain, R.anim.maintoleft);
		setContentView(R.layout.activity_main_slider);
		setTitle("Coreon Mobile");

		mPager = (ViewPager) findViewById(R.id.pager);

		// get dimensions for different screens
		margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20 * 3, getResources().getDisplayMetrics());
		mPager.setPageMargin(-margin);

		// mPager.setPageMargin(-150);
		mPager.setOnPageChangeListener(onPageMove());
		// mPager.setHorizontalFadingEdgeEnabled(true);
		// mPager.setFadingEdgeLength(30);

		// getFragmentManager() changed to
		// getSupportFragmentManager() //11-14-13 bug

		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

		mPager.setAdapter(mPagerAdapter);

		// set main view
		mPager.setCurrentItem(1);
	}

	private OnPageChangeListener onPageMove()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onBackPressed()
	{
		if (mPager.getCurrentItem() == 0)
		{
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			super.onBackPressed();
			// finish();
			overridePendingTransition(R.anim.lefttomain, R.anim.maintoright);
		}
		else
		{
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	public void openAccountInfo(View view)
	{
		return;
	}

	public void openSendMoney(View view)
	{
		Intent intent = new Intent(this, GlobeSendMoney.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// overridePendingTransition(R.anim.righttoleft,R.anim.lefttoright);
		startActivity(intent);
		return;
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
	{
		public ScreenSlidePagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			return new MainSlidePageFragment(position);
		}

		@Override
		public int getCount()
		{
			return NUM_PAGES;
		}
	}
}
