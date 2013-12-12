package com.itcorea.coreonmobile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import static android.content.Context.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("CutPasteId")
public class CoreonMain extends FragmentActivity
{
	public static ViewPager			mPager;
	public static int				history;
	int								margin;

	boolean							noticeSelected	= false;
	boolean							offerSelected	= false;
	boolean							helpSelected	= false;

	ExpandableListAdapter			listAdapter;
	ExpandableListView				expListView;
	MyPagerAdapter					adapter;
	List<String>					listDataHeader;
	HashMap<String, List<String>>	listDataChild;

	ArrayList<String>				_title			= new ArrayList<String>();
	ArrayList<String>				_content		= new ArrayList<String>();
	ArrayList<String>				_date			= new ArrayList<String>();
	ArrayList<String>				_image			= new ArrayList<String>();
	ArrayList<String>				_type			= new ArrayList<String>();
	ArrayList<String>				_extra			= new ArrayList<String>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// slide from right animation
		overridePendingTransition(R.anim.righttomain, R.anim.maintoleft);
		setContentView(R.layout.activity_coreon_main);

		mPager = (ViewPager) findViewById(R.id.pager);

		adapter = new MyPagerAdapter();
		mPager.setAdapter(adapter);

		// add margin for viewpager
		margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
		mPager.setPageMargin(-margin);
		mPager.setCurrentItem(1);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = preferences.edit();
		Boolean tr = true;
		editor.putBoolean("LoggedIn", tr); // value to store
		editor.commit();

