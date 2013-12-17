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

public class CardArrayAdapter extends ArrayAdapter<String>
{
	private final Context	context;

	ArrayList<String>		_image		= new ArrayList<String>();

	public CardArrayAdapter(Context context, ArrayList<String> values)
	{
		super(context, R.layout.card_text_image_notice_content, values);
		this.context = context;
	}

	public void initiatizeStringsValues()
	{
		_image = new ArrayList<String>();

		this.clear();
	}

	public void addStrings(int image)
	{
		_image.add(String.valueOf(image));

		this.add("");
	}

	@SuppressLint("InlinedApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View rowView;
		

		if (convertView != null)
		{
			rowView = convertView;
		}
		else
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.card_chooser_fragment, parent, false);
		}
		
		try
		{
			ImageView image = (ImageView) rowView.findViewById(R.id.imageViewCard);
			image.setImageResource(Integer.parseInt(_image.get(position).toString()));
			
		}
		catch(Exception e)
		{
			
		}

		return rowView;
	}
}