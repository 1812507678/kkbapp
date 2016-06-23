package com.ttxgps.gpslocation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.BabyDetailActivity.InitReceiver;
import com.ttxgps.msg.MsgActivity;
import com.ttxgps.pay.PayActivity;
import com.ttxgps.utils.AsyncHttpUtil;
import com.ttxgps.utils.AsyncImageLoader;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.HttpUploadfile;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.Urls;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.AsyncImageLoader.ImageCallback;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.ttxgps.zxing.core.CaptureActivity;
import com.xtst.gps.R;

public class BabydetailFragment extends Fragment  implements OnClickListener{
	private final int FLAG_HEIGHT = 1;
	private final int FLAG_NICK_NAME = 0;
	private final int FLAG_PHONE = 3;
	private final int FLAG_WEIGHT = 2;

	public static final int UPLOAD_USERINFO_SUCCESS = 13; //上传用户信息成功
	public static final int UPLOAD_USERINFO_FAIL = 14; //上传用户信息失败


	private File mCurrentPhotoFile;


	private boolean IsAlterPhoto = false; //是否有修改头像
	private String PhotoPath; //头像路径
	private Bitmap PhotoBitmap;//头像

	private ImageView imgHeadIcon;
	private TextView tvBirthday;
	private TextView tvHeight;
	private TextView tvNickName;
	private TextView tvSn;
	private TextView tvPhoneNum;
	private TextView tvRelation;
	private TextView tvSex;
	private TextView tvTitle;
	private TextView tvWeight;
	private TextView tvDeviceId;
	private TextView tvExpireTime;
	private int sex = 0;
	private BabyInfoBean babyInfoBean;

	private boolean isAdmin;
	private String Height,Weight;
	private final AsyncImageLoader imageLoader = new AsyncImageLoader();

	private final InitReceiver receiver = new InitReceiver();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inflate = inflater.inflate(R.layout.activity_baby_detail, null);
		babyInfoBean =User.curBabys;// (BabyInfoBean) getIntent().getSerializableExtra("data");

		initTitle();
		initView();
		getBaby();
		initReceiver();

		String string="aa";
		String stringa="aa";