		// Toast.makeText(getApplicationContext(), " Expanded", Toast.LENGTH_SHORT).show();
	}

	public void openCards(View view)
	{
		// open and close card menu button
		if (mPager.getCurrentItem() == 1)
		{
			mPager.setCurrentItem(2);
		}
		else
		{
			mPager.setCurrentItem(1);
		}
		return;
	}

	public void openMenu(View view)
	{
		// open and close menu button
		if (mPager.getCurrentItem() == 1)
		{
			mPager.setCurrentItem(0);
		}
		else
		{
			mPager.setCurrentItem(1);
		}
		return;
	}

	public View setLayout(int id)
	{
		// remove child views
		ViewGroup layout = (ViewGroup) findViewById(R.id.dynamic_layout_main);
		layout.removeAllViews();

		// get layout to insert
		LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(id, null);

		// insert into main view
		View insertPoint = findViewById(R.id.dynamic_layout_main);
		((ViewGroup) insertPoint).addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		return insertPoint;
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.coreon_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onBackPressed()
	{
		if (mPager.getCurrentItem() == 1)
		{
			adapter.setHome();

			// super.onBackPressed();
			// System.exit(0);
			// overridePendingTransition(R.anim.lefttomain, R.anim.maintoright);
		}
		else
		{
			// Otherwise, select main info.
			mPager.setCurrentItem(1);
		}
	}

	public void openAccountInfo(View view)
	{
		Intent intent = new Intent(this, ZDeleteAccountInfo.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// overridePendingTransition(R.anim.righttoleft,R.anim.lefttoright);
		startActivity(intent);
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

	public void test()
	{
		int num = 0;
	}

	public void openNotice(View view)
	{
		// if (!noticeSelected)
		// {

		// Calendar c = Calendar.getInstance();
		// int seconds = c.get(Calendar.SECOND);

		ImageButton imo = (ImageButton) findViewById(R.id.imageButtonOffers);
		imo.setBackgroundColor(Color.TRANSPARENT);

		ImageButton imh = (ImageButton) findViewById(R.id.imageButtonHelp);
		imh.setBackgroundColor(Color.TRANSPARENT);

		ImageButton im = (ImageButton) findViewById(R.id.imageButtonNotice);
		im.setBackgroundColor(Color.WHITE);

		// setLayout(R.layout.notices);
		// noticeSelected = true;
		// offerSelected = false;
		// helpSelected = false;

		View v = setLayout(R.layout.notices);
		ListView listViewNotice = (ListView) v.findViewById(R.id.listViewNotices);

		final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

		removeStringsValues();

		// addStrings("space", "space", "space", 0);
		addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
				"www.google.com", "textimagenotice");
		addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_2,
				"www.google.com", "textimagenotice");
		addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
				"www.google.com", "textimagenotice");
		addStrings(
				"Dong Won Restaurant",
				"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
				"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
		addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_2,
				"www.google.com", "textimagenotice");
		addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
				"www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1, "www.coreonmobile.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
		// addStrings("space", "space", "space", 0);
		// cannot retrieve correct layout

		adapter.setValues(_title, _content, _date, _image, _type, _extra);
		// adapter.setNotice(true);
		listViewNotice.setAdapter(adapter);
		listViewNotice.setDividerHeight(0);

		listViewNotice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				if (view.getTag().equals("textimagenotice"))
				{
					// open notice content full
					adapter.setNotice(false);

					View noticeView = setLayout(R.layout.notice_content);
					ListView listView = (ListView) noticeView.findViewById(R.id.listViewNoticeContent);

					TextView textTitle = (TextView) view.findViewById(R.id.lblSubTitleText);
					TextView textDate = (TextView) view.findViewById(R.id.lblDateText);

					String image = _image.get(position).toString();
					String url = _extra.get(position).toString();
					String content = _content.get(position).toString();

					int imageInt = Integer.parseInt(image);

					removeStringsValues();
					addStrings("", "", "", 0, "", "space");
					addStrings(textTitle.getText().toString(), content, textDate.getText().toString(), imageInt, url, "noticecontent");
					addStrings("", "", "", 0, "", "space");

					MySimpleArrayAdapter noticeContentAdapter = new MySimpleArrayAdapter(getApplicationContext(), _title);
					noticeContentAdapter.setValues(_title, _content, _date, _image, _type, _extra);
					listView.setAdapter(noticeContentAdapter);
					listView.setDividerHeight(0);
				}
			}
		});
		// }
		// else
		// {
		// ImageButton im = (ImageButton) findViewById(R.id.imageButtonNotice);
		// im.setBackgroundColor(Color.TRANSPARENT);
		// setLayout(R.layout.account_info);
		// noticeSelected = false;
		// }

		return;
	}

	public void addStrings(String title, String content, String date, int image, String extra, String type)
	{
		_title.add(title);
		_content.add(content);
		_date.add(date);
		_image.add(String.valueOf(image));
		_extra.add(extra);
		_type.add(type);
	}

	public void removeStringsValues()
	{
		_title = new ArrayList<String>();
		_content = new ArrayList<String>();
		_date = new ArrayList<String>();
		_image = new ArrayList<String>();
		_extra = new ArrayList<String>();
		_type = new ArrayList<String>();
	}

	public void openHome(View view)
	{
		mPager.setCurrentItem(1);
		// setLayout(R.layout.notices);
		return;
	}

	public void enrollCard(View view)
	{
		// Toast.makeText(view.getContext(), "enroll Card", Toast.LENGTH_SHORT).show();
		return;
	}

	public void openOffers(View view)
	{
		// if (!offerSelected)
		// {

		ImageButton imn = (ImageButton) findViewById(R.id.imageButtonNotice);
		imn.setBackgroundColor(Color.TRANSPARENT);

		ImageButton imh = (ImageButton) findViewById(R.id.imageButtonHelp);
		imh.setBackgroundColor(Color.TRANSPARENT);

		ImageButton im = (ImageButton) findViewById(R.id.imageButtonOffers);
		im.setBackgroundColor(Color.WHITE);

		// ImageView imdr = (ImageView) findViewById(R.id.imageDivRight);
		// imdr.setVisibility(View.INVISIBLE);

		setLayout(R.layout.offers);
		offerSelected = true;
		noticeSelected = false;
		helpSelected = false;

		ListView listViewNotice = (ListView) findViewById(R.id.listViewOffers);

		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

		removeStringsValues();

		addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
				"www.google.com", "textimagenotice");
		addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_2,
				"www.google.com", "textimagenotice");
		addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
				"www.google.com", "textimagenotice");
		addStrings(
				"Dong Won Restaurant",
				"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
				"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
		addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_2,
				"www.google.com", "textimagenotice");
		addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
				"www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
		addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2, "www.google.com", "textimagenotice");

		adapter.setValues(_title, _content, _date, _image, _type, _extra);
		adapter.setNotice(true);
		listViewNotice.setAdapter(adapter);
		listViewNotice.setDividerHeight(0);

		listViewNotice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Log.i("test", "tester");
				// Toast.makeText(getApplicationContext(), "Position :" + position ,
				// Toast.LENGTH_LONG).show();

			}
		});

		// }
		// else
		// {
		// // ImageView imdl = (ImageView) findViewById(R.id.imageDivRight);
		// // imdl.setVisibility(View.VISIBLE);
		//
		// ImageButton im = (ImageButton) findViewById(R.id.imageButtonOffers);
		// im.setBackgroundColor(Color.TRANSPARENT);
		// setLayout(R.layout.account_info);
		// offerSelected = false;
		// }

		return;
	}

	public void openHelp(View view)
	{
		if (!helpSelected)
		{
			ImageButton imn = (ImageButton) findViewById(R.id.imageButtonNotice);
			imn.setBackgroundColor(Color.TRANSPARENT);

			ImageButton imo = (ImageButton) findViewById(R.id.imageButtonOffers);
			imo.setBackgroundColor(Color.TRANSPARENT);

			ImageButton imh = (ImageButton) findViewById(R.id.imageButtonHelp);
			imh.setBackgroundColor(Color.WHITE);

			// ImageView imdl = (ImageView) findViewById(R.id.imageDivLeft);
			// imdl.setVisibility(View.INVISIBLE);

			setLayout(R.layout.help);
			helpSelected = true;
			noticeSelected = false;
			offerSelected = false;
		}
		else
		{
			// ImageView imdr = (ImageView) findViewById(R.id.imageDivLeft);
			// imdr.setVisibility(View.VISIBLE);

			ImageButton im = (ImageButton) findViewById(R.id.imageButtonHelp);
			im.setBackgroundColor(Color.TRANSPARENT);
			setLayout(R.layout.account_info);
			helpSelected = false;
		}

		return;
	}

	public static void hideSoftKeyboard(Activity activity)
	{

		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
}

