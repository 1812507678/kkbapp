package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.User;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class LocationMode extends BaseActivity{

	private Button btnConfir;
	private final String id = "";
	private boolean isAdmin = false;
	private RadioGroup mRadioGroup;
	private RadioButton normalRb;
	private String normal_mode;
	private final int power = 0;
	private RadioButton preciRb;
	private String preci_mode;
	private TextView tvPower;
	private int type = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_mode);
		initTitle();
		initView();
		initData();
		setListener();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.location_mode);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		this.tvPower = (TextView) findViewById(R.id.power_tv);
		this.mRadioGroup = (RadioGroup) findViewById(R.id.location_mode_rg);
		this.normalRb = (RadioButton) findViewById(R.id.normal_mode_rb);
		this.preciRb = (RadioButton) findViewById(R.id.precision_mode_rb);
		this.btnConfir = (Button) findViewById(R.id.confir_btn);
		this.isAdmin = PrefHelper.getBooleanData(PrefHelper.P_ISADMIN);
		if (!this.isAdmin) {
			this.btnConfir.setVisibility(View.GONE);
			mRadioGroup.setClickable(false);
		}
		this.normal_mode = getString(R.string.location_mode_def_time);
		this.preci_mode = getString(R.string.location_mode_def_time);
	}
	private void initData() {
		// TODO Auto-generated method stub
		uplocation("", "");
	}

	private void checkButton(int type) {
		if (type != -1) {
			if (type == 0) {
				this.mRadioGroup.check(R.id.normal_mode_rb);
			} else {
				this.mRadioGroup.check(R.id.precision_mode_rb);
			}
		}
	}
	private void setListener() {
		this.mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (LocationMode.this.isAdmin) {
					switch (checkedId) {
					case R.id.normal_mode_rb:
						LocationMode.this.type = 0;
						return;
					case R.id.precision_mode_rb:
						LocationMode.this.type = 1;
						return;
					default:
						return;
					}
				}
				LocationMode.this.checkButton(LocationMode.this.type);
			}
		});

		btnConfir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				uplocation(String.valueOf(type),"");
			}
		});
	}
	private void uplocation(final String LocationMode,final String IsOffWatch){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("LocationMode", LocationMode));
		linkedlist.add(new WebServiceProperty("IsOffWatch", IsOffWatch));
		linkedlist.add(new WebServiceProperty("UserID", User.id));


		WebServiceTask wsk = new WebServiceTask("GetOrSetModelIsOffWatch", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
						String str=jsonObject.optString("Msg");
						msg=str;

						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							if(LocationMode.isEmpty()&&IsOffWatch.isEmpty()){//获取
								if(jsonObject.optString("LocationMode").equals("0")){
									mRadioGroup.check(R.id.normal_mode_rb);
								}else{
									mRadioGroup.check(R.id.precision_mode_rb);
								}
							}else{//设置
								Utils.showToast(msg);
								finish();
							}
						}
						else{
							Utils.showToast(msg);
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
		wsk.execute("GetOrSetModelIsOffWatchResult");
	}


}
