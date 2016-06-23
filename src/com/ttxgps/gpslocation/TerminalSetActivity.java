package com.ttxgps.gpslocation;
  
import java.io.IOException;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.transport.HttpResponseException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.palmtrends.loadimage.Utils;
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

public class TerminalSetActivity extends BaseActivity implements OnClickListener, WebServiceResult{

	private CheckBox excise_watch_warn_cbox;

	private View family_set,stealth_set,whitelist_set,
	location_mode,expel_mosq,shutdown,findWatch;

	private View vFindWatch;
	private View vShutdown;
	private int time = 3;
	private Runnable timerRunnable;

	private boolean IsAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminal_set);
		initTitle();
		initView();

		startGetStealthTimeList(this);
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.my_terminal_set);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		family_set = findViewById(R.id.family_set_rl);
		stealth_set = findViewById(R.id.stealth_set_rl);
		whitelist_set = findViewById(R.id.whitelist_set_rl);
		location_mode = findViewById(R.id.location_mode_rl);
		expel_mosq = findViewById(R.id.expel_mosq_ll);
		shutdown = findViewById(R.id.shutdown_ll);
		findWatch = findViewById(R.id.findWatch_ll);
		vFindWatch = findViewById(R.id.find_watch_v);
		vFindWatch.setOnClickListener(this);
		vShutdown = findViewById(R.id.shutdown_v);
		vShutdown.setOnClickListener(this);
		family_set.setOnClickListener(this);
		stealth_set.setOnClickListener(this);
		whitelist_set.setOnClickListener(this);
		location_mode.setOnClickListener(this);
		expel_mosq.setOnClickListener(this);
		shutdown.setOnClickListener(this);
		findWatch.setOnClickListener(this);
		excise_watch_warn_cbox = (CheckBox)findViewById(R.id.excise_watch_warn_cbox);
		excise_watch_warn_cbox.setOnClickListener(this);
		IsAdmin = true;//PrefHelper.getBooleanData(PrefHelper.P_ISADMIN);
		if(IsAdmin){
			this.excise_watch_warn_cbox.setEnabled(true);
			this.excise_watch_warn_cbox.setFocusable(true);
			this.excise_watch_warn_cbox.setFocusableInTouchMode(true);
			this.excise_watch_warn_cbox.setClickable(true);
		}else{
			this.excise_watch_warn_cbox.setEnabled(false);
			this.excise_watch_warn_cbox.setFocusable(false);
			this.excise_watch_warn_cbox.setFocusableInTouchMode(false);
			this.excise_watch_warn_cbox.setClickable(false);

		}
		exciseWactch("");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.family_set_rl:
			startActivity(new Intent(getBaseContext(), FamilyNum.class));
			//		{
			//			LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
			//			linkedlist.add(new WebServiceProperty("DeviceID",3));
			//			linkedlist.add(new WebServiceProperty("UserID", 18));
			//			linkedlist.add(new WebServiceProperty("FamilyList", "FN&&bb&&18617141119&&am&&16790774051&&eee&&13490774051&&cc&&13990774051##"));
			//			WebServiceTask wsk = new WebServiceTask("GetOrSetFamilyList", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {
			//
			//				@Override
			//				public void webServiceResult(String result, String data) {
			//					// TODO Auto-generated method stub
			//					String msg;
			//					if(result!=null){//错误信息
			//						msg=result;
			//					}
			//					else{//正确信息
			//
			//						try {
			//							JSONObject jsonObject = new JSONObject(data);
			//							String str=(String) jsonObject.get("Msg");
			//							msg=str;
			//						} catch (JSONException e) {
			//							// TODO Auto-generated catch block
			//							e.printStackTrace();
			//							msg=e.getMessage();
			//						}
			//					}
			//					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			//				}
			//			});
			//			wsk.execute("GetOrSetFamilyListResult");
			//		}

			break;
		case R.id.stealth_set_rl:
			startActivity(new Intent(getBaseContext(), StealthTimeSet.class));
			break;
		case R.id.whitelist_set_rl:
			startActivity(new Intent(getBaseContext(), WhiteListActivity.class));
			/*{
			LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
			linkedlist.add(new WebServiceProperty("DeviceID", 3));
			linkedlist.add(new WebServiceProperty("UserID", 18));
			linkedlist.add(new WebServiceProperty("WhiteList", "WN&&bb&&18617141119&&am&&16790774051&&eee&&13490774051&&cc&&13990774051##"));
			WebServiceTask wsk = new WebServiceTask("GetOrSetWhiteList", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							msg=e.getMessage();
						}
					}
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			});
			wsk.execute("GetOrSetWhiteListResult");
		}*/
			break;
		case R.id.location_mode_rl:
			startActivity(new Intent(getBaseContext(), LocationMode.class));
			//sendCMDToDevice("GPSModel","60");//0,1
			break;
		case R.id.expel_mosq_ll:
			startActivity(new Intent(getBaseContext(), ExpelMosquitoActivity.class));
			break;
		case R.id.shutdown_ll:
			//sendCMDToDevice("GJ","");
			showShutdownDialog();
			break;
		case R.id.findWatch_ll:
			//sendCMDToDevice("CZSB","");
			findDevice();
			break;
		case R.id.excise_watch_warn_cbox:
			String mIsRwatchstatus = excise_watch_warn_cbox.isChecked()?"1":"0";
			exciseWactch(mIsRwatchstatus);
			break;
		case R.id.shutdown_v:
			this.vShutdown.setVisibility(View.GONE);
			break;
		case R.id.find_watch_v:
			GoneFindWatchV();
			break;
		default:
			break;
		}
	}
	int test=0;

	private void showShutdownDialog() {
		this.vShutdown.setVisibility(View.VISIBLE);
		findViewById(R.id.confir_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shutdownDevice();
				vShutdown.setVisibility(View.GONE);
			}
		});
		findViewById(R.id.cancel_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vShutdown.setVisibility(View.GONE);
			}
		});
	}

	private void shutdownDevice() {
		DevicesUtils.sendCMDToDevice(this,User.id,User.curBabys.getId(),"GJ","",this);
	}

	private void findDevice() {
		DevicesUtils.sendCMDToDevice(this,User.id,User.curBabys.getId(),"CZSB","",new WebServiceResult() {

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
							vFindWatch.setVisibility(View.VISIBLE);
							timepiece();
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


	private void timepiece() {
		this.timerRunnable = new Runnable() {
			@Override
			public void run() {
				time = time - 1;
				if (time <= 0) {
					GoneFindWatchV();
				} else {
					vFindWatch.postDelayed(timerRunnable, 1000);
				}
			}
		};
		this.vFindWatch.postDelayed(this.timerRunnable, 1000);
	}

	private void GoneFindWatchV() {
		this.time = 3;
		this.vFindWatch.setVisibility(View.GONE);
	}

	private void exciseWactch(final String IsOffWatch){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("IsOffWatch", IsOffWatch));
		linkedlist.add(new WebServiceProperty("LocationMode", ""));
		linkedlist.add(new WebServiceProperty("UserID", User.id));

		WebServiceTask wsk = new WebServiceTask("GetOrSetModelIsOffWatch", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							if(IsOffWatch.equals("")){ //读取
								if(jsonObject.optString("IsOffWatch").equals("1")){//打开
									excise_watch_warn_cbox.setChecked(true);
								}else{//关闭
									excise_watch_warn_cbox.setChecked(false);
								}
							}else{// 设置

							}
						}


					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				//				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
		wsk.execute("GetOrSetModelIsOffWatchResult");
	}

	void startGetStealthTimeList(Context context){

		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("StealthTime", ""));
		WebServiceTask timeWeb = new WebServiceTask("GetOrSetStealthTime", linkedlist, WebService.URL_OTHER, context, new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				if(result!=null){//错误信息

				}
				else{//正确信息
					try {
						JSONObject json = new JSONObject(data);
						if (json.has("Status") && (json.getInt("Status") == 0)) {


							User.curBabys.StealthTimeStr = json.getString("InvisibleTime");
							//StealthTimeStr=DevicesUtils.timeBeanparseJSON(data);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		timeWeb.execute("GetOrSetStealthTimeResult");
	}

	@Override
	public void webServiceResult(String result, String data) {
		// TODO Auto-generated method stub

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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg=e.getMessage();
			}
		}
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}


}
