package com.ttxgps.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class WebService
{

	private static final String NAMESPACE = "http://tempuri.org/";
	public static final String URL_OTHER = "http://api.kokobao.com:8001/Other.asmx";//112.74.130.65:8001
	public static final String URL_OPEN = "http://api.kokobao.com:8001/OpenAPIV2.asmx";
	public static final String URL_GEOFENCE = "http://api.kokobao.com:8001/Geofence.asmx";//192.168.1.101:26924
	public static final String URL_WX = "http://112.74.130.65:8001/Weixin/Pay.asmx";//112.74.130.65:8001
	private SoapSerializationEnvelope envelope;
	private final String methodName;
	private SoapObject result;
	private final SoapObject rpc;
	static String cookie="";

	public WebService(Context context, String s)
	{
		methodName = s;
		Log.e("WebService", methodName);
		rpc = new SoapObject(NAMESPACE, methodName);
	}

	public String Get(String s,String url) throws HttpResponseException, IOException, XmlPullParserException
	{
		envelope = new SoapSerializationEnvelope(110);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);
		(new MarshalBase64()).register(envelope);
		HttpTransportSE httptransportse = new HttpTransportSE(url);
		httptransportse.debug = true;
		String s1;

		ArrayList<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
		if(cookie!=""){
			HeaderProperty headerPropertyObj = new HeaderProperty("cookie", cookie);
			headerList.add(headerPropertyObj);
		}
		LinkedList<HeaderProperty> hlist=(LinkedList) httptransportse.call((new StringBuilder(NAMESPACE)).append(methodName).toString(), envelope,headerList);
		for(int i=0;hlist!=null&&i<hlist.size();i++){
			//Log.e("ttxgps", "key="+hlist.get(i).getKey()+",value="+hlist.get(i).getValue());
			if(hlist.get(i).getKey()=="cookie"){
				cookie=hlist.get(i).getValue();
				break;
			}
		}
		result = (SoapObject)envelope.bodyIn;
		s1 = result.getProperty(s).toString();

		return s1;
	}

	public void SetProperty(List<?> list)
	{
		int i = 0;
		do
		{
			if(i >= list.size())
				return;
			rpc.addProperty(((WebServiceProperty)list.get(i)).property, ((WebServiceProperty)list.get(i)).value);
			i++;
		} while(true);
	}
}
