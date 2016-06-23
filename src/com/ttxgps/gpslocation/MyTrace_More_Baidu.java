package com.ttxgps.gpslocation;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Stroke;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.xtst.gps.BuildConfig;
import com.xtst.gps.R;
import com.xtst.gps.R.id;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.bean.FenceBaseInfo;
import com.ttxgps.entity.TeamMember;
import com.ttxgps.entity.ItemH;
import com.ttxgps.entity.MyOverLayItem;
import com.ttxgps.entity.Trace;
import com.ttxgps.entity.User;
import com.ttxgps.utils.AsyncImageLoader;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.DensityUtil;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.AsyncImageLoader.ImageCallback;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.utils.RegularUtils;
import com.palmtrends.loadimage.Utils;

public class MyTrace_More_Baidu extends Activity {
	public static final String CURRENT_FLAG_KEY = "10001";
	public static final String CURRENT_POSTIONS_KEY = "10002";
	public static final String CURRENT_INDEX_KEY = "10003";
	public static final String CURRENT_INITLOAD_KEY = "10004";
	public static final String CURRENT_TAB_STR = "current_tab";
	public static final int LOCATION_TAB = 0;
	public static final int REPLAY_TAB = 1;
	public static final int SAFETY_TAB = 2;
	static MapView mMapView = null;
	BDLocationListener mLocationListener = null;// onResume时注册此listener，onPause时需要Remove
	MyLocationOverlay mLocationOverlay = null; // 定位图层
	static View mPopView = null; // 点击mark时弹出的气泡View
	/**
	 * 定位SDK的核心类
	 */
	private LocationClient mLocClient;
	/**
	 * 用户位置信息
	 */
	private LocationData mLocData;
	private BDLocation location;

	// TraceServcie traceServcie;
	public Trace myTraces;
	SharedPreferences sp;
	Drawable marker = null;
	public LinearLayout pop_device_inf;
	public TextView pop_title;
	public TextView pop_address;
	public TextView pop_state;
	public TextView pop_device_state;
	public TextView pop_posType;
	public TextView pop_posTime;
	static HashMap<String, Trace> map = new HashMap<String, Trace>();
	Timer timer = new Timer();
	TextView timer_text;
	TextView text_name;
	TextView text_date;
	public int refresh_period = 10;
	boolean isback;
	SeekBar playBar;
	BabyInfoBean currentMember;
	// View next_content;
	ImageView control;
	TextView title;
	ImageView play_h;
	Button control_btn;
	// RadioButton replay_btn,current_location_btn;
	RadioButton replay_tab, location_tab, safety_tab;
	RadioGroup navigation_radiogroup;
	// 当前状态 0表示多车 1表示单车 2表示历史

	// public int select_flag = 0;
	public int current_tab = 0;
	boolean isReplay = true;
	ProgressBar prog;
	static int zoom = 15;
	// Default zoom
	final int zoom_location = 17;
	final int zoom_safety = 17;
	final int zoom_replay = 15;

	private static String[] refreshTable = null;
	private static String[] CAR_ALARM = null;
	private static String[] CAR_STATE = null;

	public boolean initload = false;

