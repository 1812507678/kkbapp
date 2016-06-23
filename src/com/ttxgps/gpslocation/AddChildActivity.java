package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xtst.gps.R;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.TeamMember;
import com.ttxgps.utils.MySSLSocketFactory;
import com.ttxgps.utils.Urls;
import com.ttxgps.utils.PrefHelper;
import com.utils.RegularUtils;

public class AddChildActivity extends Activity {
	ImageView reg_photo;
	EditText nick;
	EditText age;
	RadioGroup gender;
	EditText back;
	View login_status;
	View gps_state;
	ImageView gps_state_flag;
	EditText gps_number;
	EditText gps_pwd;
	boolean isAddNew = true;
	public String mid;
	TextView state_text;
	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addchild);
		tv = (TextView) findViewById(R.id.gps_info);
		gps_number = (EditText) findViewById(R.id.gps_number);
		gps_pwd = (EditText) findViewById(R.id.gps_pwd);
		reg_photo = (ImageView) findViewById(R.id.reg_photo);
		nick = (EditText) findViewById(R.id.reg_nick);
		gender = (RadioGroup) findViewById(R.id.gender_selector);
		age = (EditText) findViewById(R.id.reg_age);
		back = (EditText) findViewById(R.id.reg_back);
		login_status = findViewById(R.id.login_status);
		gps_state = findViewById(R.id.gps_state);
		gps_state_flag = (ImageView) findViewById(R.id.gps_state_flag);
		mid = getIntent().getStringExtra("mid");
		if (mid == null) {
			isAddNew = true;
		} else {
			findViewById(R.id.gps_state_control).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
						}
					});
			TextView title = (TextView) findViewById(R.id.title);
			title.setText("会员用户");

			tv.setText("已绑定");
			new InitData().execute(mid);
			isAddNew = false;
		}
		// gender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

	public class InitData extends AsyncTask<String, String, TeamMember> {

		@Override
		protected TeamMember doInBackground(String... params) {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			// param.add(new BasicNameValuePair("mobile", Build.MODEL));
			param.add(new BasicNameValuePair("serviceid", PrefHelper
					.getStringData(PrefHelper.P_USER_ID)));
			param.add(new BasicNameValuePair("memberid", params[0]));
			try {
				String json = MySSLSocketFactory.getinfo(
						Urls.vip_bcs_getmember, param);
				JSONObject jsonobj = new JSONObject(json);
				if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
					// CommonUtils.showToast("删除成功");
					TeamMember item = new TeamMember();
					JSONObject obj = new JSONObject(jsonobj.getString("info"));
					// {
					// “success”: true
					// “info”:{
					// “type”:1,
					// “name”:”父亲”,
					// “sex”:1,
					// “age”:68,
					// “remark”:”我父亲年老”,
					// “mno”:”渝B7A398”
					// }
					// }

					item.type = obj.getString("type");
					item.name = obj.getString("name");
					item.gender = obj.getString("sex");
					item.address = obj.getString("remark");
					item.lat = obj.getString("age");
					item.status = obj.getString("mno");
					return item;
				} else {
					Utils.showToast("读取失败");
				}
			} catch (Exception e) {
				Utils.showToast("读取失败");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(TeamMember item) {
			// TODO Auto-generated method stub
			if (item != null) {
				gps_number.setText(item.status);
				nick.setText(item.name);
				age.setText(item.lat);
				back.setText(item.address);
				tv.setText(item.status);
				if ("1".equals(item.gender)) {
					gender.check(R.id.man);
				} else {
					gender.check(R.id.ld);
				}
			}
			super.onPostExecute(item);
		}
	}

	public void things(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.sign_add_button:
			doReg();
			break;
		case R.id.gps_state_control:
			if (gps_state.getVisibility() == View.VISIBLE) {
				gps_state.setVisibility(View.GONE);
			} else {
				gps_state.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	public void doReg() {
		String mno = gps_number.getText().toString();
		String pwd = gps_pwd.getText().toString();
		String type = "1";
		String name = nick.getText().toString();
		String sex = "1";
		if (R.id.man == gender.getCheckedRadioButtonId()) {
			sex = "1";
		} else {
			sex = "0";
		}
		String age = this.age.getText().toString();
		String remark = back.getText().toString();
		if ("".equals(mno) || "".equals(name)) {
			Utils.showToast("信息不能为空");
			return;
		}
		login_status.setVisibility(View.VISIBLE);
		new RegRequst().execute(mno, pwd, type, name, sex, age, remark);
	}

	public class RegRequst extends AsyncTask<String, String, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			// param.add(new BasicNameValuePair("mobile", Build.MODEL));
			param.add(new BasicNameValuePair("mno", params[0]));
			param.add(new BasicNameValuePair("pwd", params[1]));
			param.add(new BasicNameValuePair("type", params[2]));
			param.add(new BasicNameValuePair("name", params[3]));
			param.add(new BasicNameValuePair("sex", params[4]));
			param.add(new BasicNameValuePair("age", params[5]));
			param.add(new BasicNameValuePair("remark", params[6]));
			param.add(new BasicNameValuePair("serviceid", PrefHelper
					.getStringData(PrefHelper.P_USER_ID)));
			String url = Urls.vip_bcs_addmember;
			if (!isAddNew) {
				param.add(new BasicNameValuePair("memberid", mid));
				url = Urls.vip_bcs_editmember;
			}
			try {
				String json = MySSLSocketFactory.getinfo(url, param);
				JSONObject jsonobj = new JSONObject(json);
				if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
					// PrefHelper.setInfo(PrefHelper.P_NAME_TEXT, params[0]);
					// PrefHelper.setInfo(PrefHelper.P_PWD_TEXT, params[1]);
					// PrefHelper.setInfo(PrefHelper.P_LOGIN_STATE, true);
					// PrefHelper.setInfo(PrefHelper.P_USER_ID,
					// jsonobj.getString("uid"));
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
				// Intent i = new Intent();
				// i.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				// i.setClass(RegisterActivity.this, TeamActivity.class);
				// startActivity(i);
				if (!isAddNew) {
					Utils.showToast("修改成功");
				} else {
					Utils.showToast("添加成功");
				}
				setResult(1, getIntent());
				finish();

				// mLoginStatusView.setVisibility(View.VISIBLE);
			} else {
				if (!isAddNew) {
					Utils.showToast("修改失败");
				} else {
					Utils.showToast("添加失败");
				}

			}
			login_status.setVisibility(View.GONE);
		}
	}

	public boolean verification_phone(String phone) {

		return RegularUtils.getPhone(phone);
	}

	public boolean verification_email(String email) {

		return RegularUtils.getEmail(email);
	}
}
