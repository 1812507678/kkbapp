package com.ttxgps.utils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;



import org.json.JSONObject;

import com.ttxgps.gpslocation.BabyDetailActivity;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpUploadfile {

	public Handler handler;
	public Thread thread;
	String jsonObject;
	public HttpUploadfile(final String url,final String userid,final String Deviceid,final byte[] content,final String TypeInt,final String SoundTime, final Handler handler) {

		thread = new Thread(){
			@Override
			public void run(){
				//type 0 内容为文字，1 内容为语音

				Bundle bundle = new Bundle();
				StringBuffer b =new StringBuffer();
				Message msg = new Message();
				try
				{
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(url);
					httpPost.addHeader("UserID", userid);
					httpPost.addHeader("DeviceID", Deviceid);
					httpPost.addHeader("con-lenght", content.length+"");
					httpPost.addHeader("TypeInt", TypeInt);
					httpPost.addHeader("SoundTime", SoundTime);
					ByteArrayEntity se;
					se = new ByteArrayEntity(content);
					httpPost.setEntity(se);

					HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);

					String str = EntityUtils.toString(httpResponse.getEntity());
					JSONObject json = new JSONObject(str);
					String result = json.optString("Status");
					//			          bundle.putString("json",EntityUtils.toString(httpResponse.getEntity()));
					if(result.equals("0"))
						msg.what = BabyDetailActivity.UPLOAD_USERINFO_SUCCESS;
					else if(result.equals("100"))
						msg.what = 15;
					else
						msg.what = BabyDetailActivity.UPLOAD_USERINFO_FAIL;
				}
				catch(Exception e)
				{
					//			          System.out.println("文件上传失败");
					//			          bundle.putString("json",String.valueOf(R.string.Community_NetWork_File_Fail));
					msg.what = BabyDetailActivity.UPLOAD_USERINFO_FAIL;
				}



				//				  msg.setData(bundle);
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}


}