	private static SimpleDateFormat dateFormater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.PRC);

	// {"userID":"165","userName":"nestorzhang","loginName":"nestor", ... }
	// private static int userID = LoginActivity.userID;
	private static String timeZone = LoginActivity.timeZone;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		// outState.putInt(CURRENT_FLAG_KEY, select_flag);
		outState.putInt(CURRENT_INDEX_KEY, mCurrentindex);
		outState.putBoolean(CURRENT_INITLOAD_KEY, initload);
		outState.putInt(CURRENT_TAB_STR, current_tab);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_baidu);

		refreshTable = getResources().getStringArray(R.array.refresh_table);
		CAR_ALARM = getResources().getStringArray(R.array.car_alarm);
		CAR_STATE = getResources().getStringArray(R.array.car_state);

		// traceServcie = new TraceServcie();
		sp = this.getSharedPreferences("data", MODE_WORLD_READABLE);
		currentMember = User.curBabys;
		myTraces = currentMember.traces;
		// myTraces = traceServcie.findTracesByUid(uid);
		// item = null;
		prog = (ProgressBar) findViewById(R.id.process);
		MapAPP app = (MapAPP) this.getApplication();
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(getApplication());
			app.mBMapMan.init(new MapAPP.MyGeneralListener());
		}
		app.mBMapMan.start();
		// 如果使用地图SDK，请初始化地图Activity
		// super.initMapActivity(app.mBMapMan);
		// setSelect(getIntent().getIntExtra("type", 1));
		current_tab = getIntent().getIntExtra(CURRENT_TAB_STR, 1);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(CURRENT_FLAG_KEY)) {
			// setSelect(savedInstanceState.getInt(CURRENT_FLAG_KEY));
			mCurrentindex = savedInstanceState.getInt(CURRENT_INDEX_KEY);
			initload = savedInstanceState.getBoolean(CURRENT_INITLOAD_KEY);
			current_tab = savedInstanceState.getInt(CURRENT_TAB_STR);
		}

		text_date = (TextView) findViewById(R.id.text_date);
		text_date.setText(dateFormater.format(new Date()));
		mMapView = (MapView) findViewById(R.id.bmapView);
		play_h = (ImageView) findViewById(R.id.replay);
		// mMapView.setBuiltInZoomControls(true);
		// next_content = findViewById(R.id.nextcontrol);
		control = (ImageView) findViewById(R.id.map);
		title = (TextView) findViewById(R.id.title_text);
		timer_text = (TextView) findViewById(R.id.text_time);
		text_name = (TextView) findViewById(R.id.text_name);
		if (currentMember != null) {
			text_name.setText(currentMember.getNickName());
		}

		(findViewById(R.id.add_ef_tv)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				addNewFence();
			}
		});
		// control_btn = (Button)findViewById(R.id.control_btn);
		// replay_btn = (RadioButton)findViewById(R.id.replay_btn);
		// current_location_btn =
		// (RadioButton)findViewById(R.id.current_location_btn);

		safety_tab = (RadioButton) findViewById(R.id.safety_tab);
		replay_tab = (RadioButton) findViewById(R.id.replay_tab);
		location_tab = (RadioButton) findViewById(R.id.location_tab);
		navigation_radiogroup = (RadioGroup) findViewById(R.id.navigation_radiogroup);

		// 实例化定位服务，LocationClient类必须在主线程中声明
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(new BDLocationListenerImpl());// 注册定位监听接口

		/**
		 * LocationClientOption 该类用来设置定位SDK的定位方式。
		 */
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setPriority(LocationClientOption.GpsFirst); // 设置GPS优先
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.disableCache(false);// 禁止启用缓存定位
		// option.setPoiNumber(5); //最多返回POI个数
		// option.setPoiDistance(1000); //poi查询距离
		// option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
		mLocClient.setLocOption(option); // 设置定位参数
		// 添加定位图层
		mLocationOverlay = new MyLocationOverlay(mMapView);
		// 实例化定位数据，并设置在我的位置图层
		mLocData = new LocationData();
		mLocationOverlay.setData(mLocData);
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(mLocationOverlay);

		map.clear();

		mPopView = super.getLayoutInflater().inflate(R.layout.popview_b, null);
		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		mPopView.setVisibility(View.GONE);

		pop_title = (TextView) mPopView.findViewById(R.id.pop_title);
		pop_address = (TextView) mPopView.findViewById(R.id.pop_address);
		pop_state = (TextView) mPopView.findViewById(R.id.pop_state);
		pop_posType=(TextView) mPopView.findViewById(R.id.pop_pos_type);
		pop_posTime=(TextView) mPopView.findViewById(R.id.pop_time);
		pop_device_state = (TextView) mPopView
				.findViewById(R.id.pop_device_state);
		pop_device_inf = (LinearLayout) mPopView.findViewById(R.id.pop_device_inf);

		View content = mPopView.findViewById(R.id.content);
		int w = 280 * getResources().getDisplayMetrics().widthPixels / 480;
		int dp=110;
		if(getResources().getDisplayMetrics().heightPixels<=800)
			dp=123;
		int h = DensityUtil.dip2px(this, dp); //getResources().getDisplayMetrics().heightPixels /4;
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(w, h);
		content.setLayoutParams(llp);

		// 注册定位事件

		marker = getResources().getDrawable(R.drawable.car_icon); // 得到需要标在地图上的资源
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); // 为maker定义位置和边界
		mLocationListener = new BDLocationListener() {
			public void onLocationChanged(BDLocation location) {
				if (location != null) {
					GeoPoint pt = new GeoPoint(
							(int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6));
				}
			}

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReceivePoi(BDLocation poiLocation) {
				// TODO Auto-generated method stub
				if (poiLocation == null) {
					return;
				}

			}
		};
		CheckBox cb = (CheckBox) findViewById(R.id.change_map_type);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				mMapView.setSatellite(arg1);
			}
		});

		mMapView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(current_tab==SAFETY_TAB){
						if(currentFenceIndex==-1&&currentFence!=null){
							GeoPoint geoPoint = mMapView.getMapCenter();//getProjection().fromPixels(mMapView.getWidth()/2,mMapView.getHeight()/2);
							currentFence.latitude=geoPoint.getLatitudeE6()/1e6;
							currentFence.longitude=geoPoint.getLongitudeE6()/1e6;
							moveToCurrentFence();
						}
					}
				}
				Log.e("mMapView", "setOnTouchListener:"+event.getHistorySize()+",event="+event.getAction());
				return false;
			}
		});

		mMapView.regMapTouchListner(new MKMapTouchListener() {

			@Override
			public void onMapLongClick(GeoPoint arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMapDoubleClick(GeoPoint arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMapClick(GeoPoint arg0) {
				// TODO Auto-generated method stub
				if(current_tab==SAFETY_TAB){
					if(currentFenceIndex==-1&&currentFence!=null)
						handler.sendMessage(handler.obtainMessage(MSG_WHAT_NEW_GEOPOINT, arg0));

				}
				Log.e("mMapView", "onMapClick: lat="+arg0.getLatitudeE6()+",lng="+arg0.getLongitudeE6());
			}
		});


		getFenceList();
		initBabyHeader();
	}

	@Override
	protected void onResume() {
		super.onResume();
		int[] groupCheck = { location_tab.getId(), replay_tab.getId(),
				safety_tab.getId() };
		navigation_radiogroup.check(groupCheck[current_tab]);
		// userID = LoginActivity.userID;
		timeZone = LoginActivity.timeZone;

		// Initialize refresh_period
		String idString;
		// if (select_flag == 0) { // 多车监控
		// if (PrefHelper.hasKey(SetRefreshActivity.REFRESH_PERIOD_TEAM)) {
		// idString =
		// PrefHelper.getStringData(SetRefreshActivity.REFRESH_PERIOD_TEAM);
		// } else { // Use default value
		// idString = SetRefreshActivity.REFRESH_PERIOD_DEFAULT_TEAM;
		// }
		// } else
		{ // 单车跟踪
			if (PrefHelper.hasKey(SetRefreshActivity.REFRESH_PERIOD_SINGLE)) {
				idString = PrefHelper
						.getStringData(SetRefreshActivity.REFRESH_PERIOD_SINGLE);
			} else {
				idString = SetRefreshActivity.REFRESH_PERIOD_DEFAULT_SINGLE;
			}
		}
		refresh_period = Integer.parseInt(idString);

		// mMapView.setDrawOverlayWhenZooming(true);
		// Initialize default zoom
		// if (select_flag == 0) {
		// zoom = zoom_team;
		// } else
		if (current_tab == REPLAY_TAB) {
			zoom = zoom_replay;
		}
		if (current_tab == LOCATION_TAB) {
			zoom = zoom_location;
		} else { // select_flag == 2
			zoom = zoom_safety;
		}
		mMapView.getController().setZoom(zoom);
		// mLocationOverlay.enableMyLocation();
		// app.mBMapMan.start();

		initMap();
		mLocationOverlay.enableCompass(); // 打开指南针

	}


	void initBabyHeader(){
		BitmapDrawable cachedImage = (BitmapDrawable) downloadphoto(this,User.curBabys.getHeadIconPath());
		if(cachedImage!=null){
			compoundHeader(cachedImage);
		}
	}

	void compoundHeader(BitmapDrawable cachedImage){
		Bitmap bgImage= ((BitmapDrawable) getResources().getDrawable(R.drawable.bg_car_icon)).getBitmap();
		Bitmap headerBitmap =CommonUtils.toRoundBitmap(cachedImage.getBitmap());

		Bitmap bm =Bitmap.createBitmap(bgImage.getWidth(), bgImage.getHeight(), Config.ARGB_8888);
		Canvas cv = new Canvas(bm);
		cv.drawBitmap(headerBitmap,  new Rect(0, 0, headerBitmap.getWidth(), headerBitmap.getHeight()),new Rect(2, 2, cv.getWidth()-2, cv.getWidth()-2), new Paint());
		cv.drawBitmap(bgImage, new Rect(0, 0, bgImage.getWidth(), bgImage.getHeight()),new Rect(0, 0, cv.getWidth(), cv.getHeight()),  new Paint());

		marker=new BitmapDrawable(bm);
	}

	public  Drawable downloadphoto(Context context,String url){
		AsyncImageLoader imageLoader = new AsyncImageLoader();
		Drawable cachedImage = null;
		cachedImage = imageLoader.loadDrawable(url, context,
				true, new ImageCallback()
		{

			@Override
			public void imageLoaded(Drawable imageDrawable,
					String imageUrl)
			{
				if (imageDrawable != null)
				{
					compoundHeader((BitmapDrawable) imageDrawable);

				}

			}
		});
		return cachedImage;
	}

	OverItemT oTrack;
	OverItemH oHistory;

	@Override
	protected void onPause() {
		super.onPause();

		// MapAPP app = (MapAPP) this.getApplication();
		// app.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		// mLocationOverlay.disableMyLocation();
		mLocationOverlay.disableCompass(); // 关闭指南针
		// app.mBMapMan.stop();
		if (timer != null) {
			timer.cancel();
		}
		if (oHistory != null) {
			play_h.setImageResource(R.drawable.map_pause);
		}
		isback = true;
	}

	/**
	 * 多车监控界面
	 */
	public void initMore() {

	}

	/**
	 * 历史显示
	 */
	String date_b; // Begin Time
	String date_e; // End Time
	Date begin;
	Date end;

	public void initHistory(Intent intent) {
		// setSelect(2);
		if (oHistory != null) {
			return;
		}
		if (timer != null) {
			timer.cancel();
		}

		// next_content.setVisibility(View.GONE);
		// control.setVisibility(View.GONE);
		date_b = intent.getStringExtra("date_b");
		date_e = intent.getStringExtra("date_e");
		((TextView)findViewById(id.time_start)).setText(date_b);
		((TextView)findViewById(id.time_end)).setText(date_e);
		if (date_b == null || date_e == null) {
			return;
		}
		playBar = (SeekBar) findViewById(R.id.playBar);

		mPopView.setVisibility(View.GONE);
		mMapView.refresh();
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(mLocationOverlay);
		marker = getResources().getDrawable(R.drawable.icon_gcoding);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		try {
			begin = dateFormater.parse(date_b);
			end = dateFormater.parse(date_e);
			int i = (int) ((end.getTime() - begin.getTime()) / 10000);
			playBar.setMax(i);
			playBar.setProgress(0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oHistory = new OverItemH(marker, mMapView);
		timer_text.setText(R.string.downloading);
	}

	private RadioOnClick radioOnClick = new RadioOnClick(1);

	public void initRadioOnclick() {

		switch (refresh_period) {
		case 15:
			radioOnClick = new RadioOnClick(0);
			break;
		case 30:
			radioOnClick = new RadioOnClick(1);
			break;
		case 45:
			radioOnClick = new RadioOnClick(2);
			break;
		case 60:
			radioOnClick = new RadioOnClick(3);
			break;
		case 300:
			radioOnClick = new RadioOnClick(4);
			break;
		case 60 * 15:
			radioOnClick = new RadioOnClick(5);
			break;
		}
	}

	class RadioOnClick implements DialogInterface.OnClickListener {
		private int index;

		public RadioOnClick(int index) {
			this.index = index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			setIndex(which);
			int current_time = 15;
			switch (which) {
			case 0:
				current_time = 15;
				break;
			case 1:
				current_time = 30;
				break;
			case 2:
				current_time = 45;
				break;
			case 3:
				current_time = 60;
				break;
			case 4:
				current_time = 300;
				break;
			case 5:
				current_time = 60 * 15;
				break;
			}

			// if (select_flag == 0) {
			// PrefHelper.setInfo(SetRefreshActivity.REFRESH_PERIOD_TEAM,
			// current_time + "");
			// } else
			{
				PrefHelper.setInfo(SetRefreshActivity.REFRESH_PERIOD_SINGLE,
						current_time + "");
			}
			refresh_period = current_time;
			dialog.dismiss();
		}
	}

	/**
	 * 事件处理
	 *
	 * @param view
	 */
	public void things(View view) {
		// Intent intent = new Intent();
		// intent.setClass(this, cls);
		switch (view.getId()) {
		case R.id.text_time:
			initRadioOnclick();
			AlertDialog ad = new AlertDialog.Builder(this)
			.setTitle(R.string.refresh_time_setting)
			.setSingleChoiceItems(refreshTable,
					radioOnClick.getIndex(), radioOnClick).create();
			// ad.getListView();
			ad.show();
			break;
		case R.id.zoomin_ib:
			if ((zoom + 1) > 18) {
				Utils.showToast("已放大至最高级别");
			} else {
				mMapView.getController().setZoom(++zoom);
			}
			break;
		case R.id.zoomout_ib:
			if ((zoom - 1) < 3) {
				Utils.showToast("已缩小至最低级别");
			} else {
				mMapView.getController().setZoom(--zoom);
			}
			break;
			// case R.id.next_down:
			// if (oTrack == null) {
			// Utils.showToast(R.string.downloading);
			// return;
			// }
			// if (ts.size() == 0) {
			// return;
			// }
			// mCurrentindex++;
			// // CommonUtils.showProcessDialog(MyTrace_More.this, "正在获取数据...");
			// oTrack.new initAddress(Math.abs(mCurrentindex %
			// ts.size())).execute();
			// break;
			// case R.id.next_up:
			// if (oTrack == null) {
			// Utils.showToast(R.string.downloading);
			// return;
			// }
			// if (ts.size() == 0) {
			// return;
			// }
			// mCurrentindex--;
			// // CommonUtils.showProcessDialog(MyTrace_More.this, "正在获取数据...");
			// oTrack.new initAddress(Math.abs(mCurrentindex %
			// ts.size())).execute();
			//
			// break;
		case R.id.replay:
			if (isReplay) {
				((ImageView) view).setImageResource(R.drawable.map_pause);
				isReplay = false;
			} else {
				isReplay = true;
				if (oHistory.mPosition == oHistory.total - 1) {
					oHistory.mPosition = 0;
				}
				oHistory.handler.sendEmptyMessage(1);
				((ImageView) view).setImageResource(R.drawable.map_play);
			}
			break;
		case R.id.reflush:
			if (oHistory != null) {
				oHistory.iscolse = true;
				mMapView.getOverlays().remove(oHistory);
			}
			oHistory = null;
			if (timer != null) {
				timer.cancel();
			}
			if (mLocClient != null) {
				mLocClient.stop();
			}
			initMap();
			break;
			// case R.id.map:// 监示停止
			// if (select_flag == 2) {
			// currentMember = null;
			// setSelect(0);
			// initMap();
			// return;
			// }
			// if (timer != null) {
			// ((ImageView) view)
			// .setImageResource(R.drawable.map_control_close);
			// timer.cancel();
			// timer_text.setText(R.string.refresh_is_disabled);
			// Utils.showToast(R.string.refresh_is_disabled);
			// timer = null;
			// PrefHelper.setInfo(SetRefreshActivity.REFRESH_DISABLE, true);
			// } else {
			// PrefHelper.setInfo(SetRefreshActivity.REFRESH_DISABLE, false);
			// ((ImageView) view)
			// .setImageResource(R.drawable.map_control_open);
			// if (timer != null) {
			// timer.cancel();
			// }
			// timer = new Timer();
			// timer.schedule(new TimerTask() {
			// int i = 0;
			//
			// @Override
			// public void run() {
			// handler.sendEmptyMessage(i);
			// if (i == refresh_period) {// 时间设置
			// timer.cancel();
			// }
			// i++;
			// }
			// }, 1000, 1000);
			// }
			// break;
		case R.id.content: // Show more info window
			Intent info = new Intent();
			// BabyInfoBean item = (BabyInfoBean) pop_title.getTag();
			info.setClass(this, InfoActivity.class);
			// info.putExtra("item", item);
			startActivity(info);
			break;
		case R.id.back:
		case R.id.back_btn:
			finish();
			break;
			// case R.id.control_btn:
			// Intent control = new Intent();
			// control.setClass(this, ControlActivity.class);
			// BabyInfoBean item1 = (BabyInfoBean) pop_title.getTag();
			// control.putExtra("item", item1);
			// startActivityForResult(control, 1);
			// break;
			// case R.id.replay_btn:
			// setCuttentTab(SAFETY_TAB);
			// break;
			// case R.id.current_location_btn:
			// setSelect(1);
			// //findViewById(R.id.seek_content).setVisibility(View.GONE);
			// mLocClient.start(); // 调用此方法开始定位
			// break;
		case R.id.safety_tab:
			setCuttentTab(SAFETY_TAB);

			break;
		case R.id.replay_tab: {
			setCuttentTab(REPLAY_TAB);

		}
		break;
		case R.id.location_tab:
			setCuttentTab(LOCATION_TAB);

			break;

		case R.id.device_bt:
			DevicesUtils.sendCMDToDevice(this,User.id,User.curBabys.getId(),"DW","",null);
			//oTrack.new initAddress(0).execute();
			new initData().execute();
			break;
		case R.id.location_bt:
			mLocClient.start(); // 调用此方法开始定位

			break;
		case R.id.replayTime:
		{
			//setCuttentTab(REPLAY_TAB);
			Intent intent = new Intent();
			intent.setClass(MyTrace_More_Baidu.this, SelectReplayDate.class);
			startActivityForResult(intent, 1000);
		}
		break;
		case R.id.add_ef_tv:
		{
			addNewFence();
		}
		break;
		case R.id.fence_list_btn:
			View vi = findViewById(R.id.ef_ll);
			if (vi.getVisibility() == View.GONE)
				vi.setVisibility(View.VISIBLE);
			else
				vi.setVisibility(View.GONE);
			break;
		}
	}

	int curren;
	Handler pro_handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (prog.getProgress() == 120 && msg.what < 120) {
				return;
			}
			if (msg.what > 120) {
				prog.setVisibility(View.GONE);
				Utils.showToast(R.string.picture_upload_failed);
				return;
			}
			if (curren % 10 == 0) {
				new initData().execute();
			}
			prog.setProgress(curren);
			if (msg.what <= 120) {
				pro_handler.sendEmptyMessageDelayed(curren, 1000); // 1second
				// later
				curren++;
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==1000){
			if (resultCode == SelectReplayDate.success) {
				date_b = data.getStringExtra("date_b");
				date_e = data.getStringExtra("date_e");
				getIntent().putExtra("date_b", date_b);
				getIntent().putExtra("date_e", date_e);
				isReplay = true;
				initHistory(getIntent());

				// currentMember = (TeamMember) pop_title.getTag();
				text_name.setText(currentMember.getNickName());
				getIntent().putExtra(CURRENT_TAB_STR, REPLAY_TAB);
			} else {
				findViewById(R.id.seek_content).setVisibility(View.GONE);
			}
		}
		else
			if(requestCode==2000&&resultCode==Activity.RESULT_OK){
				currentFenceIndex=data.getIntExtra("FenceListInedx", -1);
				String action =data.getStringExtra("action");
				if(action=="del"){
					delFecne(currentFenceIndex);

				}
				else{
					FenceBaseInfo fbi = (FenceBaseInfo) data.getSerializableExtra("fence");
					String s1=FenceBaseInfo.FenceToJSONStr(fbi);
					String s2=FenceBaseInfo.FenceToJSONStr(currentFence);
					if(s1!=s2){
						FenceBaseInfo.getFenceByJson(FenceBaseInfo.FenceToJSONStr(fbi),currentFence);
						if(currentFenceIndex==-1)
							currentMember.fencelist.add(currentFence);
						saveFenceListToServer();
					}
					initFenceListView();
				}

			}

		super.onActivityResult(requestCode, resultCode, data);
	}

	final int MSG_WHAT_REFLESH=1;
	final int MSG_WHAT_NEW_FENCE=2;
	final int MSG_WHAT_NEW_GEOPOINT=3;
	int refreshIndex=0;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (isback) {
				return;
			}

			switch(msg.what){
			case MSG_WHAT_REFLESH:
				if (!PrefHelper.getBooleanData(SetRefreshActivity.REFRESH_DISABLE)) {
					if (msg.arg1 < 0 || msg.arg1 >= refresh_period) { // 时间设置
						timer_text.setText(R.string.refreshing);
						text_date.setText(dateFormater.format(new Date()));
						stopLocClient();
						refreshIndex++;
						if(refreshIndex%6==0)
							DevicesUtils.sendCMDToDevice(getApplicationContext(),User.id,User.curBabys.getId(),"DW","",null);
						new initData().execute();
					} else {
						timer_text.setText((refresh_period - msg.arg1)
								+ getString(R.string.second) + "后刷新");
					}
				}
				break;
			case MSG_WHAT_NEW_FENCE:
				break;
			case MSG_WHAT_NEW_GEOPOINT:
			{
				GeoPoint geoPoint = (GeoPoint) msg.obj;//mMapView.getMapCenter();//getProjection().fromPixels(mMapView.getWidth()/2,mMapView.getHeight()/2);
				currentFence.latitude=geoPoint.getLatitudeE6()/1e6;
				currentFence.longitude=geoPoint.getLongitudeE6()/1e6;
				moveToCurrentFence();
			}
			break;

			}

		}
	};

	boolean initGeoPoint() {

		try {
			String result = "";
			int deviceID = -1;
			if (currentMember != null) { // Single car track
				deviceID = Integer.parseInt(currentMember.getId());
			}
			WebService webservice = new WebService(MyTrace_More_Baidu.this,
					"GetTrackingByUserID");
			LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
			linkedlist.add(new WebServiceProperty("UserID", User.id));
			linkedlist.add(new WebServiceProperty("DeviceID", deviceID));
			linkedlist.add(new WebServiceProperty("MapType", "Baidu"));
			webservice.SetProperty(linkedlist);
			result = webservice.Get("GetTrackingByUserIDResult",
					WebService.URL_OPEN);

			JSONObject jsonObj = new JSONObject(result);
			if (jsonObj.has("Status") && (jsonObj.getInt("Status") == 0)) {
				JSONArray ja = jsonObj.getJSONArray("devices");
				String[] strs = ja.toString().replace("[", "").replace("]", "")
						.replace("\"", "").split(",");
				int count = strs.length;
				for (int i = 0; i < count; i++) {
					if (strs[i] == null || strs[i].equals("")) {
						continue;
					}

					String[] info = strs[i].split(";");
					Trace t = new Trace();
					t.initTrace(info);
					myTraces = t;
					currentMember.traces = myTraces;

				}
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// CommonUtils.showToast("数据刷新错误");
			e.printStackTrace();
		}
		return false;
	}

	public class initData extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			if (!initload && currentMember != null) {
				initload = true;

				myTraces = currentMember.traces;
				Log.v("initData", "initload=false");

				return initload;
			}

			return initGeoPoint();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (!result) {
				handler.sendMessage(handler.obtainMessage(MSG_WHAT_REFLESH,10,0));
				return;
			}
			if (isback) {
				return;
			}

			initmapinfo();
		}
	}

	ArrayList<Trace> ts = new ArrayList<Trace>();

	public void initmapinfo() {
		oHistory = null;
		isReplay = false;
		if (current_tab == LOCATION_TAB) {
			text_name.setText(currentMember.getNickName());
			// if ("1".equals(currentMember.get)) {
			// title.setText(R.string.car_track);
			// } else {
			title.setText(R.string.team_track_title);
			// }

			// next_content.setVisibility(View.GONE);

		} else {
			title.setText(R.string.team_track_title);
			// next_content.setVisibility(View.VISIBLE);
		}

		if (timer != null)
			timer.cancel();
		timer = null;
		if (!PrefHelper.getBooleanData(SetRefreshActivity.REFRESH_DISABLE)) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				int i = 0;

				@Override
				public void run() {
					handler.sendMessage(handler.obtainMessage(MSG_WHAT_REFLESH,i,0));
					//handler.sendEmptyMessage(i);
					if (i == refresh_period) {// 时间设置
						timer.cancel();
					}
					i++;
				}
			}, 1000, 1000);
		} else {
			control.setImageResource(R.drawable.map_control_close);
			if (timer != null)
				timer.cancel();
			timer_text.setText(R.string.refresh_is_disabled);
			timer = null;
		}

		if (myTraces != null)
			map.put(myTraces.id, myTraces);

		ts.clear();
		for (Map.Entry<String, Trace> value : map.entrySet()) {
			if (current_tab == LOCATION_TAB
					|| value.getValue().id.equals(currentMember.getId())) {
				ts.add(value.getValue());
				if (currentMember != null
						&& currentMember.getId().equals(value.getValue().id)) {
					mCurrentindex = ts.indexOf(value.getValue());
				}
			}
		}
		mMapView.refresh();
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(mLocationOverlay);
		if (oTrack == null) {
			oTrack = new OverItemT(marker, mMapView);
		}
		oTrack.initItems(ts);
		mMapView.getOverlays().add(oTrack);
	}

	public void initMap() {
		// ////////////////////开启
		if (current_tab == LOCATION_TAB) {
			isback = false;
			timer_text.setText(R.string.refreshing);
			findViewById(R.id.seek_content).setVisibility(View.GONE);
			new initData().execute();
		} else { // select_flag = 2; History replay
			isback = false;
			// isReplay = true;
			initHistory(getIntent());
		}
	}

	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopLocClient();
		MapAPP.handler_map = null;
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		// TODO Auto-generated method stub
		if (arg0 == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(arg0, arg1);
	}

	// 历史回放
	class OverItemH extends ItemizedOverlay<MyOverLayItem> {
		MyOverLayItem mCurrent;
		private final List<MyOverLayItem> mGeoList_current = new ArrayList<MyOverLayItem>();
		// private final ArrayList<Trace> traces = new ArrayList<Trace>();
		public boolean iscolse = false;
		private String nextStart = "";

		int mPosition = 0;
		public int total;
		Paint paint = new Paint();

		public OverItemH(Drawable marker, MapView mapView) {
			super(marker, mapView);
			// this.mContext = context;
			paint.setColor(Color.BLUE);
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setSubpixelText(true);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(4);
			new initPath().execute();
		}

		public class initPath extends AsyncTask<Void, Void, ArrayList<ItemH>> {

			@Override
			protected ArrayList<ItemH> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					WebService webservice = new WebService(
							MyTrace_More_Baidu.this, "GetDeviceHistory");
					LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
					linkedlist.add(new WebServiceProperty("DeviceID", Integer
							.parseInt(currentMember.getId())));
					linkedlist.add(new WebServiceProperty("StartTime",
							(nextStart.equals("") ? date_b : nextStart)));
					linkedlist.add(new WebServiceProperty("EndTime", date_e));
					linkedlist
					.add(new WebServiceProperty("TimeZone", timeZone));
					linkedlist.add(new WebServiceProperty("ShowLBS", 0)); // Drop
					// LBS
					// positions
					linkedlist.add(new WebServiceProperty("MapType", "Baidu"));
					linkedlist.add(new WebServiceProperty("SelectCount", 3000));
					webservice.SetProperty(linkedlist);
					String result = webservice.Get("GetDeviceHistoryResult",
							WebService.URL_OPEN);

					JSONObject jsonObj = new JSONObject(result);
					ArrayList<ItemH> items = new ArrayList<ItemH>();
					if (BuildConfig.DEBUG) {
						System.out.println(result);
					}
					if (jsonObj.has("Status") && jsonObj.getInt("Status") == 0) {
						nextStart = jsonObj.getString("nextStart");
						JSONArray ja = jsonObj.getJSONArray("history");
						String datas[] = ja.toString().replace("[", "")
								.replace("]", "").replace("\"", "").split(",");

						int count = datas.length;
						for (int i = 0; i < count; i++) {
							String[] strs = datas[i].split(";");
							// CommonUtils.showToast("Add ItemH: " + datas[i]);
							ItemH item = new ItemH();
							item.time = strs[0];

							try {
								item.lat = Double.parseDouble(strs[1]);
								item.lng = Double.parseDouble(strs[2]);
								item.olat = Double.parseDouble(strs[3]);
								item.olng = Double.parseDouble(strs[4]);

								item.speed = strs[5];
								if (strs[6] == null) {
									item.angle = 0;
								} else {
									item.angle = Integer.parseInt(strs[6]);
								}
								// !!!WARNING!!! strs[7] could not be existed!
								if (strs.length == 8) {
									item.status = strs[7];
								} else {
									item.status = "0";
								}
							} catch (Exception e) {
								continue;
							}

							items.add(item);
						}

					}
					return items;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					Utils.showToast(R.string.no_record);
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<ItemH> result) {
				// TODO Auto-generated method stub
				if (result == null || result.size() == 0) {
					Utils.showToast(R.string.no_record);
					findViewById(R.id.seek_content).setVisibility(View.GONE);
					return;
				}
				findViewById(R.id.seek_content).setVisibility(View.VISIBLE);
				for (ItemH item : result) {
					GeoPoint p = new GeoPoint((int) (item.lat * 1000000),
							(int) (item.lng * 1000000));
					MyOverLayItem oi = new MyOverLayItem(p, "" + item.lat, ""
							+ item.lng, item.angle, item.speed, item.time, "");
					oi.setAnchor(OverlayItem.ALING_CENTER);
					mGeoList_current.add(oi);
				}
				if (mPosition != 0 && mPosition >= total) {
					total += result.size();
					handler.sendEmptyMessage(0);
				} else {
					total += result.size();
				}
				if (mCurrent == null) {
					mMapView.getOverlays().clear();
					mMapView.getOverlays().add(mLocationOverlay);
					mMapView.getOverlays().add(oHistory);
					mCurrent = mGeoList_current.get(0);
					mMapView.getController().setZoom(zoom_replay);
					mMapView.getController().animateTo(mCurrent.getPoint());
					handler.sendEmptyMessage(0);
					addItem(mCurrent);
					mMapView.refresh();
				}
				if ("".equals(nextStart)) {
					return;
				}
				if (currentMember != null && current_tab == REPLAY_TAB) {
					new initPath().execute();
				}
				super.onPostExecute(result);
			}
		}

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (iscolse) {
					return;
				}
				if (isback) {
					return;
				}
				if (!isReplay) {
					return;
				}

				// History Replay start...

				// populate();
				// mMapView.postInvalidate();
				// mCurrent = mGeoList_current.get(mPosition
				// % mGeoList_current.size());
				// getAllItem().clear();

				mPosition++;
				text_date.setText(mCurrent.date);
				// timer_text.setText(mCurrent.speed + "km/h");
				if (mPosition % 25 == 0) {
					mMapView.getController().animateTo(mCurrent.getPoint());
				}
				if (mPosition >= total) {
					mPosition = total - 1;
					int i = (int) ((end.getTime() - begin.getTime()) / 10000);
					playBar.setProgress(i);
					// timer_text.setText("0km/h");
					isReplay = false;
					return;
				}
				try {
					Date c_time = dateFormater.parse(mCurrent.date);
					int i = (int) ((c_time.getTime() - begin.getTime()) / 10000);
					playBar.setProgress(i);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessageDelayed(1, 150);
				dar();
				OverItemH.this.removeItem(mCurrent);
				mCurrent = mGeoList_current.get(mPosition
						% mGeoList_current.size());
				OverItemH.this.addItem(mCurrent);
				mMapView.refresh();
			};
		};

		GraphicsOverlay graphicsOverlay;
		Symbol lineSymbol = new Symbol();

		public void dar() {
			if (graphicsOverlay == null) {
				graphicsOverlay = new GraphicsOverlay(mMapView);
				mMapView.getOverlays().add(graphicsOverlay);
				Symbol.Color lineColor = lineSymbol.new Color();
				lineColor.red = 255;
				lineColor.green = 0;
				lineColor.blue = 0;
				lineColor.alpha = 255;
				Stroke stroke = new Stroke(5, lineSymbol.new Color(0x00000000));
				lineSymbol.setSurface(lineColor, 0, 5, stroke);// 设置样式参数，颜色：palaceColor是否填充距形：是线
			}
			graphicsOverlay.setData(drawLine());
		}

		public Graphic drawLine() {
			// 设定折线点坐标
			Geometry lineGeometry = new Geometry();
			int start = mPosition > 5 ? mPosition - 5 : 0;
			GeoPoint[] linePoints = new GeoPoint[mPosition - start];
			for (int i = start; i < mPosition; i++) {
				OverlayItem overLayItem = mGeoList_current.get(i);
				linePoints[i - start] = overLayItem.getPoint();
			}
			lineGeometry.setPolyLine(linePoints);
			// 设定样式
			// 生成Graphic对象
			mCurrent.setGeoPoint(linePoints[linePoints.length - 1]);

			Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
			return lineGraphic;
		}



		@Override
		protected MyOverLayItem createItem(int i) {
			// TODO Auto-generated method stub
			return mCurrent;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 1;
		}

	}

	public Drawable initMarker(int angle) {
		/*if (currentMember != null) {
			BitmapDrawable bd = (BitmapDrawable) marker;
			Bitmap bit = bd.getBitmap();
			Matrix m = new Matrix();
			m.setRotate(angle, bit.getWidth() / 2, bit.getHeight() / 2);
			bit = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
					bit.getHeight(), m, true);
			marker = new BitmapDrawable(bit);
		}*/

		return marker;
	}

	int drawableids[][] = new int[][] {
			{ R.drawable.group_car_h, R.drawable.group_car,
				R.drawable.group_car },// 0离线 1 静止2运动
				{ R.drawable.people_location_offline,
					R.drawable.people_location_offline,
					R.drawable.people_location_online } };

	public int mCurrentindex;
	public static int em = 0;

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		em = 0;
		super.finish();
	}

	class OverItemT extends ItemizedOverlay<MyOverLayItem> {

		private final ArrayList<MyOverLayItem> mGeoList = new ArrayList<MyOverLayItem>();
		private final Drawable marker;
		// private final Context mContext;
		private ArrayList<Trace> traces = new ArrayList<Trace>();
		MyOverLayItem mCurrent;
		Paint paint = new Paint();

		public OverItemT(Drawable marker, MapView mapView) {
			super(marker, mapView);
			this.marker = marker;
			// this.mContext = context;
			// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
			// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段

			// new initPath().execute();
			paint.setColor(Color.BLUE);
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setSubpixelText(true);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(4);
			singleitem.clear();
			// handler_draw.sendEmptyMessage(1);
			map_draw.sendEmptyMessage(em);
		}

		GraphicsOverlay graphicsOverlay;
		Symbol lineSymbol = new Symbol();

		Handler map_draw = new Handler() { // 报警画圆
			@Override
			public void handleMessage(Message msg) {

			};
		};

		int mPosition = 0;
		List<MyOverLayItem> singleitem = new ArrayList<MyOverLayItem>();

		public void drawLine() {// 跟踪画线

			Geometry lineGeometry = new Geometry();

			// 设定折线点坐标
			System.out.println("poso==1=" + singleitem.size());

			int count = singleitem.size();
			if (count < 2) {
				return;
			}
			if (mPosition >= singleitem.size()) {
				return;
			}
			mPosition++;
			lineGeometry = new Geometry();
			int start = mPosition > 3 ? mPosition - 3 : 0;
			GeoPoint[] linePoints = new GeoPoint[count - start];
			for (int i = start; i < count; i++) {
				OverlayItem overLayItem = singleitem.get(i);
				linePoints[i - start] = overLayItem.getPoint();
			}

			if (linePoints.length == 0) {
				return;
			}
			lineGeometry.setPolyLine(linePoints);
			Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
			graphicsOverlay.setData(lineGraphic);

		}

		int i = 0;

		public Drawable initMarker(int angle) {// 箭头方向改变
			/*BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.car_icon);
			Bitmap bit = bd.getBitmap();
			Matrix m = new Matrix();
			m.setRotate(angle, bit.getWidth() / 2, bit.getHeight() / 2);
			bit = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
					bit.getHeight(), m, true);
			marker = new BitmapDrawable(bit);*/
			//marker=getResources().getDrawable(R.drawable.car_icon);
			return marker;
		}

		public void initItems(ArrayList<Trace> traces) {
			mGeoList.clear();
			getAllItem().clear();
			this.traces = traces;
			for (int i = 0; i < traces.size(); i++) {
				int lat = traces.get(i).lat;
				int lon = traces.get(i).lng;
				Trace ts = traces.get(i);
				/*	String[] tps = ts.alarm_state.split(":");
				if (tps.length >= 1
						&& RegularUtils.getNumber(tps[tps.length - 1])) {
					if (em == 0) {
						map_draw.sendEmptyMessage(em);
						em++;
					}
				}*/
				GeoPoint p = new GeoPoint(lat, lon);
				int location = 0;
				try {
					location = Integer.parseInt(traces.get(i).direction);
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}// ///
				MyOverLayItem oi = new MyOverLayItem(p, "" + traces.get(i).lat,
						"" + traces.get(i).lng, location, traces.get(i).speed,
						"", traces.get(i).alarm_state);

				oi.setAnchor(OverlayItem.ALING_CENTER);
				if (current_tab == LOCATION_TAB) {
					oi.setMarker(initMarker(oi.course));
				}
				addItem(oi);
				mGeoList.add(oi);
			}
			if (this.traces.size() == 1 && current_tab == LOCATION_TAB) {
				mCurrent = mGeoList.get(0);
				if (mCurrent != null) {
					if (singleitem.size() == 0
							|| (singleitem.size() > 0 && (mCurrent.getPoint()
									.getLatitudeE6() != singleitem
									.get(singleitem.size() - 1).getPoint()
									.getLatitudeE6() && mCurrent.getPoint()
									.getLongitudeE6() != singleitem
									.get(singleitem.size() - 1).getPoint()
									.getLongitudeE6()))) {
						singleitem.add(mCurrent);
						// mCurrent.setMarker(initMarker(mCurrent.angle));
						updateItem(mCurrent);
						if (graphicsOverlay == null) {
							graphicsOverlay = new GraphicsOverlay(mMapView);
							Symbol.Color lineColor = lineSymbol.new Color();
							lineColor.red = 255;
							lineColor.green = 0;
							lineColor.blue = 0;
							lineColor.alpha = 255;
							Stroke stroke = new Stroke(5, lineSymbol.new Color(
									0x00000000));
							lineSymbol.setSurface(lineColor, 0, 5, stroke);// 设置样式参数，颜色：palaceColor是否填充距形：是线
						}
						drawLine();
						mCurrent.setMarker(initMarker(mCurrent.course));
						updateItem(mCurrent);
						mMapView.refresh();
					}
				}
				mMapView.getController().animateTo(mCurrent.getPoint());
				mMapView.getOverlays().add(graphicsOverlay);
				new initAddress(0).execute();
			}
			// else {
			// singleitem.clear();
			// // CommonUtils.showProcessDialog(MyTrace_More.this, "正在获取数据...");
			// new initAddress(mCurrentindex).execute();
			// }
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mMapView.refresh();
				}
			});
		}

		@Override
		protected MyOverLayItem createItem(int i) {
			// TODO Auto-generated method stub
			return mGeoList.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mGeoList.size();
		}

		public class initAddress extends AsyncTask<Void, Void, String> {
			int i = 0;
			GeoPoint pt;

			public initAddress(int p) {
				i = p;
				if (i > mGeoList.size() - 1) {
					return;
				}
				pt = mGeoList.get(i).getPoint();
				mMapView.getController().animateTo(pt);
				showPop(pt);
				Trace ts = traces.get(i);
				mCurrentindex = i;
				// CommonUtils.dismissProcessDialog();
				// Item item = TeamActivity.items_map.get(t.id);
				// int type = Integer.parseInt(item.on);
				String carStatus = "";
				int type = Integer.parseInt(ts.car_state);
				// 0 LogOff; 1 Move; 2 Stop; 3 Offline; 4 Arrears
				switch (type) {
				case 0:
					carStatus = getString(R.string.offline);
					break;
				case 1:
					carStatus = getString(R.string.online);// + "    " +
					// ts.speed +
					// "km/h";
					break;

				}

				/*
				 * Device State Bits[ 4 3 2 1 0 ] Bit[4] 0:PowerOn; 1: PowerLost
				 * Bit[3] 0:ACC Off; 1: ACC On Bit[2] 0:Door Closed; 1:Door
				 * Opened Bit[1] 0:油门开; 1: 油门断 Bit[0] 0:DisArm(撤防); 1: Fortified
				 * (已设防)
				 */
				type = Integer.parseInt(ts.car_state);
				String deviceStatus = "";
				/*int tempShift = 0;
				for (; tempShift < 5; tempShift++) {
					if (((type >> tempShift) & 0x1) == 0x1) {
						deviceStatus += CAR_STATE[tempShift + 1] + ",";
						if (tempShift == 4) {
							deviceStatus = CAR_STATE[tempShift + 1]; // PowerLost
							// is
							// special
						}
					}
				}
				if (deviceStatus.equals("")) {
					deviceStatus = CAR_STATE[0];
				}
				if (deviceStatus.endsWith(",")) {
					deviceStatus = deviceStatus.substring(0,
							deviceStatus.length() - 1);
				}
				 */
				/*
				 * String[] tps = ts.alarm_state.split(":"); if (tps.length >= 1
				 * && RegularUtils.getNumber(tps[tps.length - 1])) { typename +=
				 * "," + CAR_ALARM[Integer.parseInt(tps[tps.length - 1])]; }
				 */

				// currentMember = new BabyInfoBean();
				// currentMember.setNickName(TeamActivity.memberList.get(ts.id).name);
				// currentMember.type = TeamActivity.memberList.get(ts.id).type;
				currentMember.setStatusInf(carStatus);
				// currentMember.device_state = deviceStatus;
				currentMember.setId(ts.id);
				currentMember.setLat(ts.real_lat);
				currentMember.setLng(ts.real_lng);

				pop_title.setTag(currentMember);

				pop_title.setText(currentMember.getNickName());
				text_name.setText(currentMember.getNickName());
				pop_device_inf.setVisibility(View.VISIBLE);
				pop_posType.setVisibility(View.VISIBLE);
				pop_state.setText(carStatus);
				pop_device_state.setText(deviceStatus);
				pop_posTime.setText(ts.pos_time);

				pop_posType.setText(ts.pos_type);
				pop_address.setText(R.string.downloading);
			}

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					WebService webservice = new WebService(
							MyTrace_More_Baidu.this, "GetAddressByLatlng");
					LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
					linkedlist.add(new WebServiceProperty("Lat",
							traces.get(i).real_lat));
					linkedlist.add(new WebServiceProperty("Lng",
							traces.get(i).real_lng));
					linkedlist.add(new WebServiceProperty("MapType", ""));//Baidu

					String language = "zh-cn";
					if (!timeZone.equals("China Standard Time")) {
						language = "en-us";
					}
					linkedlist
					.add(new WebServiceProperty("Language", language));
					webservice.SetProperty(linkedlist);
					String ss = webservice.Get("GetAddressByLatlngResult",
							WebService.URL_OPEN);
					/*String[] str = ss.split("\\.");
					if (str.length == 0)
						str = ss.split(",");
					if (str.length == 0)
						str = ss.split("。");
					if (str.length == 0)
						str = ss.split("，");
					if (str.length != 0)
						ss = str[0];*/
					Log.d("GetAddressByLatlngResult", ss);
					return ss;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub

				if (mCurrentindex == i && currentMember != null) {

					pop_address.setText(result);
					currentMember.setAddress(result);
					pop_title.setTag(currentMember);
					showPop(pt);
				}
				super.onPostExecute(result);
			}
		}

		@Override
		// 处理当点击事件
		protected boolean onTap(int i) {
			// 更新气泡位置,并使之显示
			// GeoPoint pt = mGeoList.get(i).getPoint();
			mPopView.setVisibility(View.VISIBLE);

			// showInfoWindow();

			return true;
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			// 消去弹出的气泡
			mPopView.setVisibility(View.GONE);

			return super.onTap(arg0, arg1);
		}

	}

	public void showPop(GeoPoint gp) {
		int w = 280 * getResources().getDisplayMetrics().widthPixels / 480;
		MapView.LayoutParams ml = new MapView.LayoutParams(w,
				MapView.LayoutParams.WRAP_CONTENT, gp,
				MapView.LayoutParams.CENTER_HORIZONTAL
				| MapView.LayoutParams.BOTTOM);
		ml.y -= 40;
		mMapView.updateViewLayout(mPopView, ml);
		mPopView.setVisibility(View.VISIBLE);
	}

	public String minToDateDiff(long min) {
		long n = min;
		long diffm = n % 60;
		n = (n - diffm) / 60;
		long diffh = n % 24;
		n = (n - diffh) / 24;
		long diffd = n;

		return (diffd > 0 ? (diffd + getString(R.string.day)) : "")
				+ (diffh > 0 ? (diffh + getString(R.string.hour)) : "")
				+ (diffm > 0 && diffd == 0 ? (diffm + getString(R.string.minute))
						: "");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * 定位接口，需要实现两个方法
	 *
	 * @author xiaanming
	 *
	 */
	public class BDLocationListenerImpl implements BDLocationListener {

		/**
		 * 接收异步返回的定位结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}

			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			Log.e("log", sb.toString());

			MyTrace_More_Baidu.this.location = location;

			mLocData.latitude = location.getLatitude();
			mLocData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			mLocData.accuracy = location.getRadius();
			mLocData.direction = location.getDerect();

			// 将定位数据设置到定位图层里
			mLocationOverlay.setData(mLocData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();

			// 将给定的位置点以动画形式移动至地图中心
			mMapView.getController().animateTo(
					new GeoPoint((int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6)));
			// showPopupOverlay(location);
			pop_address.setText(location.getAddrStr());

			pop_device_inf.setVisibility(View.GONE);
			pop_title.setText("我的位置");

			pop_posType.setVisibility(View.GONE);
			GeoPoint pt = new GeoPoint((int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			showPop(pt);
		}

		/**
		 * 接收异步返回的POI查询结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}

	private void stopLocClient() {
		if (mLocClient != null) {
			mLocClient.stop();
		}
	}

	//

	void getFenceList() {
		DevicesUtils.getDevicePenceList(this, currentMember.getId(), timeZone,
				"Baidu", new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				String msg;
				if (result != null) {// 错误信息
					msg = result;
					Utils.showToast(msg);
				} else {// 正确信息

					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(data);

						JSONArray arr = jsonObj
								.getJSONArray("geofences");
						if (arr != null) {
							currentMember.fencelist.clear();
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jobject = arr
										.getJSONObject(i);
								FenceBaseInfo fence = FenceBaseInfo
										.getFenceByJson(jobject.toString(),null);
								currentMember.fencelist.add(fence);
							}
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//
						Utils.showToast("获取数据失败！");
					}
				}

			}
		});
	}

	void saveFenceListToServer(){
		DevicesUtils.saveDevicePence(this, User.id, currentMember.getId(), currentFence.fenceName, currentFence.remark, currentFence.latitude, currentFence.longitude, currentFence.radius, currentFence.fenceId, currentFence.fenceType, "Baidu",new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				if (result != null) {// 错误信息

					Utils.showToast(result);
				} else {// 正确信息

					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(data);

						if (jsonObj.has("Status") && (jsonObj.getInt("Status") == 0)) {
							Utils.showToast(jsonObj.getString("Msg"));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Utils.showToast("数据格式错误！");
					}
				}
			}
		} );
	}

	void delFecne(int index){
		if(index!=-1)
		{

			DevicesUtils.delFence(this,currentMember.getId(),currentMember.fencelist.get(index).fenceId+"",new WebServiceResult() {

				@Override
				public void webServiceResult(String result, String data) {
					// TODO Auto-generated method stub
					if (result != null) {// 错误信息

						Utils.showToast(result);
					} else {// 正确信息

					}
				}
			} );
			currentMember.fencelist.remove(index);
		}
		initFenceListView();
	}

	void addNewFence(){
		GeoPoint geoPoint = mMapView.getMapCenter();//mMapView.getProjection().fromPixels(mMapView.getWidth()/2,mMapView.getHeight()/2);
		currentFence  = new FenceBaseInfo();
		currentFence.latitude=geoPoint.getLatitudeE6()/1e6;
		currentFence.longitude=geoPoint.getLongitudeE6()/1e6;
		currentFenceIndex=-1;
		moveToCurrentFence();
	}

	void initFenceListView() {

		if(fenceOverlay!= null){
			fenceOverlay.removeAll();
			fenceOverlay=null;
		}

		if(markItem!=null){
			markItem.removeAll();
			markItem=null;
		}
		mMapView.refresh();

		final ListView fenceListView = (ListView) findViewById(R.id.fence_listview);
		ArrayList<Map<String, Object>> namelist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < currentMember.fencelist.size(); i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			String name =currentMember.fencelist.get(i).fenceName;
			if(name.isEmpty())name="未设置";
			item.put("title", name);
			namelist.add(item);
		}
		SimpleAdapter simAdpter = new SimpleAdapter(this, namelist,R.layout.item_fence_list, new String[] { "title" },new int[] { R.id.title });
		fenceListView.setAdapter(simAdpter);

		fenceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				currentFenceIndex=arg2;
				currentFence = currentMember.fencelist.get(arg2);
				moveToCurrentFence();
			}
		});

	}


	void moveToCurrentFence(){

		drawFence();
		fenceOverlay.setData(fenceDrawLine(currentFence.latitude, currentFence.longitude,currentFence.radius));
		mMapView.refresh();
		findViewById(R.id.safety_edit_Layout).setVisibility(View.VISIBLE);
		findViewById(R.id.layout_fence_title).setVisibility(View.VISIBLE);
		TextView tv=(TextView) findViewById(R.id.fence_title);
		tv.setText(currentFence.fenceName);
		Button btn_edit=(Button) findViewById(R.id.safety_edit_btn);
		Button btn_del = (Button) findViewById(R.id.safety_del_btn);
		if(currentFenceIndex==-1){
			btn_edit.setText("确认");
			btn_del.setText("取消");
		}else{
			btn_edit.setText("编辑");
			btn_del.setText("删除");
		}
		btn_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				findViewById(R.id.safety_edit_Layout).setVisibility(View.INVISIBLE);
				findViewById(R.id.layout_fence_title).setVisibility(View.INVISIBLE);
				Intent intent = new Intent();
				intent.putExtra("fence",currentFence);
				intent.putExtra("action", "edit");
				intent.putExtra("FenceListInedx", currentFenceIndex);
				intent.setClass(MyTrace_More_Baidu.this, FenceSettingActivity.class);
				startActivityForResult(intent,2000);

			}
		});

		btn_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				findViewById(R.id.safety_edit_Layout).setVisibility(View.INVISIBLE);
				findViewById(R.id.layout_fence_title).setVisibility(View.INVISIBLE);
				delFecne(currentFenceIndex);
				//currentMember.fencelist.remove(currentFence);
				//initFenceListView();


			}
		});
	}





	class OverItemM extends ItemizedOverlay<MyOverLayItem> {
		MyOverLayItem mCurrent;

		Paint paint = new Paint();

		public OverItemM(Drawable marker, MapView mapView) {
			super(marker, mapView);

		}
	}
	GraphicsOverlay fenceOverlay = null;
	Symbol fenceSymbol = new Symbol();
	OverItemM markItem ;
	int currentFenceIndex;
	FenceBaseInfo currentFence;
	void drawFence() {
		// 添加圆
		if (fenceOverlay == null) {
			fenceOverlay = new GraphicsOverlay(mMapView);
			// mMapView.getOverlays().add(fenceOverlay);
			Symbol.Color color = fenceSymbol.new Color();
			color.red = 31;
			color.green = 147;
			color.blue = 235;
			color.alpha = 120;
			Stroke stroke = new Stroke(0, fenceSymbol.new Color(0x502092eb));
			fenceSymbol.setSurface(color, 1, 0, stroke);// 设置样式参数，颜色：palaceColor是否填充距形：是线
			// mMapView.getOverlays().clear();
			mMapView.getOverlays().add(fenceOverlay);
			mMapView.refresh();
		}

	}

	public Graphic fenceDrawLine(double lat, double lng, int radius) {
		// 设定折线点坐标

		GeoPoint point = new GeoPoint((int) (lat * 1e6), (int) (lng * 1e6));
		if(markItem==null){
			marker = getResources().getDrawable(R.drawable.item_homework_icon); // 得到需要标在地图上的资源
			marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					marker.getIntrinsicHeight()); // 为maker定义位置和边界
			markItem=new OverItemM(marker, mMapView);

			OverlayItem  oi = new OverlayItem(point,null,null);

			oi.setAnchor(OverlayItem.ALING_CENTER);

			oi.setMarker(marker);

			markItem.addItem(oi);

			mMapView.getOverlays().add(markItem);
		}
		markItem.getItem(0).setGeoPoint(point);
		markItem.updateItem(markItem.getItem(0));


		fenceOverlay.removeAll();

		mMapView.getController().animateTo(point);

		Geometry circleeometry = new Geometry();
		circleeometry.setCircle(point, radius);
		// 设定样式
		// 生成Graphic对象
		Graphic graphic = new Graphic(circleeometry, fenceSymbol);
		return graphic;
	}

	// private void setSelect(int select){
	// select_flag = select;
	// }

	private void setCuttentTab(int tab) {

		switch (current_tab) {
		case LOCATION_TAB:
			if (timer != null)
				timer.cancel();
			timer = null;
			mPopView.setVisibility(View.INVISIBLE);
			findViewById(R.id.device_bt).setVisibility(View.INVISIBLE);
			findViewById(R.id.location_bt).setVisibility(View.INVISIBLE);
			break;
		case SAFETY_TAB:
			findViewById(id.layout_safety).setVisibility(View.INVISIBLE);
			findViewById(R.id.safety_edit_Layout).setVisibility(View.GONE);
			findViewById(R.id.layout_fence_title).setVisibility(View.GONE);
			if(markItem!=null)
				markItem.removeAll();
			mMapView.getOverlays().remove(markItem);
			if(fenceOverlay!=null)
				fenceOverlay.removeAll();
			fenceOverlay=null;
			markItem=null;
			break;
		case REPLAY_TAB:
			findViewById(id.replayTime).setVisibility(View.GONE);
			isReplay = false;
			findViewById(R.id.seek_content).setVisibility(View.GONE);

			if (oHistory != null) {
				oHistory.iscolse = true;

				// oHistory.handler.removeMessages(10);
				// mMapView.getOverlays().remove(oHistory);
				// mMapView.getOverlays().remove(oHistory.graphicsOverlay);
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(mLocationOverlay);

				oHistory = null;
				mMapView.refresh();

			}
			break;
		}
		current_tab = tab;
		switch (tab) {

		case LOCATION_TAB:
			zoom = zoom_location;
			initmapinfo();
			mMapView.getController().setZoom(zoom);
			findViewById(R.id.device_bt).setVisibility(View.VISIBLE);
			findViewById(R.id.location_bt).setVisibility(View.VISIBLE);
			title.setText(R.string.team_track_title);
			// setSelect(1);
			// findViewById(R.id.seek_content).setVisibility(View.GONE);
			if (oTrack != null){
				DevicesUtils.sendCMDToDevice(this,User.id,User.curBabys.getId(),"DW","",null);
				//oTrack.new initAddress(0).execute();
				new initData().execute();
			}
			break;
		case SAFETY_TAB:
			zoom = zoom_safety;
			initFenceListView();
			mMapView.getController().setZoom(zoom);
			findViewById(id.layout_safety).setVisibility(View.VISIBLE);
			mPopView.setVisibility(View.GONE);
			title.setText(R.string.ef_list_btn_text);
			break;
		case REPLAY_TAB:
			zoom = zoom_replay;
			mMapView.getController().setZoom(zoom);
			findViewById(id.replayTime).setVisibility(View.VISIBLE);
			// setSelect(2);
			mPopView.setVisibility(View.GONE);
			title.setText(R.string.history_replay);
			Intent intent = new Intent();
			intent.setClass(MyTrace_More_Baidu.this, SelectReplayDate.class);
			startActivityForResult(intent, 1000);

			break;

		}

	}

}
