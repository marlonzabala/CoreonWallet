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
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
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
public class CoreonMain extends FragmentActivity
{
	final static int				CAMERA_PIC_REQUEST	= 1;
	final static int				PIC_CROP			= 2;
	
	
	public Uri						picUri;
	public static ViewPager			mPager;
	public static int				history;
	int								margin;

	boolean							noticeSelected		= false;
	boolean							offerSelected		= false;
	boolean							helpSelected		= false;

	ExpandableListAdapter			listAdapter;
	ListView						listViewCard;
	MySimpleArrayAdapter			cardAdapter;
	ExpandableListView				expListView;
	List<String>					listDataHeader;
	HashMap<String, List<String>>	listDataChild;
	MySimpleArrayAdapter			adapterHome;
	Uri								mPhotoUri;

	ArrayList<String>				_title				= new ArrayList<String>();
	ArrayList<String>				_stack				= new ArrayList<String>();

	List<String>					listDataHeaderImage;
	HashMap<String, List<String>>	listDataChildImage;
	View							view;
	int								dev					= 15;

	SlidingMenu						menu;

	// network related variables
	// desktop set to static ip 192.168.123.111
	String							ipAdd				= "192.168.123.111";
	int								timeoutsec			= 20000;					// 20 second
																					// timeout

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// slide from right animation
		overridePendingTransition(R.anim.righttomain, R.anim.maintoleft);

		setContentView(R.layout.coreon_main_info);

		InitializeSlidingMenu();

		// set preferences as logged in
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = preferences.edit();
		Boolean tr = true;
		editor.putBoolean("LoggedIn", tr); // value to store
		editor.commit();

		InitializeMenu();
		InitializeCardList();

		// dev set to capture image
		// showEnrollCardRegister(1);
		// home page as starting page

		// return state
		// _stack = null;
		boolean secondMenu = false;
		boolean firstMenu = false;

		if (savedInstanceState != null)
		{
			_stack = savedInstanceState.getStringArrayList("stack");
			firstMenu = savedInstanceState.getBoolean("firstMenu", false);
			secondMenu = savedInstanceState.getBoolean("secondMenu", false);
		}

		Log.e("second", String.valueOf(secondMenu));
		Log.e("first", String.valueOf(firstMenu));

		if (_stack != null && (_stack.size() != 0))
		{
			String initView = _stack.get(_stack.size() - 1).toString();
			viewPage(initView);
		}
		else
		{
			SetHomepage();
			// showEnrollCardRegister(1);
		}

		if (secondMenu)
		{
			menu.showSecondaryMenu(true);
		}
		else if (firstMenu)
		{
			menu.showMenu();
		}

