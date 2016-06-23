package com.ttxgps.gpslocation;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ttxgps.entity.User;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class ExpelMosquitoActivity extends BaseActivity implements OnCheckedChangeListener, OnClickListener{
	private String availableTime;
	private Button btnConfir;
	private String id;
	private RadioGroup mRadioGroup;
	private final int power = 0;
	private String status = "0";
	private TextView tvPower;
	private TextView tvUseDuration;
	private View useDurationV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expel_mosquito);
		initTitle();
		initView();
		initData();
		setListener();

	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.dis_expel_mosq_text);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	protected void initView() {
		this.tvPower = (TextView) findViewById(R.id.power_tv);
		this.mRadioGroup = (RadioGroup) findViewById(R.id.expel_switch_rg);
		this.tvUseDuration = (TextView) findViewById(R.id.use_duration_tv);
		this.useDurationV = findViewById(R.id.use_duration_ll);
		this.btnConfir = (Button) findViewById(R.id.confir_btn);
	}

	protected void setListener() {
		this.btnConfir.setOnClickListener(this);
		this.mRadioGroup.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		updateData();
	}

	private void initData() {
		status = User.curBabys.getstatusmosq();
		if(!TextUtils.isEmpty(status)){
			if(status.equals("0")){
				mRadioGroup.check(R.id.off_rb);
			}else{
				mRadioGroup.check(R.id.on_rb);
			}
		}
	}



	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.on_rb:
			this.status = "1";
			break;
		case R.id.off_rb:
			this.status = "0";
			break;
		default:
		}
	}

	private void updateData() {
		CommonUtils.showProgress(this, "提交中···");
		String str = this.status != "1"?"GBQW":"KQQW";;
		DevicesUtils.sendCMDToDevice(this,User.id,User.curBabys.getId(),str,"",new WebServiceResult() {

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
						if (jsonObject.has(Constants.STATUS) && jsonObject.optString(Constants.STATUS).equals("0")) {
							User.curBabys.setstatusmosq(String.valueOf(status));
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
	}

	private void getData(){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID",User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID",User.id));
		WebServiceTask wsk = new WebServiceTask("GetDeviceDetail", linkedlist, WebService.URL_OTHER,getBaseContext(),new WebServiceResult() {

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
							String userInfoStr = jsonObject.optString("info");
							JSONObject beaninfo = new JSONObject(userInfoStr);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				//				Toast.makeText(getBaseContext(), "获取驱蚊状态失败！", Toast.LENGTH_LONG).show();
			}
		});
		wsk.execute("GetDeviceDetailResult");
	}
}
