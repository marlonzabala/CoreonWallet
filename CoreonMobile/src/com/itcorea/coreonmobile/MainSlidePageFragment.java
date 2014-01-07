package com.itcorea.coreonmobile;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class MainSlidePageFragment extends Fragment
{
	public int	pageNum	= 0;

	@SuppressLint("ValidFragment")
	public MainSlidePageFragment(int position)
	{
		// TODO Auto-generated constructor stub
		pageNum = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_main_options, container, false);

		if (pageNum == 0)
		{
			rootView = (ViewGroup) inflater.inflate(R.layout.gcash_main_information, container, false);
		}
		else if (pageNum == 1)
		{
			rootView = (ViewGroup) inflater.inflate(R.layout.activity_account_info, container, false);
		}
		else if (pageNum == 2)
		{
			rootView = (ViewGroup) inflater.inflate(R.layout.gcash_main_information, container, false);
		}

		return rootView;
	}
	
	
}
