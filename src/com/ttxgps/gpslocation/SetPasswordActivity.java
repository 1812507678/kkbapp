package com.ttxgps.gpslocation;
  
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.utils.MySSLSocketFactory;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.Urls;
import com.xtst.gps.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetPasswordActivity extends BaseActivity implements OnClickListener{

	EditText password,submit_pwd;
	Button del_account_pwd,del_pwd,next_btn;
	String phone;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setpassword);
		this.phone = getIntent().getStringExtra("account_text");
		initTitle();
		initView();
	}

	private void initView(){
		password = (EditText)findViewById(R.id.password_edt);
		del_account_pwd = (Button)findViewById(R.id.main_login_account_del);
		submit_pwd = (EditText)findViewById(R.id.submit_pwd_edt);
		del_pwd = (Button)findViewById(R.id.main_login_pwd_del);
		next_btn = (Button)findViewById(R.id.next_btn);
		del_account_pwd.setOnClickListener(this);
		del_pwd.setOnClickListener(this);
		next_btn.setOnClickListener(this);
	}

	private void initTitle() {
		((TextView) findViewById(R.id.title_tv)).setText(R.string.set_pwd_title);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	/**
	 * 
	 */
	public class RegRequst extends AsyncTask<String, String, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			// param.add(new BasicNameValuePair("mobile", Build.MODEL));
			param.add(new BasicNameValuePair("name", params[0]));
			param.add(new BasicNameValuePair("pwd", params[1]));
			param.add(new BasicNameValuePair("phone", params[2]));
			param.add(new BasicNameValuePair("email", params[3]));
			try {
				String json = MySSLSocketFactory.getinfo(Urls.reg_url, param);
				JSONObject jsonobj = new JSONObject(json);
				if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
					PrefHelper.setInfo(PrefHelper.P_LOGIN_NAME, params[0]);
					PrefHelper.setInfo(PrefHelper.P_PWD_TEXT, params[1]);
					PrefHelper.setInfo(PrefHelper.P_LOGIN_STATE, true);
					PrefHelper.setInfo(PrefHelper.P_USER_ID,
							jsonobj.getString("uid"));
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result) {
				Intent i = new Intent();
				i.setClass(SetPasswordActivity.this, LoginBindDeviceActivity.class);
				startActivity(i);
				finish();
				// mLoginStatusView.setVisibility(View.VISIBLE);
			} else {
				Utils.showToast("");
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_login_account_del:
			password.setText("");
			break;
		case R.id.main_login_pwd_del:
			submit_pwd.setText("");
			break;
		case R.id.next_btn:
			//			new RegRequst().execute("");
			Intent i = new Intent();
			i.setClass(SetPasswordActivity.this, LoginBindDeviceActivity.class);
			startActivity(i);
			finish();
			break;
		default:
			break;
		}
	}
}