		return inflate;
	}

	private void initTitle(){
		tvTitle = (TextView) inflate.findViewById(R.id.title_tv);
		inflate.findViewById(R.id.title_layout).setVisibility(View.GONE);
	}

	private void initView(){
		this.imgHeadIcon = (ImageView)inflate.findViewById(R.id.head_icon_iv);
		this.tvNickName = (TextView)inflate.findViewById(R.id.nick_name_tv);
		this.tvSn = (TextView)inflate.findViewById(R.id.device_sn);
		this.tvBirthday = (TextView)inflate.findViewById(R.id.birthday_tv);
		this.tvSex = (TextView)inflate.findViewById(R.id.sex_tv);
		this.tvHeight = (TextView)inflate.findViewById(R.id.height_u);
		this.tvWeight = (TextView)inflate.findViewById(R.id.weight_u);
		this.tvRelation = (TextView)inflate.findViewById(R.id.relation_tv);
		this.tvPhoneNum = (TextView)inflate.findViewById(R.id.phone_tv);
		this.tvExpireTime = (TextView)inflate.findViewById(R.id.expire_time_tv);
		tvDeviceId = (TextView)inflate.findViewById(R.id.device_id_tv);
		isAdmin = PrefHelper.getBooleanData(PrefHelper.P_ISADMIN);

		RelativeLayout nick_name_rl = (RelativeLayout) inflate.findViewById(R.id.nick_name_rl);
		RelativeLayout two_dimension_code_rl = (RelativeLayout) inflate.findViewById(R.id.two_dimension_code_rl);
		RelativeLayout birthday_rl = (RelativeLayout) inflate.findViewById(R.id.birthday_rl);
		RelativeLayout sex_rl = (RelativeLayout) inflate.findViewById(R.id.sex_rl);
		RelativeLayout height_rl = (RelativeLayout) inflate.findViewById(R.id.height_rl);
		RelativeLayout weight_rl = (RelativeLayout) inflate.findViewById(R.id.weight_rl);
		RelativeLayout relation_rl = (RelativeLayout) inflate.findViewById(R.id.relation_rl);
		RelativeLayout device_rl = (RelativeLayout) inflate.findViewById(R.id.device_rl);
		RelativeLayout expire_time_rl = (RelativeLayout) inflate.findViewById(R.id.expire_time_rl);
		RelativeLayout attention_rl = (RelativeLayout) inflate.findViewById(R.id.attention_rl);


		imgHeadIcon.setOnClickListener(this);
		nick_name_rl.setOnClickListener(this);
		two_dimension_code_rl.setOnClickListener(this);
		birthday_rl.setOnClickListener(this);
		sex_rl.setOnClickListener(this);
		height_rl.setOnClickListener(this);
		weight_rl.setOnClickListener(this);
		relation_rl.setOnClickListener(this);
		device_rl.setOnClickListener(this);
		expire_time_rl.setOnClickListener(this);
		attention_rl.setOnClickListener(this);


		/*if(!isAdmin){
			inflate.findViewById(R.id.arrow1).setVisibility(View.INVISIBLE);
			inflate.findViewById(R.id.arrow2).setVisibility(View.INVISIBLE);
			inflate.findViewById(R.id.arrow3).setVisibility(View.INVISIBLE);
			inflate.findViewById(R.id.arrow4).setVisibility(View.INVISIBLE);
			inflate.findViewById(R.id.arrow5).setVisibility(View.INVISIBLE);
		}*/

		if(Constants.PAY_STATUE){
			inflate.findViewById(R.id.expire_time_rl).setVisibility(View.VISIBLE);
		}

		if(babyInfoBean!=null){
			Height = babyInfoBean.getHeight();
			Weight = babyInfoBean.getWeight();
			tvNickName.setText(babyInfoBean.getNickName());
			tvBirthday.setText(babyInfoBean.getBirthday().replace("/", "-"));
			tvHeight.setText(Height+"cm");
			tvWeight.setText(Weight+"kg");
			tvRelation.setText(babyInfoBean.getRelation());
			tvPhoneNum.setText(babyInfoBean.getPhoneNum());
			//			tvDeviceId.setText(babyInfoBean.getdeviceVersion());
			tvSn.setText(babyInfoBean.getSn());
			if(babyInfoBean.getSex()==1){
				tvSex.setText("男");
			}else{
				tvSex.setText("女");
			}
			sex = babyInfoBean.getSex();
			initIcon();
		}

	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_UPDATE_BABYDETAIL);
		getActivity().registerReceiver(this.receiver, filter);
	}

	private void unRegistReceiver() {
		getActivity().unregisterReceiver(this.receiver);
	}

	class InitReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.ACTION_UPDATE_BABYDETAIL.equals(intent.getAction())) {
				getBaby();
			}
		}
	}

	private void initIcon(){
		if(babyInfoBean==null){
			return;
		}
		if(!TextUtils.isEmpty(babyInfoBean.getHeadIconPath())){
			PhotoPath = imageLoader.getCacheImgFileName(babyInfoBean.getHeadIconPath());
			if (PhotoPath == null)
			{
				Drawable cachedImage = null;
				cachedImage = imageLoader.loadDrawable(babyInfoBean.getHeadIconPath(), getActivity(),
						true, new ImageCallback()
				{

					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl)
					{
						if (imageDrawable != null)
						{
							imgHeadIcon.setImageDrawable(imageDrawable);
						}
						else
						{
							if(babyInfoBean.getSex() == 1)
								imgHeadIcon.setImageResource(R.drawable.icon_boy);
							else
								imgHeadIcon.setImageResource(R.drawable.icon_girl);
						}
					}
				});
				if (cachedImage != null)
				{
					imgHeadIcon.setImageDrawable(cachedImage);

				}
			}
			else
			{
				imgHeadIcon.setImageBitmap(BitmapFactory.decodeFile(PhotoPath));
			}
		}else{
			if(babyInfoBean.getSex() == 1)
				imgHeadIcon.setImageResource(R.drawable.icon_boy);
			else
				imgHeadIcon.setImageResource(R.drawable.icon_girl);
		}
	}


	protected void doTakePhoto()
	{
		try
		{
			// Launch camera to take photo for selected contact
			Constants.PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(Constants.HEADER_DIR, getPhotoFileName());
			startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE").putExtra("output", Uri.fromFile(mCurrentPhotoFile)), 18);
			PhotoPath = mCurrentPhotoFile.getAbsolutePath();

			//			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			//			startActivityForResult(intent, Constants.CAMERA_WITH_DATA);
		}
		catch (ActivityNotFoundException e)
		{
			Toast.makeText(getActivity(), "异常", Toast.LENGTH_LONG).show();
		}
	}

	protected void doPickPhotoFromGallery()
	{
		try
		{
			// Launch picker to choose photo for selected contact
			//			final Intent intent = getPhotoPickIntent();
			//			startActivityForResult(intent, Constants.PHOTO_PICKED_WITH_DATA);
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
			Toast.makeText(getActivity(), "异常", Toast.LENGTH_LONG).show();
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
					getActivity(),
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
			Toast.makeText(getActivity(), "异常", Toast.LENGTH_LONG).show();
		}
	}

	public void selectPictureDialog() {
		Builder builder = new Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(R.string.set_logo);
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.radiobutton_item, null);
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
		DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT, DateSet, Integer.parseInt(str[0]), Integer.parseInt(str[1])-1, Integer.parseInt(str[2]));
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
	private View inflate;

	private void upInfo(){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("NickName", tvNickName.getText().toString()));
		linkedlist.add(new WebServiceProperty("Height", Height));
		linkedlist.add(new WebServiceProperty("DeviceMob", tvPhoneNum.getText().toString()));
		linkedlist.add(new WebServiceProperty("Birthday", tvBirthday.getText().toString()));
		linkedlist.add(new WebServiceProperty("DeviceID", babyInfoBean.getId()));
		linkedlist.add(new WebServiceProperty("Sex", sex));
		linkedlist.add(new WebServiceProperty("Weight", Weight));
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("RelashionNick", tvRelation.getText().toString()));
		WebServiceTask wsk = new WebServiceTask("DeviceEdit", linkedlist, WebService.URL_OTHER,getActivity().getBaseContext(),new WebServiceResult() {

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
						String str=jsonObject.optString("Msg");
						msg=str;
						if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
							//							Intent i = new Intent();
							//							i.setClass(BabyDetailActivity.this, MainFragmentActivity.class);
							//							startActivity(i);
							//setResult(7777);
							//finish();
							return;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				Toast.makeText(getActivity().getBaseContext(), msg, Toast.LENGTH_LONG);
				if(IsAlterPhoto){
					//setResult(7777);
				}
				//finish();
			}
		});
		wsk.execute("DeviceEditResult");
	}


	private void upicon(){
		//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		File file;
		file = new File(PhotoPath);

		//		PhotoBitmap.compress(CompressFormat.PNG, 100, stream);
		//				mBitmap.recycle();
		byte[] imgbuf = CommonUtils.getBytesFromFile(file);
		HttpUploadfile http = new HttpUploadfile(Urls.edit_device_header,"0", babyInfoBean.getId(), imgbuf,"","",
				new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case UPLOAD_USERINFO_SUCCESS:
					Utils.showToast("头像修改成功");
					break;
				case UPLOAD_USERINFO_FAIL:
					Utils.showToast("头像修改失败");
					break;
				}

				if (PhotoPath.contains(CommonUtils.COMPRESS_TEMP_FILE_NAME)) {
					CommonUtils.deleteTempFile(PhotoPath);
				}

			}

		});
	}

	private void unbind(){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("DeviceID", babyInfoBean.getId()));
		linkedlist.add(new WebServiceProperty("GuardianRelashion", "1"));
		WebServiceTask wsk = new WebServiceTask("GuardianDel", linkedlist, WebService.URL_OTHER,getActivity().getBaseContext(),new WebServiceResult() {

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
						String str=jsonObject.optString("Msg");
						msg=str;
						if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
							for(int i=0;i<User.babyslist.size();i++){
								if(User.babyslist.get(i).getId().equals(babyInfoBean.getId())){
									User.babyslist.remove(i);
									break;
								}
							}
							//setResult(6666);
							//finish();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				Toast.makeText(getActivity().getBaseContext(), msg, Toast.LENGTH_LONG);
			}
		});
		wsk.execute("GuardianDelResult");
	}

	private void getBaby(){
		CommonUtils.showProgress(getActivity(), "正在加载...",null);
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID",babyInfoBean.getId()));
		linkedlist.add(new WebServiceProperty("UserID",User.id));
		WebServiceTask wsk = new WebServiceTask("GetDeviceDetail", linkedlist, WebService.URL_OTHER,getActivity().getBaseContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				CommonUtils.closeProgress();
				String msg;
				if(result!=null){//错误信息
					msg=result;
				}
				else{//正确信息
					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=jsonObject.optString("Msg");
						msg=str;
						if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
							String userInfoStr = jsonObject.optString("info");
							JSONObject beaninfo = new JSONObject(userInfoStr);
							parse(beaninfo);
						}
						else{
							Utils.showToast(str);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
			}
		});
		wsk.execute("GetDeviceDetailResult");
	}

	private void parse(JSONObject jsonobject){
		//babyInfoBean = new BabyInfoBean();
		babyInfoBean.setPhoneNum(jsonobject.optString("Mob"));
		babyInfoBean.setNickName(jsonobject.optString("DeviceNick"));
		babyInfoBean.setBirthday(jsonobject.optString("Birthday"));
		babyInfoBean.setHeadIconPath(jsonobject.optString("HeaderPicUrl"));
		if(TextUtils.isEmpty(jsonobject.optString("Sex"))){
			babyInfoBean.setSex(0);
		}else
			babyInfoBean.setSex(Integer.parseInt(jsonobject.optString("Sex")));
		babyInfoBean.setHeight(jsonobject.optString("Height"));
		babyInfoBean.setWeight(jsonobject.optString("Weight"));
		babyInfoBean.setRelation(jsonobject.optString("RelashionNick"));
		babyInfoBean.setSn(jsonobject.optString("SN"));
		babyInfoBean.setId(babyInfoBean.getId());
		babyInfoBean.setIsAdmin(jsonobject.optInt("IsAdmin")==1?true:false);
		babyInfoBean.setstatusmosq(jsonobject.optString("QuWen"));
		babyInfoBean.setdeviceVersion(jsonobject.optString("Version"));
		babyInfoBean.setdeviceTime(jsonobject.optString("HireExpireDate"));
		//User.curBabys = babyInfoBean;

		Height = babyInfoBean.getHeight();
		Weight = babyInfoBean.getWeight();
		tvNickName.setText(babyInfoBean.getNickName());
		tvBirthday.setText(babyInfoBean.getBirthday().replace("/", "-"));
		tvHeight.setText(Height+"cm");
		tvWeight.setText(Weight+"kg");
		tvRelation.setText(babyInfoBean.getRelation());
		tvPhoneNum.setText(babyInfoBean.getPhoneNum());
		tvDeviceId.setText(babyInfoBean.getdeviceVersion());
		tvSn.setText(babyInfoBean.getSn());
		tvExpireTime.setText(babyInfoBean.getdeviceTime().replace("/", "-"));
		if(babyInfoBean.getSex()==1){
			tvSex.setText("男");
		}else{
			tvSex.setText("女");
		}
		sex = babyInfoBean.getSex();
		initIcon();
	}

	private void unBind(boolean isAdmin) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.admin_unbind_popup, null);
		final PopupWindow popup = new PopupWindow(view, -1, -1);
		TextView tvMessage = (TextView) view.findViewById(R.id.message_tv);
		CheckBox cbox = (CheckBox) view.findViewById(R.id.cbox);
		Button btnConfir = (Button) view.findViewById(R.id.confir_btn);
		Button btnCancel = (Button) view.findViewById(R.id.cancel_btn);
		if (isAdmin) {
			tvMessage.setText(R.string.admin_unbind_content);
			cbox.setVisibility(view.GONE);
		} else {
			tvMessage.setText(R.string.unbind_content);
			cbox.setVisibility(view.GONE);
		}
		btnConfir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				unbind();
				popup.dismiss();

			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popup.dismiss();
			}
		});
		view.findViewById(R.id.popup_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popup.dismiss();
			}
		});
		popup.setBackgroundDrawable(new ColorDrawable(FLAG_NICK_NAME));
		popup.setOutsideTouchable(true);
		popup.showAtLocation(this.tvTitle, 17, FLAG_NICK_NAME, FLAG_NICK_NAME);
	}

	private void setHeadIcon(String path) {
		this.PhotoPath = path;
		Bitmap bmp = CommonUtils.getSmallBitmap(this.PhotoPath);
		if (bmp != null) {
			this.imgHeadIcon.setImageBitmap(bmp);
		}
		String tempFilePath = CommonUtils.compressLocalPic(getActivity(), this.PhotoPath);
		if (!TextUtils.isEmpty(tempFilePath)) {
			CommonUtils.deleteTempFile(PhotoPath);
			PhotoPath = tempFilePath;
		}
		upicon();
	}

	private void Showdevice_id(String str){
		if(TextUtils.isEmpty(str))
			return;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		builder.setMessage(str);
		builder.setPositiveButton("确定",  new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.head_icon_iv:
			if(isAdmin)
				selectPictureDialog();
			break;
		case R.id.nick_name_rl:
			if (BabydetailFragment.this.isAdmin) {
				Intent intent = new Intent(getActivity(), BabyDetailUpdateActivity.class);
				intent.putExtra("flag", FLAG_NICK_NAME);
				intent.putExtra("text",tvNickName.getText().toString());
				intent.putExtra("maxLength", 6);
				startActivityForResult(intent, FLAG_HEIGHT);
			}
			break;
		case R.id.two_dimension_code_rl:
			Intent intent2 = new Intent(getActivity(), CaptureActivity.class);
			intent2.putExtra("qr_text",babyInfoBean.getSn());
			intent2.putExtra("IMEI",babyInfoBean.getSn());
			startActivity(intent2);
			break;
			/*case R.id.phone_rl:
			if (BabydetailFragment.this.isAdmin) {
				Intent intent = new Intent(getActivity(), BabyDetailUpdateActivity.class);
				intent.putExtra("flag", FLAG_PHONE);
				intent.putExtra("text",tvPhoneNum.getText().toString());
				intent.putExtra("maxLength", 11);
				startActivityForResult(intent, FLAG_HEIGHT);
			}
			break;*/
		case R.id.birthday_rl:
			if (BabydetailFragment.this.isAdmin)
				showDatePicker();
			break;
		case R.id.sex_rl:
			if (BabydetailFragment.this.isAdmin) {
				startActivityForResult(new Intent(getActivity(), BabySex.class).putExtra("sex", BabydetailFragment.this.sex), FLAG_HEIGHT);
			}
			break;
		case R.id.height_rl:
			if (BabydetailFragment.this.isAdmin) {
				Intent intent = new Intent(getActivity(), BabyDetailUpdateActivity.class);
				intent.putExtra("flag", FLAG_HEIGHT);
				intent.putExtra("text",Height);
				intent.putExtra("maxLength", FLAG_PHONE);
				startActivityForResult(intent, FLAG_HEIGHT);
			}
			break;
		case R.id.weight_rl:
			if (BabydetailFragment.this.isAdmin) {
				Intent intent = new Intent(getActivity(), BabyDetailUpdateActivity.class);
				intent.putExtra("flag", FLAG_WEIGHT);
				intent.putExtra("text",Weight);
				intent.putExtra("maxLength", FLAG_PHONE);
				startActivityForResult(intent, FLAG_HEIGHT);
			}
			break;
		case R.id.relation_rl:
			Intent intent = new Intent(getActivity(), BabyRelationActivity.class);
			intent.putExtra("relation", tvRelation.getText().toString());
			startActivityForResult(intent, FLAG_HEIGHT);
			break;
		case R.id.device_rl:
			Showdevice_id(tvDeviceId.getText().toString());
			break;
		case R.id.expire_time_rl:
			startActivity(new Intent(getActivity(), PayActivity.class));
			break;
		case R.id.attention_rl:
			//			unbind();
			unBind(isAdmin);
			break;
		default:
			break;
		}


	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

			if (data == null)
				return;

			Bitmap photo = data.getParcelableExtra("data");
			if (null == photo)
			{
				String path = data.getStringExtra("croppedpath");
				photo = BitmapFactory.decodeFile(path);
				PhotoPath = path;
			}
			PhotoBitmap = photo;
			imgHeadIcon.setImageBitmap(photo);
			IsAlterPhoto = true;
			upicon();
			break;
		}

		case 19:
		{
			Uri selectedImage = data.getData();
			if (selectedImage != null) {
				String picPath = CommonUtils.getPicPathByUri(getActivity(), selectedImage);
				if (TextUtils.isEmpty(picPath)) {
					Utils.showToast(R.string.cant_find_pictures);
				} else {
					setHeadIcon(picPath);
				}
			}

			//			if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists())
			//				doCropPhoto(mCurrentPhotoFile);
			break;
		}

		}

		if (resultCode == 666 && data != null) {
			int flag = data.getIntExtra("flag", -1);
			String value = data.getStringExtra("value");
			switch (flag) {
			case FLAG_NICK_NAME :
				this.tvNickName.setText(value);
				break;
			case FLAG_HEIGHT :
				this.tvHeight.setText(value+"cm");
				Height = value;
				break;
			case FLAG_WEIGHT :
				this.tvWeight.setText(value+"kg");
				Weight = value;
				break;
			case FLAG_PHONE :
				this.tvPhoneNum.setText(value);
				break;

			}
		}else if (resultCode == 667 && data != null) {
			sex = data.getIntExtra("sex", FLAG_HEIGHT);
			if (this.sex == FLAG_HEIGHT) {
				this.tvSex.setText(R.string.man);
			} else {
				this.tvSex.setText(R.string.lula);
			}
		}else if (resultCode == 668 && data != null) {
			String relation = data.getStringExtra("relation");
			this.tvRelation.setText(relation);
		}
	}


	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			upInfo();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}*/

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegistReceiver();
		upInfo();
	}

}
