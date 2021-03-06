package com.ttxgps.gpslocation;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.ttxgps.entity.User;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class ThridStepSetting extends BaseActivity {

	private String deviceID;
	private String imeiTypeValue;
	//private String SN;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent intent = getIntent();
		deviceID = intent.getStringExtra("deviceID");
		imeiTypeValue = intent.getStringExtra("imeiTypeValue");

		User.SaveUserShared(this,Constants.CUR_DEVICE_ID,deviceID);

		if (imeiTypeValue!=null) {
			if(imeiTypeValue.equals("书包")){
				setContentView(R.layout.activity_thrid_step_setting_schoolbag);
			}else if(imeiTypeValue.equals("鞋")){
				setContentView(R.layout.activity_thrid_step_setting_childrenshoes);
			}else if(imeiTypeValue.equals("宠物")){
				setContentView(R.layout.activity_thrid_step_setting_pet);
			}else if (imeiTypeValue.equals("手表")) {
				setContentView(R.layout.activity_thrid_step_setting_watch);
			}else if(imeiTypeValue.equals("机器人")){
				setContentView(R.layout.activity_thrid_step_setting_robot);
			}else if (imeiTypeValue.equals("智能家居")) {
				setContentView(R.layout.activity_thrid_step_setting_smarthome);
			}else if (imeiTypeValue.equals("自行车")){
				setContentView(R.layout.activity_thrid_step_setting_bicycle);
			}
		}
	}

	//上一步
	public void preStep(View view) {
		startActivity(new Intent(this,LoginBindDeviceActivity.class));
	}

	//下一步
	public void nextStep(View view) {
		upInfo();
	}


	//向服务器提交数据
	private void upInfo(){

		Log.i("DeviceMob",imeiTypeValue);
		Log.i("User", User.id);

		CommonUtils.showProgress(this, "正在提交···");
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();

		linkedlist.add(new WebServiceProperty("NickName", "未设置"));
		linkedlist.add(new WebServiceProperty("Height", "0"));
		linkedlist.add(new WebServiceProperty("DeviceMob", imeiTypeValue));  //需要传过来
		linkedlist.add(new WebServiceProperty("Birthday", "2000-2-2"));
		linkedlist.add(new WebServiceProperty("DeviceID", deviceID));
		linkedlist.add(new WebServiceProperty("Sex","0"));
		linkedlist.add(new WebServiceProperty("Weight", "0"));
		linkedlist.add(new WebServiceProperty("User", User.id));
		linkedlist.add(new WebServiceProperty("RelashionNick", "未设置"));


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
									i.setClass(ThridStepSetting.this, MainFragmentActivity.class);
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
}
