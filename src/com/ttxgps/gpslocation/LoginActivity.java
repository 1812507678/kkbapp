package com.ttxgps.gpslocation;
import java.io.File;
import java.util.LinkedList;

import org.android.agoo.IService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.palmtrends.app.ShareApplication;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.User;
import com.ttxgps.utils.AsyncHttpUtil;
import com.ttxgps.utils.AsyncHttpUtil.JsonHttpHandler;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Deviceinf;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.Urls;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;
import com.xtst.gps.R;

public class LoginActivity extends BaseActivity implements OnClickListener{

	public static int userID = -1;
	public static int deviceID = -1;
	public static String timeZone = "China Standard Time";
	public static boolean newLogin = false;


	private JSONObject jsonObject;
	private JSONObject jOTemp;


	// Values for email and password at the time of the login attempt.
	private String mLoginName;
	private String mPassword;
	private int userType;

	// UI references.
	private EditText mLoginNameView;
	private EditText mPasswordView;
	private Button mClearAccountBtn;
	private Button mClearPwBtn;

	//	private View mLoginStatusView;
	//	private TextView mLoginStatusMessageView;
	//	private RadioButton person_button;
	//	private RadioButton device_button;
	private Button login_button;

	private TextView forgin_pwd,register_tv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		PushAgent mPushAgent = PushAgent.getInstance(getBaseContext());
		mPushAgent.enable();
		UmengUpdateAgent.update(this);
		User.GetUserData(getBaseContext());

		File file = new File(Constants.HEALTH_ROOD);
		if (!file.exists())
		{
			file.mkdirs();
		}
		file = new File(Constants.HEADER_DIR);
		if (!file.exists())
		{
			file.mkdirs();
		}
		file = new File(Constants.VOICE_DIR);
		if (!file.exists())
		{
			file.mkdirs();
		}

		// 0: user ; 1:Device
		userType = PrefHelper.getIntData(PrefHelper.P_TYPE_ID);

		//		if (userType == 0 || userType == -1) {
		//			person_button.setChecked(true);
		//		} else {
		//			device_button.setChecked(true);
		//		}

		// Set up name and password
		mLoginNameView = (EditText) findViewById(R.id.main_login_account);


		mPasswordView = (EditText) findViewById(R.id.main_login_pwd);


		login_button = (Button) findViewById(R.id.main_login_login);

		forgin_pwd =(TextView)findViewById(R.id.main_login_forgin_pwd);
		register_tv = (TextView)findViewById(R.id.register_tv);
		forgin_pwd.setOnClickListener(this);
		register_tv.setOnClickListener(this);

		this.mClearAccountBtn = (Button) findViewById(R.id.main_login_account_del);
		this.mClearPwBtn = (Button) findViewById(R.id.main_login_pwd_del);
		this.mClearAccountBtn.setOnClickListener(this);
		this.mClearPwBtn.setOnClickListener(this);

