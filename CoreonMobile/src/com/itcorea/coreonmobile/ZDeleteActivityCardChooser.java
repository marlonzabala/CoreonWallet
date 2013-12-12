package com.itcorea.coreonmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.itcorea.coreonmobile.R;

public class ZDeleteActivityCardChooser extends FragmentActivity
{

	private DrawerLayout			mDrawerLayout;
	private ListView				mDrawerList;
	private ActionBarDrawerToggle	mDrawerToggle;

	private CharSequence			mDrawerTitle;
	private CharSequence			mTitle;
	private String[]				mPlanetTitles	= { "My Account", "Choose Card", "Enroll New Card", "Rewards", "Change Password", "Promos",
			"Log Out"								};

	private static final int		NUM_PAGES		= 3;
	private ViewPager				mPager;
	private PagerAdapter			mPagerAdapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		overridePendingTransition(R.anim.righttomain, R.anim.maintoleft);
		setContentView(R.layout.account_info);
		setTitle("Coreon Mobile");
		mPager = (ViewPager) findViewById(R.id.button1);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		mTitle = mDrawerTitle = getTitle();

		// mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.button1);
		mDrawerList = (ListView) findViewById(R.id.button1);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view)
			{
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView)
			{
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				//sayang tong code na mga nagawa ko :(
				//
				//
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null)
		{
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content view
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		// Handle action buttons
		switch (item.getItemId())
		{
		// case R.id.action_websearch:
		// // create intent to perform web search for this planet
		// Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		// intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
		// // catch event that there's no activity to handle intent
		// if (intent.resolveActivity(getPackageManager()) != null)
		// {
		// startActivity(intent);
		// }
		// else
		// {
		// Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
		// }
		// return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			selectItem(position);
		}
	}

	private void selectItem(int position)
	{
		// update the main content by replacing fragments
		Fragment fragment = new PlanetFragment();
		Bundle args = new Bundle();
		args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and
	 * onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Fragment that appears in the "content_frame", shows a planet
	 */
	public static class PlanetFragment extends Fragment
	{
		public static final String	ARG_PLANET_NUMBER	= "planet_number";

		public PlanetFragment()
		{
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.activity_main_slider, container, false);
			// int i = getArguments().getInt(ARG_PLANET_NUMBER);
			// String planet = getResources().getStringArray(R.array.planets_array)[i];
			//
			// int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
			// "drawable", getActivity().getPackageName());
			// ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
			// getActivity().setTitle(planet);
			return rootView;
		}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.lefttomain, R.anim.maintoright);
	}

	// ///////////////////////////////////////////////////////////////

	public void openAccountInfo(View view)
	{
		Intent intent = new Intent(this, ZDeleteAccountInfo.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// overridePendingTransition(R.anim.righttoleft,R.anim.lefttoright);
		startActivity(intent);
		return;
	}

	// /////////////////////////////////////////////////////////////

	public void openGcash(View view)
	{
		Intent intent = new Intent(this, MainSliderActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// overridePendingTransition(R.anim.righttoleft,R.anim.lefttoright);
		startActivity(intent);
		return;
	}

	public void openSmart(View view)
	{
		Intent intent = new Intent(this, SmartMain.class);
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
			return new CardChooserFragment(position);
		}

		@Override
		public int getCount()
		{
			return NUM_PAGES;
		}
	}
}
