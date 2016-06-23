package com.ttxgps.gpslocation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.User;
import com.ttxgps.utils.AsyncImageLoader;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.Deviceinf;
import com.ttxgps.utils.HttpUploadfile;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.Urls;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.AsyncImageLoader.ImageCallback;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class AccountActivity extends BaseActivity implements OnClickListener{

	private String PhotoPath; //头像路径
	private Bitmap PhotoBitmap;//头像
	private File mCurrentPhotoFile;

	TextView account_tv;
	ImageView head_icon_iv;
	private String mHeadIconPath;
	TextView nick_name_tv;
	TextView phone_tv;
	TextView sex_tv;
	TextView sign_show_tv;
	private int sex = 0;
	private final AsyncImageLoader imageLoader = new AsyncImageLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		initTitle();
		initView();
		//		getInfo();
		initUserData();
		initIcon();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.my_account_manage);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		head_icon_iv = (ImageView)findViewById(R.id.head_icon_iv);
		account_tv = (TextView)findViewById(R.id.userid_detail_tv);
		nick_name_tv = (TextView)findViewById(R.id.nick_name_detail_tv);
		phone_tv = (TextView)findViewById(R.id.phone_detail_tv);
		sex_tv = (TextView)findViewById(R.id.sex_tv);
		sign_show_tv =(TextView)findViewById(R.id.sign_show_tv);

		findViewById(R.id.nick_rl).setOnClickListener(this);
		findViewById(R.id.sex_rl).setOnClickListener(this);
		findViewById(R.id.sign_rl).setOnClickListener(this);
		findViewById(R.id.email_rl).setOnClickListener(this);
		findViewById(R.id.account_ok).setOnClickListener(this);
		this.head_icon_iv.setOnClickListener(this);
	}

	private void initUserData() {
		this.nick_name_tv.setText(User.niceName);
		this.account_tv.setText(User.loginName);
		if (User.sex.equals("1")) {
			this.sex_tv.setText(R.string.man);
		} else {
			this.sex_tv.setText(R.string.lula);
		}
		this.sign_show_tv.setText(User.Signature);
		sex = Integer.parseInt(User.sex);
	}


	private void initIcon(){
		if(!User.headerurl.isEmpty()){
			PhotoPath = imageLoader.getCacheImgFileName(User.headerurl);
			if (PhotoPath == null)
			{
				Drawable cachedImage = null;
				cachedImage = imageLoader.loadDrawable(User.headerurl, getBaseContext(),
						true, new ImageCallback()
				{

					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl)
					{
						if (imageDrawable != null)
						{
							head_icon_iv.setImageDrawable(imageDrawable);
						}
						else
						{
						}
					}
				});
				if (cachedImage != null)
				{
					head_icon_iv.setImageDrawable(cachedImage);

				}
			}
			else
			{
				head_icon_iv.setImageBitmap(BitmapFactory.decodeFile(PhotoPath));
			}
		}else{
			head_icon_iv.setImageResource(R.drawable.home_icon_head);
		}
	}


	protected void doTakePhoto()
	{
		try
		{
			// Launch camera to take photo for selected contact
			mCurrentPhotoFile = new File(Constants.HEADER_DIR, getPhotoFileName());
			startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE").putExtra("output", Uri.fromFile(mCurrentPhotoFile)), 18);
			PhotoPath = mCurrentPhotoFile.getAbsolutePath();
		}
		catch (ActivityNotFoundException e)
		{
			Toast.makeText(this, "异常", Toast.LENGTH_LONG).show();
		}
	}

	protected void doPickPhotoFromGallery()
	{
		try
		{
			Intent intent;
			if (VERSION.SDK_INT < 19) {
				intent = new Intent("android.intent.action.GET_CONTENT");
				intent.setType("image/*");
			} else {
				intent = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
			}
			startActivityForResult(intent, 19);
		}
		catch (ActivityNotFoundException e)
		{
			Toast.makeText(this, "异常", Toast.LENGTH_LONG).show();
		}
	}

	private String getPhotoFileName()
	{
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	public static Intent getTakePickIntent(File f)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	public static Intent getPhotoPickIntent()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", Constants.ICON_SIZE);
		intent.putExtra("outputY", Constants.ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}

	public static Intent getCropImageIntent(Uri photoUri)
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		//	        intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", Constants.ICON_SIZE);
		intent.putExtra("outputY", Constants.ICON_SIZE);
		intent.putExtra("return-data", true);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("noFaceDetection", true);
		return intent;
	}

	protected void doCropPhoto(File f)
	{
		try
		{

			// Add the image to the media store
			MediaScannerConnection.scanFile(
					this,
					new String[]
							{
							f.getAbsolutePath()
							},
							new String[]
									{
							null
									},
									null);

			// Launch gallery to crop the photo
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, Constants.PHOTO_PICKED_WITH_DATA);
		}
		catch (Exception e)
		{
			//	            Log.e(TAG, "Cannot crop image", e);
			Toast.makeText(this, "异常", Toast.LENGTH_LONG).show();
		}
	}

	public void selectPictureDialog() {

		Builder builder = new Builder(this,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(R.string.set_logo);
		View view = LayoutInflater.from(this).inflate(R.layout.radiobutton_item, null);
		builder.setView(view);
		builder.setNegativeButton(R.string.cancel, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final Dialog finalDialog = builder.create();

		RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_choice);
		((RadioButton) view.findViewById(R.id.rb_one)).setText(R.string.dialog_new_pic);
		((RadioButton) view.findViewById(R.id.rb_two)).setText(R.string.dialog_select_pic);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				// TODO Auto-generated method stub
				finalDialog.dismiss();
				if(id == R.id.rb_one){
					doTakePhoto();
				}else{
					doPickPhotoFromGallery();
				}
			}
		});
		finalDialog.show();
	}

	private void getInfo(){
		String imei =  Deviceinf.getDeviceId(getBaseContext());
		String imsi =  Deviceinf.getSubscriberId(getBaseContext());
		if(TextUtils.isEmpty(imei))
			imei = "";
		if(TextUtils.isEmpty(imsi))
			imsi = "";
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Name", User.loginName));
		linkedlist.add(new WebServiceProperty("Pass", User.LoginPwd));
		linkedlist.add(new WebServiceProperty("LoginType", 2));
		linkedlist.add(new WebServiceProperty("Imei", imei));
		linkedlist.add(new WebServiceProperty("Imsi", imsi));
		linkedlist.add(new WebServiceProperty("Platform", "android"));

		WebServiceTask wsk = new WebServiceTask("Login", linkedlist, WebService.URL_OPEN,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				CommonUtils.closeProgress();
				if(result!=null){
					Utils.showToast(result);
				}
				else{
					try {
						JSONObject jsonObject = new JSONObject(data);
						if (jsonObject.has(Constants.STATUS) && jsonObject.optString(Constants.STATUS).equals("0")) {
							// Save user info from Server if Login successfully
							String userInfoStr = jsonObject.optString("userInfo");
							JSONObject jOTemp = new JSONObject(userInfoStr);
							User.niceName = jOTemp.optString("userName");
							User.sex = jOTemp.optString("sex");
							User.headerurl = jOTemp.optString("picurl");
							User.Signature = jOTemp.optString("signature");
							initUserData();
							initIcon();
						}
						else{
							Utils.showToast(jsonObject.optString(Constants.MSG));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		wsk.execute("LoginResult");
	}

	private void getInfo2(){
		String imei =  Deviceinf.getDeviceId(getBaseContext());
		String imsi =  Deviceinf.getSubscriberId(getBaseContext());
		if(TextUtils.isEmpty(imei))
			imei = "";
		if(TextUtils.isEmpty(imsi))
			imsi = "";
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Name", User.loginName));
		linkedlist.add(new WebServiceProperty("Pass", User.LoginPwd));
		linkedlist.add(new WebServiceProperty("LoginType", 2));
		linkedlist.add(new WebServiceProperty("Imei", imei));
		linkedlist.add(new WebServiceProperty("Imsi", imsi));
		linkedlist.add(new WebServiceProperty("Platform", "android"));
		WebServiceTask wsk = new WebServiceTask("Login", linkedlist, WebService.URL_OPEN,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				CommonUtils.closeProgress();
				if(result!=null){
					Utils.showToast(result);
				}
				else{
					try {
						JSONObject jsonObject = new JSONObject(data);
						if (jsonObject.has(Constants.STATUS) && jsonObject.optString(Constants.STATUS).equals("0")) {
							// Save user info from Server if Login successfully
							String userInfoStr = jsonObject.optString("userInfo");
							JSONObject jOTemp = new JSONObject(userInfoStr);
							User.niceName = jOTemp.optString("userName");
							User.sex = jOTemp.optString("sex");
							User.headerurl = jOTemp.optString("picurl");
							User.Signature = jOTemp.optString("signature");
							CommonUtils.downloadphoto(getBaseContext(), User.headerurl, null);
						}
						else{
							Utils.showToast(jsonObject.optString(Constants.MSG));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		wsk.execute("LoginResult");
	}

	private void upInfo(){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Nick", nick_name_tv.getText().toString()));
		linkedlist.add(new WebServiceProperty("Mob", User.phone));
		linkedlist.add(new WebServiceProperty("Sex", sex));
		linkedlist.add(new WebServiceProperty("Signature", sign_show_tv.getText().toString()));
		//		linkedlist.add(new WebServiceProperty("Email", User.email));
		WebServiceTask wsk = new WebServiceTask("InfoEdit", linkedlist, WebService.URL_OTHER,getBaseContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				String msg;
				if(result!=null){//错误信息
					msg=result;
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=(String) jsonObject.get("Msg");
						msg=str;
						if (jsonObject.has(Constants.STATUS) && jsonObject.optString(Constants.STATUS).equals("0")) {
							if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
								User.niceName = nick_name_tv.getText().toString();
								User.sex = String.valueOf(sex);
								User.Signature = sign_show_tv.getText().toString();
								finish();
							}
						}
						else{
							Utils.showToast(jsonObject.optString(Constants.MSG));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				Utils.showToast(msg);
			}
		});
		wsk.execute("InfoEditResult");
	}

	private void upicon(){
		//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		File file;
		file = new File(PhotoPath);

		//		PhotoBitmap.compress(CompressFormat.PNG, 100, stream);
		//				mBitmap.recycle();
		byte[] imgbuf = CommonUtils.getBytesFromFile(file);
		HttpUploadfile http = new HttpUploadfile(Urls.edit_device_header, User.id,"0", imgbuf,"","",
				new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case 13:
					Utils.showToast("头像修改成功");
					getInfo2();
					break;
				case 14:
					Utils.showToast("头像修改失败");
					break;
				}
				if (PhotoPath.contains(CommonUtils.COMPRESS_TEMP_FILE_NAME)) {
					CommonUtils.deleteTempFile(PhotoPath);
				}
			}

		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.head_icon_iv:
			selectPictureDialog();
			break;
		case R.id.nick_rl:
			Intent intent = new Intent(this, BabyDetailUpdateActivity.class);
			intent.putExtra("flag", 0);
			intent.putExtra("text",nick_name_tv.getText().toString());
			intent.putExtra("maxLength", 11);
			startActivityForResult(intent, 1);
			break;
		case R.id.sex_rl:
			startActivityForResult(new Intent(this, BabySex.class).putExtra("sex",sex), 1);
			break;
		case R.id.sign_rl:
			Intent intent3 = new Intent(this, ModifySign.class);
			intent3.putExtra("text",sign_show_tv.getText().toString());
			startActivityForResult(intent3, 2);
			break;
		case R.id.phone_rl:
			Intent intent2 = new Intent(this, BabyDetailUpdateActivity.class);
			intent2.putExtra("flag", 3);
			intent2.putExtra("text",phone_tv.getText().toString());
			intent2.putExtra("maxLength", 12);
			startActivityForResult(intent2, 0);
			break;
		case R.id.account_ok:
			upInfo();
			break;
		default:

		}
	}

	private void setHeadIcon(String path) {
		this.PhotoPath = path;
		Bitmap bmp = CommonUtils.getSmallBitmap(this.PhotoPath);
		if (bmp != null) {
			this.head_icon_iv.setImageBitmap(bmp);
		}
		String tempFilePath = CommonUtils.compressLocalPic(this, this.PhotoPath);
		if (!TextUtils.isEmpty(tempFilePath)) {
			CommonUtils.deleteTempFile(PhotoPath);
			PhotoPath = tempFilePath;
		}
		upicon();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode)
		{
		case 18:
		{
			if (resultCode == -1) {
				try {
					File cameraFile = new File(PhotoPath);
					if (cameraFile != null && cameraFile.exists()) {
						setHeadIcon(cameraFile.getAbsolutePath());
						return;
					}
					return;
				} catch (Exception e) {
					Utils.showToast(R.string.photo_get_fail);
					e.printStackTrace();
					return;
				}
			}
			CommonUtils.deleteTempFile(PhotoPath);

			break;
		}

		case 19:
		{
			Uri selectedImage = data.getData();
			if (selectedImage != null) {
				String picPath = CommonUtils.getPicPathByUri(this, selectedImage);
				if (TextUtils.isEmpty(picPath)) {
					Utils.showToast(R.string.cant_find_pictures);
				} else {
					setHeadIcon(picPath);
				}
			}
			break;
		}

		}
		if (resultCode == 1000 && data != null) {
			sign_show_tv.setText(data.getStringExtra("value"));
		}
		else if (resultCode == 666 && data != null) {
			int flag = data.getIntExtra("flag", -1);
			String value = data.getStringExtra("value");
			switch (flag) {
			case 0 /*0*/:
				this.nick_name_tv.setText(value);
				break;
			case 3 /*3*/:
				this.phone_tv.setText(value);
				break;

			}
		}else if (resultCode == 667 && data != null) {
			sex = data.getIntExtra("sex", 1);
			if (this.sex == 1) {
				this.sex_tv.setText(R.string.man);
			} else {
				this.sex_tv.setText(R.string.lula);
			}
		}
	}


}
