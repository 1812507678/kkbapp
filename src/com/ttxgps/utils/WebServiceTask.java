package com.ttxgps.utils;

import java.io.IOException;
import java.util.LinkedList;

import org.ksoap2.transport.HttpResponseException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class WebServiceTask extends AsyncTask<String, String, String> {

	private final Context context;
	private final  String methonName;
	private final  LinkedList<WebServiceProperty> mlist;
	private final  String mUrl;
	private String data;

	WebServiceResult callback;
	public WebServiceTask(String method, LinkedList<WebServiceProperty> list,String url,Context ct,WebServiceResult wsResult ){
		methonName=method;
		mlist = list;
		mUrl = url;
		callback = wsResult;
		context=ct;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO: attempt authentication against a network service.
		try {
			Log.e(methonName, params[0]);
			WebService webservice = new WebService(context, methonName);

			webservice.SetProperty(mlist);
			data = webservice.Get(params[0],mUrl);
			Log.e(methonName, data.toString());


			return null;


			// Simulate network access.
		}catch(HttpResponseException he){
			he.printStackTrace();
			return "ÍøÂç´íÎó£¡";

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ÍøÂç´íÎó£¡";
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Êý¾Ý¸ñÊ½´íÎó£¡";
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Î´Öª´íÎó£¡";
		}

		// TODO: register the new account here.

	}

	@Override
	protected void onPostExecute(final String result) {
		if(callback!=null)
			callback.webServiceResult(result,data);

	}

	@Override
	protected void onCancelled() {

	}

	public interface WebServiceResult {

		void webServiceResult(String result,String data);

	}
}