class MyPagerAdapter extends PagerAdapter
{

	ExpandableListAdapter			listAdapter;
	ExpandableListView				expListView;
	List<String>					listDataHeader;
	HashMap<String, List<String>>	listDataChild;
	List<String>					listDataHeaderImage;
	HashMap<String, List<String>>	listDataChildImage;
	Context							con;
	View							view;

	ArrayList<String>				_title		= new ArrayList<String>();
	ArrayList<String>				_content	= new ArrayList<String>();
	ArrayList<String>				_date		= new ArrayList<String>();
	ArrayList<String>				_image		= new ArrayList<String>();
	ArrayList<String>				_type		= new ArrayList<String>();
	ArrayList<String>				_extra		= new ArrayList<String>();

	public int getCount()
	{
		return 3;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("CutPasteId")
	public View setPage(int idChange)
	{
		// remove child views
		ViewGroup layout = (ViewGroup) view.findViewById(R.id.dynamic_layout_main);

		// get layout to insert
		LayoutInflater vi = (LayoutInflater) con.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v1 = vi.inflate(idChange, null);

		// insert into main view

		try
		{
			layout.removeAllViews();
			((ViewGroup) layout).addView(v1, 0);
		}
		catch (Exception e)
		{
			Log.i("remove views", e.toString());
		}

		return v1;
	}

	public void removeHeaderbackColor(View parent)
	{
		View viewParent = (View) parent.getParent().getParent();
		ImageButton imn = (ImageButton) viewParent.findViewById(R.id.imageButtonNotice);
		imn.setBackgroundColor(Color.TRANSPARENT);
		ImageButton imo = (ImageButton) viewParent.findViewById(R.id.imageButtonOffers);
		imo.setBackgroundColor(Color.TRANSPARENT);
		ImageButton imh = (ImageButton) viewParent.findViewById(R.id.imageButtonHelp);
		imh.setBackgroundColor(Color.TRANSPARENT);
	}

	// @Override
	// public float getPageWidth(int position)
	// {
	// switch (position)
	// {
	// case 0:
	// return .85f;
	// case 1:
	// return 1;
	// case 2:
	// return .85f;
	// default:
	// return 1;
	// }
	// }

	public Object instantiateItem(View collection, int position)
	{
		LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		con = collection.getContext();
		view = collection;

		int resId = 0;
		switch (position)
		{
			case 0:
				// menu drawer

				resId = R.layout.coreon_main_menu;
				View view0 = inflater.inflate(resId, null);

				expListView = (ExpandableListView) view0.findViewById(R.id.lvExp);
				prepareListData();
				listAdapter = new ExpandableListAdapter(collection.getContext(), listDataHeader, listDataChild, listDataHeaderImage,
						listDataChildImage);
				expListView.setAdapter(listAdapter);

				DisplayMetrics displayMetrics = new DisplayMetrics();
				displayMetrics = con.getResources().getDisplayMetrics();
				int mScreenWidth = displayMetrics.widthPixels;

				mScreenWidth = (mScreenWidth - 150);// convertDpToPixel(R.dimen.main_margin,con));

				// Toast.makeText(con, String.valueOf(mScreenWidth), Toast.LENGTH_SHORT).show();

				// get width of screen to programmatically adjust the expandable list indicator
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
				{
					// works for 4.3 and lower
					expListView.setIndicatorBounds((int) (mScreenWidth - 70), (int) mScreenWidth);
				}
				else
				{
					// works for version 4.3
					expListView.setIndicatorBoundsRelative((int) (mScreenWidth - 70), (int) mScreenWidth);
				}

				// Listview on child click listener

				expListView.setOnChildClickListener(new OnChildClickListener() {

					@SuppressLint("CutPasteId")
					@Override
					public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
					{
						switch (groupPosition)
						{
							case 0:
								CoreonMain.mPager.setCurrentItem(1);

								View view6 = setPage(R.layout.main_info_home_list);

								SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);

								String fname = prefs.getString("fname", "firstname");
								String lname = prefs.getString("lname", "lastname");
								String points = prefs.getString("points", "0");
								// TextView t = new TextView(con);

								try
								{
									removeHeaderbackColor((View) view6.getParent().getParent());
								}
								catch (Exception e)
								{
									Log.i("exception on remove header back color", e.toString());
								}

								// Get ListView object from xml
								ListView listView = (ListView) view6.findViewById(R.id.listView1);

								removeStringsValues();

								addStrings("space", "space", "space", 0, "space", "space");

								addStrings("userinfo", fname, "userinfo", 0, "userinfo", "userinfo");
								addStrings("My Cards", "0 Cards", "userinfo", 0, "userinfo", "usercontent");
								addStrings("", "", "", 0, "", "userline");
								addStrings("Coreon Points", points + " Points", "userinfo", 0, "userinfo", "usercontent");
								addStrings("", "", "", 0, "", "userline");
								addStrings("Notice", "12", "userinfo", 0, "userinfo", "usercontent");
								addStrings("userinfo", "userinfo", "userinfo", 0, "userinfo", "userbottom");

								addStrings("space", "space", "space", 0, "space", "space");

								addStrings("Exclusive Offers", "", "", 0, "header", "header");
								addStrings("Dong Won Restaurant",
										"Get 50% off on your test test test test test test test test payment of Coreon Card",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimage");
								addStrings(
										"Dong Won Restaurant",
										"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "http://www.coreonmobile.com/", "textimage");
								addStrings(
										"Dong Won Restaurant",
										"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "http://www.coreonmobile.com/", "textimage");
								addStrings("Dong Won Restaurant", "payment of Coreon Card", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
										"www.yahoo.com", "textimage");
								addStrings("", "", "", 0, "", "bottomshadow");
								addStrings("space", "space", "space", 0, "space", "space");
								addStrings("Notice", "", "", 0, "header", "header");
								addStrings(
										"Dong Won Restaurant",
										"Get 50% off on your payment of Coreon CardGet 50% off on your payment of Coreon CardGet 50% off on your payment of Coreon Card",
										"August 25, 2013 at 11:30 PM", 0, "text", "text");
								addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card", "August 25, 2013 at 11:30 PM", 0,
										"text", "text");
								addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card", "August 25, 2013 at 11:30 PM", 0,
										"text", "text");
								addStrings("", "", "", 0, "", "bottomshadow");
								addStrings("", "", "", 0, "", "space");

								addStrings("Exclusive Offers", "", "", 0, "header", "header");
								addStrings("Dong Won Restaurant",
										"Get 50% off on your test test test test test test test test payment of Coreon Card",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimage");
								addStrings(
										"Dong Won Restaurant",
										"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "http://www.coreonmobile.com/", "textimage");
								addStrings(
										"Dong Won Restaurant",
										"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "http://www.coreonmobile.com/", "textimage");
								addStrings("Dong Won Restaurant", "payment of Coreon Card", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
										"www.yahoo.com", "textimage");
								addStrings("", "", "", 0, "", "bottomshadow");
								addStrings("", "", "", 0, "", "space");

								addStrings("Exclusive Offers", "", "", 0, "header", "header");
								addStrings("Dong Won Restaurant",
										"Get 50% off on your test test test test test test test test payment of Coreon Card",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimage");
								addStrings(
										"Dong Won Restaurant",
										"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "http://www.coreonmobile.com/", "textimage");
								addStrings(
										"Dong Won Restaurant",
										"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
										"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "http://www.coreonmobile.com/", "textimage");
								addStrings("Dong Won Restaurant", "payment of Coreon Card", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
										"www.yahoo.com", "textimage");
								addStrings("", "", "", 0, "", "bottomshadow");

								addStrings("", "", "", 0, "", "space");
								addStrings("", "", "", 0, "", "space");
								addStrings("", "", "", 0, "", "space");
								addStrings("", "", "", 0, "", "space");
								addStrings("", "", "", 0, "", "space");
								addStrings("", "", "", 0, "", "space");
								addStrings("", "", "", 0, "", "space");

								MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(con, _title);

								adapter.setValues(_title, _content, _date, _image, _type, _extra);

								// Assign adapter to ListView
								listView.setAdapter(adapter);

								listView.setDividerHeight(0);

								// ListView Item Click Listener
								listView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
									{
										if (position == 2)
										{
											// clicked my cards
											CoreonMain.mPager.setCurrentItem(2);
										}
										else if (position == 4)
										{
											// clicked coreon points
											View v = setPage(R.layout.points);
										}
										else if (position == 6)
										{
											// clicked notices

											View v = setPage(R.layout.notices);
											ListView listViewNotice = (ListView) v.findViewById(R.id.listViewNotices);

											removeStringsValues();

											// addStrings("space", "space", "space", 0);
											addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card",
													"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
											addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin",
													"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
											addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te",
													"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
											addStrings(
													"Dong Won Restaurant",
													"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
													"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
											addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin",
													"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
											addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te",
													"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimagenotice");
											addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1, "www.coreonmobile.com",
													"textimagenotice");
											addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
											addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2, "www.google.com", "textimagenotice");
											// addStrings("space", "space", "space", 0);
											// cannot retrieve correct layout

											final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(con, _title);
											adapter.setValues(_title, _content, _date, _image, _type, _extra);
											listViewNotice.setAdapter(adapter);
											listViewNotice.setDividerHeight(0);

											listViewNotice.setOnItemClickListener(new OnItemClickListener() {

												@Override
												public void onItemClick(AdapterView<?> parent, View view, int position, long id)
												{

													if (view.getTag().equals("textimagenotice"))
													{
														// open notice content full
														adapter.setNotice(false);

														View noticeView = setPage(R.layout.notice_content);
														ListView listView = (ListView) noticeView.findViewById(R.id.listViewNoticeContent);

														TextView textTitle = (TextView) view.findViewById(R.id.lblSubTitleText);
														TextView textDate = (TextView) view.findViewById(R.id.lblDateText);

														String image = _image.get(position).toString();
														String url = _extra.get(position).toString();
														String content = _content.get(position).toString();

														int imageInt = Integer.parseInt(image);

														removeStringsValues();
														addStrings("", "", "", 0, "", "space");
														addStrings(textTitle.getText().toString(), content, textDate.getText().toString(), imageInt,
																url, "noticecontent");
														addStrings("", "", "", 0, "", "space");

														MySimpleArrayAdapter noticeContentAdapter = new MySimpleArrayAdapter(con, _title);
														noticeContentAdapter.setValues(_title, _content, _date, _image, _type, _extra);
														listView.setAdapter(noticeContentAdapter);
														listView.setDividerHeight(0);
													}
												}
											});
										}
										else if (view.getTag().equals("textimage"))
										{
											// open notice content full

											View noticeView = setPage(R.layout.notice_content);
											ListView listView = (ListView) noticeView.findViewById(R.id.listViewNoticeContent);

											TextView textTitle = (TextView) view.findViewById(R.id.lblSubTitleText);
											TextView textDate = (TextView) view.findViewById(R.id.lblDateText);

											String image = _image.get(position).toString();
											String url = _extra.get(position).toString();
											String content = _content.get(position).toString();

											int imageInt = Integer.parseInt(image);
											removeStringsValues();

											addStrings("space", "space", "space", 0, "space", "space");
											addStrings(textTitle.getText().toString(), content, textDate.getText().toString(), imageInt, url,
													"noticecontent");
											addStrings("space", "space", "space", 0, "space", "space");

											MySimpleArrayAdapter noticeContentAdapter = new MySimpleArrayAdapter(con, _title);
											noticeContentAdapter.setValues(_title, _content, _date, _image, _type, _extra);
											listView.setAdapter(noticeContentAdapter);
											listView.setDividerHeight(0);
										}
										else if (view.getTag().equals("header"))
										{
											String title = _title.get(position).toString();
											if (title.equals("Notice"))
											{
												// go to notices page
												// open notices page
												Toast.makeText(con, "Notices", Toast.LENGTH_LONG).show();
											}
											else if (title.equals("Exclusive Offers"))
											{
												// go to offers page
												// open offers page
												Toast.makeText(con, "Offers", Toast.LENGTH_LONG).show();
											}
										}

										// Toast.makeText(con, "Position :" + itemPosition +
										// "  ListItem : " + itemValue, Toast.LENGTH_LONG).show();
									}
								});

								ImageView iv = (ImageView) view6.findViewById(R.id.imageViewMyCards);
								iv.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v)
									{
										CoreonMain.mPager.setCurrentItem(2);
									}
								});

								CoreonMain.mPager.setCurrentItem(1);
								break;
							case 1:
								switch (childPosition)
								{
									case 0:
										// Toast.makeText(con, "Account info",
										// Toast.LENGTH_SHORT).show();
										CoreonMain.mPager.setCurrentItem(1);
										View viewChild = setPage(R.layout.account_info);
										removeHeaderbackColor((View) viewChild.getParent().getParent());
										break;
									case 1:
										// Toast.makeText(con, "Points", Toast.LENGTH_SHORT).show();
										CoreonMain.mPager.setCurrentItem(1);
										View v2 = setPage(R.layout.points);
										break;
									case 2:
										// Toast.makeText(con, "Change Password",
										// Toast.LENGTH_SHORT).show();
										CoreonMain.mPager.setCurrentItem(1);
										View viewChild1 = setPage(R.layout.change_password);
										removeHeaderbackColor((View) viewChild1.getParent().getParent());

										// CoreonMain.setLayout(R.layout.change_password);
										break;
									case 3:
										// Toast.makeText(con, "App Settings",
										// Toast.LENGTH_SHORT).show();

										Intent intent = new Intent(con, SettingsActivity.class);
										// intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
										// overridePendingTransition(R.anim.righttoleft,R.anim.lefttoright);
										con.startActivity(intent);

										break;
									case 4:

										// ////////Logout button

										// 1. Instantiate an AlertDialog.Builder with its
										// constructor
										AlertDialog.Builder builder = new AlertDialog.Builder(con);

										// 2. Chain together various setter methods to set the
										// dialog characteristics
										builder.setMessage("Logout your account?").setTitle("Coreon Mobile");

										builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id)
											{
												// User clicked OK button

												SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(con);
												SharedPreferences.Editor editor = preferences.edit();
												Boolean fl = false;
												editor.putBoolean("LoggedIn", fl);
												// set value of Logged In to false to invoke log in
												// screen on startup
												editor.commit();

												Toast.makeText(con, "Logging Out..", Toast.LENGTH_SHORT).show();

												// ////
												((Activity) con).finish();
											}
										});
										builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id)
											{
												// User cancelled the dialog
											}
										});

										// 3. Get the AlertDialog from create()
										AlertDialog dialog = builder.create();

										dialog.show();

										// ((Activity)con).finish();
										// CoreonMain.finishActivity();

										break;
									default:
										break;
								}

