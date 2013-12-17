package com.itcorea.coreonmobile;

import static android.content.Context.TELEPHONY_SERVICE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

@SuppressLint("CutPasteId")
public class CoreonMain extends FragmentActivity implements OnClickListener
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

	List<String>					listDataHeaderImage;
	HashMap<String, List<String>>	listDataChildImage;
	View							view;
	int								dev				= 15;

	SlidingMenu						menu;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// slide from right animation
		overridePendingTransition(R.anim.righttomain, R.anim.maintoleft);

		setContentView(R.layout.coreon_main_info);

		menu = new SlidingMenu(this);
		menu.setSlidingEnabled(true);
		menu.setMode(SlidingMenu.LEFT_RIGHT);
		menu.setShadowWidthRes(R.dimen.shadow_margin);
		menu.setShadowDrawable(R.drawable.right_shadow);
		menu.setSecondaryShadowDrawable(R.drawable.leftshadow_menu);
		menu.setBehindOffsetRes(R.dimen.main_margin);
		menu.setMenu(R.layout.coreon_main_menu);
		menu.setSecondaryMenu(R.layout.coreon_main_card_chooser);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setFadeDegree(0.45f);
		menu.setBehindScrollScale(0.25f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

		// set preferences as logged in
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = preferences.edit();
		Boolean tr = true;
		editor.putBoolean("LoggedIn", tr); // value to store
		editor.commit();

		// GetInfoAsync n = new GetInfoAsync(getApplicationContext(), CoreonMain.this);
		// n.execute("test", "test", "offer");

		expListView = (ExpandableListView) findViewById(R.id.lvExp);
		prepareListData();
		listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild, listDataHeaderImage, listDataChildImage);
		expListView.setAdapter(listAdapter);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		int mScreenWidth = displayMetrics.widthPixels;

		mScreenWidth = (mScreenWidth - 150);// convertDpToPixel(R.dimen.main_margin,con));

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
						// home button click
						menu.toggle();
						SetHomepage();
						break;
					case 1:
						switch (childPosition)
						{
							case 0:
								// open account information
								menu.toggle();
								ShowAccountInformation();

								break;
							case 1:
								// open points
								menu.toggle();
								ShowPoints();

								break;
							case 2:
								// open change password
								menu.toggle();
								ShowChangePassword();

								break;
							case 3:
								// Settings
								OpenSettings();

								break;
							case 4:
								// Logout
								Logout();

								break;
							default:
								break;
						}

						break;
					case 2:
						switch (childPosition)
						{
							case 0:
								// Enroll card
								menu.toggle();
								ShowEnrollCard();

								break;
							case 1:
								// Promos
								menu.toggle();
								ShowPromos();

								break;
							case 2:
								// Notices
								menu.toggle();
								openNotice(null);

								break;
							default:
								break;
						}
						break;

					case 3:
						// logo
						LogoClick();

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
		expListView.expandGroup(3);// logo

		// for dev

		//set home page as starting page
		SetHomepage();

		// int item = 3;
		// expListView.performItemClick(expListView.getAdapter().getView(item, null, null), item,
		// expListView.getAdapter().getItemId(item));

	}

	public void LogoClick()
	{
		// add egg
		dev--;
		if (dev >= 10)
			;
		else if (dev < 10 && dev >= 0)
		{
			Toast.makeText(getApplicationContext(), "You are " + String.valueOf(dev) + " clicks away to becoming a developer", Toast.LENGTH_SHORT)
					.show();
		}
		else
			Toast.makeText(getApplicationContext(), "Congratulations you are now a developer", Toast.LENGTH_SHORT).show();
		Log.i("info", "Logo click");
	}

	public void ShowPromos()
	{
		View viewChild1 = setPage(R.layout.promos);
		removeHeaderbackColor();// ((View) viewChild1.getParent().getParent());
	}

	public void ShowChangePassword()
	{
		View viewChild1 = setPage(R.layout.change_password);
		removeHeaderbackColor();// ((View) viewChild1.getParent().getParent());
	}

	public void ShowPoints()
	{
		setPage(R.layout.points);
	}

	public void OpenSettings()
	{
		Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(intent);
	}

	public void ShowEnrollCard()
	{
		final View viewChild = setPage(R.layout.enroll_card);
		removeHeaderbackColor();// ((View) viewChild.getParent().getParent());
		TextView cashcard = (TextView) viewChild.findViewById(R.id.TextView02);

		cashcard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				View view = setPage(R.layout.enroll_card_cash);
				TextView register = (TextView) view.findViewById(R.id.TextView02);

				// onclick of first selection
				register.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v)
					{
						View view2 = setPage(R.layout.enroll_card_register);

						TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
						String number = tm.getLine1Number();

						TextView textNumber = (TextView) view2.findViewById(R.id.textViewNumber);
						textNumber.setText(number.toString());

						ImageView im = (ImageView) view2.findViewById(R.id.imageViewPicture);
						// on click on capture image
						im.setOnClickListener(new OnClickListener() {
							@SuppressLint("ShowToast")
							@Override
							public void onClick(View v)
							{
								Toast.makeText(getApplicationContext(), "Open Camera", Toast.LENGTH_LONG);
								// Intent cameraIntent = new
								// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
								// con.startActivityForResult(cameraIntent,
								// 1888);
							}
						});
					}
				});
			}
		});
	}

	private class ShowAccountInformation extends AsyncTask<String, Void, String>
	{
		MySimpleArrayAdapter	noticeContentAdapter;
		ListView				listView2;

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			noticeContentAdapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

			noticeContentAdapter.initiatizeStringsValues();
			noticeContentAdapter.addStrings("EMAIL ACCOUNT", "", "", 0, "", "accountheader");
			noticeContentAdapter.addStrings("", "email@yahoo.com", "", 0, "", "accountemail");
			noticeContentAdapter.addStrings("", "", "", 0, "", "accountlineorange");
			noticeContentAdapter.addStrings("ACCOUNT INFORMATION", "", "", 0, "", "accountheader");
			noticeContentAdapter.addStrings("First Name", "Ariel", "", 0, "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", 0, "", "accountlinegray");
			noticeContentAdapter.addStrings("Middle Name", "Belo", "", 0, "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", 0, "", "accountlinegray");
			noticeContentAdapter.addStrings("Last Name", "Surca", "", 0, "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", 0, "", "accountlinegray");
			noticeContentAdapter.addStrings("Birthday", "June 19 1979", "", 0, "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", 0, "", "accountlinegray");
			noticeContentAdapter.addStrings("Mobile Number", "09177896541", "", 0, "", "accountcontentmobile");
			noticeContentAdapter.addStrings("", "", "", 0, "", "accountlinegray");
			noticeContentAdapter.addStrings("Address", "29 Sitio Upper Manalite II Brgy. Sta. Cruz Antipolo City Rizal 12700", "", 0, "", "accountcontentaddress");
			noticeContentAdapter.addStrings("", "", "", 0, "", "accountlineorange");
			noticeContentAdapter.addStrings("WALLET INFORMATION", "", "", 0, "", "accountheader");
			noticeContentAdapter.addStrings("My Cards", "7 Cards", "", 0, "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", 0, "", "accountlinegray");
			noticeContentAdapter.addStrings("Coreon Points", "Points", "", 0, "", "accountcontent");

			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			// Long time = System.currentTimeMillis();
			// Log.i("Exception1", String.valueOf(System.currentTimeMillis() - time));

			View viewChild = setPage(R.layout.account_info);
			// Log.i("Exception2", String.valueOf(System.currentTimeMillis() - time));

			listView2 = (ListView) viewChild.findViewById(R.id.listViewAccount);
			listView2.setAdapter(noticeContentAdapter);
			listView2.setDividerHeight(0);
		}

		@Override
		protected void onPreExecute()
		{
			removeHeaderbackColor();
			setPage(R.layout.progress);
		}

		@Override
		protected void onProgressUpdate(Void... values)
		{
		}
	}

	public void ShowAccountInformation()
	{
		new ShowAccountInformation().execute("tester");
	}

	public void Logout()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(CoreonMain.this);
		builder.setMessage("Logout your account?").setTitle("Coreon Mobile");

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id)
			{
				// User clicked OK button

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				Boolean fl = false;
				editor.putBoolean("LoggedIn", fl);
				// set value of Logged In to false to invoke log in
				// on screen on startup
				editor.commit();

				Toast.makeText(getApplicationContext(), "Logging Out..", Toast.LENGTH_SHORT).show();

				// finish this activity
				((Activity) CoreonMain.this).finish();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id)
			{
				// User cancelled the dialog do nothing
			}
		});

		// build the dialog then show
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private class ShowHomePage extends AsyncTask<String, Void, String>
	{
		MySimpleArrayAdapter	adapter;

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			adapter = new MySimpleArrayAdapter(getApplicationContext(), _title);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

			String fname = prefs.getString("fname", "firstname");
			String lname = prefs.getString("lname", "lastname");
			String points = prefs.getString("points", "0");

			adapter.initiatizeStringsValues();

			adapter.addStrings("space", "space", "space", 0, "space", "space");

			adapter.addStrings("userinfo", fname, "userinfo", 0, "userinfo", "userinfo");
			adapter.addStrings("My Cards", "0 Cards", "userinfo", 0, "userinfo", "usercontent");
			adapter.addStrings("", "", "", 0, "", "userline");
			adapter.addStrings("Coreon Points", points + " Points", "userinfo", 0, "userinfo", "usercontent");
			adapter.addStrings("", "", "", 0, "", "userline");
			adapter.addStrings("Notice", "12", "userinfo", 0, "userinfo", "usercontent");
			adapter.addStrings("userinfo", "userinfo", "userinfo", 0, "userinfo", "userbottom");

			adapter.addStrings("space", "space", "space", 0, "space", "space");

			adapter.addStrings("Exclusive Offers", "", "", 0, "header", "header");
			adapter.addStrings("Dong Won Restaurant", "Get 50% off on your test test test test test test test test payment of Coreon Card",
					"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com", "textimage");
			adapter.addStrings(
					"Dong Won Restaurant",
					"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
					"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "http://www.coreonmobile.com/", "textimage");
			adapter.addStrings(
					"Dong Won Restaurant",
					"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
					"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "http://www.coreonmobile.com/", "textimage");
			adapter.addStrings("Dong Won Restaurant", "payment of Coreon Card", "August 25, 2013 at 11:30 PM", R.drawable.offer_image_1,
					"www.yahoo.com", "textimage");
			adapter.addStrings("", "", "", 0, "", "bottomshadow");
			adapter.addStrings("space", "space", "space", 0, "space", "space");
			adapter.addStrings("Notice", "", "", 0, "header", "header");
			adapter.addStrings("Dong Won Restaurant",
					"Get 50% off on your payment of Coreon CardGet 50% off on your payment of Coreon CardGet 50% off on your payment of Coreon Card",
					"August 25, 2013 at 11:30 PM", 0, "text", "text");
			adapter.addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card", "August 25, 2013 at 11:30 PM", 0, "text", "text");
			adapter.addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card", "August 25, 2013 at 11:30 PM", 0, "text", "text");
			adapter.addStrings("", "", "", 0, "", "bottomshadow");

			// show seven spaces
			for (int i = 0; i < 6; i++)
			{
				adapter.addStrings("", "", "", 0, "", "space");
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			View view6 = setLayout(R.layout.main_info_home_list);

			ListView listView = (ListView) view6.findViewById(R.id.listView1);

			listView.setAdapter(adapter);
			listView.setDividerHeight(0);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{
					if (position == 2)
					{
						// clicked my cards
						menu.showSecondaryMenu(true);

						// CoreonMain.mPager.setCurrentItem(2);
					}
					else if (position == 4)
					{
						// clicked coreon points
						ShowPoints();
					}
					else if (position == 6)
					{
						// clicked notices
						openNotice(null);
					}
					else if (view.getTag().equals("textimage"))
					{
						// open notice content full

						View noticeView = setPage(R.layout.notice_content);

						TextView textTitle = (TextView) view.findViewById(R.id.lblSubTitleText);
						TextView textDate = (TextView) view.findViewById(R.id.lblDateText);

						String image = adapter._image.get(position).toString();
						String url = adapter._extra.get(position).toString();
						String content = adapter._content.get(position).toString();

						int imageInt = Integer.parseInt(image);
						MySimpleArrayAdapter noticeContentAdapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

						noticeContentAdapter.initiatizeStringsValues();
						noticeContentAdapter.addStrings("space", "space", "space", 0, "space", "space");
						noticeContentAdapter.addStrings(textTitle.getText().toString(), content, textDate.getText().toString(), imageInt, url,
								"noticecontent");
						noticeContentAdapter.addStrings("space", "space", "space", 0, "space", "space");

						ListView listView = (ListView) noticeView.findViewById(R.id.listViewNoticeContent);
						listView.setAdapter(noticeContentAdapter);
						listView.setDividerHeight(0);
					}
					else if (view.getTag().equals("header"))
					{
						String title = _title.get(position).toString();
						if (title.equals("Notice"))
						{
							// Notice
							openNotice(null);
						}
						else if (title.equals("Exclusive Offers"))
						{
							// Notice
							openOffers(null);
						}
					}
				}
			});

			// click cards
			ImageView iv = (ImageView) view6.findViewById(R.id.imageViewMyCards);
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					menu.showSecondaryMenu(true);
				}
			});
		}

		@Override
		protected void onPreExecute()
		{
			removeHeaderbackColor();
			setPage(R.layout.progress);
		}

		@Override
		protected void onProgressUpdate(Void... values)
		{
		}
	}

	public void SetHomepage()
	{
		new ShowHomePage().execute();
	}

	public View setPage(int id)
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

	public void removeHeaderbackColor()
	{
		ImageButton imn = (ImageButton) findViewById(R.id.imageButtonNotice);
		imn.setBackgroundColor(Color.TRANSPARENT);
		ImageButton imo = (ImageButton) findViewById(R.id.imageButtonOffers);
		imo.setBackgroundColor(Color.TRANSPARENT);
		ImageButton imh = (ImageButton) findViewById(R.id.imageButtonHelp);
		imh.setBackgroundColor(Color.TRANSPARENT);
	}

	private void prepareListData()
	{
		// Prepare expandable menu list drawer data

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

	public void openCards(View view)
	{
		menu.showSecondaryMenu(true);
		return;
	}

	public void openMenu(View view)
	{
		menu.toggle();
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
	int stack = 1;

	@Override
	public void onBackPressed()
	{
		if (menu.isMenuShowing())
		{
			stack = 1;
			menu.showContent(true);
			return;
		}
		else if(stack == 1)
		{
			SetHomepage();
			stack = 0;
			return;
		}
	
		

		super.onBackPressed();
	}

	public void openSendMoney(View view)
	{
		Intent intent = new Intent(this, GlobeSendMoney.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// overridePendingTransition(R.anim.righttoleft,R.anim.lefttoright);
		startActivity(intent);
		return;
	}

	public void openNotice(View view)
	{
		stack = 1;
		removeHeaderbackColor();

		ImageButton im = (ImageButton) findViewById(R.id.imageButtonNotice);
		im.setBackgroundColor(Color.WHITE);

		// noticeSelected = true;
		// offerSelected = false;
		// helpSelected = false;

		View v = setLayout(R.layout.notices);

		// Create Inner Thread Class
		Thread background = new Thread(new Runnable() {

			private final HttpClient	Client	= new DefaultHttpClient();
			private String				URL		= "http://androidexample.com/media/webservice/getPage.php";

			// After call for background.start this run method call
			public void run()
			{
				try
				{
					String SetServerString = "";
					HttpGet httpget = new HttpGet(URL);
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					SetServerString = Client.execute(httpget, responseHandler);
					threadMsg(SetServerString);
				}
				catch (Throwable t)
				{
					// just end the background thread
					Log.i("Animation", "Thread  exception " + t);
				}
			}

			private void threadMsg(String msg)
			{
				if (!msg.equals(null) && !msg.equals(""))
				{
					Message msgObj = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("message", msg);
					msgObj.setData(b);
					handler.sendMessage(msgObj);
				}
			}

			// Define the Handler that receives messages from the thread and update the
			// progress
			private final Handler	handler	= new Handler(getApplicationContext().getMainLooper()) {

												public void handleMessage(Message msg)
												{
													String aResponse = msg.getData().getString("message");

													if ((null != aResponse))
													{

														// ALERT MESSAGE
														Toast.makeText(getBaseContext(), "Server Response: " + aResponse, Toast.LENGTH_SHORT).show();

														try
														{

															// hide progress bar
															final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
															progressBar.setVisibility(View.GONE);

															ListView listViewNotice = (ListView) findViewById(R.id.listViewNotices);

															final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(),
																	_title);

															adapter.initiatizeStringsValues();

															adapter.addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com",
																	"textimagenotice");
															adapter.addStrings(
																	"Dong Won Restaurant",
																	"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1,
																	"www.coreonmobile.com", "textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2,
																	"www.google.com", "textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1,
																	"www.google.com", "textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2,
																	"www.google.com", "textimagenotice");

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
																		ListView listView = (ListView) noticeView
																				.findViewById(R.id.listViewNoticeContent);

																		TextView textTitle = (TextView) view.findViewById(R.id.lblSubTitleText);
																		TextView textDate = (TextView) view.findViewById(R.id.lblDateText);

																		String image = adapter._image.get(position).toString();
																		String url = adapter._extra.get(position).toString();
																		String content = adapter._content.get(position).toString();

																		int imageInt = Integer.parseInt(image);

																		// removeStringsValues();
																		MySimpleArrayAdapter noticeContentAdapter = new MySimpleArrayAdapter(
																				getApplicationContext(), _title);

																		noticeContentAdapter.initiatizeStringsValues();

																		noticeContentAdapter.addStrings("", "", "", 0, "", "space");
																		noticeContentAdapter.addStrings(textTitle.getText().toString(), content,
																				textDate.getText().toString(), imageInt, url, "noticecontent");
																		noticeContentAdapter.addStrings("", "", "", 0, "", "space");

																		// noticeContentAdapter.setValues(_title,
																		// _content, _date, _image,
																		// _type, _extra);
																		listView.setAdapter(noticeContentAdapter);
																		listView.setDividerHeight(0);
																	}
																}
															});

														}
														catch (Exception e)
														{
															Log.i("Ex", e.toString());
														}
													}
													else
													{
														// ALERT MESSAGE
														Toast.makeText(getBaseContext(), "Not Got Response From Server.", Toast.LENGTH_SHORT).show();
													}

												}
											};

		});
		// Start Thread
		background.start(); // After call start method thread called run Method

		return;
	}

	public void openOffers(View view)
	{
		stack = 1;
		removeHeaderbackColor();

		ImageButton im = (ImageButton) findViewById(R.id.imageButtonOffers);
		im.setBackgroundColor(Color.WHITE);

		setLayout(R.layout.offers);
		offerSelected = true;
		noticeSelected = false;
		helpSelected = false;

		// Create Inner Thread Class
		Thread background = new Thread(new Runnable() {

			private final HttpClient	Client	= new DefaultHttpClient();
			private String				URL		= "http://androidexample.com/media/webservice/getPage.php";

			// After call for background.start this run method call
			public void run()
			{
				try
				{
					// Thread.sleep(1000);

					String SetServerString = "";
					HttpGet httpget = new HttpGet(URL);
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					SetServerString = Client.execute(httpget, responseHandler);
					threadMsg(SetServerString);

				}
				catch (Throwable t)
				{
					// just end the background thread
					Log.i("Animation", "Thread  exception " + t);
				}
			}

			private void threadMsg(String msg)
			{
				if (!msg.equals(null) && !msg.equals(""))
				{
					Message msgObj = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("message", msg);
					msgObj.setData(b);
					handler.sendMessage(msgObj);
				}
			}

			// Define the Handler that receives messages from the thread and update the
			// progress
			private final Handler	handler	= new Handler() {

												public void handleMessage(Message msg)
												{
													String aResponse = msg.getData().getString("message");

													if ((null != aResponse))
													{
														// ALERT MESSAGE
														// Toast.makeText(getBaseContext(),
														// "Server Response: " + aResponse,
														// Toast.LENGTH_SHORT)
														// .show();

														try
														{

															// hide progress bar
															final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
															progressBar.setVisibility(View.GONE);

															ListView listViewNotice = (ListView) findViewById(R.id.listViewOffers);

															MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

															// removeStringsValues();

															// adapter.setValues(_title, _content,
															// _date, _image, _type, _extra);

															// adapter.setNotice(true);

															adapter.initiatizeStringsValues();

															adapter.addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com",
																	"textimagenotice");
															adapter.addStrings(
																	"Dong Won Restaurant",
																	"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Globe G-Cash", "Lorem ipsum dolor sit amet, consectetur  adipiscin",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_2, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Coreon Mobile", "Discount Curabitur et justo egestas, tristique te",
																	"August 25, 2013 at 11:30 PM", R.drawable.offer_image_1, "www.google.com",
																	"textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1,
																	"www.google.com", "textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2,
																	"www.google.com", "textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1,
																	"www.google.com", "textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2,
																	"www.google.com", "textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_1,
																	"www.google.com", "textimagenotice");
															adapter.addStrings("Offers", "Tester", "Date", R.drawable.offer_image_2,
																	"www.google.com", "textimagenotice");

															listViewNotice.setAdapter(adapter);
															listViewNotice.setDividerHeight(0);

															listViewNotice.setOnItemClickListener(new OnItemClickListener() {

																@Override
																public void onItemClick(AdapterView<?> parent, View view, int position, long id)
																{
																	Log.i("test", "tester");
																}
															});

														}
														catch (Exception e)
														{
															Log.i("Ex", e.toString());
														}
													}
													else
													{
														// ALERT MESSAGE
														Toast.makeText(getBaseContext(), "Not Got Response From Server.", Toast.LENGTH_SHORT).show();
													}
												}
											};

		});
		// Start Thread
		background.start(); // After call start method thread called run Method

		return;
	}

	public void openHelp(View view)
	{
		if (!helpSelected)
		{
			removeHeaderbackColor();

			ImageButton imh = (ImageButton) findViewById(R.id.imageButtonHelp);
			imh.setBackgroundColor(Color.WHITE);

			setLayout(R.layout.help);
			helpSelected = true;
			noticeSelected = false;
			offerSelected = false;
		}
		else
		{

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

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

	}
}

// old code
// to be deleted
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
	int								dev		= 15;

	ArrayList<String>				_title	= new ArrayList<String>();

	public Object instantiateItem(View collection, int position)
	{
		LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		con = collection.getContext();
		view = collection;

		int resId = 0;
		switch (position)
		{

		}

		return resId;

	}

	public void setHome()
	{
		expListView.performItemClick(expListView.getAdapter().getView(1, null, null), 1, expListView.getAdapter().getItemId(1));
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
	public int getCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}