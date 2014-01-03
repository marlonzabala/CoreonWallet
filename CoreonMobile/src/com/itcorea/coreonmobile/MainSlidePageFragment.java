package com.itcorea.coreonmobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.itcorea.coreonmobile.R;

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
			rootView = (ViewGroup) inflater.inflate(R.layout.activity_main_information, container, false);
		}
		else if (pageNum == 1)
		{
			rootView = (ViewGroup) inflater.inflate(R.layout.activity_account_info, container, false);
		}
		else if (pageNum == 2)
		{
			rootView = (ViewGroup) inflater.inflate(R.layout.activity_main_information, container, false);
		}

		return rootView;
	}
	
	
	
	private final String	NAMESPACE	= "http://www.w3schools.com/webservices/";
	private final String	URL			= "http://www.w3schools.com/webservices/tempconvert.asmx";
	private final String	SOAP_ACTION	= "http://www.w3schools.com/webservices/CelsiusToFahrenheit";
	private final String	METHOD_NAME	= "CelsiusToFahrenheit";
	private String			TAG			= "PGGURU";
	private static String	celcius;
	private static String	fahren;
	
	
	
	public void getFahrenheit(String celsius)
	{
//		// Create request
//		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//		// Property which holds input parameters
//		PropertyInfo celsiusPI = new PropertyInfo();
//		// Set Name
//		celsiusPI.setName("Celsius");
//		// Set Value
//		celsiusPI.setValue(celsius);
//		// Set dataType
//		celsiusPI.setType(double.class);
//		// Add the property to request object
//		request.addProperty(celsiusPI);
//		// Create envelope
//		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//		envelope.dotNet = true;
//		// Set output SOAP object
//		envelope.setOutputSoapObject(request);
//		// Create HTTP call object
//		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
//
//		try
//		{
//			// Invole web service
//			androidHttpTransport.call(SOAP_ACTION, envelope);
//			// Get the response
//			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
//			// Assign it to fahren static variable
//			fahren = response.toString();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}
}
