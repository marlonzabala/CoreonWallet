package com.itcorea.coreonwallet;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter
{

	private Context							_context;
	private List<String>					_listDataHeader;
	private HashMap<String, List<String>>	_listDataChild;

	private List<String>					_listDataHeaderImage;
	private HashMap<String, List<String>>	_listDataChildImage;

	public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData,
			List<String> listDataHeaderImage, HashMap<String, List<String>> listChildDataImage)
	{
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this._listDataHeaderImage = listDataHeaderImage;
		this._listDataChildImage = listChildDataImage;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon)
	{
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
	}

	public int getChildImage(int groupPosition, int childPosititon)
	{
		return Integer.parseInt(this._listDataChildImage.get(this._listDataHeaderImage.get(groupPosition)).get(childPosititon));
	}

	// test comment

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		String childText = (String) getChild(groupPosition, childPosition);
		int imageId = getChildImage(groupPosition, childPosition);
		View v;

		LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (childText == "Home")
		{
			Log.i("test", childText);
			// enlarge home button
			v = infalInflater.inflate(R.layout.list_item_home, null);
			v.setTag("home");
		}
		else if (childText == "LOGO")
		{
			// enlarge home button
			v = infalInflater.inflate(R.layout.list_item_logo, null);
			childText = "";
			v.setTag("logo");
		}
		else
		{
			if (convertView != null && convertView.getTag().equals("child"))
			{
				v = convertView;
			}
			else
			{
				v = infalInflater.inflate(R.layout.list_item, parent, false);
			}

			v.setTag("child");
			// v = infalInflater.inflate(R.layout.list_item, null);
		}

		TextView txtListChild = (TextView) v.findViewById(R.id.lblListItem);

		txtListChild.setText(childText);

		ImageView lblListHeaderImage = (ImageView) v.findViewById(R.id.imageViewIcon);
		lblListHeaderImage.setImageResource(imageId);

		return v;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		String headerTitle = (String) getGroup(groupPosition);
		View v;
		// int imageId = getGroupImage(groupPosition);

		LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (headerTitle.equals(""))
		{
			v = infalInflater.inflate(R.layout.blank_layout, null);
		}
		else if (headerTitle.equals("Home"))
		{
			v = infalInflater.inflate(R.layout.blank_layout, null);
		}
		else
		{
			v = infalInflater.inflate(R.layout.list_group, null);

			TextView lblListHeader = (TextView) v.findViewById(R.id.lblListHeader);
			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(headerTitle);
		}
		v.setTag("header");

		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return this._listDataHeader.get(groupPosition);
	}

	public int getGroupImage(int groupPosition)
	{
		return Integer.parseInt(this._listDataHeaderImage.get(groupPosition));
	}

	@Override
	public int getGroupCount()
	{
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}
}