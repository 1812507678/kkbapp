package com.ttxgps.gpslocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.message.PushAgent;
import com.xtst.gps.R;

public class ActivityInfoCentre extends Activity implements OnClickListener
{

	private WebView mWebview;
	public String url = "http://115.28.81.143:8080/wap/info.aspx";
	private String TitleStr;
	private TextView tvTitle;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating_content);
		PushAgent.getInstance(getBaseContext()).onAppStart();

		url = getIntent().getStringExtra("web_url");
		TitleStr = getIntent().getStringExtra("web_title");

		initTitle();
		init();

	}

	private void init()
	{
		// TODO Auto-generated method stub
		mWebview = (WebView) findViewById(R.id.rating_webview);
		//		mloading = findViewById(R.id.box_wait_loading);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setAppCacheEnabled(true);
		mWebview.getSettings().setAppCachePath(getBaseContext().getFilesDir().getAbsolutePath());
		mWebview.setWebViewClient(new webviewClient());
		mWebview.setDownloadListener(new WebViewDownLoadListener());
		mWebview.setWebChromeClient(new chromeClient());

		mWebview.loadUrl(url);


	}

	private void initTitle(){
		tvTitle = (TextView) findViewById(R.id.title_tv);
		tvTitle.setText(TitleStr);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void onStart()
	{
		super.onStart();

	}

	public class webviewClient extends WebViewClient
	{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			// TODO Auto-generated method stub
			mWebview.loadUrl(url);
			return true;
			//            return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			return;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl)
		{
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			return;
		}
	}

	public class chromeClient extends WebChromeClient
	{

		@Override
		public void onReceivedTitle(WebView view, String title)
		{
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
			//			hdui.sendMessage(hdui.obtainMessage(0, title));
		}

	}



	private class WebViewDownLoadListener implements DownloadListener
	{

		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition,
				String mimetype,
				long contentLength)
		{
			try
			{
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	Handler hdui = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0://前进
				if(mWebview.canGoBack()){

				}
				break;

			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if ((keyCode == KeyEvent.KEYCODE_BACK)){
			if(mWebview.canGoBack()&&!TitleStr.equals("意见反馈")){

				mWebview.goBack();
				return true;
			}
			else{
				finish();
			}
		}
		return false;
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/** 友盟 */
	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.back_btn:
			finish();
			break;
		}

	}
}
