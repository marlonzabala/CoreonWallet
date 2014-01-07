package com.itcorea.coreonmobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
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
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
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

import eu.janmuller.android.simplecropimage.CropImage;

@SuppressLint("CutPasteId")
public class CoreonMain extends FragmentActivity
{
	final static int				PIC_CROP		= 0x1;
	final static int				CAMERA_CAPTURE	= 0x2;

	public Uri						picUri;
	public static ViewPager			mPager;
	public static int				history;
	int								margin;
	int								cards			= 0;

	boolean							noticeSelected	= false;
	boolean							offerSelected	= false;
	boolean							helpSelected	= false;

	ExpandableListAdapter			listAdapter;
	ListView						listViewCard;
	MySimpleArrayAdapter			cardAdapter;
	ExpandableListView				expListView;
	List<String>					listDataHeader;
	HashMap<String, List<String>>	listDataChild;
	MySimpleArrayAdapter			adapterHome;
	Uri								mPhotoUri;

	ArrayList<String>				_title			= new ArrayList<String>();
	ArrayList<String>				_stack			= new ArrayList<String>();

	List<String>					listDataHeaderImage;
	HashMap<String, List<String>>	listDataChildImage;
	View							view;
	int								dev				= 15;

	SlidingMenu						menu;

	// network related variables
	// desktop set to static ip 192.168.123.111
	String							ipAdd			= "125.5.16.155/coreonwallet";	// "192.168.123.111";
	int								timeoutsec		= 20000;						// 20 second
																					// timeout
	boolean							timeout			= false;

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

		// align expandable list indicator to right
		DisplayMetrics displayMetrics = new DisplayMetrics();
		displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		int mScreenWidth = displayMetrics.widthPixels;

		mScreenWidth = (mScreenWidth - 150);

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
		cardAdapter.addStrings("Globe Gcash", "", "", String.valueOf(R.drawable.card1), "", "card");
		// cardAdapter.addStrings("", "", "", String.valueOf(R.drawable.card2), "", "card");
		cardAdapter.addStrings("Visa", "", "", String.valueOf(R.drawable.card3), "", "card");
		cardAdapter.addStrings("Coreon ph Visa card", "", "", String.valueOf(R.drawable.card4), "", "card");

		cards = 0;

		// get card pictures saved from preference file
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String cardcnt = prefs.getString("cardcount", "0");
		int cardCount = Integer.parseInt(cardcnt);
		for (int i = 0; i < cardCount; i++)
		{
			String cardPath = prefs.getString("card" + String.valueOf(i), "");
			if (!cardPath.equals(""))
			{
				cardAdapter.addStrings("Custom card", cardPath, "card" + String.valueOf(i), "0", "path", "card");
				cards++;
			}
		}

		cardAdapter.addStrings("", "30", "", "", "", "space");

		listViewCard = (ListView) findViewById(R.id.listViewCards);
		listViewCard.setAdapter(cardAdapter);

		// context menus
		registerForContextMenu(listViewCard);

