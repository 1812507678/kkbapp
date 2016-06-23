package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.phone.mrpc.core.v;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.gpslocation.MyTrace_More_Baidu.initData;
import com.ttxgps.utils.MySSLSocketFactory;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.Urls;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.ttxgps.zxing.core.CaptureActivity;
import com.xtst.gps.R;



public class LoginBindDeviceActivity extends BaseActivity implements OnClickListener{

	Button btnNext;
	ImageView iv_showPopup;
	EditText imei_id;
	EditText imei_type;
	TextView scan_code;
	private String[] typeList;

	private String SN;
	private String imeiTypeValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device);
		initTitle();
		initView();
		initData();
	}

	private void initData() {
		typeList = new String[]{"鞋","书包","宠物","手表","机器人","智能家居","自行车"};

	}

	private void initTitle() {
		((TextView) findViewById(R.id.title_tv)).setText("绑定设备");
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	private void initView(){
		btnNext = (Button)findViewById(R.id.next_btn);
		iv_showPopup = (ImageView)findViewById(R.id.iv_showPopup);
		imei_id = (EditText)findViewById(R.id.imei_id);
		imei_type = (EditText)findViewById(R.id.imei_type);
		scan_code = (TextView)findViewById(R.id.scan_code);



		btnNext.setOnClickListener(this);
		scan_code.setOnClickListener(this);
		iv_showPopup.setOnClickListener(this);

	}

	public void doReg() {
		SN = imei_id.getText().toString();
		imeiTypeValue = imei_type.getText().toString();

		Log.i("LoginBindDeviceActivity", imeiTypeValue);

		if(SN.isEmpty()){
			Utils.showToast("请输入设备号");
			return;
		}
		if(imeiTypeValue.isEmpty()){
			Utils.showToast("请选择设备类型");
			return;
		}
		bindDevice();
	}

	private void bindDevice(){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("strSN", SN));
		linkedlist.add(new WebServiceProperty("UserID", PrefHelper.getStringData(PrefHelper.P_USER_ID)));
		WebServiceTask wsk = new WebServiceTask("BindDevice", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
						if (jsonObject.has("Status") && jsonObject.optString("Status").equals("0")) {
							PrefHelper.setInfo(PrefHelper.P_LOGIN_STATE, true);
							Intent i = new Intent();

							i.putExtra("deviceID", jsonObject.optString("DeviceID"));
							//i.putExtra("SN", SN);
							i.putExtra("imeiTypeValue", imeiTypeValue);
							//i.setClass(LoginBindDeviceActivity.this, LoginEditBabyInfoActivity.class);
							i.setClass(LoginBindDeviceActivity.this, ThridStepSetting.class);
							startActivity(i);
							setResult(100);
							finish();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
		wsk.execute("BindDeviceResult");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.next_btn:
			doReg();
			//			Intent i = new Intent();
			//			i.setClass(LoginBindDeviceActivity.this, LoginEditBabyInfoActivity.class);
			//			startActivity(i);
			//			finish();
			break;
		case R.id.scan_code:
			startActivityForResult(new Intent(getBaseContext(), CaptureActivity.class), 100);
			break;
		case R.id.iv_showPopup:
			showPopup();
			break;
		default:
			break;
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1 && data != null) {
			this.imei_id.setText(data.getStringExtra("barcode"));
		}

	}

	public void showPopup() {
		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(this).inflate(R.layout.pop_window, null);
		ListView lv_popwindow = (ListView) contentView.findViewById(R.id.lv_popwindow);
		lv_popwindow.setAdapter(new PopwindowListAdapter());

		final PopupWindow popupWindow = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		//设置如下四条信息，当点击其他区域使其隐藏，要在show之前配置
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		// 设置好参数之后再show,0为x方向偏移量，20为y方向偏移量
		popupWindow.showAsDropDown(iv_showPopup,0,20);

		lv_popwindow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				imei_type.setText(typeList[position]);
				//点击后popupWindow消失
				popupWindow.dismiss();
			}
		});
	}

	class PopwindowListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return typeList.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TextView textView = new TextView(LoginBindDeviceActivity.this);
			//textView.setBackgroundResource(R.drawable.bg_type);
			//LayoutParams layoutParams = new LayoutParams(300, 100);
			//textView.setLayoutParams(layoutParams);
			textView.setTextSize(20);
			textView.setTextColor(Color.WHITE);
			textView.setGravity(Gravity.CENTER);
			textView.setText(typeList[position]);
			return textView;
		}
	}

}