		this.mLoginNameView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s == null || s.length() <= 0) {
					LoginActivity.this.mClearAccountBtn.setVisibility(View.INVISIBLE);
				} else {
					LoginActivity.this.mClearAccountBtn.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		this.mPasswordView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s == null || s.length() <= 0) {
					LoginActivity.this.mClearPwBtn.setVisibility(View.INVISIBLE);
				} else {
					LoginActivity.this.mClearPwBtn.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mLoginName = User.getLoginName(getBaseContext());//PrefHelper.getStringData(PrefHelper.P_LOGIN_NAME);
		if(!TextUtils.isEmpty(mLoginName))
			mLoginNameView.setText(mLoginName);

		mPassword = User.getPwd(getBaseContext());//PrefHelper.getStringData(PrefHelper.P_PWD_TEXT);
		if(!TextUtils.isEmpty(mPassword))
			mPasswordView.setText(mPassword);

		add_listener();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		ShareApplication.flag = true;
		super.onResume();
	}

	public void add_listener() {

		// Save password
		/*	login_password_save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				PrefHelper.setInfo(PrefHelper.P_PWD_STATE, isChecked);
			}
		});

		// Auto login
		login_loginstate_save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PrefHelper.setInfo(PrefHelper.P_AUTOLOGIN_STATE, isChecked);
			}
		});*/

		// Password Input
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) { /*
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				} */
				return false;
			}
		});

		// Login
		login_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		if (User.getIsNoLogin(getBaseContext()) &&
				!"".equals(mLoginName) &&
				!"".equals(mPassword)) {
			//			mLoginStatusView.setVisibility(View.VISIBLE);
			//			mLoginStatusMessageView.setText(getString(R.string.account) + mLoginName +
			//					" " + getString(R.string.logging_in));
			CommonUtils.showProgress(LoginActivity.this, "正在登录...",null);
			login();
			return;
		}
	}

	private int get_user_type() {
		int userType = 2;
		//		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
		//		switch (group.getCheckedRadioButtonId()) {
		//		case R.id.login_radio_person:
		//			userType = 0;
		//			break;
		//		case R.id.login_radio_device:
		//			userType = 1;
		//			break;
		//		}
		PrefHelper.setInfo(PrefHelper.P_TYPE_ID, userType);
		return userType;
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mLoginNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mLoginName = mLoginNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (TextUtils.isEmpty(mLoginName)) {
			// mEmailView.setError(getString(R.string.error_field_required));
			Utils.showToast(R.string.empty_account);
			focusView = mLoginNameView;
			cancel = true;

		}
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			// mPasswordView.setError(getString(R.string.error_field_required));
			Utils.showToast(R.string.empty_password);
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			// mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			Utils.showToast(R.string.error_wrong_password);
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			//			mLoginStatusMessageView.setText(R.string.logging_in);
			//			mLoginStatusView.setVisibility(View.VISIBLE);
			CommonUtils.showProgress(LoginActivity.this, "正在登录...",null);
			login();
			//			RequestParams params = new RequestParams();
			//			params.put("Name", mLoginName);
			//			params.put("Pass", mPassword);
			//			params.put("LoginType", 2);
			//			AsyncHttpUtil.get("http://www.baidu.com/", params, loginJsonHttpHandler);
		}
	}

	private void login(){
		userType = get_user_type();
		String imei =  Deviceinf.getDeviceId(getBaseContext());
		String imsi =  Deviceinf.getSubscriberId(getBaseContext());
		if(TextUtils.isEmpty(imei))
			imei = "";
		if(TextUtils.isEmpty(imsi))
			imsi = "";
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Name", mLoginName));
		linkedlist.add(new WebServiceProperty("Pass", mPassword));
		linkedlist.add(new WebServiceProperty("LoginType", userType));
		linkedlist.add(new WebServiceProperty("Imei", imei));
		linkedlist.add(new WebServiceProperty("Imsi", imsi));
		linkedlist.add(new WebServiceProperty("Platform", "android"));

		WebServiceTask wsk = new WebServiceTask("Login", linkedlist, WebService.URL_OPEN,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub

				if(result!=null){
					Utils.showToast(result);
				}
				else{
					try {
						jsonObject = new JSONObject(data);
						if (jsonObject.has(Constants.STATUS) && jsonObject.optString(Constants.STATUS).equals("0")) {
							// Save user info from Server if Login successfully
							User user = new User(getBaseContext());
							user.loginName = mLoginName;
							user.LoginPwd = mPassword;
							String userInfoStr = jsonObject.optString("userInfo");
							jOTemp = new JSONObject(userInfoStr);
							user.id = jOTemp.optString("userID");
							user.niceName = jOTemp.optString("userName");
							user.sex = jOTemp.optString("sex");
							user.timeZone = jOTemp.optString("timeZone");
							user.phone = jOTemp.optString("mobile");
							user.headerurl = jOTemp.optString("picurl");
							user.Signature = jOTemp.optString("signature");
							user.SN = jOTemp.optString("SN");
							user.email = jOTemp.optString("smsemail");
							user.isLogin = true;
							user.isSavepwd = true;

							user.putUser(user);
							User.GetUserData(getBaseContext());

							newLogin = true;

							new Thread(){

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(jOTemp.optString("hasdevice").equals("0")){
										CommonUtils.closeProgress();
										PrefHelper.setInfo(PrefHelper.P_USER_ID, jOTemp.optString("userID"));
										User.SaveUserShared(getBaseContext(), "LoginName", mLoginName);
										User.SaveUserShared(getBaseContext(), "PassWord", mPassword);
										startActivityForResult(new Intent(getBaseContext(), LoginBindDeviceActivity.class),100);
										Utils.showToast("请先绑定设备");
										return;
									}

									User.getDevicesList(getApplicationContext());

									if(User.babyslist==null||User.babyslist.size()==0){
										PrefHelper.setInfo(PrefHelper.P_USER_ID, jOTemp.optString("userID"));
										startActivityForResult(new Intent(getBaseContext(), LoginBindDeviceActivity.class),100);
										//Utils.showToast(jsonObject.optString("Msg"));
									}
									else{
										if(!DevicesUtils.getcurDevice(getBaseContext()))
											User.curBabys=User.babyslist.get(0);
										if(User.curBabys.getIsAdmin()){
											User.SaveUserSharedBoolean(getBaseContext(), Constants.KEY_IS_ADMIN, true);
										}else{
											User.SaveUserSharedBoolean(getBaseContext(), Constants.KEY_IS_ADMIN, false);
										}
										Intent teamActivity = new Intent();
										teamActivity.setClass(LoginActivity.this, MainFragmentActivity.class);
										startActivity(teamActivity);
										finish();
									}


								}


							}.start();


						}else if(jsonObject.optString(Constants.STATUS).equals("1000")){
							PrefHelper.setInfo(PrefHelper.P_USER_ID, jOTemp.optString("userID"));
							User.SaveUserShared(getBaseContext(), "LoginName", mLoginName);
							User.SaveUserShared(getBaseContext(), "PassWord", mPassword);
							startActivity(new Intent(getBaseContext(), LoginBindDeviceActivity.class));
							Utils.showToast(jsonObject.optString(Constants.MSG));
						}
						else{
							Utils.showToast(jsonObject.optString(Constants.MSG));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Utils.showToast("数据解析错误!");
					}
				}
				CommonUtils.closeProgress();
			}
		});
		wsk.execute("LoginResult");
	}



	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_login_forgin_pwd:
			startActivityForResult(new Intent(getApplicationContext(),ForgetPasswordActivity.class),100);
			break;
		case R.id.register_tv:
			startActivityForResult(new Intent(getApplicationContext(),RegisterActivity.class),100);
			break;
		case R.id.main_login_account_del:
			this.mLoginNameView.setText("");
			break;
		case R.id.main_login_pwd_del:
			this.mPasswordView.setText("");
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100){
			finish();
		}
	}

}
