package com.ttxgps.gpslocation;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.entity.User;
import com.ttxgps.utils.AsyncHttpUtil;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.HttpUploadfile;
import com.ttxgps.utils.MySSLSocketFactory;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.Urls;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class LoginEditBabyInfoActivity extends BaseActivity implements OnClickListener{


	private final int FLAG_HEIGHT = 1;
	private final int FLAG_NICK_NAME = 0;
	private final int FLAG_PHONE = 3;
	private final int FLAG_WEIGHT = 2;

	private File mCurrentPhotoFile;//头像路径

	private ImageView imgHeadIcon;
	private Button btnConfir;
	private TextView tvBirthday;
	private TextView tvHeight;
	private TextView tvImei;
	private TextView tvNickName;
	private TextView tvPhoneNum;
	private TextView tvRelation;
	private TextView tvSex;
	private TextView tvTitle;
	private TextView tvWeight;
	private String Height= "0";
	private String Weight = "0";
	private String DeviceID;
	private String SN;
	private int sex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_babyinfo);
		DeviceID = getIntent().getStringExtra("deviceID");
		SN = getIntent().getStringExtra("SN");
		initTitle();
		initView();

	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.edit_device_info);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		this.imgHeadIcon = (ImageView)findViewById(R.id.head_icon_iv);
		this.tvNickName = (TextView)findViewById(R.id.nick_name_tv);
		this.tvImei = (TextView)findViewById(R.id.imei_tv);
		this.tvBirthday = (TextView)findViewById(R.id.birthday_tv);
		this.tvSex = (TextView)findViewById(R.id.sex_tv);
		this.tvHeight = (TextView)findViewById(R.id.height_u);
		this.tvWeight = (TextView)findViewById(R.id.weight_u);
		this.tvRelation = (TextView)findViewById(R.id.relation_tv);
		this.tvPhoneNum = (TextView)findViewById(R.id.phone_tv);
		this.btnConfir = (Button)findViewById(R.id.confir_btn);
		imgHeadIcon.setOnClickListener(this);
		tvHeight.setText("0cm");
		tvWeight.setText("0kg");
		tvImei.setText(SN);
		tvPhoneNum.setText("");
		tvBirthday.setText("2000-2-2");
	}


	protected void doTakePhoto()
	{
		try
		{
			// Launch camera to take photo for selected contact
			Constants.PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(Constants.PHOTO_DIR, getPhotoFileName());
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, Constants.CAMERA_WITH_DATA);
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
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, Constants.PHOTO_PICKED_WITH_DATA);
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

	private void upInfo(){
		CommonUtils.showProgress(this, "正在提交···");
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("NickName", tvNickName.getText().toString()));
		linkedlist.add(new WebServiceProperty("Height", Height));
		linkedlist.add(new WebServiceProperty("DeviceMob", tvPhoneNum.getText().toString()));
		linkedlist.add(new WebServiceProperty("Birthday", tvBirthday.getText().toString()));
		linkedlist.add(new WebServiceProperty("DeviceID", DeviceID));
		linkedlist.add(new WebServiceProperty("Sex",sex));
		linkedlist.add(new WebServiceProperty("Weight", Weight));
		linkedlist.add(new WebServiceProperty("User", User.id));
		linkedlist.add(new WebServiceProperty("RelashionNick", tvRelation.getText().toString()));

		Log.i("NickName", tvNickName.getText().toString());
		Log.i("Height", Height);
		Log.i("DeviceMob", tvPhoneNum.getText().toString());
		Log.i("Birthday", tvBirthday.getText().toString());
		Log.i("DeviceID", DeviceID);
		Log.i("Sex", sex+"");
		Log.i("Weight", Weight);
		Log.i("User", User.id);
		Log.i("RelashionNick", tvRelation.getText().toString());

		WebServiceTask wsk = new WebServiceTask("DeviceEdit", linkedlist, WebService.URL_OTHER,getBaseContext(),new WebServiceResult() {

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
						if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
							new Thread(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									User.getDevicesList(getApplicationContext());
									if(!DevicesUtils.getcurDevice(getBaseContext()))
										User.curBabys=User.babyslist.get(0);
									PrefHelper.setInfo(PrefHelper.P_LOGIN_STATE, true);
									PrefHelper.setInfo(PrefHelper.P_ISADMIN, true);
									CommonUtils.closeProgress();
									Intent i = new Intent();
									i.setClass(LoginEditBabyInfoActivity.this, MainFragmentActivity.class);
									startActivity(i);
									finish();
								}


							}.start();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				CommonUtils.closeProgress();
				Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
		wsk.execute("DeviceEditResult");
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


	private void showDatePicker()
	{
		String[]str = tvBirthday.getText().toString().split("-");
		DatePickerDialog datePickerDialog = new DatePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT, DateSet, Integer.parseInt(str[0]), Integer.parseInt(str[1])-1, Integer.parseInt(str[2]));
		datePickerDialog.show();
	}

	DatePickerDialog.OnDateSetListener DateSet = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// 每次保存设置的日期

			String str = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
			tvBirthday.setText(str);
		}
	};


	private void upicon(Bitmap photo){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		photo.compress(CompressFormat.PNG, 100, stream);
		//				mBitmap.recycle();
		byte[] imgbuf = stream.toByteArray();
		HttpUploadfile http = new HttpUploadfile(Urls.edit_device_header,"0", DeviceID, imgbuf,"","",
				new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case 13:
					Utils.showToast("头像修改成功");
					break;
				case 14:
					Utils.showToast("头像修改失败");
					break;
				}
			}

		});
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.head_icon_iv:
			selectPictureDialog();
			break;
		case R.id.nick_name_rl:
			Intent intent = new Intent(this, BabyDetailUpdateActivity.class);
			intent.putExtra("flag", FLAG_NICK_NAME);
			intent.putExtra("text",tvNickName.getText().toString());
			intent.putExtra("maxLength", 20);
			startActivityForResult(intent, FLAG_HEIGHT);
			break;
		case R.id.phone_rl:
			Intent intent1 = new Intent(this, BabyDetailUpdateActivity.class);
			intent1.putExtra("flag", FLAG_PHONE);
			intent1.putExtra("text",tvPhoneNum.getText().toString());
			intent1.putExtra("maxLength", 12);
			startActivityForResult(intent1, FLAG_HEIGHT);
			break;
		case R.id.birthday_rl:
			showDatePicker();
			break;
		case R.id.sex_rl:
			startActivityForResult(new Intent(this, BabySex.class).putExtra("sex", 1), FLAG_HEIGHT);
			break;
		case R.id.height_rl:
			Intent intent2 = new Intent(this, BabyDetailUpdateActivity.class);
			intent2.putExtra("flag", FLAG_HEIGHT);
			intent2.putExtra("text",Height);
			intent2.putExtra("maxLength", FLAG_PHONE);
			startActivityForResult(intent2, FLAG_HEIGHT);
			break;
		case R.id.weight_rl:
			Intent intent3 = new Intent(this, BabyDetailUpdateActivity.class);
			intent3.putExtra("flag", FLAG_WEIGHT);
			intent3.putExtra("text",Weight);
			intent3.putExtra("maxLength", FLAG_PHONE);
			startActivityForResult(intent3, FLAG_HEIGHT);
			break;
		case R.id.relation_rl:
			Intent intent4 = new Intent(this, BabyRelationActivity.class);
			intent4.putExtra("relation", tvRelation.getText().toString());
			startActivityForResult(intent4, FLAG_HEIGHT);
			break;
		case R.id.confir_btn:
			upInfo();
			break;

		default:
			break;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode)
		{
		case Constants.PHOTO_PICKED_WITH_DATA:
		{
			if (data == null)
				return;

			Bitmap photo = data.getParcelableExtra("data");
			if (null == photo)
			{
				String path = data.getStringExtra("croppedpath");
				photo = BitmapFactory.decodeFile(path);
			}
			imgHeadIcon.setImageBitmap(photo);
			upicon(photo);
			break;
		}

		case Constants.CAMERA_WITH_DATA:
		{
			if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists())
				doCropPhoto(mCurrentPhotoFile);
			break;
		}

		}
		if (resultCode == 666 && data != null) {
			int flag = data.getIntExtra("flag", -1);
			String value = data.getStringExtra("value");
			switch (flag) {
			case FLAG_NICK_NAME /*0*/:
				this.tvNickName.setText(value);
				break;
			case FLAG_HEIGHT /*1*/:
				this.tvHeight.setText(value+"cm");
				Height = value;
				break;
			case FLAG_WEIGHT /*2*/:
				this.tvWeight.setText(value+"kg");
				Weight = value;
				break;
			case FLAG_PHONE /*3*/:
				this.tvPhoneNum.setText(value);
				break;

			}
		}else if (resultCode == 667 && data != null) {
			sex = data.getIntExtra("sex", FLAG_HEIGHT);
			if (sex == FLAG_HEIGHT) {
				this.tvSex.setText(R.string.man);
			} else {
				this.tvSex.setText(R.string.lula);
			}
		}else if (resultCode == 668 && data != null) {
			String relation = data.getStringExtra("relation");
			this.tvRelation.setText(relation);
		}
	}

}
