package com.ttxgps.gpslocation;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xtst.gps.BuildConfig;
import com.xtst.gps.R;
import com.ttxgps.gpslocation.view.MapWrapperLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.TeamMember;
import com.ttxgps.entity.ItemH;
import com.ttxgps.entity.MyMarker;
import com.ttxgps.entity.Trace;
import com.ttxgps.utils.BitmapDownloaderTask;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.utils.RegularUtils;

public class MyTrace_More_Google extends FragmentActivity
implements OnCameraChangeListener {
	public static final String CURRENT_FLAG_KEY = "10001";
	public static final String CURRENT_POSTIONS_KEY = "10002";
	public static final String CURRENT_INDEX_KEY = "10003";
	public static final String CURRENT_INITLOAD_KEY = "10004";
	public static final String CURRENT_MAP_KEY = "10005";
	MyMapView mMapView = null;
	GoogleMap gmap;
	// TraceServcie traceServcie;
	public ArrayList<Trace> myTraces = new ArrayList<Trace>();

	static HashMap<String, Trace> map = new HashMap<String, Trace>();
	Timer timer = new Timer();
	TextView timer_text;
	TextView text_name;
	TextView text_date;
	public int refresh_period = 15;
	SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	boolean isback;
	SeekBar playBar;
	TeamMember currentMember;
	View next_content;
	ImageView control;
	TextView title;
	ImageView play_h;
	public int select_flag = 0;// 当前状态 0表示多车 1表示单车 2表示历史
	boolean isReplay = true;
	static int zoom = 15;
	int zoom_track = 15, zoom_replay = 13, zoom_team = 5;
	// MAP_TYPE_NORMAL or MAP_TYPE_SATELLITE
	static int mapType = GoogleMap.MAP_TYPE_NORMAL;

	private static String[] refreshTable = null;
	private static String[] CAR_ALARM = null;
	private static String[] CAR_STATE = null;

	// {"userID":"165","userName":"nestorzhang","loginName":"nestor", ... }
	private static int userID = LoginActivity.userID;
	private static String timeZone = LoginActivity.timeZone;

	ProgressBar prog;
	MapWrapperLayout mapWrapperLayout;// 点击事件处理
	public Map<String, TeamMember> allitem;

	public boolean initload = false;
	public int mCurrentindex;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putInt(CURRENT_FLAG_KEY, select_flag);
		outState.putInt(CURRENT_INDEX_KEY, mCurrentindex);
		outState.putBoolean(CURRENT_INITLOAD_KEY, initload);
		outState.putSerializable(CURRENT_MAP_KEY, (Serializable) allitem);
		mMapView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_google);

		refreshTable = getResources().getStringArray(R.array.refresh_table);
		CAR_ALARM = getResources().getStringArray(R.array.car_alarm);
		CAR_STATE = getResources().getStringArray(R.array.car_state);

		currentMember = (TeamMember) getIntent().getSerializableExtra("item");
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(CURRENT_MAP_KEY)) {
			allitem = (Map<String, TeamMember>) savedInstanceState
					.getSerializable(CURRENT_MAP_KEY);
		}

		// myTraces = traceServcie.findTracesByUid(uid);
		prog = (ProgressBar) findViewById(R.id.process);
		// 如果使用地图SDK，请初始化地图Activity
		select_flag = getIntent().getIntExtra("type", 0);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(CURRENT_FLAG_KEY)) {
			//select_flag = savedInstanceState.getInt(CURRENT_FLAG_KEY);
			mCurrentindex = savedInstanceState.getInt(CURRENT_INDEX_KEY);
			initload = savedInstanceState.getBoolean(CURRENT_INITLOAD_KEY);
		}

		text_date = (TextView) findViewById(R.id.text_date);
		text_date.setText(dateFormater.format(new Date()));
		mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
		mMapView = (MyMapView) findViewById(R.id.googlemap);
		mMapView.onCreate(savedInstanceState);
		try {
			MapsInitializer.initialize(getApplication());
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initGoogleMap();
	}

	@Override
	protected void onResume() {
		super.onResume();

		userID = LoginActivity.userID;
		timeZone = LoginActivity.timeZone;

		// Initialize refresh_period
		String idString;
		if (select_flag == 0) { // 多车监控
			if (PrefHelper.hasKey(SetRefreshActivity.REFRESH_PERIOD_TEAM)) {
				idString = PrefHelper.getStringData(SetRefreshActivity.REFRESH_PERIOD_TEAM);
			} else { // Use default value
				idString = SetRefreshActivity.REFRESH_PERIOD_DEFAULT_TEAM;
			}
		} else { // 单车跟踪
			if (PrefHelper.hasKey(SetRefreshActivity.REFRESH_PERIOD_SINGLE)) {
				idString = PrefHelper.getStringData(SetRefreshActivity.REFRESH_PERIOD_SINGLE);
			} else {
				idString = SetRefreshActivity.REFRESH_PERIOD_DEFAULT_SINGLE;
			}
		}
		refresh_period = Integer.parseInt(idString);

		// Initialize default zoom
		if (select_flag == 0) {
			zoom = zoom_team;
		} else if (select_flag == 1) {
			zoom = zoom_track;
		} else {
			zoom = zoom_replay;
		}

		mMapView.onResume();
		initMap();
	}


	private void refreshMap() {
		if (timer != null) {
			timer.cancel();
		}
		initMap();
	}

	public void initGoogleMap() {
		gmap = mMapView.getMap();
		gmap.setOnCameraChangeListener(this);
		gmap.getUiSettings().setCompassEnabled(true);
		gmap.getUiSettings().setMyLocationButtonEnabled(true);
		mapWrapperLayout.init(gmap, getPixelsFromDp(this, 39 + 20));

		play_h = (ImageView) findViewById(R.id.replay);
		next_content = findViewById(R.id.nextcontrol);
		control = (ImageView) findViewById(R.id.map);
		title = (TextView) findViewById(R.id.title_text);
		timer_text = (TextView) findViewById(R.id.text_time);
		text_name = (TextView) findViewById(R.id.text_name);
		if (currentMember != null) {
			text_name.setText(currentMember.name);
		}
		// 设置在缩放动画过程中也显示overlay,默认为不绘制
		CheckBox cb = (CheckBox) findViewById(R.id.change_map_type);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					mapType = GoogleMap.MAP_TYPE_SATELLITE;
					gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

					// TODO: Don't refresh if replaying.
					if (select_flag != 2) {
						refreshMap();
					}
				} else {
					mapType = GoogleMap.MAP_TYPE_NORMAL;
					gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

					// TODO: Don't refresh if replaying.
					if (select_flag != 2) {
						refreshMap();
					}
				}
			}
		});
	}

	public static int getPixelsFromDp(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * 多车监控界面
	 */
	public void initMore() {

	}

	/**
	 * 历史显示
	 * 
	 * @param intent
	 */
	public void initHistory(Intent intent) {
		select_flag = 2;
		// if (oh != null) {
		// return;
		// }
		if (timer != null) {
			timer.cancel();
		}
		title.setText(R.string.history_replay);
		next_content.setVisibility(View.GONE);
		// control.setVisibility(View.GONE);
		date_b = intent.getStringExtra("date_b");
		date_e = intent.getStringExtra("date_e");
		playBar = (SeekBar) findViewById(R.id.playBar);
		findViewById(R.id.seek_content).setVisibility(View.VISIBLE);
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
		if (oh == null) {
			oh = new OverItemH();// 加载历史记录
		}
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

			if (select_flag == 0) {
				PrefHelper.setInfo(SetRefreshActivity.REFRESH_PERIOD_TEAM,
						current_time + "");
			} else {
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
			.setSingleChoiceItems(refreshTable, radioOnClick.getIndex(),
					radioOnClick).create();
			// ad.getListView();
			ad.show();
			break;
			/* Google Map has its own zoom in and zoom out button
		case R.id.map_big:
			++zoom;
			break;
		case R.id.map_small:
			--zoom;
			break;
			 */
		case R.id.next_down:
			if (ot == null) {
				Utils.showToast(R.string.loading);
				return;
			}
			if (ts.size() == 0) {
				return;
			}
			mCurrentindex++;
			if (mCurrentindex == ts.size()) {
				mCurrentindex = 0;
			}
			// CommonUtils.showProcessDialog(MyTrace_More.this, "正在获取数据...");
			ot.new initAddress(mCurrentindex).execute();
			break;
		case R.id.next_up:
			if (ot == null) {
				Utils.showToast(R.string.loading);
				return;
			}
			if (ts.size() == 0) {
				return;
			}
			mCurrentindex--;
			if (mCurrentindex < 0) {
				mCurrentindex = ts.size() - 1;
			}
			// CommonUtils.showProcessDialog(MyTrace_More.this, "正在获取数据...");
			ot.new initAddress(mCurrentindex).execute();

			break;
		case R.id.replay:
			if (isReplay) {
				((ImageView) view).setImageResource(R.drawable.map_pause);
				isReplay = false;
			} else {
				isReplay = true;
				// if (oh.mPosition == oh.total - 1) {
				// oh.mPosition = 0;
				// }
				// oh.handler.sendEmptyMessage(1);
				((ImageView) view).setImageResource(R.drawable.map_play);
			}
			break;
		case R.id.reflush:
			refreshMap();
			break;
		case R.id.map:// 监示停止
			if (select_flag == 2) {
				currentMember = null;
				select_flag = 0;
				initMap();
				return;
			}
			if (timer != null) {
				((ImageView) view)
				.setImageResource(R.drawable.map_control_close);
				timer.cancel();
				timer_text.setText(R.string.refresh_is_disabled);
				Utils.showToast(R.string.refresh_is_disabled);
				timer = null;
				PrefHelper.setInfo(SetRefreshActivity.REFRESH_DISABLE, true);
			} else {
				PrefHelper.setInfo(SetRefreshActivity.REFRESH_DISABLE, false);
				((ImageView) view)
				.setImageResource(R.drawable.map_control_open);
				if (timer != null) {
					timer.cancel();
				}
				timer = new Timer();
				timer.schedule(new TimerTask() {
					int i = 0;

					@Override
					public void run() {
						handler.sendEmptyMessage(i);
						if (i == refresh_period) {// 时间设置
							timer.cancel();
						}
						i++;
					}
				}, 1000, 1000);
			}
			break;
		case R.id.back:
			finish();
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
				pro_handler.sendEmptyMessageDelayed(curren, 1000);
				curren++;
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == SelectReplayDate.success) {
			date_b = data.getStringExtra("date_b");
			date_e = data.getStringExtra("date_e");
			getIntent().putExtra("date_b", date_b);
			getIntent().putExtra("date_e", date_e);
			text_name.setText(currentMember.name);
			getIntent().putExtra("type", 2);
			initHistory(getIntent());
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (isback) {
				return;
			}
			if (!PrefHelper.getBooleanData(SetRefreshActivity.REFRESH_DISABLE)) {
				if (msg.what < 0 || msg.what >= refresh_period) {// // 时间设置
					timer_text.setText(R.string.refreshing);
					text_date.setText(dateFormater.format(new Date()));
					new initData().execute();
				} else {
					timer_text.setText((refresh_period - msg.what)
							+ getString(R.string.second)
							+ "后刷新");
				}
			}
		}
	};

	boolean initLatLng() {
		try {
			String result = "";
			int deviceID = -1;
			if (currentMember != null) { // Single car track
				deviceID = Integer.parseInt(currentMember.id);
			}
			WebService webservice = new WebService(MyTrace_More_Google.this, "GetTrackingByUserID");
			LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
			linkedlist.add(new WebServiceProperty("UserID", userID));
			linkedlist.add(new WebServiceProperty("DeviceID", deviceID));
			linkedlist.add(new WebServiceProperty("MapType", "Google"));
			webservice.SetProperty(linkedlist);
			result = webservice.Get("GetTrackingByUserIDResult",WebService.URL_OPEN);

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

					Trace t = new Trace();
					String[] info = strs[i].split(";");
					t.initTrace(info);
					myTraces.add(t);
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

	public void showImage(final String info) {
		View view = LayoutInflater.from(this).inflate(R.layout.showimage, null);
		ImageView img = (ImageView) view.findViewById(R.id.pop_image);
		Button save = (Button) view.findViewById(R.id.save);
		Button close = (Button) view.findViewById(R.id.close);

		final PopupWindow pop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		img.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
			}
		});
		final BitmapDownloaderTask task = new BitmapDownloaderTask(img);
		task.execute(info);
		// img.setImageResource(R.drawable.app_logo);
		pop.setBackgroundDrawable(getResources().getDrawable(
				R.color.translucent));
		pop.showAsDropDown(view);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (task.bit != null) {
					Utils.showProcessDialog(MyTrace_More_Google.this,
							R.string.picture_saving);
					BitmapDownloaderTask.writeToFile(task.bit, info);
				} else {
					Utils.showToast(R.string.picture_downloading);
				}
			}
		});
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pop != null)
					pop.dismiss();
			}
		});
	}

	public class initData extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			myTraces.clear();
			map.clear();
			if (!initload && TeamActivity.memberTraces != null
					&& TeamActivity.memberTraces.size() > 0) {
				initload = true;
				for (Map.Entry<String, Trace> item : TeamActivity.memberTraces
						.entrySet()) {
					myTraces.add(item.getValue());
				}
				return initload;
			}
			return initLatLng();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (!result) {
				handler.sendEmptyMessage(10);
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
		// oh = null;
		// 注册定位事件

		isReplay = false;
		if (select_flag == 1) {
			text_name.setText(currentMember.name);
			if ("1".equals(currentMember.type)) {
				title.setText(R.string.car_track);
			} else {
				title.setText(R.string.person_track_title);
			}

			next_content.setVisibility(View.GONE);
		} else {
			title.setText(R.string.team_track_title);
			next_content.setVisibility(View.VISIBLE);
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
					handler.sendEmptyMessage(i);
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
		for (Trace item_t : myTraces) {
			map.put(item_t.id, item_t);
		}
		ts.clear();
		for (Map.Entry<String, Trace> value : map.entrySet()) {
			if (select_flag == 0 || value.getValue().id.equals(currentMember.id)) {
				ts.add(value.getValue());
				if (currentMember != null && currentMember.id.equals(value.getValue().id)) {
					mCurrentindex = ts.indexOf(value.getValue());
				}
			}
		}
		if (ot == null) {
			ot = new OverItemT();
			gmap.setInfoWindowAdapter(ot);
			gmap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

				@Override
				public void onInfoWindowClick(Marker arg0) {
					Intent info = new Intent();
					info.setClass(MyTrace_More_Google.this, InfoActivity.class);
					info.putExtra("item", currentMember);
					startActivity(info);
				}
			});
		}
		ot.initItems(ts);
	}

	OverItemT ot;
	OverItemH oh;

	@Override
	protected void onPause() {
		// MapAPP app = (MapAPP) this.getApplication();
		// app.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		// app.mBMapMan.stop();
		if (timer != null) {
			timer.cancel();
		}
		// if (oh != null) {
		// play_h.setImageResource(R.drawable.map_pause);
		// // ispaly = false;
		// }
		mMapView.onPause();
		isback = true;
		super.onPause();
	}

	String date_b;
	String date_e;
	Date begin;
	Date end;

	public void initMap() {
		// ////////////////////开启
		if (select_flag != 2) {
			isback = false;
			timer_text.setText(R.string.refreshing);
			findViewById(R.id.seek_content).setVisibility(View.GONE);
			new initData().execute();
		} else { // History replay
			isback = false;
			isReplay = true;
			initHistory(getIntent());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MapAPP.handler_map = null;
		mMapView.onDestroy();
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

	public int mPosition = 0;
	public int total = 0;
	Marker currentmarker;

	/**
	 * 历史回放
	 * @author Administrator
	 *
	 */
	class OverItemH {
		MyMarker mCurrent_h;
		private final List<MyMarker> mGeoList_current = new ArrayList<MyMarker>();
		private final Vector<LatLng> lans = new Vector<LatLng>();

		public boolean iscolse = false;
		private String nextStart = "";

		public OverItemH() {
			gmap.clear();
			new initPath().execute();
		}

		public class initPath extends AsyncTask<Void, Void, ArrayList<ItemH>> {

			@Override
			protected ArrayList<ItemH> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					WebService webservice = new WebService(
							MyTrace_More_Google.this, "GetDeviceHistory");
					LinkedList<WebServiceProperty> linkedlist =
							new LinkedList<WebServiceProperty>();
					linkedlist.add(new WebServiceProperty("DeviceID",
							Integer.parseInt(currentMember.id)));
					linkedlist.add(new WebServiceProperty("StartTime",
							(nextStart.equals("") ? date_b : nextStart)));
					linkedlist.add(new WebServiceProperty("EndTime", date_e));
					linkedlist.add(new WebServiceProperty("TimeZone", timeZone));
					linkedlist.add(new WebServiceProperty("ShowLBS", 0)); // Drop LBS positions
					linkedlist.add(new WebServiceProperty("MapType", "Google"));
					linkedlist.add(new WebServiceProperty("SelectCount", 300));
					webservice.SetProperty(linkedlist);
					String result = webservice.Get("GetDeviceHistoryResult",WebService.URL_OPEN);

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
							String[] strs =	datas[i].split(";");
							// CommonUtils.showToast("Add ItemH: " + datas[i]);
							ItemH item = new ItemH();
							item.time = strs[0];

							try {
								item.lat = Double.parseDouble(strs[1]);
								item.lng = Double.parseDouble(strs[2]);
								item.olat = Double.parseDouble(strs[3]);
								item.olng = Double.parseDouble(strs[4]);

								item.speed = strs[5];
								strs[6] = (strs[6] == null) ? "0" : strs[6];
								item.angle = Integer.parseInt(strs[6]);
								item.status = strs[7];
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
					return;
				}

				for (ItemH itemh : result) {
					LatLng ll = new LatLng(itemh.lat, itemh.lng);
					MyMarker oi = new MyMarker("" + itemh.lat, "" + itemh.lng,
							itemh.angle, itemh.speed, itemh.time, nextStart,
							mPosition, mPosition, nextStart, nextStart);
					mGeoList_current.add(oi);
					lans.add(ll);
				}

				total += result.size();

				if (mCurrent_h == null) {
					handler.sendEmptyMessage(0);
				}

				if ("".equals(nextStart)) {
					return;
				}
				if (currentMember != null && select_flag == 2) {
					new initPath().execute();
				}
				super.onPostExecute(result);
			}
		}

		PolylineOptions options;
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

				/// For History Replay ///

				mCurrent_h = mGeoList_current.get(mPosition);
				mPosition++;
				timer_text.setText(mCurrent_h.speed + "km/h");
				text_date.setText(mCurrent_h.date);

				// End of replay?
				if (mPosition >= total) {
					mPosition = total - 1;
					int i = (int) ((end.getTime() - begin.getTime()) / 10000);
					playBar.setProgress(i);
					timer_text.setText("0km/h");
					isReplay = false;
					return;
				}

				try {
					Date c_time = dateFormater.parse(mCurrent_h.date);
					int i = (int) ((c_time.getTime() - begin.getTime()) / 10000);
					playBar.setProgress(i);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (options == null) {
					options = new PolylineOptions();
				}
				MyMarker overLayItem = mGeoList_current.get(mPosition);
				options.add(overLayItem.getLatlng()).width(5).color(Color.BLUE)
				.geodesic(true);
				gmap.addPolyline(options);

				if (currentmarker == null) {
					currentmarker = gmap
							.addMarker(new MarkerOptions()
							.position(
									mGeoList_current.get(mPosition)
									.getLatlng())
									.title("1")
									.snippet("Population: 2,074,200")
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.map_move)));
					/*
					 * Disable animation at the first positioning.
					 * So use moveCamera() instead of animateCamera().
					 */
					gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
							mGeoList_current.get(mPosition).getLatlng(), zoom));
				} else {
					currentmarker.setPosition(mGeoList_current.get(mPosition)
							.getLatlng());
				}

				if (mPosition % 25 == 0) {
					gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							mGeoList_current.get(mPosition).getLatlng(), zoom));
				}

				// // 第二个画笔 画线
				//
				// // 连接 所有点
				// Path path = new Path();
				// path.moveTo(points.get(0).x, points.get(0).y);
				// for (int i = 1; i < mPosition - start; i++) {
				// path.lineTo(points.get(i).x, points.get(i).y);
				// }
				handler.sendEmptyMessageDelayed(1, 150);
			};
		};

	}

	int drawableids[][] = new int[][] {
			{ R.drawable.group_car_h, R.drawable.group_car,
				R.drawable.group_car },// 0离线 1 静止2运动
				{ R.drawable.people_location_offline,
					R.drawable.people_location_offline,
					R.drawable.people_location_online } };

	public static int em = 0;

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();

		map_draw = null;
		em = 0;
	}

	// /////////////////////////////---------------------------------------
	int c = 1;
	Handler map_draw;

	class OverItemT implements InfoWindowAdapter, OnMarkerClickListener {

		private ArrayList<Trace> traces = new ArrayList<Trace>();
		private final ArrayList<MyMarker> markers = new ArrayList<MyMarker>();

		private final View mPopView; // custom info window

		MyMarker mCurrent;

		public OverItemT() {
			mPopView = getLayoutInflater().inflate(R.layout.popview_g, null);
		}

		public void initItems(final ArrayList<Trace> traces) {
			gmap.clear();
			markers.clear();
			circles.clear();
			this.traces = traces;
			for (int i = 0; i < traces.size(); i++) {
				double lat, lng;
				if (mapType == GoogleMap.MAP_TYPE_NORMAL) {
					lat = ((double) traces.get(i).lat) / 1000000;
					lng = ((double) traces.get(i).lng) / 1000000;
				} else {
					lat = Double.parseDouble(traces.get(i).real_lat);
					lng = Double.parseDouble(traces.get(i).real_lng);
				}
				Trace ts = traces.get(i);
				String[] tps = ts.alarm_state.split(":");

				int location = 0;
				try {
					location = Integer.parseInt(traces.get(i).direction);
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}// ///
				TeamMember currrent = TeamActivity.memberList
						.get(traces.get(i).id);
				int type = Integer.parseInt(currrent.status);
				int peopleorcar = Integer.parseInt(currrent.type) - 1;
				MyMarker oi = new MyMarker("" + lat, "" + lng, location,
						ts.speed, "", ts.alarm_state, peopleorcar, type,
						currrent.name, currrent.id);
				oi.item = currrent;
				if (select_flag == 0) {
					try {
						// 0 LogOff; 1 Move; 2 Stop; 3 Offline; 4 Arrears
						switch (type) {
						case 0:// 未开通
							oi.icon = drawableids[peopleorcar][0];
							break;
						case 1:// Moving
							oi.icon = drawableids[peopleorcar][2];
							break;
						case 2:// Stop
							oi.icon = drawableids[peopleorcar][2];
							break;
						case 3:// Offline
							oi.icon = drawableids[peopleorcar][0];
							break;
						case 4:// Arrears
							oi.icon = drawableids[peopleorcar][0];
							break;
						default:
							oi.icon = drawableids[peopleorcar][0];
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					oi.icon = R.drawable.location_gps;
				}
				oi.marker = gmap.addMarker(oi.buildMarker(i));
				markers.add(oi);

				map_draw = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						for (int i = 0; i < traces.size(); i++) {
							Trace ts = traces.get(i);
							String[] tps = ts.alarm_state.split(":");
							if (RegularUtils.getNumber(tps[tps.length - 1])) {
								TeamMember currrent = TeamActivity.memberList
										.get(traces.get(i).id);

								double lat, lng;
								if (mapType == GoogleMap.MAP_TYPE_NORMAL) {
									lat = ((double) traces.get(i).lat) / 1000000;
									lng = ((double) traces.get(i).lng) / 1000000;
								} else {
									lat = Double.parseDouble(traces.get(i).real_lat);
									lng = Double.parseDouble(traces.get(i).real_lng);
								}

								Circle circle = circles.get(currrent.id);
								if (circle == null) {
									circle = gmap.addCircle(new CircleOptions()
									.center(new LatLng(lat, lng))
									.radius(1000).strokeWidth(4)
									.strokeColor(Color.BLUE));
									circles.put(currrent.id, circle);
									Circle circle1 = gmap
											.addCircle(new CircleOptions()
											.center(new LatLng(lat, lng))
											.radius(4000)
											.strokeWidth(4)
											.strokeColor(Color.BLUE));
									circles1.put(currrent.id, circle1);
								} else {
									double r = circle.getRadius();
									r = r + 1000;
									if (r > 8000) {
										r = 1000;
									}
									circle.setRadius(r);
									circle.setCenter(new LatLng(lat, lng));
									Circle circle1 = circles1.get(currrent.id);
									r = circle1.getRadius();
									r = r + 1000;
									if (r > 8000) {
										r = 1000;
									}
									circle1.setCenter(new LatLng(lat, lng));
									circle1.setRadius(r);
								}
							}
						}
						if (map_draw != null)
							map_draw.sendEmptyMessageDelayed(1, 120);
					};
				};
				if (em == 0) {
					map_draw.sendEmptyMessage(em);
					em++;
				}
			}
			new initAddress(mCurrentindex).execute();
		}

		Map<String, Circle> circles = new HashMap<String, Circle>();
		Map<String, Circle> circles1 = new HashMap<String, Circle>();

		public class initAddress extends AsyncTask<Void, Void, String> {
			int i = 0;

			public initAddress(int p) {
				Utils.showProcessDialog(MyTrace_More_Google.this, R.string.downloading);
				i = p;
				mCurrentindex = p;
				mCurrent = markers.get(p);
			}

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					WebService webservice = new WebService(
							MyTrace_More_Google.this, "GetAddressByLatlng");
					LinkedList<WebServiceProperty> linkedlist =
							new LinkedList<WebServiceProperty>();
					linkedlist.add(new WebServiceProperty("Lat", traces.get(i).real_lat));
					linkedlist.add(new WebServiceProperty("Lng", traces.get(i).real_lng));
					linkedlist.add(new WebServiceProperty("MapType", "Google"));

					String language = "zh-cn";
					if (!timeZone.equals("China Standard Time")) {
						language = "en-us";
					}
					linkedlist.add(new WebServiceProperty("Language", language));
					webservice.SetProperty(linkedlist);
					return webservice.Get("GetAddressByLatlngResult",WebService.URL_OPEN);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				// 更新气泡位置,并使之显示
				Utils.dismissProcessDialog();

				mCurrent.address = result;
				mCurrent.marker.showInfoWindow();
				/*
				 * Disable animation at the first positioning.
				 * So use moveCamera() instead of animateCamera().
				 */
				gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						mCurrent.getLatlng(), zoom));
				currentMember = mCurrent.item;
				currentMember.address = result;
				currentMember.lat = mCurrent.point_la;
				currentMember.lng = mCurrent.point_ln;
				super.onPostExecute(result);
			}
		}

		@Override
		public View getInfoContents(Marker arg0) {
			// TODO Auto-generated method stub

			return null;
		}

		@Override
		public View getInfoWindow(Marker arg0) {
			// TODO Auto-generated method stub
			render(arg0, mPopView);
			return mPopView;
		}

		private void render(Marker arg0, View PopView) {
			final TextView pop_title =
					(TextView) mPopView.findViewById(R.id.pop_title);
			final TextView pop_address =
					(TextView) mPopView.findViewById(R.id.pop_address);
			final  TextView pop_state =
					(TextView) mPopView.findViewById(R.id.pop_state_g);
			final  TextView pop_device_state =
					(TextView) mPopView.findViewById(R.id.pop_device_state_g);

			mCurrentindex = Integer.parseInt(arg0.getTitle());
			Trace ts = traces.get(mCurrentindex);

			String carStatus = "";
			currentMember = TeamActivity.memberList.get(ts.id);
			int type = Integer.parseInt(currentMember.status);
			// 0 LogOff; 1 Move; 2 Stop; 3 Offline; 4 Arrears
			switch (type) {
			case 0:
				carStatus = getString(R.string.unregistered);
				break;
			case 1:
				carStatus = getString(R.string.running) + "    " + ts.speed + "km/h";
				break;
			case 2:
				carStatus = getString(R.string.stop) + "    " +
						minToDateDiff(Long.parseLong(ts.pos_time));
				break;
			case 3:
				carStatus = getString(R.string.offline);
				break;
			case 4:
				carStatus = getString(R.string.arrears);
				break;
			}

			type = Integer.parseInt(ts.car_state);
			String deviceStatus = "";
			int tempShift = 0;
			for (; tempShift < 5; tempShift++) {
				if (((type >> tempShift) & 0x1) == 0x1) {
					deviceStatus += CAR_STATE[tempShift + 1] + ",";
					if (tempShift == 4) {
						deviceStatus = CAR_STATE[tempShift + 1]; // PowerLost is special
					}
				}
			}
			if (deviceStatus.endsWith(",")) {
				deviceStatus = deviceStatus.substring(0, deviceStatus.length() - 1);
			}
			if (deviceStatus.equals("")) {
				deviceStatus = CAR_STATE[0];
			}

			currentMember.state = carStatus;
			currentMember.device_state = deviceStatus;
			/*
			String[] tps = t.alarm_state.split(":");
			if (tps.length >= 1 && RegularUtils.getNumber(tps[tps.length - 1])) {
				typename += ","
						+ CAR_ALARM[Integer.parseInt(tps[tps.length - 1])];
			}
			 */
			text_name.setText(mCurrent.title);
			pop_title.setText(mCurrent.title);
			pop_state.setText(carStatus);
			pop_device_state.setText(deviceStatus);
			pop_address.setText(mCurrent.address);
		}

		@Override
		public boolean onMarkerClick(Marker arg0) {
			mCurrentindex = Integer.parseInt(arg0.getTitle());
			try {
				mCurrent = MyMarker.buildMyMarker(arg0.getSnippet());
				mCurrent.marker = arg0;

				new initAddress(mCurrentindex).execute();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// item = TeamActivity.allitem.get(mCurrent_h.id);

			return true;
		}

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
				+ (diffm > 0 && diffd == 0 ? (diffm + getString(R.string.minute)) : "");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		// TODO Auto-generated method stub

		// Skip the zooming event when the map is first loaded.
		if ((zoom - (int) position.zoom) > 3)
			return;

		// Save new zoom after zooming by user.
		zoom = (int) position.zoom;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// No menus for history replay
		if (select_flag == 2) return false;

		menu.clear();

		if (select_flag == 0) {
			menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.car_track));
		}
		if (select_flag == 0 || select_flag == 1) {
			menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.remote_control));
			menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.history_replay));
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case 0: // Car Track
			select_flag = 1;
			if ("2".equals(currentMember.type)) {
				title.setText(R.string.person_track);
			} else {
				title.setText(R.string.car_track);
			}
			text_name.setText(currentMember.name);
			if (timer != null) {
				timer.cancel();
			}

			initmapinfo();
			return true;

		case 1: // Remote Control
			Intent control = new Intent();
			control.setClass(this, ControlActivity.class);
			control.putExtra("item", currentMember);
			startActivityForResult(control, 1);
			return true;

		case 2: // History Replay
			select_flag = 2;
			Intent intent = new Intent();
			intent.setClass(MyTrace_More_Google.this, SelectReplayDate.class);
			startActivityForResult(intent, 1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

