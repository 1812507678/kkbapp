package com.ttxgps.gpslocation;
import org.json.JSONException;
import org.json.JSONObject;

import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.BaseActivity.MainReceiver;
import com.ttxgps.msg.push.PushMessageManager;
import com.ttxgps.msg.push.PushViewEventListener;
import com.ttxgps.msg.push.PushViewType;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.DevicesUtils;
import com.xtst.gps.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

public class BaseActivity extends Activity {

	public static final String ACTIVITY_FINSH = "com.activity.finish";
	FinishCastReceiver finishBroadCastReceiver;
	private MainReceiver receiver;
	private boolean pushWindowIsVisible;

	public void setFinish() {
		// TODO Auto-generated method stub
		// 生成广播处理
		finishBroadCastReceiver = new FinishCastReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTIVITY_FINSH);
		// 注册广播
		registerReceiver(finishBroadCastReceiver, intentFilter);
	}

	public class FinishCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}

	@Override
	public void finish() {
		//		if (finishBroadCastReceiver != null)
		//			unregisterReceiver(finishBroadCastReceiver);
		super.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		setFinish();
		this.receiver = new MainReceiver();
		initReceiver();
		pushWindowIsVisible = false;
		MapAPP.getInstance().add(this);
	}

	class MainReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Constants.ACTION_LOGINOUT.equals(action)){//别处登录
				showOffline("");
			}else if(Constants.ACTION_GUARDIANDEL.equals(action)){//解除监护人
				String content = intent.getStringExtra("content");
				showPassive(content, Constants.ACTION_GUARDIANDEL);
			}else if(Constants.ACTION_MNSCHANGE.equals(action)){//管理员转让
				String content = intent.getStringExtra("content");
				showPassive(content, Constants.ACTION_MNSCHANGE);
			}
		}
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_LOGINOUT);
		filter.addAction(Constants.ACTION_GUARDIANDEL);
		filter.addAction(Constants.ACTION_MNSCHANGE);
		registerReceiver(this.receiver, filter);
	}

	private void unRegistReceiver() {
		unregisterReceiver(this.receiver);
	}

	private void showOffline(String content) {
		pushWindowIsVisible = true;
		PushMessageManager.getInstance().show(this, PushViewType.OFFLINE, getString(R.string.account_other_device_login), new PushViewEventListener() {
			@Override
			public void onListener(int postion) {
				PushMessageManager.getInstance().dismiss();
				if (postion == 0) {
					Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
					startActivity(intent);
					MainFragmentActivity.instance.finish();
					MapAPP.getInstance().quitApp();
				} else {
					MainFragmentActivity.instance.finish();
					MapAPP.getInstance().quitApp();
				}
				pushWindowIsVisible = false;
			}
		});

	}
	String deviceId;
	private void showPassive(String content,final String command) {
		String details = null;
		if (!TextUtils.isEmpty(content)) {
			try {
				JSONObject jo = new JSONObject(content);
				if (jo != null) {
					details = jo.getString("Msg");
					deviceId = jo.getString("DeviceID");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		PushViewType type = null;
		if (Constants.ACTION_GUARDIANDEL.equals(command)) {
			type = PushViewType.UNBIND;
		} else if (Constants.ACTION_MNSCHANGE.equals(command)) {
			type = PushViewType.SETADMIN;
		}
		pushWindowIsVisible = true;
		PushMessageManager.getInstance().show(this, type, details, new PushViewEventListener() {
			@Override
			public void onListener(int postion) {
				PushMessageManager.getInstance().dismiss();
				if (Constants.ACTION_GUARDIANDEL.equals(command)) {
					DevicesUtils.deleteDevice(deviceId);
					if (User.babyslist.size() > 0) {
						User.isGetBabyDdtail = true;
						User.curBabys = User.babyslist.get(0);
						if (postion == 0) {
							BaseActivity.this.startActivity(new Intent(BaseActivity.this, MyBabyActivity.class));
						}
					} else {
						Intent mIntent = new Intent(BaseActivity.this, LoginBindDeviceActivity.class);
						BaseActivity.this.startActivity(mIntent);
						MainFragmentActivity.instance.finish();
						MapAPP.getInstance().quitApp();
					}
				} else if (Constants.ACTION_MNSCHANGE.equals(command)) {
					if (User.curBabys.getId().equals(deviceId)) {
						User.SaveUserSharedBoolean(getBaseContext(), Constants.KEY_IS_ADMIN, true);
					}
					if (postion == 0) {
						BaseActivity.this.startActivity(new Intent(BaseActivity.this, MyBabyActivity.class));
					}
				}
				pushWindowIsVisible = false;
				PushMessageManager.getInstance().dismiss();
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(pushWindowIsVisible)
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(receiver != null)
			unRegistReceiver();
		MapAPP.getInstance().remove(this);
		super.onDestroy();
	}
}
