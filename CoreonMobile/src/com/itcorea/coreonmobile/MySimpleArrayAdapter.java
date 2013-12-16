package com.itcorea.coreonmobile;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
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

public class MySimpleArrayAdapter extends ArrayAdapter<String>
{
	private final Context	context;
	Boolean					_notice		= false;

	ArrayList<String>		_title		= new ArrayList<String>();
	ArrayList<String>		_content	= new ArrayList<String>();
	ArrayList<String>		_date		= new ArrayList<String>();
	ArrayList<String>		_image		= new ArrayList<String>();
	ArrayList<String>		_type		= new ArrayList<String>();
	ArrayList<String>		_extra		= new ArrayList<String>();

	public MySimpleArrayAdapter(Context context, ArrayList<String> values)
	{
		super(context, R.layout.card_text_image_notice_content, values);
		this.context = context;
	}

	public void setValues(ArrayList<String> title, ArrayList<String> content, ArrayList<String> date, ArrayList<String> image,
			ArrayList<String> type, ArrayList<String> extra)
	{
		this._title = title;
		this._content = content;
		this._date = date;
		this._image = image;
		this._type = type;
		this._extra = extra;
	}

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

	public void addStrings(String title, String content, String date, int image, String extra, String type)
	{
		_title.add(title);
		_content.add(content);
		_date.add(date);
		_image.add(String.valueOf(image));
		_type.add(type);
		_extra.add(extra);

		this.add(title);
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

	public void setNotice(Boolean notice)
	{
		this._notice = notice;
	}

	@SuppressLint("InlinedApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View rowView;

		String type = _type.get(position).toString();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//Log.i("type", type.toString());

		if (type.equals("userinfo"))
		{
			if (convertView != null && convertView.getTag().equals("userinfo"))
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

			rowView.setTag("userinfo");
		}
		else if (type.equals("usercontent"))
		{
			if (convertView != null && convertView.getTag().equals("usercontent"))
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

			rowView.setTag("usercontent");
		}
		else if (type.equals("userline"))
		{
			if (convertView != null && convertView.getTag().equals("userline"))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_user_line, parent, false);
			}
			rowView.setTag("userline");
		}
		else if (type.equals("userbottom"))
		{
			if (convertView != null && convertView.getTag().equals("userbottom"))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_user_bottom, parent, false);
			}
			rowView.setTag("userbottom");
		}
		else if (type.toString().equals("header"))
		{
			if (convertView != null && convertView.getTag().equals("header"))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.card_title, parent, false);
			}
			TextView textTitle = (TextView) rowView.findViewById(R.id.lblHeaderTitleText);
			textTitle.setText(_title.get(position).toString());

			rowView.setTag("header");
		}
		else if (type.toString().equals("space"))
		{
			if (convertView != null && convertView.getTag().equals("space"))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.blank_layout_card, parent, false);
			}
			TextView textTitle = (TextView) rowView.findViewById(R.id.textViewblank);
			textTitle.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 25));

			rowView.setEnabled(false);
			rowView.setOnClickListener(null);

			rowView.setTag("space");
		}
		else if (type.toString().equals("bottomshadow"))
		{
			rowView = inflater.inflate(R.layout.card_bottom_shadow, parent, false);

			rowView.setEnabled(false);
			rowView.setOnClickListener(null);

			rowView.setTag("bottomshadow");
		}
		else if (type.toString().equals("text"))
		{
			rowView = inflater.inflate(R.layout.card_text, parent, false);
			TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			TextView textContent = (TextView) rowView.findViewById(R.id.lblContentText);
			TextView textDate = (TextView) rowView.findViewById(R.id.lblDateText);
			textTitle.setText(_title.get(position).toString());
			textDate.setText(_date.get(position).toString());
			textContent.setText(_content.get(position).toString());

			rowView.setTag("text");
		}
		else if (type.toString().equals("textimagenotice"))
		{
			if (convertView != null && convertView.getTag().equals("textimagenotice"))
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
			image.setImageResource(Integer.parseInt(_image.get(position).toString()));

			rowView.setTag("textimagenotice");
		}
		else if (type.toString().equals("textimage"))
		{
			if (convertView != null && convertView.getTag().equals("textimage"))
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
			image.setImageResource(Integer.parseInt(_image.get(position).toString()));

			rowView.setTag("textimage");
		}
		else if (type.toString().equals("noticecontent"))
		{
			if (convertView != null && convertView.getTag().equals("noticecontent"))
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
			image.setImageResource(Integer.parseInt(_image.get(position).toString()));

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

			rowView.setTag("noticecontent");
		}
		else if (type.toString().equals("accountheader"))
		{
			String tag = "accountheader";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_header, parent, false);
			}
			// TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			// textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountemail"))
		{
			String tag = "accountemail";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_email, parent, false);
			}
			// TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			// textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountcontent"))
		{
			String tag = "accountcontent";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_info_content, parent, false);
			}
			// TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			// textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountcontentmobile"))
		{
			String tag = "accountcontentmobile";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_info_content_mobile, parent, false);
			}
			// TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			// textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountcontentaddress"))
		{
			String tag = "accountcontentaddress";
			if (convertView != null && convertView.getTag().equals(tag))
			{
				rowView = convertView;
			}
			else
			{
				rowView = inflater.inflate(R.layout.account_info_content_address, parent, false);
			}
			// TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			// textTitle.setText(_title.get(position).toString());
			rowView.setTag(tag);
		}
		else if (type.toString().equals("accountlinegray"))
		{
			String tag = "accountlinegray";
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
			String tag = "accountlineorange";
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
		else
		{
			rowView = inflater.inflate(R.layout.card_text_image, parent, false);
			TextView textTitle = (TextView) rowView.findViewById(R.id.lblSubTitleText);
			TextView textContent = (TextView) rowView.findViewById(R.id.lblContentText);
			TextView textDate = (TextView) rowView.findViewById(R.id.lblDateText);
			ImageView image = (ImageView) rowView.findViewById(R.id.imageViewContentImage);
			textTitle.setText("Test");
			textContent.setText(_content.get(position).toString());
			textDate.setText(_date.get(position).toString());
			image.setImageResource(Integer.parseInt(_image.get(position).toString()));
		}

		return rowView;
	}
}