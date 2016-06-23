package com.ttxgps.gpslocation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.adapter.MainViewPagerAdapter;
import com.ttxgps.entity.User;
import com.ttxgps.msg.push.PushMessageManager;
import com.ttxgps.msg.push.PushViewEventListener;
import com.ttxgps.msg.push.PushViewType;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.Deviceinf;
import com.ttxgps.utils.DevicesUtils;
import com.umeng.message.PushAgent;
import com.xtst.gps.R;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainFragmentActivity extends FragmentActivity implements OnClickListener{

	private final static int PAGE_KUKUBAO = 0;
	private final static int PAGE_FIND = 1;
	private final static int PAGE_BABYDETAIL = 2;
	private final static int PAGE_CENTER = 3;

	private ViewPager viewPager;
	private MainViewPagerAdapter viewPagerAdapter;
	private RelativeLayout homeLayout, findLayout,babydetailLayout,mycenterLayout;
	private View currentButton;
	public static MainFragmentActivity instance;
	private MainReceiver receiver;
	private boolean pushWindowIsVisible;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);

		initView();
		senum();
		instance = this;
		this.receiver = new MainReceiver();
		initReceiver();
		pushWindowIsVisible = false;
	}

	private void initView(){
		homeLayout = (RelativeLayout)findViewById(R.id.bottom_layout_home);
		findLayout = (RelativeLayout)findViewById(R.id.bottom_layout_find);
		babydetailLayout = (RelativeLayout)findViewById(R.id.bottom_layout_babydetail);
		mycenterLayout = (RelativeLayout)findViewById(R.id.bottom_layout_mycenter);

		homeLayout.setOnClickListener(this);
		findLayout.setOnClickListener(this);
		babydetailLayout.setOnClickListener(this);
		mycenterLayout.setOnClickListener(this);

		//setButton(homeLayout);

		viewPager = (ViewPager)findViewById(R.id.viewpager);
		viewPager.setOnPageChangeListener(pageChangeListener);

		List<Fragment> fragments = new ArrayList<Fragment>();

		fragments.add(new HomeFragment());
		fragments.add(new MyFragment());
		fragments.add(new BabydetailFragment());
		fragments.add(new PersonalCenterFragment());

		viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), fragments);

		viewPager.setAdapter(viewPagerAdapter);


		viewPager.setCurrentItem(PAGE_KUKUBAO);

		((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xff000000);
		((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_pets_black_24dp);
	}

	private void setButton(View v) {

		if (currentButton != null && currentButton.getId() != v.getId()) {
			currentButton.setEnabled(true);
		}

		v.setEnabled(false);
		currentButton = v;
	}


	private final OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == PAGE_KUKUBAO) {
				//setButton(homeLayout);
				((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xff000000);
				((TextView)findViewById(R.id.buttom_find)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_babydetail)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_mycenter)).setTextColor(0xffffffff);

				((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_child_care_black_24dp);
				((ImageView)findViewById(R.id.bottom_icon_find)).setImageResource(R.drawable.ic_pets_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_babydetail)).setImageResource(R.drawable.ic_assignment_ind_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_mycenter)).setImageResource(R.drawable.ic_folder_shared_white_24dp);
			} else if (arg0 == PAGE_FIND) {
				//setButton(findLayout);
				((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_find)).setTextColor(0xff000000);
				((TextView)findViewById(R.id.buttom_babydetail)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_mycenter)).setTextColor(0xffffffff);

				((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_child_care_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_find)).setImageResource(R.drawable.ic_pets_black_24dp);
				((ImageView)findViewById(R.id.bottom_icon_babydetail)).setImageResource(R.drawable.ic_assignment_ind_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_mycenter)).setImageResource(R.drawable.ic_folder_shared_white_24dp);

			}
			else if (arg0 == PAGE_BABYDETAIL) {
				//setButton(findLayout);
				((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_find)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_babydetail)).setTextColor(0xff000000);
				((TextView)findViewById(R.id.buttom_mycenter)).setTextColor(0xffffffff);

				((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_child_care_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_find)).setImageResource(R.drawable.ic_pets_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_babydetail)).setImageResource(R.drawable.ic_assignment_ind_black_24dp);
				((ImageView)findViewById(R.id.bottom_icon_mycenter)).setImageResource(R.drawable.ic_folder_shared_white_24dp);

			}
			else if (arg0 == PAGE_CENTER) {
				//setButton(findLayout);
				((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_find)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_babydetail)).setTextColor(0xffffffff);
				((TextView)findViewById(R.id.buttom_mycenter)).setTextColor(0xff000000);

				((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_child_care_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_find)).setImageResource(R.drawable.ic_pets_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_babydetail)).setImageResource(R.drawable.ic_assignment_ind_white_24dp);
				((ImageView)findViewById(R.id.bottom_icon_mycenter)).setImageResource(R.drawable.ic_folder_shared_black_24dp);

			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bottom_layout_home:
			((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xff000000);
			((TextView)findViewById(R.id.buttom_find)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_babydetail)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_mycenter)).setTextColor(0xffffffff);

			((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_child_care_black_24dp);
			((ImageView)findViewById(R.id.bottom_icon_find)).setImageResource(R.drawable.ic_pets_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_babydetail)).setImageResource(R.drawable.ic_assignment_ind_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_mycenter)).setImageResource(R.drawable.ic_folder_shared_white_24dp);
			viewPager.setCurrentItem(PAGE_KUKUBAO);
			break;
		case R.id.bottom_layout_find:
			((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_find)).setTextColor(0xff000000);
			((TextView)findViewById(R.id.buttom_babydetail)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_mycenter)).setTextColor(0xffffffff);

			((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_child_care_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_find)).setImageResource(R.drawable.ic_pets_black_24dp);
			((ImageView)findViewById(R.id.bottom_icon_babydetail)).setImageResource(R.drawable.ic_assignment_ind_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_mycenter)).setImageResource(R.drawable.ic_folder_shared_white_24dp);

			viewPager.setCurrentItem(PAGE_FIND);
			break;
		case R.id.bottom_layout_babydetail:
			((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_find)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_babydetail)).setTextColor(0xff000000);
			((TextView)findViewById(R.id.buttom_mycenter)).setTextColor(0xffffffff);

			((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_child_care_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_find)).setImageResource(R.drawable.ic_pets_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_babydetail)).setImageResource(R.drawable.ic_assignment_ind_black_24dp);
			((ImageView)findViewById(R.id.bottom_icon_mycenter)).setImageResource(R.drawable.ic_folder_shared_white_24dp);
			viewPager.setCurrentItem(PAGE_BABYDETAIL);
			break;
		case R.id.bottom_layout_mycenter:
			((TextView)findViewById(R.id.buttom_sales)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_find)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_babydetail)).setTextColor(0xffffffff);
			((TextView)findViewById(R.id.buttom_mycenter)).setTextColor(0xff000000);

			((ImageView)findViewById(R.id.bottom_icon_sales)).setImageResource(R.drawable.ic_child_care_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_find)).setImageResource(R.drawable.ic_pets_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_babydetail)).setImageResource(R.drawable.ic_assignment_ind_white_24dp);
			((ImageView)findViewById(R.id.bottom_icon_mycenter)).setImageResource(R.drawable.ic_folder_shared_black_24dp);

			viewPager.setCurrentItem(PAGE_CENTER);
			break;

		default:
			break;
		}
	}

	private void senum(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					PushAgent.getInstance(getBaseContext()).addAlias( User.id,"userid");
					if(User.phone!=""){
						PushAgent.getInstance(getBaseContext()).addAlias( User.phone,"phone");
					}
					String imei =  Deviceinf.getDeviceId(getBaseContext());
					String imsi = Deviceinf.getSubscriberId(getBaseContext());
					if(!TextUtils.isEmpty(imei))
						PushAgent.getInstance(getBaseContext()).addAlias(imei,"imei");
					if(!TextUtils.isEmpty(imsi))
						PushAgent.getInstance(getBaseContext()).addAlias(imsi,"imsi" );



				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}).start();

	}

	private void hideNotification() {
		((NotificationManager) getSystemService("notification")).cancelAll();
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
					Intent intent = new Intent(MainFragmentActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				} else {
					finish();
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
							MainFragmentActivity.this.startActivity(new Intent(MainFragmentActivity.this, MyBabyActivity.class));
						}
					} else {
						Intent mIntent = new Intent(MainFragmentActivity.this, LoginBindDeviceActivity.class);
						MainFragmentActivity.this.startActivity(mIntent);
						MainFragmentActivity.this.finish();
					}
				} else if (Constants.ACTION_MNSCHANGE.equals(command)) {
					if (User.curBabys.getId().equals(deviceId)) {
						User.SaveUserSharedBoolean(getBaseContext(), Constants.KEY_IS_ADMIN, true);
					}
					if (postion == 0) {
						MainFragmentActivity.this.startActivity(new Intent(MainFragmentActivity.this, MyBabyActivity.class));
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
			exitApp();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		User.SaveUserData(getBaseContext());
		instance = null;
		unRegistReceiver();
		hideNotification();
	}

	private void exitApp(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("提示");
		builder.setMessage("是否退出应用?");
		builder.setNegativeButton("取消",  new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		builder.setPositiveButton("退出",  new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.create().show();
	}
}