								break;
							case 2:
								switch (childPosition)
								{
									case 0:
										// Toast.makeText(con, "Enroll Card",
										// Toast.LENGTH_SHORT).show();
										CoreonMain.mPager.setCurrentItem(1);
										final View viewChild = setPage(R.layout.enroll_card);
										removeHeaderbackColor((View) viewChild.getParent().getParent());
										TextView cashcard = (TextView) viewChild.findViewById(R.id.TextView02);
										// cashcard.setOnClickListener(new onc);

										cashcard.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v)
											{
												View view = setPage(R.layout.enroll_card_cash);
												TextView register = (TextView) view.findViewById(R.id.TextView02);

												// ImageView backbutton = (ImageView)
												// viewChild.findViewById(R.id.TextView02);
												register.setOnClickListener(new OnClickListener() {
													@Override
													public void onClick(View v)
													{
														View view2 = setPage(R.layout.enroll_card_register);

														TelephonyManager tm = (TelephonyManager) con.getSystemService(TELEPHONY_SERVICE);
														String number = tm.getLine1Number();
														// Toast.makeText(con, number,
														// Toast.LENGTH_SHORT).show();

														TextView textNumber = (TextView) view2.findViewById(R.id.textViewNumber);
														textNumber.setText(number.toString());

														ImageView im = (ImageView) view2.findViewById(R.id.imageViewPicture);
														im.setOnClickListener(new OnClickListener() {
															@Override
															public void onClick(View v)
															{

																// Toast.makeText(con,
																// "Open Camera",
																// Toast.LENGTH_SHORT).show();

																// Intent cameraIntent = new
																// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
																// con.startActivityForResult(cameraIntent,
																// 1888);
															}

															// protected void onActivityResult(int
															// requestCode, int resultCode, Intent
															// data) {
															// if (requestCode == CAMERA_REQUEST &&
															// resultCode == RESULT_OK) {
															// Bitmap photo = (Bitmap)
															// data.getExtras().get("data");
															// imageView.setImageBitmap(photo);
															// }
															// }
														});

													}
												});
											}
										});

										break;
									case 1:
										// Toast.makeText(con, "Promos", Toast.LENGTH_SHORT).show();
										CoreonMain.mPager.setCurrentItem(1);

										View viewChild1 = setPage(R.layout.promos);
										removeHeaderbackColor((View) viewChild1.getParent().getParent());
										break;
									case 2:
										// Toast.makeText(con, "Notices",
										// Toast.LENGTH_SHORT).show();
										CoreonMain.mPager.setCurrentItem(1);
										setPage(R.layout.notices);
										break;
									default:
										break;
								}
								break;

							case 3:
								// logo click
								// add egg

								Log.i("info", "Logo click");
								break;
							default:
								break;
						}
						return false;
					}
				});

				expListView.expandGroup(0);// home button
				expListView.expandGroup(1);// my account
				expListView.expandGroup(2);// actions
				expListView.expandGroup(3);// unclickable logo

				// click home button
				setHome();
				// expListView.performItemClick(expListView.getAdapter().getView(1, null, null), 1,
				// expListView.getAdapter().getItemId(1));

				((ViewPager) collection).addView(view0, 0);
				return view0;

			case 1:
				// main home container

				resId = R.layout.coreon_main_info;
				View view1 = inflater.inflate(resId, null);
				((ViewPager) collection).addView(view1, 0);

				// set page to main home layout
				// setPage(R.layout.main_info_home);

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);
				String fname = prefs.getString("fname", null);
				String lname = prefs.getString("lname", null);
				String points = prefs.getString("points", null);

				// TextView t = new TextView(con);

				// ImageView iv = (ImageView) view1.findViewById(R.id.imageViewMyCards);
				// iv.setOnClickListener(new OnClickListener() {
				// @Override
				// public void onClick(View v)
				// {
				// CoreonMain.mPager.setCurrentItem(2);
				// }
				// });

				// t = (TextView) view1.findViewById(R.id.textViewName);
				// t.setText(fname);
				//
				// t = (TextView) view1.findViewById(R.id.textCoreonPointsNumber);
				// t.setText(points + " Points");

				return view1;

			case 2:
				resId = R.layout.coreon_main_card_chooser;
				View view2 = inflater.inflate(resId, null);
				((ViewPager) collection).addView(view2, 0);
				return view2;

		}

		return resId;

	}

	public void setHome()
	{
		// try
		// {
		expListView.performItemClick(expListView.getAdapter().getView(1, null, null), 1, expListView.getAdapter().getItemId(1));
		// }
		// catch (Exception e)
		// {
		// Log.i("setHome", e.toString());
		// }
	}

	public void removeStringsValues()
	{
		_title = new ArrayList<String>();
		_content = new ArrayList<String>();
		_date = new ArrayList<String>();
		_image = new ArrayList<String>();
		_type = new ArrayList<String>();
		_extra = new ArrayList<String>();
	}

	public void addStrings(String title, String content, String date, int image, String extra, String type)
	{
		_title.add(title);
		_content.add(content);
		_date.add(date);
		_image.add(String.valueOf(image));
		_type.add(type);
		_extra.add(extra);
	}

	public static float convertDpToPixel(float dp, Context context)
	{
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public static int dpToPx(int dp)
	{
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	public static int pxToDp(int px)
	{
		return (int) (px / Resources.getSystem().getDisplayMetrics().density);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2)
	{
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return arg0 == ((View) arg1);
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	private void prepareListData()
	{
		// Prepare menu list drawer

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		listDataHeaderImage = new ArrayList<String>();
		listDataChildImage = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("Home");
		listDataHeaderImage.add(String.valueOf(R.drawable.home_icon)); // dummy icon
		listDataHeader.add("MY ACCOUNT");
		listDataHeaderImage.add(String.valueOf(R.drawable.edittext_style));// dummy icon
		listDataHeader.add("ACTIONS");
		listDataHeaderImage.add(String.valueOf(R.drawable.buttondivider));// dummy icon
		listDataHeader.add("");
		listDataHeaderImage.add(String.valueOf(R.drawable.cwallet));// dummy icon

		List<String> home = new ArrayList<String>();
		List<String> homeImage = new ArrayList<String>();

		home.add("Home");
		homeImage.add(String.valueOf(R.drawable.home_icon));

		// Adding child data for My account
		List<String> myaccount = new ArrayList<String>();
		List<String> myaccountImage = new ArrayList<String>();
		myaccount.add("Account Info");
		myaccountImage.add(String.valueOf(R.drawable.icon_account_info));

		myaccount.add("Points");
		myaccountImage.add(String.valueOf(R.drawable.icon_account_info));

		myaccount.add("Change Password");
		myaccountImage.add(String.valueOf(R.drawable.icon_change_pass));

		myaccount.add("App Settings");
		myaccountImage.add(String.valueOf(R.drawable.icon_app_settings));

		myaccount.add("Logout");
		myaccountImage.add(String.valueOf(R.drawable.icon_logout));

		// adding child data for actions
		List<String> actions = new ArrayList<String>();
		List<String> actionsImage = new ArrayList<String>();

		actions.add("Enroll Card");
		actionsImage.add(String.valueOf(R.drawable.icon_enroll_card));

		actions.add("Promos");
		actionsImage.add(String.valueOf(R.drawable.icon_promos));

		actions.add("Notices");
		actionsImage.add(String.valueOf(R.drawable.icon_notice));

		// adding child data for actions
		List<String> logo = new ArrayList<String>();
		List<String> logoImage = new ArrayList<String>();

		//
		logo.add("LOGO");
		logoImage.add(String.valueOf(R.drawable.cwallet));

		listDataChild.put(listDataHeader.get(0), home); // Header, Child data
		listDataChild.put(listDataHeader.get(1), myaccount);
		listDataChild.put(listDataHeader.get(2), actions);
		listDataChild.put(listDataHeader.get(3), logo);
		listDataChildImage.put(listDataHeaderImage.get(0), homeImage);
		listDataChildImage.put(listDataHeaderImage.get(1), myaccountImage);
		listDataChildImage.put(listDataHeaderImage.get(2), actionsImage);
		listDataChildImage.put(listDataHeaderImage.get(3), logoImage);

	}
}

class ChangeView extends AsyncTask<String, Integer, Long>
{

	String				useremail	= "";
	boolean				network		= true;
	boolean				timeout		= false;
	private Context		mContext;
	private Activity	mActivity;
	ProgressDialog		mDialog;

	public ChangeView(Context context, Activity activity)
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

		JSONArray jArray = null;
		String result = null;
		StringBuilder sb = null;
		InputStream is = null;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try
		{
			int timeoutsec = 20000; // 20 second timeout
			// desktop set to static ip 192.168.123.111
			String ipAdd = "192.168.123.111";
			String httpAddress = "http://" + ipAdd + "/android/androidsql.php?email='" + params[0] + "'&pw='" + params[1] + "'";

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
			return null;
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
		}
		catch (ParseException e1)
		{
			// Log.e("info_tag", "parseexcept:" + e1.toString());
			e1.printStackTrace();
		}

		useremail = "mpin: " + name;

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = preferences.edit();
		// Boolean tr = true;
		editor.putString("fname", fname); // value to store
		editor.putString("lname", lname); // value to store
		editor.putString("points", points); // value to store
		editor.commit();

		// Log.e("info_tag", "usermail: " + name.toString());
		// Toast.makeText(getBaseContext(),name, Toast.LENGTH_SHORT).show();

		return null;
	}

	protected void onProgressUpdate(Integer... progress)
	{
		// setProgressPercent(progress[0]);
	}

	@Override
	protected void onPreExecute()
	{
		// super.onPreExecute();
		//
		// mDialog = new ProgressDialog(mActivity);
		// mDialog.setMessage("Loggin in..");
		// mDialog.show();
	}

	protected void onPostExecute(Long result)
	{

		mDialog.dismiss();

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
			Toast.makeText(mContext, "No network connection", Toast.LENGTH_SHORT).show();
		}

	}
}
