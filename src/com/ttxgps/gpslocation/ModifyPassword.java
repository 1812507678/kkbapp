package com.ttxgps.gpslocation;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.User;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class ModifyPassword extends BaseActivity{

	EditText new_psw_edt;
	EditText old_psw_edt;
	EditText submit_pwd_edt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_password);
		initTitle();
		initView();

		findViewById(R.id.submit_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String oldPsw = ModifyPassword.this.old_psw_edt.getText().toString();
				String nPsw = ModifyPassword.this.new_psw_edt.getText().toString();
				String secondPsw = ModifyPassword.this.submit_pwd_edt.getText().toString();
				if (TextUtils.isEmpty(oldPsw)) {
					Utils.showToast(R.string.please_input_old_pwd);
				} else if (oldPsw.length() < 6 || oldPsw.length() > 16) {
					Utils.showToast(R.string.hint_password);
				} else if (TextUtils.isEmpty(nPsw)) {
					Utils.showToast(R.string.input_new_pwd);
				} else if (nPsw.length() < 6 || nPsw.length() > 16) {
					Utils.showToast(R.string.hint_password);
				} else if (TextUtils.isEmpty(secondPsw)) {
					Utils.showToast(R.string.hint_password_again);
				} else if (!nPsw.equals(secondPsw)) {
					Utils.showToast(R.string.pwd_inconformity);
				}else{
					upPwd(User.phone, nPsw, oldPsw);
				}
			}
		});
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.my_upd_pwd);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		new_psw_edt = (EditText)findViewById(R.id.password_edt);
		old_psw_edt = (EditText)findViewById(R.id.old_psw_edt);
		submit_pwd_edt = (EditText)findViewById(R.id.submit_pwd_edt);
	}

	private void upPwd(String mob,String pwd,String opwd){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Mob", mob));
		linkedlist.add(new WebServiceProperty("Pwd", pwd));
		linkedlist.add(new WebServiceProperty("OldPwd", opwd));
		WebServiceTask wsk = new WebServiceTask("PwdEdit", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				String msg = null;
				if(result!=null){//错误信息
					msg=result;
					Utils.showToast(msg);
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=(String) jsonObject.get("Msg");
						msg=str;

						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							User.SaveUserShared(getBaseContext(), "PassWord", "");
							setResult(111);
							finish();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//

					}
				}
				Utils.showToast(msg);

			}
		});
		wsk.execute("PwdEditResult");
	}

}
