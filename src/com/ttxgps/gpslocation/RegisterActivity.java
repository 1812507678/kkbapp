package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xtst.gps.R;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.User;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Deviceinf;
import com.ttxgps.utils.MySSLSocketFactory;
import com.ttxgps.utils.Urls;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.utils.RegularUtils;

public class RegisterActivity extends BaseActivity {
	EditText phone;
	EditText code;
	TextView getCode;
	View login_status;
	EditText password,submit_pwd;

	int times = 120;
	Runnable reSendRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initTitle();
		initView();
	}

	private void initView(){
		phone = (EditText) findViewById(R.id.account_edt);
		code = (EditText)findViewById(R.id.ver_code_edt);
		getCode = (TextView)findViewById(R.id.verCode_btn);
		login_status = findViewById(R.id.login_status);
		password = (EditText)findViewById(R.id.password_edt);
		submit_pwd = (EditText)findViewById(R.id.submit_pwd_edt);

		getCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getVerCode();
			}
		});
	}

	private void initTitle() {
		((TextView) findViewById(R.id.title_tv)).setText("帐号注册");
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	public void doReg() {
		String phone_t = phone.getText().toString();
		String code_t = code.getText().toString();
		String pwd = password.getText().toString();
		String s_pwd = submit_pwd.getText().toString();
		if (!verification_phone(phone_t)) {
			Utils.showToast("请输入正确的手机号");
			return;
		}
		if(code_t.isEmpty()){
			Utils.showToast("请输入验证码");
			return;
		}
		if(pwd.isEmpty()||s_pwd.isEmpty()){
			Utils.showToast("请输入密码");
			return;
		}
		if(!pwd.equals(s_pwd)){
			Utils.showToast("两个密码不一样");
			return;
		}
		RegRequst(phone_t,pwd,code_t);
	}


	private void RegRequst(final String phone,final String pwd,String code){
		CommonUtils.showProgress(this, "正在注册...",null);
		String imei =  Deviceinf.getDeviceId(getBaseContext());
		String imsi =  Deviceinf.getSubscriberId(getBaseContext());
		if(TextUtils.isEmpty(imei))
			imei = "";
		if(TextUtils.isEmpty(imsi))
			imsi = "";
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Mob",phone));
		linkedlist.add(new WebServiceProperty("Pwd", pwd));
		linkedlist.add(new WebServiceProperty("Num", code));
		linkedlist.add(new WebServiceProperty("Imei", imei));
		linkedlist.add(new WebServiceProperty("Imsi", imsi));
		WebServiceTask wsk = new WebServiceTask("Regist", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
						String str=(String) jsonObject.get("Msg");
						msg=str;
						if (jsonObject.has("Status") && jsonObject.getString("Status").equals("0")) {
							User.SaveUserShared(getBaseContext(), "LoginName", phone);
							User.SaveUserShared(getBaseContext(), "PassWord", pwd);
							User.SaveUserShared(getBaseContext(), "Phone", phone);
							User.SaveUserShared(getBaseContext(), "Userid", jsonObject.getString("Userid"));
							Intent i = new Intent();
							i.setClass(RegisterActivity.this, LoginBindDeviceActivity.class);
							startActivityForResult(i, 100);
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
		wsk.execute("RegistResult");
	}

	private void getCode(){
		CommonUtils.showProgress(this, "请求中...",null);
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Mob",phone.getText().toString()));
		linkedlist.add(new WebServiceProperty("Type","1"));

		WebServiceTask wsk = new WebServiceTask("SendNumToMobile", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
						String str=(String) jsonObject.get("Msg");
						msg=str;
						if (jsonObject.has("Status") && jsonObject.getString("Status").equals("0")) {
							setReSendBtn();
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
		wsk.execute("SendNumToMobileResult");
	}

	private void getVerCode()
	{
		String str = this.phone.getText().toString();
		if (TextUtils.isEmpty(str.trim()))
		{
			Utils.showToast(R.string.input_phone_email);
			return;
		}
		if (verification_phone(str))
		{
			getCode();
			return;
		}

		Utils.showToast(R.string.input_correct_phone_num);
	}

	private void setReSendBtn()
	{
		this.getCode.setText(getResources().getString(R.string.resend_validation_request, new Object[] { Integer.valueOf(this.times) }));
		this.getCode.setClickable(false);
		this.reSendRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				RegisterActivity localRegisterActivity = RegisterActivity.this;
				localRegisterActivity.times -= 1;
				if (RegisterActivity.this.times <= 0)
				{
					RegisterActivity.this.getCode.setText(RegisterActivity.this.getResources().getString(R.string.resend_validation_request, new Object[] { "" }));
					RegisterActivity.this.getCode.setClickable(true);
					RegisterActivity.this.times = 120;
					return;
				}
				RegisterActivity.this.getCode.setText(RegisterActivity.this.getResources().getString(R.string.resend_validation_request, new Object[] { RegisterActivity.this.times + RegisterActivity.this.getString(R.string.count_down) }));
				RegisterActivity.this.getCode.setClickable(false);
				RegisterActivity.this.getCode.postDelayed(this, 1000L);
			}
		};
		this.getCode.postDelayed(this.reSendRunnable, 1000L);
	}


	public boolean verification_phone(String phone) {

		return RegularUtils.getPhone(phone);
	}

	public boolean verification_email(String email) {

		return RegularUtils.getEmail(email);
	}

	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.next_btn:
			doReg();
			break;
		case R.id.verCode_btn:
			getVerCode();
			break;
		case R.id.cancel_btn:
			finish();
			break;

		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100){
			setResult(100);
			finish();
		}
	}
}
