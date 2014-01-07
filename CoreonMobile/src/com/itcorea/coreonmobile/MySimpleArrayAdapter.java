package com.itcorea.coreonmobile;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class MySimpleArrayAdapter extends ArrayAdapter<String> implements Serializable
{
	private transient static Context	context;

	private transient final String		ipAdd				= "125.5.16.155/coreonwallet";	// "192.168.123.111";
	private static final long			serialVersionUID	= 6529685098267757690L;

	public transient ArrayList<String>	_title				= new ArrayList<String>();
	public transient ArrayList<String>	_content			= new ArrayList<String>();
	public transient ArrayList<String>	_date				= new ArrayList<String>();
	public transient ArrayList<String>	_image				= new ArrayList<String>();
	public transient ArrayList<String>	_type				= new ArrayList<String>();
	public transient ArrayList<String>	_extra				= new ArrayList<String>();

	public MySimpleArrayAdapter()
	{
		super(context, 0);
	}

	@SuppressWarnings("static-access")
	public MySimpleArrayAdapter(Context context, ArrayList<String> values)
	{
		super(context, R.layout.card_text_image_notice_content, values);
		this.context = context;
	}

	// public void setValues(ArrayList<String> title, ArrayList<String> content, ArrayList<String>
	// date, ArrayList<String> image,
	// ArrayList<String> type, ArrayList<String> extra)
	// {
	// this._title = title;
	// this._content = content;
	// this._date = date;
	// this._image = image;
	// this._type = type;
	// this._extra = extra;
	// }

	public void initiatizeStringsValues()
	{
		_title = new ArrayList<String>();
		_content = new ArrayList<String>();
		_date = new ArrayList<String>();
		_image = new ArrayList<String>();
		_type = new ArrayList<String>();
		_extra = new ArrayList<String>();

		this.clear();
	}
	
	public void removeValue(int position)
	{
		if (position == 0)
		{
			Log.e("conract", "one or more of data are 0");
			// throw new IllegalArgumentException("Category 0" + category);
		}

		// Integer.parseInt();
		
		_title.remove(position);
		_content.remove(position);
		_date.remove(position);
		_image.remove(position);
		_type.remove(position);
		_extra.remove(position);
	}

	public void addStrings(String title, String content, String date, String image, String extra, String type)
	{
		if (title == null || content == null || date == null || extra == null || type == null)
		{
			Log.e("conract", "one or more of data are null");
			// throw new IllegalArgumentException("Category 0" + category);
		}

		// Integer.parseInt();

		_title.add(title);
		_content.add(content);
		_date.add(date);
		_image.add(image);
		_type.add(type);
		_extra.add(extra);

		this.add(title);
	}

	@Override
	public int getCount()
	{
		return this._title.size();
	}

	@SuppressLint("InlinedApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{

		// for development
		// if (_type.size() <= position || this.getCount() <= position)
		// {
		//
		// Log.i("small size", String.valueOf(this.getItem(position - 1)) + " " +
		// String.valueOf(position));
		// View v = new View(context);
		// v.setTag("null");
		// return v;
		// }

		View rowView;

		String type = _type.get(position).toString();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		String tag = "null";
		// Log.i("type", type.toString());

		if (type.equals("userinfo"))
		{
			tag = "userinfo";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_user_main, parent, false);
			}

			TextView textName = (TextView) rowView.findViewById(R.id.textViewName);
			TextView textHi = (TextView) rowView.findViewById(R.id.textViewHi);

			Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "Roboto-Thin.ttf");
			textName.setTypeface(typeFace);
			textHi.setTypeface(typeFace);
			textName.setText(_content.get(position).toString());

			rowView.setTag(tag);
		}
		else if (type.equals("usercontent"))
		{
			tag = "usercontent";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_user_content, parent, false);
			}

			TextView textContent = (TextView) rowView.findViewById(R.id.textContent);
			TextView textContentNumber = (TextView) rowView.findViewById(R.id.textContentNumber);

			textContent.setText(_title.get(position).toString());
			textContentNumber.setText(_content.get(position).toString());

			rowView.setTag(tag);
		}
		else if (type.equals("userline"))
		{
			tag = "userline";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_user_line, parent, false);
			}
			rowView.setTag(tag);
		}
		else if (type.equals("userbottom"))
		{
			tag = "userbottom";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_user_bottom, parent, false);
			}
			rowView.setTag(tag);
		}
		else if (type.toString().equals("header"))
		{
			tag = "header";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_title, parent, false);
			}
			TextView textTitle = (TextView) rowView.findViewById(R.id.lblHeaderTitleText);
			textTitle.setText(_title.get(position).toString());

			rowView.setTag(tag);
		}
		else if (type.toString().equals("space"))
		{
			// try
			// {
			tag = "space";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.blank_layout_card, parent, false);
			}
			TextView textTitle = (TextView) rowView.findViewById(R.id.textViewblank);

			int spaceHeight = 25;
			if (!_content.get(position).toString().equals("") && !_content.get(position).toString().equals("space"))
			{
				spaceHeight = Integer.parseInt(_content.get(position).toString());
			}

			textTitle.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, spaceHeight));

			rowView.setEnabled(false);
			rowView.setOnClickListener(null);

			// }
			// catch (Exception e)
			// {
			// View v = new View(context);
			// rowView = v;
			// }

			rowView.setTag(tag);
		}
		else if (type.toString().equals("bottomshadow"))
		{
			tag = "bottomshadow";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_bottom_shadow, parent, false);
			}

			rowView.setEnabled(false);
			rowView.setOnClickListener(null);

			rowView.setTag(tag);
		}
		else if (type.toString().equals("text"))
		{
			tag = "text";

			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_text, parent, false);
			}

			TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			TextView textContent = (TextView) rowView.findViewById(R.id.lblContentText);
			TextView textDate = (TextView) rowView.findViewById(R.id.lblDateText);
			textTitle.setText(_title.get(position).toString());
			textDate.setText(_date.get(position).toString());
			textContent.setText(_content.get(position).toString());

			rowView.setTag(tag);
		}
		else if (type.toString().equals("textimagenotice"))
		{
			tag = "textimagenotice";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_text_image_notice, parent, false);
			}
			TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			TextView textContent = (TextView) rowView.findViewById(R.id.lblContentText);
			TextView textDate = (TextView) rowView.findViewById(R.id.lblDateText);
			ImageView image = (ImageView) rowView.findViewById(R.id.imageViewContentImage);
			textTitle.setText(_title.get(position).toString());
			textContent.setText(_content.get(position).toString());
			textDate.setText(_date.get(position).toString());

			// insert urlimage viewer

			String imageUrl = "http://" + ipAdd + "/image/" + _image.get(position).toString();
			// Log.i("imageurl", imageUrl);

			UrlImageViewHelper.setUrlDrawable(image, imageUrl);

			// image.setImageResource(Integer.parseInt(_image.get(position).toString()));

			rowView.setTag(tag);
		}
		else if (type.toString().equals("textimage"))
		{
			tag = "textimage";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_text_image, parent, false);
			}

			TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			TextView textContent = (TextView) rowView.findViewById(R.id.lblContentText);
			TextView textDate = (TextView) rowView.findViewById(R.id.lblDateText);
			ImageView image = (ImageView) rowView.findViewById(R.id.imageViewContentImage);
			textTitle.setText(_title.get(position).toString());
			textContent.setText(_content.get(position).toString());
			textDate.setText(_date.get(position).toString());

			String imageUrl = "http://" + ipAdd + "/image/" + _image.get(position).toString();
			UrlImageViewHelper.setUrlDrawable(image, imageUrl);
			// image.setImageResource(Integer.parseInt(_image.get(position).toString()));

			rowView.setTag(tag);
		}
		else if (type.toString().equals("noticecontent"))
		{
			tag = "noticecontent";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_text_image_notice_content, parent, false);
			}
			TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			TextView textContent = (TextView) rowView.findViewById(R.id.lblContentText);
			TextView textDate = (TextView) rowView.findViewById(R.id.lblDateText);
			ImageView image = (ImageView) rowView.findViewById(R.id.imageViewContentImage);
			textTitle.setText(_title.get(position).toString());
			textContent.setText(_content.get(position).toString());
			textDate.setText(_date.get(position).toString());

			String imageUrl = "http://" + ipAdd + "/image/" + _image.get(position).toString();
			UrlImageViewHelper.setUrlDrawable(image, imageUrl);

			// image.setImageResource(Integer.parseInt(_image.get(position).toString()));

			TextView textButton = (TextView) rowView.findViewById(R.id.imageButtonVisit);
			textButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0)
				{

					String url = _extra.get(position).toString();

					if (!url.startsWith("http://") && !url.startsWith("https://"))
						url = "http://" + url;

					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(browserIntent);

				}
			});

			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountheader"))
		{
			tag = "accountheader";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_header, parent, false);
			}
			TextView textTitle = (TextView) rowView.findViewById(R.id.textViewHeader);
			textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountemail"))
		{
			tag = "accountemail";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_email, parent, false);
			}
			TextView textEmail = (TextView) rowView.findViewById(R.id.textViewEmail);
			textEmail.setText(_content.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountcontent"))
		{
			tag = "accountcontent";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_info_content, parent, false);
			}
			TextView textDescription = (TextView) rowView.findViewById(R.id.textLabel);
			TextView textContent = (TextView) rowView.findViewById(R.id.textContent);
			textDescription.setText(_title.get(position).toString());
			textContent.setText(_content.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountcontentmobile"))
		{
			tag = "accountcontentmobile";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_info_content_mobile, parent, false);
			}
			TextView textDescription = (TextView) rowView.findViewById(R.id.textLabel);
			TextView textContent = (TextView) rowView.findViewById(R.id.textContentMobile);
			textDescription.setText(_title.get(position).toString());
			textContent.setText(_content.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountcontentaddress"))
		{
			tag = "accountcontentaddress";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_info_content_address, parent, false);
			}
			TextView textDescription = (TextView) rowView.findViewById(R.id.textLabel);
			TextView textContent = (TextView) rowView.findViewById(R.id.textContentAddress);
			textDescription.setText(_title.get(position).toString());
			textContent.setText(_content.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountlinegray"))
		{
			tag = "accountlinegray";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_line_gray, parent, false);
			}
			// TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			// textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountlineorange"))
		{
			tag = "accountlineorange";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_line_orange, parent, false);
			}
			// TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			// textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("card"))
		{
			tag = "card";
			// try
			// {

			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_chooser_fragment, parent, false);
			}

			ImageView image = (ImageView) rowView.findViewById(R.id.imageViewCard);
			if (_extra.get(position).toString().equals("path"))
			{
				File imgFile = new File(_content.get(position));
				if (imgFile.exists())
				{
					Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
					image.setImageBitmap(myBitmap);
				}
			}
			else
			{
				image.setImageResource(Integer.parseInt(_image.get(position).toString()));
			}

			// }
			// catch (Exception e)
			// {
			// View v = new View(context);
			// rowView = v;
			// }
			// TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			// textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else
		{
			View v = new View(context);
			v.setTag("error");
			return v;
		}

		return rowView;
	}
}