		listViewCard.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
			{
				// show card when clicked
				showPhoto(Integer.parseInt(cardAdapter._image.get(position)), cardAdapter._content.get(position));
			};
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		if (v.getId() == R.id.listViewCards)
		{
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(cardAdapter._title.get(info.position).toString());

			String[] menuItems = new String[2];
			menuItems[0] = "Go to card menu";
			menuItems[1] = "Remove card";

			for (int i = 0; i < menuItems.length; i++)
			{
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();

		if (menuItemIndex == 0)// go to card menu
		{
			Toast.makeText(getApplicationContext(), "Go to card menu", Toast.LENGTH_SHORT).show();
		}
		else if (menuItemIndex == 1)// remove card
		{
			// dialog for approval of card removal
			new AlertDialog.Builder(CoreonMain.this).setTitle("Delete this Card?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					// remove from preferences
					String preferenceName = cardAdapter._date.get(info.position).toString();
					// Toast.makeText(getApplicationContext(),
					// cardAdapter._date.get(info.position).toString(), Toast.LENGTH_SHORT).show();
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString(preferenceName, "");
					editor.commit();

					// Toast.makeText(getApplicationContext(), String.valueOf(info.position),
					// Toast.LENGTH_SHORT).show();
					cardAdapter.removeValue(info.position);
					cardAdapter.notifyDataSetChanged();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					// Do nothing.
				}
			}).show();
		}
		return true;
	}

	@SuppressLint("InlinedApi")
	private void showPhoto(int drawable, String Image)
	{
		if (drawable == 0)
		{
			Log.e("conract", "drawable is 0");
		}

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.card_chooser_fragment, (ViewGroup) findViewById(R.id.root), false);
		ImageView imageCard = (ImageView) rowView.findViewById(R.id.imageViewCard);

		if (drawable == 0)
		{
			File imgFile = new File(Image);
			if (imgFile.exists())
			{

				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				imageCard.setImageBitmap(myBitmap);

			}

		}
		else
		{
			imageCard.setImageResource(drawable);
		}

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
		// View viewChild =
		setPage(R.layout.promos);
		removeHeaderbackColor();
	}

	public void ShowChangePassword()
	{
		historyStackAdd("changepassword");
		// View viewChild =
		setPage(R.layout.change_password);
		removeHeaderbackColor();
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

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 15;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	String	tempPicturePath;

	private void takePictureAndCrop()
	{

		// File mydir = getApplicationContext().getDir("mydir", Context.MODE_PRIVATE); // Creating
		// an
		// internal dir;
		// File fileWithinMyDir = new File(mydir, "myfile"); // Getting a file within the dir.

		// File file = new File(this.getFilesDir().getAbsolutePath() + "/picture.png");
		// Uri imgUri = Uri.fromFile(fileWithinMyDir);

		// file.exists();

		if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
		{
			tempPicturePath = Environment.getExternalStorageDirectory().toString() + "/temp.png";// "/sdcard/flashCropped.png";//getFilesDir().getAbsolutePath()
																									// +
																									// "/temp.png";
		}
		else
		{
			tempPicturePath = "";
			Toast.makeText(getApplicationContext(), "Cannot store to external storage", Toast.LENGTH_SHORT).show();
			Log.e("storage error path", Environment.getExternalStorageDirectory().toString() + "/temp.png");
			Log.e("storage error state", Environment.getExternalStorageState().toString());
			return;
		}
		// Toast.makeText(getApplicationContext(),
		// Environment.getExternalStorageDirectory().toString()+"temp.png",
		// Toast.LENGTH_SHORT).show();

		picUri = Uri.fromFile(new File(tempPicturePath));

		Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new
		// ContentValues());
		captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
		startActivityForResult(captureIntent, CAMERA_CAPTURE);
	}

	Bitmap	bitmapImage;

	private void runCropImage()
	{
		Intent intent = new Intent(this, CropImage.class);
		String filePath = tempPicturePath;// getRealPathFromURI(getApplicationContext(), picUri);
		intent.putExtra(CropImage.IMAGE_PATH, filePath);
		intent.putExtra(CropImage.SCALE, true);
		intent.putExtra(CropImage.ASPECT_X, 3);
		intent.putExtra(CropImage.ASPECT_Y, 2);
		startActivityForResult(intent, PIC_CROP);
	}

	public String getRealPathFromURI(Context context, Uri contentUri)
	{
		Cursor cursor = null;
		try
		{
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == CAMERA_CAPTURE)
			{
				runCropImage();
				Log.e("Camera", "Capture");
				return;
			}
			else if (requestCode == PIC_CROP)
			{
				String path = data.getStringExtra(CropImage.IMAGE_PATH);

				if (path == null)
				{
					return;
				}

				Bitmap bitmapTemp = BitmapFactory.decodeFile(path);

				ImageView picView = (ImageView) findViewById(R.id.imageViewPic);
				// display the returned cropped image

				// copy image to preferred location
				// copyfile(getRealPathFromURI(getApplicationContext(),
				// picUri),this.getFilesDir().getAbsolutePath()+"/pic.png");

				// Toast.makeText(getApplicationContext(),
				// getRealPathFromURI(getApplicationContext(), picUri), Toast.LENGTH_SHORT).show();
				// Toast.makeText(getApplicationContext(),
				// this.getFilesDir().getAbsolutePath()+"/pic.png", Toast.LENGTH_SHORT).show();

				bitmapImage = getRoundedCornerBitmap(bitmapTemp);

				picView.setImageBitmap(bitmapImage);

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

//	private static void copyfile(String srFile, String dtFile)
//	{
//		try
//		{
//			File f1 = new File(srFile);
//			File f2 = new File(dtFile);
//			InputStream in = new FileInputStream(f1);
//
//			// For Append the file.
//			// OutputStream out = new FileOutputStream(f2,true);
//
//			// For Overwrite the file.
//			OutputStream out = new FileOutputStream(f2);
//
//			byte[] buf = new byte[1024];
//			int len;
//			while ((len = in.read(buf)) > 0)
//			{
//				out.write(buf, 0, len);
//			}
//			in.close();
//			out.close();
//			System.out.println("File copied.");
//		}
//		catch (FileNotFoundException ex)
//		{
//			System.out.println(ex.getMessage() + " in the specified directory.");
//			System.exit(0);
//		}
//		catch (IOException e)
//		{
//			System.out.println(e.getMessage());
//		}
//	}

	public void showEnrollCardRegister(int category)
	{
		if (category == 0)
		{
			Log.e("conract", "category is 0");
		}

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
		String number = tm.getLine1Number();

		View view2 = setPage(R.layout.enroll_card_register);

		//final ImageView picView = (ImageView) findViewById(R.id.imageViewPic);

		// set phone number on text
		TextView textNumber = (TextView) view2.findViewById(R.id.textViewPhoneNumber);

		String phoneNumber = "none";

		final EditText input = new EditText(CoreonMain.this);
		input.setText("");

		if (number == null)
		{
			Toast.makeText(getApplicationContext(), "unable to get number", Toast.LENGTH_SHORT).show();

			new AlertDialog.Builder(CoreonMain.this).setTitle("Mobile Number").setView(input)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton)
						{
							// phoneNumber = input.getText().toString();
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

			phoneNumber = input.getText().toString();

		}
		else
		{
			phoneNumber = number.toString();
		}
		textNumber.setText(phoneNumber);

		ImageView imDefaultImage = (ImageView) view2.findViewById(R.id.imageViewDefaultPicture);
		// on click on capture image
		imDefaultImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{

			}
		});

		ImageView imPicture = (ImageView) view2.findViewById(R.id.imageViewPicture2);
		// on click on capture image
		imPicture.setOnClickListener(new OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v)
			{
				takePictureAndCrop();
			}
		});

		final EditText editTextGcashNumber = (EditText) view2.findViewById(R.id.editTextGcashNumber);
		final EditText editTextMPin = (EditText) view2.findViewById(R.id.editTextMPin);

		TextView imenroll = (TextView) view2.findViewById(R.id.imageButtonEnroll);
		// on click on capture image
		imenroll.setOnClickListener(new OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v)
			{
				if (editTextGcashNumber.getText().toString().equals("") || editTextMPin.getText().toString().equals(""))
				{
					// check for image
					// check for mobile number
					Toast.makeText(getApplicationContext(), "Please Complete the fields", Toast.LENGTH_SHORT).show();
				}
				else
				{
					try
					{
						// save image to card
						String imagePath = getFilesDir().getAbsolutePath() + "/pic.png";
						File f2 = new File(imagePath);

						int i = 0;
						while (f2.exists())
						{
							f2 = new File(getFilesDir().getAbsolutePath() + "/pic" + String.valueOf(i) + ".png");
							imagePath = getFilesDir().getAbsolutePath() + "/pic" + String.valueOf(i) + ".png";
							i++;
						}

						FileOutputStream out = new FileOutputStream(imagePath);
						bitmapImage.compress(Bitmap.CompressFormat.PNG, 90, out);
						out.close();

						// Log.e("save path", getRealPathFromURI(getApplicationContext(), picUri));

						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString("cardcount", String.valueOf(i + 1));
						editor.putString("card" + String.valueOf(i), imagePath);
						editor.commit();

						// add card to adapter list
						cardAdapter.removeValue(cardAdapter.getCount() - 1);
						cardAdapter.addStrings("Custom card", imagePath, "card" + String.valueOf(i), "0", "path", "card");
						cardAdapter.addStrings("", "30", "", "", "", "space");

						Toast.makeText(getApplicationContext(), "Card was succesfully enrolled", Toast.LENGTH_SHORT).show();

						// scroll to bottom of list
						listViewCard.setSelection(cardAdapter.getCount() - 1);
						menu.showSecondaryMenu(true);
					}
					catch (Exception e)
					{
						Log.e("error", "sdfbsdf");
						e.printStackTrace();
					}

					// BitmapFactory.Options options = new BitmapFactory.Options();
					// options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					// Bitmap bitmap = BitmapFactory.decodeFile("cardPicture.png", options);
					// picView.setImageBitmap(bitmap);
				}
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

		String					fname		= "";
		String					lname		= "";
		String					mname		= "";
		String					bday		= "";
		String					address		= "";
		String					email		= "";
		String					points		= "";
		String					accountid	= "";
		String					mobile_no	= "";

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				// Thread.sleep(400);

				// get values here
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				accountid = prefs.getString("accountid", "0");
				_accountid = accountid;

				if (accountid.equals("0"))
				{
					// error
				}

				// get notices
				String httpAddress = "http://" + ipAdd + "/accountinfo.php?id=" + accountid;

				Log.i("urlPost", httpAddress.toString());
				String result = sendPost(httpAddress);
				JSONArray jArray = null;

				jArray = new JSONArray(result);
				JSONObject json_data = null;

				for (int i = 0; i < jArray.length(); i++)
				{
					json_data = jArray.getJSONObject(i);

					fname = json_data.getString("fname");
					lname = json_data.getString("lname");
					// mname = json_data.getString("mname");
					bday = json_data.getString("bday");
					address = json_data.getString("address");
					email = json_data.getString("email");
					points = json_data.getString("points");
				}

				if (fname.equals("null"))
					fname = "";
				if (lname.equals("null"))
					lname = "";
				if (mname.equals("null"))
					mname = "";
				if (bday.equals("null"))
					bday = "";
				if (email.equals("null"))
					email = "";
				if (address.equals("null"))
					address = "";
				mobile_no = "0999568291";

				noticeContentAdapter = new MySimpleArrayAdapter(getApplicationContext(), _title);

				noticeContentAdapter.initiatizeStringsValues();
				noticeContentAdapter.addStrings("EMAIL ACCOUNT", "", "", "", "", "accountheader");
				noticeContentAdapter.addStrings("", email, "", "", "", "accountemail");
				noticeContentAdapter.addStrings("", "", "", "", "", "accountlineorange");
				noticeContentAdapter.addStrings("ACCOUNT INFORMATION", "", "", "", "", "accountheader");
				noticeContentAdapter.addStrings("First Name", fname, "", "", "", "accountcontent");
				noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
				noticeContentAdapter.addStrings("Middle Name", "Belo", "", "", "", "accountcontent");
				noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
				noticeContentAdapter.addStrings("Last Name", lname, "", "", "", "accountcontent");
				noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
				noticeContentAdapter.addStrings("Birthday", bday, "", "", "", "accountcontent");
				noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
				noticeContentAdapter.addStrings("Mobile Number", mobile_no, "", "", "", "accountcontentmobile");
				noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
				noticeContentAdapter.addStrings("Address", address, "", "", "", "accountcontentaddress");
				noticeContentAdapter.addStrings("", "", "", "", "", "accountlineorange");
				noticeContentAdapter.addStrings("WALLET INFORMATION", "", "", "", "", "accountheader");
				noticeContentAdapter.addStrings("My Cards", " Cards", "", "", "", "accountcontent");
				noticeContentAdapter.addStrings("", "", "", "", "", "accountlinegray");
				noticeContentAdapter.addStrings("Coreon Points", points + " Points", "", "", "", "accountcontent");
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

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
										//Editable value = input.getText();

										// update firstname
										new UpdateAccountInformation().execute("fname", input.getText().toString(), accountid);

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
										//Editable value = input.getText();
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
										//Editable value = input.getText();

										new UpdateAccountInformation().execute("lname", input.getText().toString(), accountid);
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

						DialogFragment newFragment = new DatePickerFragment();
						newFragment.show(getSupportFragmentManager(), "datePicker");

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
										String add = input.getText().toString();
										add = add.replace(" ", "%20");
										new UpdateAccountInformation().execute("address", add, accountid);
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

	static String	birthdatePicker	= "";
	static String	_accountid		= "";

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day)
		{

			String monthString = "";
			if (month < 10)
			{
				monthString = "0" + String.valueOf(month + 1);
			}
			else
			{
				monthString = String.valueOf(month);
			}

			String dayString = "";
			if (month < 10)
			{
				dayString = "0" + String.valueOf(day);
			}
			else
			{
				dayString = String.valueOf(day);
			}

			birthdatePicker = String.valueOf(year) + "-" + monthString + "-" + dayString;
			// Do something with the date chosen by the user

			Toast.makeText(getActivity(), birthdatePicker, Toast.LENGTH_SHORT).show();

			// new UpdateAccountInformation().execute("bday", birthdatePicker, _accountid);

			// CoreonMain myActivity = new CoreonMain();
			// CoreonMain.UpdateAccountInformation asyncTask = myActivity.new
			// UpdateAccountInformation();
			// asyncTask.execute("bday", birthdatePicker, _accountid);
		}
	}

	private class UpdateAccountInformation extends AsyncTask<String, Void, String>
	{
		String	address	= "";

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				String httpAddress = "http://" + ipAdd + "/edit.php?request=" + params[0] + "&" + params[0] + "=" + params[1] + "&id=" + params[2];
				address = httpAddress;

				Log.i("urlPost", httpAddress.toString());
				String result = sendPost(httpAddress);

				// JSONArray jArray = null;
				// jArray = new JSONArray(result);
				// JSONObject json_data = null;
				// for (int i = 0; i < jArray.length(); i++)
				// {
				// json_data = jArray.getJSONObject(i);
				// // json_dataOffer.getString("offer")
				// }

				if (result.equals("success"))
				{
					// success
				}
				else
				{
					// failed
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			Log.e("address", address);
			Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();

			new ShowAccountInformation().execute();
		}

		@Override
		protected void onPreExecute()
		{

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

				// Toast.makeText(getApplicationContext(), "Logging Out..",
				// Toast.LENGTH_SHORT).show();

				// finish this activity

				Intent newIntent = new Intent(CoreonMain.this, LogIn.class);
				startActivity(newIntent);
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
		historyStackAdd("ShowNoticeContent");

		removeHeaderbackColor();
		ImageButton im = (ImageButton) findViewById(R.id.imageButtonNotice);
		im.setBackgroundColor(Color.WHITE);

		String title = adapter._title.get(position).toString();
		String date = adapter._date.get(position).toString();
		String image = adapter._image.get(position).toString();
		String url = adapter._extra.get(position).toString();
		String content = adapter._content.get(position).toString();

		View noticeView = setPage(R.layout.notice_content);

		// int imageInt = Integer.parseInt(image);
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
		ArrayList<String[]>		noticeRowList;
		ArrayList<String[]>		offerRowList;
		MySimpleArrayAdapter	adapterHomeTemp;
		boolean					tempFileLoaded	= false;
		ListView				listView;
		View					view6;

		@Override
		protected void onPreExecute()
		{
			removeHeaderbackColor();
			setPage(R.layout.progress);

			adapterHomeTemp = new MySimpleArrayAdapter(getApplicationContext(), _title);

			try
			{

				FileInputStream fileInputStream = getApplicationContext().openFileInput("homeadapter");
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				// Object yourObject = (Object)objectInputStream.readObject();
				// Object test = objectInputStream.readObject();
				// adapterHomeTemp = (MySimpleArrayAdapter)test;
				adapterHomeTemp = (MySimpleArrayAdapter) objectInputStream.readObject();

				objectInputStream.close();

				view6 = setLayout(R.layout.main_info_home_list);
				listView = (ListView) view6.findViewById(R.id.listView1);
				listView.setAdapter(adapterHomeTemp);

				tempFileLoaded = true;

			}
			catch (FileNotFoundException e)
			{
				tempFileLoaded = false;
				e.printStackTrace();
			}
			catch (IOException e)
			{
				tempFileLoaded = false;
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				tempFileLoaded = false;
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				// get notices
				String httpAddressNotices = "http://" + ipAdd + "/notice.php";

				Log.i("urlPost", httpAddressNotices.toString());
				String result = sendPost(httpAddressNotices);
				JSONArray jArray = null;

				jArray = new JSONArray(result);
				JSONObject json_data = null;
				noticeRowList = new ArrayList<String[]>();

				int noticeCount = 0;

				for (int i = 0; i < jArray.length(); i++)
				{
					json_data = jArray.getJSONObject(i);
					noticeRowList.add(new String[] { json_data.getString("id"), json_data.getString("title"), json_data.getString("notice"),
							json_data.getString("date"), json_data.getString("url"), json_data.getString("image_path") });

					noticeCount++;
				}

				// get offers
				String httpAddressOffers = "http://" + ipAdd + "/offer.php";
				Log.i("urlPost", httpAddressOffers.toString());
				String resultOffer = sendPost(httpAddressOffers);
				JSONArray jArrayOffer = null;

				jArrayOffer = new JSONArray(resultOffer);
				JSONObject json_dataOffer = null;
				offerRowList = new ArrayList<String[]>();

				for (int i = 0; i < jArrayOffer.length(); i++)
				{
					json_dataOffer = jArrayOffer.getJSONObject(i);
					offerRowList.add(new String[] { json_dataOffer.getString("id"), json_dataOffer.getString("title"),
							json_dataOffer.getString("offer"), json_dataOffer.getString("date"), json_dataOffer.getString("url"),
							json_dataOffer.getString("image_path") });
				}

				// _stack.add("home");
				historyStackAdd("home");

				adapterHome = new MySimpleArrayAdapter(getApplicationContext(), _title);
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String accountid = prefs.getString("accountid", "0");

				String fname = "";
				String points = "";

				if (accountid.equals("0"))
				{
					// error
				}

				// get notices
				String httpAddressAccount = "http://" + ipAdd + "/accountinfo.php?id=" + accountid;

				Log.i("urlPost", httpAddressAccount.toString());
				String resultAccount = sendPost(httpAddressAccount);
				JSONArray jArrayAccount = null;

				jArrayAccount = new JSONArray(resultAccount);
				JSONObject json_dataAccount = null;

				for (int i = 0; i < jArrayAccount.length(); i++)
				{
					json_dataAccount = jArrayAccount.getJSONObject(i);

					fname = json_dataAccount.getString("fname");
					points = json_dataAccount.getString("points");
				}

				if (fname.equals("null"))
					fname = "";
				if (points.equals("null"))
					points = "";

				adapterHome.initiatizeStringsValues();

				adapterHome.addStrings("", "", "", "", "", "space");

				// user informations
				adapterHome.addStrings("userinfo", fname, "userinfo", "", "userinfo", "userinfo");
				adapterHome.addStrings("My Cards", String.valueOf(cards) + " Cards", "userinfo", "", "userinfo", "usercontent");
				adapterHome.addStrings("", "", "", "", "", "userline");
				adapterHome.addStrings("Coreon Points", points + " Points", "userinfo", "", "userinfo", "usercontent");
				adapterHome.addStrings("", "", "", "", "", "userline");
				adapterHome.addStrings("Notice", String.valueOf(noticeCount), "userinfo", "", "userinfo", "usercontent");
				adapterHome.addStrings("userinfo", "userinfo", "userinfo", "", "userinfo", "userbottom");

				adapterHome.addStrings("", "", "", "", "", "space");
				adapterHome.addStrings("Exclusive Offers", "", "", "", "header", "header");

				for (int j = 0; j < offerRowList.size(); j++)
				{
					// noticeRowList.get(i)[1]
					adapterHome.addStrings(offerRowList.get(j)[1], offerRowList.get(j)[2], offerRowList.get(j)[3], offerRowList.get(j)[5],
							offerRowList.get(j)[4], "textimage");
				}

				adapterHome.addStrings("", "", "", "", "", "bottomshadow");
				adapterHome.addStrings("", "", "", "", "", "space");
				adapterHome.addStrings("Notice", "", "", "", "header", "header");

				for (int j = 0; j < noticeRowList.size(); j++)
				{
					// noticeRowList.get(i)[1]
					adapterHome.addStrings(noticeRowList.get(j)[1], noticeRowList.get(j)[2], noticeRowList.get(j)[3], noticeRowList.get(j)[5],
							noticeRowList.get(j)[4], "text");
				}

				adapterHome.addStrings("", "", "", "", "", "bottomshadow");
				adapterHome.addStrings("", "180", "", "", "", "space");

				try
				{

					FileOutputStream fileOutputStream = getApplicationContext().openFileOutput("homeadapter", Context.MODE_PRIVATE);
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
					objectOutputStream.writeObject(adapterHome);
					objectOutputStream.close();

				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
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

			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{

			if (!tempFileLoaded)
			{
				view6 = setLayout(R.layout.main_info_home_list);
				listView = (ListView) view6.findViewById(R.id.listView1);
			}

			if (!network)
			{
				Toast.makeText(getApplicationContext(), "No Internet or Data Connection", Toast.LENGTH_LONG).show();
			}
			if (timeout)
			{
				Toast.makeText(getApplicationContext(), "Server cannot be reached", Toast.LENGTH_LONG).show();
			}

			if (adapterHomeTemp.equals(adapterHome))
			{
				listView.setAdapter(adapterHomeTemp);
			}
			else
			{
				listView.setAdapter(adapterHome);
			}

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
		timeout = false;
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
			timeout = false;
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
				Log.e("logs1", "Timeout");
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

				String httpAddress = "http://" + ipAdd + "/notice.php";

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
						rowList.add(new String[] { json_data.getString("id"), json_data.getString("title"), json_data.getString("notice"),
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
				if (timeout)
				{
					Toast.makeText(getApplicationContext(), "Server cannot be reached", Toast.LENGTH_LONG).show();
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

				String httpAddress = "http://" + ipAdd + "/offer.php";

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
					Toast.makeText(getApplicationContext(), "No Internet or Data Connection", Toast.LENGTH_LONG).show();
				}
				if (timeout)
				{
					Toast.makeText(getApplicationContext(), "Server cannot be reached", Toast.LENGTH_LONG).show();
				}

				ListView listViewOffers = (ListView) findViewById(R.id.listViewOffers);
				final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getApplicationContext(), _title);
				adapter.initiatizeStringsValues();

				// list Offers
				for (int i = 0; i < rowList.size(); i++)
				{
					// adapter.addStrings(rowList.get(i)[1].toString(),
					// rowList.get(i)[2].toString(), rowList.get(i)[3].toString(),
					// String.valueOf(R.drawable.offer_image_1), rowList.get(i)[4].toString(),
					// "textimagenotice");

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