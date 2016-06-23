package com.ttxgps.zxing.core;

import java.io.IOException;
import java.util.Collection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.ttxgps.gpslocation.BaseActivity;
import com.ttxgps.zxing.camera.CameraManager;
import com.ttxgps.zxing.camera.EncodingHandler;
import com.xtst.gps.R;

public final class CaptureActivity extends BaseActivity implements
SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();
	private static final String QR_TEXT = "qr_text_msg";
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private Result lastResult;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	// private Button from_gallery;
	static final int PARSE_BARCODE_SUC = 3035;
	static final int PARSE_BARCODE_FAIL = 3036;
	String photoPath;
	ProgressDialog mProgress;
	private SurfaceView surfaceView;
	private ImageView qrImgImageView;
	// private RelativeLayout
	/**
	 * 会员 条码 flag =1,flag=2
	 */
	private int flag;
	private RelativeLayout qrRl;
	private String qrTextStr;
	private String IMEI;
	private TextView Title,IMEI_tv;

	enum IntentSource {

		ZXING_LINK, NONE

	}


	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_capture);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		hasSurface = false;
		initTitleView();
		initView();

	}

	protected void initTitleView() {
		Title = (TextView)findViewById(R.id.title_tv);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	protected void initView() {
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		qrTextStr = getIntent().getStringExtra("qr_text");
		IMEI = getIntent().getStringExtra("IMEI");
		flag = getIntent().getIntExtra("flag", 2);
		qrImgImageView = (ImageView) this.findViewById(R.id.qrIv);
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		qrRl = (RelativeLayout) findViewById(R.id.qrRl);
		IMEI_tv = (TextView)findViewById(R.id.imei_tv);
	}

	// 解析二维码
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		lastResult = rawResult;
		String result = rawResult.getText();
		if(TextUtils.isEmpty(result)){
			Toast.makeText(this, "扫描失败!", 0).show();
		}else{
			sendBroadcast(result);
		}
		finish();

	}





	private void sendBroadcast(String result) {
		Intent resultIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("barcode", result);
		resultIntent.putExtras(bundle);
		setResult(-1, resultIntent);

	}


	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(qrTextStr)) {
			qrRl.setVisibility(View.VISIBLE);
			surfaceView.setVisibility(View.GONE);
			Title.setText("生成的二维码");
			IMEI_tv.setText(IMEI);

			// captureTitleTv.setText("生成的二维码");
			// captureTitleTv.setTextColor(getResources().getColor(R.color.black));
			try {
				Bitmap qrCodeBitmap = EncodingHandler.createQRCode(qrTextStr,
						350);
				qrImgImageView.setImageBitmap(qrCodeBitmap);

			} catch (WriterException e) {
				e.printStackTrace();
			}

		} else {
			inactivityTimer = new InactivityTimer(this);
			cameraManager = new CameraManager(getApplication());
			viewfinderView.setCameraManager(cameraManager);
			handler = null;
			lastResult = null;
			Title.setText("自动扫描二维码");
			// captureTitleTv.setText("自动扫描二维码或条码");
			// captureTitleTv.setTextColor(getResources().getColor(R.color.white));
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			if (hasSurface) {
				initCamera(surfaceHolder);
			} else {
				surfaceHolder.addCallback(this);
				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
			inactivityTimer.onResume();
			source = IntentSource.NONE;
			decodeFormats = null;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		if (inactivityTimer != null) {
			inactivityTimer.onPause();
		}
		if (cameraManager != null) {
			cameraManager.closeDriver();
		}
		if (!hasSurface) {
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (inactivityTimer != null) {
			inactivityTimer.shutdown();
		}
		/*
		 * if (mProgress!= null) { mProgress.dismiss(); }
		 */
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK)
					&& lastResult != null) {
				// restartPreviewAfterDelay(0L);
				this.finish();
				return true;
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 这里初始化界面，调用初始化相机
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	private static ParsedResult parseResult(Result rawResult) {
		return ResultParser.parseResult(rawResult);
	}

	// 初始化照相机，CaptureActivityHandler解码
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						characterSet, cameraManager);
			}
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.exit, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}



}
