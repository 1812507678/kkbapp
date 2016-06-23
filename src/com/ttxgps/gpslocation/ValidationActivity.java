package com.ttxgps.gpslocation;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class ValidationActivity extends BaseActivity{
	public static final int FLAG_EMAIL = 2;
	public static final int FLAG_PHONE = 1;
	public static final int FORWAD_TYPE_FORGOT_PASSWORD = 2;
	public static final int FORWAD_TYPE_MODIFY_USERINFO = 3;
	public static final int FORWAD_TYPE_REGISTER = 1;
	String account;
	int forwardType;
	Runnable reSendRunnable;
	Button resend_btn;
	int times;
	EditText validation_edt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validation);
		this.account = getIntent().getStringExtra("account_text");
		initTitle();
		initView();
		initResendButton();
	}
	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.input_ver_code);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		TextView no_tv = (TextView) findViewById(R.id.no_tv);
		String noStr = "";
		no_tv.setText(no_tv.getText() + getString(R.string.phone) + "+" + this.account);
		this.validation_edt = (EditText) findViewById(R.id.validation_edt);

	}
	private void initResendButton() {
		this.resend_btn = ((Button)findViewById(R.id.resend_btn));
		this.resend_btn.setText(getResources().getString(R.string.resend_validation_request, new Object[] { Integer.valueOf(this.times) }));
		this.resend_btn.setClickable(false);
		this.reSendRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				ValidationActivity localValidationActivity = ValidationActivity.this;
				localValidationActivity.times -= 1;
				if (ValidationActivity.this.times <= 0)
				{
					ValidationActivity.this.resend_btn.setText(ValidationActivity.this.getResources().getString(R.string.resend_validation_request, new Object[] { "" }));
					ValidationActivity.this.resend_btn.setClickable(true);
					return;
				}
				ValidationActivity.this.resend_btn.setText(ValidationActivity.this.getResources().getString(R.string.resend_validation_request, new Object[] { ValidationActivity.this.times + ValidationActivity.this.getString(R.string.count_down) }));
				ValidationActivity.this.resend_btn.setClickable(false);
				ValidationActivity.this.resend_btn.postDelayed(this, 1000L);
			}
		};
		this.resend_btn.postDelayed(this.reSendRunnable, 1000L);
		this.resend_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reGetVerifyCode();
			}
		});

		findViewById(R.id.next_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ValidationActivity.this, SetPasswordActivity.class);
				intent.putExtra("account_text", ValidationActivity.this.account);
				ValidationActivity.this.startActivity(intent);
				ValidationActivity.this.finish();

			}
		});

	}
	private void reGetVerifyCode(){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Mob",account));
		WebServiceTask wsk = new WebServiceTask("SendNumToMobile", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
						if (jsonObject.has("Status") && jsonObject.getString("Status").equals("0")) {
							initResendButton();
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
}