		// GetInfoAsync n = new GetInfoAsync(getApplicationContext(), CoreonMain.this);
		// n.execute("test", "test", "offer");
	}

	public void historyStackAdd(String view)
	{
		if (_stack != null && (_stack.size() != 0))
		{
			// if duplicate do not add to stack
			String lastView = _stack.get(_stack.size() - 1).toString();
			if (!lastView.equals(view))
			{
				_stack.add(view);
			}
		}
		else
		{
			_stack.add(view);
		}
	}

	public void historyStackRemoveLast()
	{
		if (_stack != null && (_stack.size() != 0))
		{
			// _stack.remove(_stack.size() - 1);
			Log.e("removed", String.valueOf(_stack.remove(_stack.size() - 1)));
		}
	}

	int	exit	= 1;

	public void viewPage(String view)
	{
		if (view.equals("home"))
			SetHomepage();
		else if (view.equals("offer"))
			openOffers(null);
		else if (view.equals("notice"))
			openNotice(null);
		else if (view.equals("help"))
			openHelp(null);
		else if (view.equals("accountinformation"))
			ShowAccountInformation();
		else if (view.equals("points"))
			ShowPoints();
		else if (view.equals("enrollcard"))
			ShowEnrollCard();
		else if (view.equals("changepassword"))
			ShowChangePassword();
		else if (view.equals("shownotice"))
			;// ShowAccountInformation();
	}

	public void historyStackShowLast()
	{
		if (_stack != null)
		{
			if (_stack.size() >= 2)
			{
				historyStackRemoveLast();
				String view = _stack.get(_stack.size() - 1).toString();

				viewPage(view);

				// String stacks = "";
				// for (int i = 0; i < _stack.size(); i++) stacks = stacks +"/"+ _stack.get(i);
				// Log.e("stack",stacks);
				exit = 1;
			}
			else if (_stack.size() >= 1)
			{
				this.finish();
			}
		}
	}

	protected void onSaveInstanceState(Bundle savedInstanceState)
	{
		// _stack.add("home");
		// savedInstanceState.putString("start", "start");
		savedInstanceState.putStringArrayList("stack", _stack);

		savedInstanceState.putBoolean("secondMenu", false);
		savedInstanceState.putBoolean("firstMenu", false);

		if (menu.isSecondaryMenuShowing())
		{
			savedInstanceState.putBoolean("secondMenu", true);
		}
		else if (menu.isMenuShowing())
		{
			savedInstanceState.putBoolean("firstMenu", true);
		}

		super.onSaveInstanceState(savedInstanceState);
	}

	public void InitializeSlidingMenu()
	{
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
	}

	@SuppressLint("NewApi")
	public void InitializeMenu()
	{
		// create expandable list
		expListView = (ExpandableListView) findViewById(R.id.lvExp);
		prepareListData();
		listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild, listDataHeaderImage, listDataChildImage);
		expListView.setAdapter(listAdapter);

		// align expandable list indicator
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
	}

	public void InitializeCardList()
	{
		cardAdapter = new MySimpleArrayAdapter(getApplicationContext(), _title);
		cardAdapter.initiatizeStringsValues();
		cardAdapter.addStrings("", "30", "", "", "", "space");
		cardAdapter.addStrings("", "", "", String.valueOf(R.drawable.card1), "", "card");
		cardAdapter.addStrings("", "", "", String.valueOf(R.drawable.card2), "", "card");
		cardAdapter.addStrings("", "", "", String.valueOf(R.drawable.card3), "", "card");
		cardAdapter.addStrings("", "", "", String.valueOf(R.drawable.card4), "", "card");
		cardAdapter.addStrings("", "30", "", "", "", "space");

		listViewCard = (ListView) findViewById(R.id.listViewCards);
		listViewCard.setAdapter(cardAdapter);

		listViewCard.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
			{
				// show card when clicked
				showPhoto(Integer.parseInt(cardAdapter._image.get(position)));
			};
		});
	}

	private void showPhoto(int drawable)
	{
		if (drawable == 0)
		{
			Log.e("conract", "drawable is 0");
		}

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.card_chooser_fragment, (ViewGroup) findViewById(R.id.root), false);
		ImageView imageCard = (ImageView) rowView.findViewById(R.id.imageViewCard);
		imageCard.setImageResource(drawable);
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(rowView);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialog.getWindow().setGravity(Gravity.CENTER);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();
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
		historyStackAdd("changepassword");
		View viewChild1 = setPage(R.layout.change_password);
		removeHeaderbackColor();// ((View) viewChild1.getParent().getParent());
	}

	public void ShowPoints()
	{
		historyStackAdd("points");
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
		historyStackAdd("enrollcard");
		final View viewChild = setPage(R.layout.enroll_card);
		removeHeaderbackColor();// ((View) viewChild.getParent().getParent());
		TextView cashcard = (TextView) viewChild.findViewById(R.id.TextView02);

		cashcard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				showEnrollCardCategory(0);
			}
		});
	}

	public void showEnrollCardCategory(int category)
	{
		if (category == 0)
		{
			Log.e("conract", "category is 0");
		}

		switch (category)
		{
			case 1:
				// credit card

				break;
			case 2:
				// membership card

				break;

			default:
				// cash card
				View view = setPage(R.layout.enroll_card_cash);
				TextView register = (TextView) view.findViewById(R.id.TextView02);

				// onclick of first selection
				register.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v)
					{
						showEnrollCardRegister(0);
					}
				});

				ImageView back = (ImageView) view.findViewById(R.id.ImageViewBackButton);
				back.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v)
					{
						ShowEnrollCard();
					}
				});

				break;
		}
	}

	final int	CAMERA_CAPTURE	= 1;

	// keep track of cropping intent

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	private void dispatchTakePictureIntent(int actionCode)
	{
		if (actionCode == 0)
		{
			Log.e("conract", "actionCode is 0");
		}

		// Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// if (takePictureIntent.resolveActivity(getPackageManager()) != null)
		// {
		// startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
		// }
		//
		Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
		captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
		// we will handle the returned data in onActivityResult
		startActivityForResult(captureIntent, CAMERA_CAPTURE);

		// picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new
		// ContentValues());
		//
		// //Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// //we will handle the returned data in onActivityResult
		// captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
		// startActivityForResult(captureIntent, CAMERA_CAPTURE);

		// try
		// {
		// String storageState = Environment.getExternalStorageState();
		// if (storageState.equals(Environment.MEDIA_MOUNTED))
		// {
		// picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new
		// ContentValues());
		//
		// picUri.getPath();
		// // picUri.getPath()
		//
		// File folder = new File(picUri.getPath());
		// boolean success = false;
		// if (!folder.exists())
		// {
		// success = folder.mkdirs();
		// }
		// if (!success)
		// {
		// Log.d("test", "Folder not created.");
		// }
		// else
		// {
		// Log.d("test", "Folder created!");
		// }
		//
		// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri.getPath());
		// startActivityForResult(intent, CAMERA_PIC_REQUEST);
		// }
		// else
		// {
		// new
		// AlertDialog.Builder(CoreonMain.this).setMessage("External Storeage (SD Card) is required.\n\nCurrent state: "
		// + storageState)
		// .setCancelable(true).create().show();
		// }
		// }
		// catch (ActivityNotFoundException anfe)
		// {
		// // display an error message
		// String errorMessage = "Whoops - your device doesn't support capturing images!";
		// Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
		// toast.show();
		// }
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			// user is returning from capturing an image using the camera
			if (requestCode == CAMERA_CAPTURE)
			{
				// get the Uri for the captured image
				// picUri = data.getData();
				// carry out the crop operation
				// performCrop();

				// call the standard crop action intent (the user device may not support it)
				Intent cropIntent = new Intent("com.android.camera.action.CROP");
				// indicate image type and Uri
				cropIntent.setDataAndType(picUri, "image/*");
				// set crop properties
				cropIntent.putExtra("crop", "true");
				// indicate aspect of desired crop
				cropIntent.putExtra("aspectX", 1);
				cropIntent.putExtra("aspectY", 1);
				// indicate output X and Y
				cropIntent.putExtra("outputX", 256);
				cropIntent.putExtra("outputY", 256);
				// retrieve data on return
				cropIntent.putExtra("return-data", true);
				// start the activity - we handle returning in onActivityResult
				startActivityForResult(cropIntent, PIC_CROP);

			}
			// user is returning from cropping the image
			else if (requestCode == PIC_CROP)
			{
				// get the returned data
				Bundle extras = data.getExtras();
				// get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");
				// retrieve a reference to the ImageView
				ImageView picView = (ImageView) findViewById(R.id.imageViewPic);
				// display the returned cropped image
				picView.setImageBitmap(getRoundedCornerBitmap(thePic));

				ImageView pic1 = (ImageView) findViewById(R.id.imageViewDefaultPicture);
				ImageView pic2 = (ImageView) findViewById(R.id.imageViewPicture2);
				pic1.setVisibility(View.GONE);
				pic2.setVisibility(View.GONE);

				TextView text1 = (TextView) findViewById(R.id.textView1Description);
				text1.setVisibility(View.GONE);

				TextView text2 = (TextView) findViewById(R.id.textView2Description);
				text2.setVisibility(View.GONE);
			}
		}
	}

	private void performCrop()
	{
		// take care of exceptions
		try
		{
			// call the standard crop action intent (the user device may not support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 3);
			cropIntent.putExtra("aspectY", 2);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 435);
			cropIntent.putExtra("outputY", 290);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);
		}
		// respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe)
		{
			// display an error message
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void showEnrollCardRegister(int category)
	{
		if (category == 0)
		{
			Log.e("conract", "category is 0");
		}

		TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
		String number = tm.getLine1Number();

		View view2 = setPage(R.layout.enroll_card_register);

		// set phone number on text
		TextView textNumber = (TextView) view2.findViewById(R.id.textViewNumber);
		textNumber.setText(number.toString());

		ImageView im = (ImageView) view2.findViewById(R.id.imageViewPicture2);
		// on click on capture image
		im.setOnClickListener(new OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v)
			{
				// dispatchTakePictureIntent(1);

				picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
				Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
				startActivityForResult(captureIntent, CAMERA_CAPTURE);

			}
		});

		ImageView back = (ImageView) view2.findViewById(R.id.ImageViewBackButton2);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				// ShowEnrollCard();
				showEnrollCardCategory(0);
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
				Thread.sleep(400);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			noticeContentAdapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

			noticeContentAdapter.initiatizeStringsValues();
			noticeContentAdapter.addStrings("EMAIL ACCOUNT", "", "", "", "", "accountheader");
			noticeContentAdapter.addStrings("", "emails@yahoo.com", "", "", "", "accountemail");
			noticeContentAdapter.addStrings("", "", "", "", "", "accountlineorange");
			noticeContentAdapter.addStrings("ACCOUNT INFORMATION", "", "", "", "", "accountheader");
			noticeContentAdapter.addStrings("First Name", "Ariel", "", "", "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
			noticeContentAdapter.addStrings("Middle Name", "Belo", "", "", "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
			noticeContentAdapter.addStrings("Last Name", "Surca", "", "", "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
			noticeContentAdapter.addStrings("Birthday", "June 19 1979", "", "", "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
			noticeContentAdapter.addStrings("Mobile Number", "09177896541", "", "", "", "accountcontentmobile");
			noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
			noticeContentAdapter.addStrings("Address", "29 Sitio Upper Manalite II Brgy. Sta. Cruz Antipolo City Rizal 12700", "", "", "",
					"accountcontentaddress");
			noticeContentAdapter.addStrings("", "", "", "", "", "accountlineorange");
			noticeContentAdapter.addStrings("WALLET INFORMATION", "", "", "", "", "accountheader");
			noticeContentAdapter.addStrings("My Cards", "7 Cards", "", "", "", "accountcontent");
			noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
			noticeContentAdapter.addStrings("Coreon Points", "Points", "", "", "", "accountcontent");

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

			listView2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{

					if (position == 4)
					{
						// first name

						final EditText input = new EditText(CoreonMain.this);
						input.setText(noticeContentAdapter._content.get(position).toString());
						// input.selectAll();

						new AlertDialog.Builder(CoreonMain.this).setTitle(noticeContentAdapter._title.get(position).toString()).setView(input)
								.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										Editable value = input.getText();
									}
								}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Do nothing.
									}
								}).show();

						input.requestFocus();
						input.postDelayed(new Runnable() {
							@Override
							public void run()
							{
								InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								keyboard.showSoftInput(input, 0);
							}
						}, 200);

					}
					else if (position == 6)
					{
						// middle name

						final EditText input = new EditText(CoreonMain.this);
						input.setText(noticeContentAdapter._content.get(position).toString());
						// input.selectAll();

						new AlertDialog.Builder(CoreonMain.this).setTitle(noticeContentAdapter._title.get(position).toString()).setView(input)
								.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										Editable value = input.getText();
									}
								}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Do nothing.
									}
								}).show();

						input.requestFocus();
						input.postDelayed(new Runnable() {
							@Override
							public void run()
							{
								InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								keyboard.showSoftInput(input, 0);
							}
						}, 200);
					}
					else if (position == 8)
					{
						// last name
						final EditText input = new EditText(CoreonMain.this);
						input.setText(noticeContentAdapter._content.get(position).toString());
						// input.selectAll();

						new AlertDialog.Builder(CoreonMain.this).setTitle(noticeContentAdapter._title.get(position).toString()).setView(input)
								.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										Editable value = input.getText();
									}
								}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Do nothing.
									}
								}).show();

						input.requestFocus();
						input.postDelayed(new Runnable() {
							@Override
							public void run()
							{
								InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								keyboard.showSoftInput(input, 0);
							}
						}, 200);
					}
					else if (position == 10)
					{
						// birth day

						final EditText input = new EditText(CoreonMain.this);
						input.setText(noticeContentAdapter._content.get(position).toString());
						// input.selectAll();

						new AlertDialog.Builder(CoreonMain.this).setTitle(noticeContentAdapter._title.get(position).toString()).setView(input)
								.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										Editable value = input.getText();
									}
								}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Do nothing.
									}
								}).show();

						input.requestFocus();
						input.postDelayed(new Runnable() {
							@Override
							public void run()
							{
								InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								keyboard.showSoftInput(input, 0);
							}
						}, 200);
					}
					else if (position == 12)
					{
						// mobile number
					}
					else if (position == 14)
					{
						// address
						final EditText input = new EditText(CoreonMain.this);
						input.setText(noticeContentAdapter._content.get(position).toString());
						// input.selectAll();

						new AlertDialog.Builder(CoreonMain.this).setTitle(noticeContentAdapter._title.get(position).toString()).setView(input)
								.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										Editable value = input.getText();
									}
								}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Do nothing.
									}
								}).show();

						input.requestFocus();
						input.postDelayed(new Runnable() {
							@Override
							public void run()
							{
								InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								keyboard.showSoftInput(input, 0);
							}
						}, 200);
					}
					else if (position == 17)
					{
						// mycards
					}
					else if (position == 19)
					{
						// points
					}
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

	public void ShowAccountInformation()
	{
		historyStackAdd("accountinformation");
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

	public void showNoticeContent(MySimpleArrayAdapter adapter, int position)
	{
		String title = adapter._title.get(position).toString();
		String date = adapter._date.get(position).toString();
		String image = adapter._image.get(position).toString();
		String url = adapter._extra.get(position).toString();
		String content = adapter._content.get(position).toString();

		View noticeView = setPage(R.layout.notice_content);

//		int imageInt = Integer.parseInt(image);
		MySimpleArrayAdapter noticeContentAdapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

		noticeContentAdapter.initiatizeStringsValues();
		noticeContentAdapter.addStrings("space", "space", "space", "", "space", "space");
		noticeContentAdapter.addStrings(title, content, date, image, url, "noticecontent");
		noticeContentAdapter.addStrings("space", "space", "space", "", "space", "space");

		ListView listView = (ListView) noticeView.findViewById(R.id.listViewNoticeContent);
		listView.setAdapter(noticeContentAdapter);
		listView.setDividerHeight(0);
	}

	private class ShowHomePage extends AsyncTask<String, Void, String>
	{
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

			// _stack.add("home");
			historyStackAdd("home");

			adapterHome = new MySimpleArrayAdapter(getApplicationContext(), _title);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

			String fname = prefs.getString("fname", "firstname");
			String lname = prefs.getString("lname", "lastname");
			String points = prefs.getString("points", "0");

			adapterHome.initiatizeStringsValues();

			adapterHome.addStrings("", "", "", "", "", "space");

			adapterHome.addStrings("userinfo", fname, "userinfo", "", "userinfo", "userinfo");
			adapterHome.addStrings("My Cards", "0 Cards", "userinfo", "", "userinfo", "usercontent");
			adapterHome.addStrings("", "", "", "", "", "userline");
			adapterHome.addStrings("Coreon Points", points + " Points", "userinfo", "", "userinfo", "usercontent");
			adapterHome.addStrings("", "", "", "", "", "userline");
			adapterHome.addStrings("Notice", "12", "userinfo", "", "userinfo", "usercontent");
			adapterHome.addStrings("userinfo", "userinfo", "userinfo", "", "userinfo", "userbottom");

			adapterHome.addStrings("", "", "", "", "", "space");

			adapterHome.addStrings("Exclusive Offers", "", "", "", "header", "header");
			adapterHome.addStrings("Dong Won Restaurant", "Get 50% off on your test test test test test test test test payment of Coreon Card",
					"August 25, 2013 at 11:30 PM", String.valueOf(R.drawable.offer_image_1), "www.google.com", "textimage");
			adapterHome
					.addStrings(
							"Dong Won Restaurant",
							"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
							"August 25, 2013 at 11:30 PM", String.valueOf(R.drawable.offer_image_2), "http://www.coreonmobile.com/", "textimage");
			adapterHome
					.addStrings(
							"Won Dong Restaurant",
							"Lorem ipsum dolor sit amet, dico simul pri ea, cum ullum euismod maiorum ex. Eum an sale copiosae, semper delenit antiopam ad vim. Eos ne accusam invidunt maiestatis, tibique legendos an pro. An discere vituperata cotidieque vis. Per laudem doming persecuti at, audire incorrupte philosophia no vis.",
							"August 25, 2013 at 11:30 PM", String.valueOf(R.drawable.offer_image_2), "http://www.coreonmobile.com/", "textimage");
			adapterHome.addStrings("Dong Won Restaurant", "payment of Coreon Card", "August 25, 2013 at 11:30 PM", String.valueOf(R.drawable.offer_image_1),
					"www.yahoo.com", "textimage");
			adapterHome.addStrings("", "", "", "", "", "bottomshadow");
			adapterHome.addStrings("", "", "", "", "", "space");
			adapterHome.addStrings("Notice", "", "", "", "header", "header");
			adapterHome.addStrings("Dong Won Restaurant",
					"Get 50% off on your payment of Coreon CardGet 50% off on your payment of Coreon CardGet 50% off on your payment of Coreon Card",
					"August 25, 2013 at 11:30 PM", "", "text", "text");
			adapterHome.addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card", "August 25, 2013 at 11:30 PM", "", "text",
					"text");
			adapterHome.addStrings("Dong Won Restaurant", "Get 50% off on your payment of Coreon Card", "August 25, 2013 at 11:30 PM", "", "text",
					"text");
			adapterHome.addStrings("", "", "", "", "", "bottomshadow");
			adapterHome.addStrings("", "180", "", "", "", "space");

			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			View view6 = setLayout(R.layout.main_info_home_list);

			ListView listView = (ListView) view6.findViewById(R.id.listView1);

			listView.setAdapter(adapterHome);
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
						showNoticeContent(adapterHome, position);
					}
					else if (view.getTag().equals("header"))
					{
						String title = adapterHome._title.get(position).toString();
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
		if (id == 0)
		{
			Log.e("conract", "id is 0");
		}

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
		if (id == 0)
		{
			Log.e("conract", "id is 0");
		}

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
		// Log.i("back button", "backpressed");
		// Toast.makeText(getApplicationContext(), "tester", Toast.LENGTH_SHORT).show();
		if (menu.isMenuShowing())
		{
			menu.showContent(true);
			return;
		}
		else
		{
			historyStackShowLast();
			return;
		}

		// super.onBackPressed();
	}

	public void openSendMoney(View view)
	{
		if (view == null)
		{
			Log.e("conract", "view is null");
		}

		Intent intent = new Intent(this, GlobeSendMoney.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		// overridePendingTransition(R.anim.righttoleft,R.anim.lefttoright);
		startActivity(intent);
		return;
	}

	private boolean isNetworkAvailable()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	boolean	network	= false;

	private String sendPost(String httpAddress)
	{
		boolean timeout = false;
		String result = "";
		StringBuilder sb = null;
		InputStream is = null;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		if (!isNetworkAvailable())
		{
			// Toast.makeText(getApplicationContext(), "No internet Conenction",
			// Toast.LENGTH_LONG).show();
			network = false;
			return "";
		}
		else
		{
			network = true;
			try
			{

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
				Log.e("log_tag", "Error in http connection " + e.toString());
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
				Log.e("log_tag", "Error converting result " + e.toString());
			}
		}

		return result;
	}

	public void openNotice(View view)
	{
		if (view == null)
		{
			Log.e("conract", "view is null");
		}

		historyStackAdd("notice");
		new OpenNoticeList().execute("");
		return;
	}

	private class OpenNoticeList extends AsyncTask<String, Void, String>
	{
		List<String[]>	rowList;

		@Override
		protected void onPreExecute()
		{
			removeHeaderbackColor();

			ImageButton im = (ImageButton) findViewById(R.id.imageButtonNotice);
			im.setBackgroundColor(Color.WHITE);

			noticeSelected = true;
			offerSelected = false;
			helpSelected = false;

			setLayout(R.layout.notices);
		}

		@Override
		protected String doInBackground(String... params)
		{
			try
			{

				String httpAddress = "http://" + ipAdd + "/android/notice.php";

				Log.i("urlPost", httpAddress.toString());
				String result = sendPost(httpAddress);
				JSONArray jArray = null;

				try
				{
					jArray = new JSONArray(result);
					JSONObject json_data = null;
					rowList = new ArrayList<String[]>();

					for (int i = 0; i < jArray.length(); i++)
					{
						json_data = jArray.getJSONObject(i);
						rowList.add(new String[] { json_data.getString("id"), json_data.getString("title"), json_data.getString("news"),
								json_data.getString("date"), json_data.getString("url"), json_data.getString("image_path") });
					}
				}
				catch (JSONException e1)
				{
					Log.e("Exception1", e1.toString());
				}
				catch (ParseException e1)
				{
					Log.e("Exception2", e1.toString());
				}

			}
			catch (Throwable t)
			{
				Log.i("Ex3", "Thread  exception " + t);
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				// hide progress bar
				final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
				progressBar.setVisibility(View.GONE);

				if (!network)
				{
					Toast.makeText(getApplicationContext(), "No Interned or Data Connection", Toast.LENGTH_LONG).show();
				}

				ListView listViewNotice = (ListView) findViewById(R.id.listViewNotices);

				final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

				adapter.initiatizeStringsValues();

				// list notices
				for (int i = 0; i < rowList.size(); i++)
				{
					adapter.addStrings(rowList.get(i)[1].toString(), rowList.get(i)[2].toString(), rowList.get(i)[3].toString(),
							rowList.get(i)[5].toString(), rowList.get(i)[4].toString(), "textimagenotice");

				}

				// adapter.addStrings("Dong Won Restaurant",
				// "Get 50% off on your payment of Coreon Card",
				// "August 25, 2013 at 11:30 PM",
				// R.drawable.offer_image_1,
				// "www.google.com",
				// "textimagenotice");

				listViewNotice.setAdapter(adapter);
				listViewNotice.setDividerHeight(0);
				listViewNotice.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						if (view.getTag().equals("textimagenotice"))
						{
							showNoticeContent(adapter, position);
						}
					}
				});
			}
			catch (Exception e)
			{
				Log.i("Ex4", e.toString());
			}
		}

		@Override
		protected void onProgressUpdate(Void... values)
		{

		}
	}
	
	
	
	
	public void openOffers(View view)
	{
		if (view == null)
		{
			Log.e("conract", "view is null");
		}

		historyStackAdd("offer");
		new OpenOffersList().execute("");

		return;
	}	
	
	private class OpenOffersList extends AsyncTask<String, Void, String>
	{
		List<String[]>	rowList;

		@Override
		protected void onPreExecute()
		{
			removeHeaderbackColor();

			ImageButton im = (ImageButton) findViewById(R.id.imageButtonOffers);
			im.setBackgroundColor(Color.WHITE);

			setLayout(R.layout.offers);
			offerSelected = true;
			noticeSelected = false;
			helpSelected = false;
		}

		@Override
		protected String doInBackground(String... params)
		{
			try
			{

				String httpAddress = "http://" + ipAdd + "/android/offer.php";

				Log.i("urlPost", httpAddress.toString());
				String result = sendPost(httpAddress);
				JSONArray jArray = null;

				try
				{
					jArray = new JSONArray(result);
					JSONObject json_data = null;
					rowList = new ArrayList<String[]>();

					for (int i = 0; i < jArray.length(); i++)
					{
						json_data = jArray.getJSONObject(i);
						rowList.add(new String[] { json_data.getString("id"), json_data.getString("title"), json_data.getString("offer"),
								json_data.getString("date"), json_data.getString("url"), json_data.getString("image_path") });
					}
				}
				catch (JSONException e1)
				{
					Log.e("Exception1", e1.toString());
				}
				catch (ParseException e1)
				{
					Log.e("Exception2", e1.toString());
				}

			}
			catch (Throwable t)
			{
				Log.i("Ex3", "Thread  exception " + t);
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				// hide progress bar
				final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
				progressBar.setVisibility(View.GONE);

				if (!network)
				{
					Toast.makeText(getApplicationContext(), "No Interned or Data Connection", Toast.LENGTH_LONG).show();
				}

				ListView listViewOffers = (ListView) findViewById(R.id.listViewOffers);
				final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), _title);
				adapter.initiatizeStringsValues();

				// list Offers
				for (int i = 0; i < rowList.size(); i++)
				{
//					adapter.addStrings(rowList.get(i)[1].toString(), rowList.get(i)[2].toString(), rowList.get(i)[3].toString(),
//							String.valueOf(R.drawable.offer_image_1), rowList.get(i)[4].toString(), "textimagenotice");
					
					adapter.addStrings(rowList.get(i)[1].toString(), rowList.get(i)[2].toString(), rowList.get(i)[3].toString(),
							rowList.get(i)[5].toString(), rowList.get(i)[4].toString(), "textimagenotice");
				}

				// adapter.addStrings("Dong Won Restaurant",
				// "Get 50% off on your payment of Coreon Card",
				// "August 25, 2013 at 11:30 PM",
				// R.drawable.offer_image_1,
				// "www.google.com",
				// "textimagenotice");

				listViewOffers.setAdapter(adapter);
				listViewOffers.setDividerHeight(0);
				listViewOffers.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						if (view.getTag().equals("textimagenotice"))
						{
							showNoticeContent(adapter, position);
						}
					}
				});
			}
			catch (Exception e)
			{
				Log.i("Ex4", e.toString());
			}
		}

		@Override
		protected void onProgressUpdate(Void... values)
		{

		}
	}	

	public void openHelp(View view)
	{
		if (view == null)
		{
			Log.e("conract", "view is null");
		}

		historyStackAdd("help");

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
		if (activity == null)
		{
			Log.e("conract", "activity is null");
		}

		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
}