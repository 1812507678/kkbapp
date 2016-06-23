package com.ttxgps.gpslocation;
///*   
// * Copyright (C) 2012 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.car.carlocation;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PaintFlagsDrawFilter;
//import android.graphics.Path;
//import android.graphics.Point;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.FragmentActivity;
//import android.text.SpannableString;
//import android.text.style.ForegroundColorSpan;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.PopupWindow;
//import android.widget.ProgressBar;
//import android.widget.RadioGroup;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.car.entity.Item;
//import com.car.entity.Trace;
//import com.car.carlocation.R;
//import com.car.utils.BitmapDownloaderTask;
//import com.car.utils.MySSLSocketFactory;
//import com.car.utils.Urls;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.Projection;
//import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.palmtrends.loadimage.Utils;
//import com.ttxgps.utils.PrefHelper;
//
///**
// * This shows how to create a simple activity with a raw MapView and add a
// * drable_marker to it. This requires forwarding all the important lifecycle methods
// * onto MapView.
// */
//public class RawMapViewDemoActivity extends FragmentActivity {
//	public static final String TIME_ONE_SET = "time_one";
//	public static final String TIME_MORE_SET = "time_more";
//	private MapView mMapView;
//	private GoogleMap mMap;
//	// MyLocationOverlay mLocationOverlay = null; // 定位图层
//	static View mPopView = null; // 点击mark时弹出的气泡View
//	// TraceServcie traceServcie;
//	public ArrayList<Trace> myTraces = new ArrayList<Trace>();
//	SharedPreferences sp;
//	private boolean isfirst = true;
//	public static String reflush = "reflush_state";
//	Drawable marker = null;
//	public TextView pop_title;
//	public TextView pop_address;
//	TextView pop_g;
//	TextView pop_h;
//	public TextView pop_state;
//	static HashMap<String, Trace> map = new HashMap<String, Trace>();
//	Timer timer = new Timer();
//	TextView timer_text;
//	TextView text_name;
//	TextView text_date;
//	public int time = 15;
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//	boolean isback;
//	SeekBar play;
//	Item item;
//	View next_content;
//	ImageView control;
//	TextView title;
//	ImageView play_h;
//	public int currentfalg = 0;// 当前状态 0表示多车 1表示单车 2表示历史
//	public static final String CURRENT_FLAG_KEY = "10001";
//	public static final String CURRENT_POSTIONS_KEY = "10002";
//	public static final String CURRENT_INDEX_KEY = "10003";
//	public static final String CURRENT_INITLOAD_KEY = "10004";
//	ProgressBar prog;
//	boolean initload;
//	public int mCurrentindex;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.raw_mapview_demo);
//		mMapView = (MapView) findViewById(R.id.map);
//		mMapView.onCreate(savedInstanceState);
//		initload = false;
//		currentfalg = getIntent().getIntExtra("type", 0);
//		sp = this.getSharedPreferences("data", MODE_WORLD_READABLE);
//	}
//
//	String date_b;
//	String date_e;
//	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
//	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		if (resultCode == DateUpdate.success) {
//			date_b = data.getStringExtra("date_b");
//			date_e = data.getStringExtra("date_e");
//			getIntent().putExtra("date_b", date_b);
//			getIntent().putExtra("date_e", date_e);
//			// initHistory(getIntent());开始启动历史记录功能
//			item = (Item) pop_title.getTag();
//			text_name.setText(item.name);
//			getIntent().putExtra("type", 2);
//		} else if (resultCode == PeopleControllActivity.success) {
//			prog.setVisibility(View.VISIBLE);
//			// pro_handler.sendEmptyMessage(1);
//			// curren = 0;
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	int i = 0;
//	Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			// if (iscolse) {
//			// return;
//			// }
//			// if (isback) {
//			// return;
//			// }
//			// if (!ispaly) {
//			// return;
//			// }
//			// populate();
//			// mMapView.postInvalidate();
//			// mCurrent_h = mGeoList_current
//			// .get(mPosition % mGeoList_current.size());
//			// mPosition++;
//			// timer_text.setText(mCurrent_h.speed + "km/h");
//			// text_date.setText(mCurrent_h.date);
//			// if (mPosition % 25 == 0) {
//			// mMapView.getController().animateTo(mCurrent_h.getPoint());
//			// }
//			// if (mPosition >= total) {
//			// mPosition = total - 1;
//			// int i = (int) ((end.getTime() - begin.getTime()) / 3600);
//			// play.setProgress(i);
//			// timer_text.setText("0km/h");
//			// ispaly = false;
//			// return;
//			// }
//			// try {
//			// Date c_time = sdf.parse(mCurrent_h.date.replace("[", "").replace(
//			// "]", ""));
//			// int i = (int) ((c_time.getTime() - begin.getTime()) / 3600);
//			// play.setProgress(i);
//			// } catch (ParseException e) {
//			// // TODO Auto-generated catch block
//			// e.printStackTrace();
//			// }
//			// handler.sendEmptyMessageDelayed(1, 100);
//		};
//	};
//
//	boolean initGeoPoint() {
//		myTraces.clear();
//		try {
//			List<NameValuePair> param = new ArrayList<NameValuePair>();
//			param.add(new BasicNameValuePair("serviceid", PrefHelper
//					.getStringData(PrefHelper.P_USER_ID)));
//			if (item != null) {
//				param.add(new BasicNameValuePair("carid", item.id));
//			}
//			param.add(new BasicNameValuePair("delta", "false"));
//			String json = MySSLSocketFactory.getinfo(Urls.car_last_pos, param);
//
//			JSONObject jsonobj = new JSONObject(json);
//			JSONArray ja = jsonobj.getJSONArray("poss");
//			String str = ja.toString().replace("]", "").replace("[", "")
//					.replace("\"", "");
//			String[] strs = str.split(",");
//			if (jsonobj.has("msg")) {
//				JSONArray msgs = jsonobj.getJSONArray("msg");
//				int c = msgs.length();
//				// if (prog.getVisibility() == View.VISIBLE && c == 0) {
//				// i += time;
//				// if (i > 120) {
//				// handler.post(new Runnable() {
//				// @Override
//				// public void run() {
//				// prog.setVisibility(View.GONE);
//				// CommonUtils.showToast("相片上传异常");
//				// }
//				// });
//				// i = 0;
//				// }
//				// } else {
//				// i = 0;
//				// }
//				for (int i = 0; i < c; i++) {
//					JSONObject msg = msgs.getJSONObject(i);
//					String type = msg.getString("t");
//					final String info = msg.getString("c");
//					if ("t".equals(type)) {
//						CommonUtils.showToast(info);
//					} else if ("p".equals(type)) {
//						handler.post(new Runnable() {
//							@Override
//							public void run() {
//								showImage(info);
//								prog.setProgress(120);
//								prog.setVisibility(View.GONE);
//							}
//						});
//					} else if ("pp".equals(type)) {
//						// handler.post(new Runnable() {
//						// @Override
//						// public void run() {
//						// prog.setProgress(Integer.parseInt(info));
//						// }
//						// });
//					}
//				}
//			}
//			if ("".equals(str)) {
//				return true;
//			}
//			int count = strs.length;
//			for (int i = 0; i < count; i++) {
//				Trace t = new Trace();
//				String[] info = strs[i].split(";");
//				t.initTrace(info);
//				myTraces.add(t);
//				// t.address = o.getString("a");
//			}
//			return true;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			// CommonUtils.showToast("数据刷新错误");
//			e.printStackTrace();
//			return true;
//		}
//	}
//
//	public void showImage(final String info) {
//		View view = LayoutInflater.from(this).inflate(R.layout.showimage, null);
//		ImageView img = (ImageView) view.findViewById(R.id.pop_image);
//		Button save = (Button) view.findViewById(R.id.save);
//		Button close = (Button) view.findViewById(R.id.close);
//
//		final PopupWindow pop = new PopupWindow(view, LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT);
//		img.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				pop.dismiss();
//			}
//		});
//		final BitmapDownloaderTask task = new BitmapDownloaderTask(img);
//		task.execute(info);
//		// img.setImageResource(R.drawable.app_logo);
//		pop.setBackgroundDrawable(getResources().getDrawable(
//				R.color.translucent));
//		pop.showAsDropDown(view);
//		save.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (task.bit != null) {
//					CommonUtils.showProcessDialog(RawMapViewDemoActivity.this,
//							"正在保存图片...");
//					BitmapDownloaderTask.writeToFile(task.bit, info);
//				} else {
//					CommonUtils.showToast("图片正在下载...");
//				}
//			}
//		});
//		close.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (pop != null)
//					pop.dismiss();
//			}
//		});
//	}
//
//	public class initData extends AsyncTask<Void, Void, Boolean> {
//
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			if (!initload && TeamActivity.map != null
//					&& TeamActivity.map.size() > 0) {
//				initload = true;
//				myTraces.clear();
//				for (Map.Entry<String, Trace> item : TeamActivity.map
//						.entrySet()) {
//					myTraces.add(item.getValue());
//				}
//				return initload;
//			}
//			return initGeoPoint();
//		}
//
//		@Override
//		protected void onPostExecute(Boolean result) {
//			super.onPostExecute(result);
//			if (!result) {
//				handler.sendEmptyMessage(10);
//				return;
//			}
//			if (isback) {
//				return;
//			}
//			if (isfirst) {
//				mPopView = getLayoutInflater().inflate(R.layout.popview, null);
//				int w = 280 * getResources().getDisplayMetrics().widthPixels / 480;
//				int h = 200 * getResources().getDisplayMetrics().widthPixels / 480;
//				// mMapView.addView(mPopView, new MapView.LayoutParams(w, h,
//				// null,
//				// MapView.LayoutParams.MATCH_PARENT));
//				mPopView.setVisibility(View.GONE);
//				pop_title = (TextView) mPopView.findViewById(R.id.pop_title);
//				pop_address = (TextView) mPopView
//						.findViewById(R.id.pop_address);
//				pop_state = (TextView) mPopView.findViewById(R.id.pop_state);
//				pop_g = (TextView) mPopView.findViewById(R.id.pop_g);
//				pop_h = (TextView) mPopView.findViewById(R.id.pop_h);
//				// w = (280 - 20) *
//				// getResources().getDisplayMetrics().widthPixels
//				// / 480;
//				View content = mPopView.findViewById(R.id.content);
//				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
//						w, h);
//				llp.gravity = Gravity.CENTER;
//				content.setLayoutParams(llp);
//				isfirst = false;
//			}
//			initmapinfo();
//		}
//	}
//
//	ArrayList<Trace> ts = new ArrayList<Trace>();
//
//	public void initmapinfo() {
//		if (currentfalg == 1) {
//			text_name.setText(item.name);
//			if ("1".equals(item.type)) {
//				title.setText("车辆跟踪");
//			} else {
//				title.setText("人员跟踪");
//			}
//			time = PrefHelper.getIntData(TIME_ONE_SET);
//			next_content.setVisibility(View.GONE);
//			mPopView.findViewById(R.id.pop_g).setVisibility(View.GONE);
//			if (time == 0) {
//				time = 15;
//			}
//		} else {
//			title.setText("监控");
//			time = PrefHelper.getIntData(TIME_MORE_SET);
//			mPopView.findViewById(R.id.pop_g).setVisibility(View.VISIBLE);
//			next_content.setVisibility(View.VISIBLE);
//			if (time == 0) {
//				time = 60;
//			}
//		}
//		if (timer != null)
//			timer.cancel();
//		timer = null;
//		if (!PrefHelper.getBooleanData(reflush)) {
//			timer = new Timer();
//			timer.schedule(new TimerTask() {
//				int i = 0;
//
//				@Override
//				public void run() {
//					handler.sendEmptyMessage(i);
//					if (i == time) {// 时间设置
//						timer.cancel();
//					}
//					i++;
//				}
//			}, 1000, 1000);
//		} else {
//			((ImageView) control)
//					.setImageResource(R.drawable.map_control_close);
//			if (timer != null)
//				timer.cancel();
//			timer_text.setText("监控停止");
//			timer = null;
//		}
//		for (Trace item_t : myTraces) {
//			map.put(item_t.id, item_t);
//		}
//		ts.clear();
//		for (Map.Entry<String, Trace> value : map.entrySet()) {
//			if (currentfalg == 0 || value.getValue().id.equals(item.id)) {
//				ts.add(value.getValue());
//				if (item != null && item.id.equals(value.getValue().id)) {
//					mCurrentindex = ts.indexOf(value.getValue());
//				}
//			}
//		}// 初始化数据
//
//		// 添加ItemizedOverlay实例到mMapView
//		// mMapView.getOverlays().add(oh); //
//		// // 添加ItemizedOverlay实例到mMapView
//
//	}
//
//	class CustomInfoWindowAdapter implements InfoWindowAdapter {
//		private final RadioGroup mOptions;
//
//		// These a both viewgroups containing an ImageView with id "badge"
//		// and
//		// two TextViews with id
//		// "title" and "snippet".
//		private final View mWindow;
//		private final View mContents;
//
//		CustomInfoWindowAdapter() {
//			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window,
//					null);
//			mContents = getLayoutInflater().inflate(
//					R.layout.custom_info_contents, null);
//			mOptions = (RadioGroup) findViewById(R.id.custom_info_window_options);
//		}
//
//		@Override
//		public View getInfoWindow(Marker marker) {
//			if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_window) {
//				// This means that getInfoContents will be called.
//				return null;
//			}
//			render(marker, mWindow);
//			return mWindow;
//		}
//
//		@Override
//		public View getInfoContents(Marker marker) {
//			if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
//				// This means that the default info contents will be used.
//				return null;
//			}
//			render(marker, mContents);
//			return mContents;
//		}
//
//		private void render(Marker marker, View view) {
//			int badge;
//			// Use the equals() method on a Marker to check for equals. Do
//			// not
//			// use ==.
//			if (marker.equals(mBrisbane)) {
//				badge = R.drawable.badge_qld;
//			} else if (marker.equals(mAdelaide)) {
//				badge = R.drawable.badge_sa;
//			} else if (marker.equals(mSydney)) {
//				badge = R.drawable.badge_nsw;
//			} else if (marker.equals(mMelbourne)) {
//				badge = R.drawable.badge_victoria;
//			} else if (marker.equals(mPerth)) {
//				badge = R.drawable.badge_wa;
//			} else {
//				// Passing 0 to setImageResource will clear the image view.
//				badge = 0;
//			}
//			((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);
//
//			String title = marker.getTitle();
//			TextView titleUi = ((TextView) view.findViewById(R.id.title));
//			if (title != null) {
//				// Spannable string allows us to edit the formatting of the
//				// text.
//				SpannableString titleText = new SpannableString(title);
//				titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
//						titleText.length(), 0);
//				titleUi.setText(titleText);
//			} else {
//				titleUi.setText("");
//			}
//
//			String snippet = marker.getSnippet();
//			TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
//			if (snippet != null && snippet.length() > 12) {
//				SpannableString snippetText = new SpannableString(snippet);
//				snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0,
//						10, 0);
//				snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12,
//						snippet.length(), 0);
//				snippetUi.setText(snippetText);
//			} else {
//				snippetUi.setText("");
//			}
//		}
//	}
//
//	public static String[] CAR_STATUS = { "已设防", "未设防", "休眠", "断电", "运动",
//			"ACC开", "车门开", "油门断", "电门断", "电机锁" };
//
//	class OverItemT {
//		/** Demonstrates customizing the info window and/or its contents. */
//		private List<Marker> mGeoList = new ArrayList<Marker>();
//		private Drawable marker;
//		private Context mContext;
//		private ArrayList<Trace> traces = new ArrayList<Trace>();
//		Marker mCurrent_h;
//		Paint paint = new Paint();
//
//		public OverItemT(Drawable marker, Context context) {
//			this.marker = marker;
//			this.mContext = context;
//			// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
//			// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
//
//			// new initPath().execute();
//			paint.setColor(Color.BLUE);
//			paint.setDither(true);
//			paint.setStyle(Paint.Style.STROKE);
//			paint.setStrokeJoin(Paint.Join.ROUND);
//			paint.setStrokeCap(Paint.Cap.ROUND);
//			paint.setSubpixelText(true);
//			paint.setAntiAlias(true);
//			paint.setStrokeWidth(4);
//			singleitem.clear();
//		}
//
//		@Override
//		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//			// TODO Auto-generated method stub
//			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
//					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//			super.draw(canvas, mapView, shadow);
//			if (mCurrent_h != null) {// 跟踪时线路
//				Projection projection = mapView.getProjection();
//				// super.draw(canvas, mapView, shadow);
//				// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
//				// 先所有点的经度转像素
//				if (mCurrent_h == null) {
//					return;
//				}
//				int count = singleitem.size();
//				if (count >= 2) {
//					ArrayList<Point> points = new ArrayList<Point>();
//					for (int i = 0; i < count; i++) {
//						Point p = new Point();
//						OverlayItem overLayItem = singleitem.get(i);
//						projection.toPixels(overLayItem.getPoint(), p);
//						points.add(p);
//					}
//					// 第二个画笔 画线
//
//					// 连接 所有点
//					Path path = new Path();
//					path.moveTo(points.get(0).x, points.get(0).y);
//					for (int i = 1; i < count; i++) {
//						path.lineTo(points.get(i).x, points.get(i).y);
//					}
//					// 画出路径
//					canvas.drawPath(path, paint);
//				}
//				mCurrent_h.setMarker(boundCenter(initMarker(mCurrent_h.angle + i)));
//			}
//			System.out.println("-0");
//		}
//
//		int i = 0;
//
//		public Drawable initMarker(int angle) {// 箭头方向改变
//			BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(
//					R.drawable.location_gps);
//			Bitmap bit = bd.getBitmap();
//			Matrix m = new Matrix();
//			m.setRotate(angle, bit.getWidth() / 2, bit.getHeight() / 2);
//			bit = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
//					bit.getHeight(), m, true);
//			marker = new BitmapDrawable(bit);
//			return marker;
//		}
//
//		public void initItems(ArrayList<Trace> traces) {
//			mGeoList.clear();
//			this.traces = traces;
//			for (int i = 0; i < traces.size(); i++) {
//				int lat = traces.get(i).lag;
//				int lon = traces.get(i).lng;
//				GeoPoint p = new GeoPoint(lat, lon);
//				int location = 0;
//				try {
//					location = Integer.parseInt(traces.get(i).location);
//				} catch (NumberFormatException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}// ///
//				Marker oi = new MyOverLayItem(p, "" + traces.get(i).lag, ""
//						+ traces.get(i).lng, location, traces.get(i).speed,
//						traces.get(i).time);
//				if (currentfalg == 0) {
//					try {
//						int type = Integer.parseInt(TeamActivity.allitem
//								.get(traces.get(i).id).on);
//						switch (type) {
//						case 0:
//							Drawable marker0 = getResources().getDrawable(
//									R.drawable.group_car_h); // 得到需要标在地图上的资源
//							marker0.setBounds(0, 0,
//									(int) (marker0.getIntrinsicWidth() / 1.2),
//									(int) (marker0.getIntrinsicHeight() / 1.2)); // 为maker定义位置和边界
//							oi.setMarker(boundCenter(marker0));
//							break;
//						case 1:
//							Drawable marker1 = getResources().getDrawable(
//									R.drawable.group_car_h); // 得到需要标在地图上的资源
//							marker1.setBounds(0, 0,
//									(int) (marker1.getIntrinsicWidth() / 1.2),
//									(int) (marker1.getIntrinsicHeight() / 1.2)); // 为maker定义位置和边界
//							oi.setMarker(boundCenter(marker1));
//							break;
//						case 2:
//							Drawable marker = getResources().getDrawable(
//									R.drawable.group_car); // 得到需要标在地图上的资源
//							marker.setBounds(0, 0,
//									(int) (marker.getIntrinsicWidth() / 1.2),
//									(int) (marker.getIntrinsicHeight() / 1.2)); // 为maker定义位置和边界
//							oi.setMarker(boundCenter(marker));
//							break;
//						case 3:
//							Drawable marker2 = getResources().getDrawable(
//									R.drawable.group_car); // 得到需要标在地图上的资源
//							marker2.setBounds(0, 0,
//									(int) (marker2.getIntrinsicWidth() / 1.2),
//									(int) (marker2.getIntrinsicHeight() / 1.2)); // 为maker定义位置和边界
//							oi.setMarker(boundCenter(marker2));
//							break;
//						default:
//							Drawable markerf = getResources().getDrawable(
//									R.drawable.group_car_h); // 得到需要标在地图上的资源
//							markerf.setBounds(0, 0,
//									(int) (markerf.getIntrinsicWidth() / 1.2),
//									(int) (markerf.getIntrinsicHeight() / 1.2)); // 为maker定义位置和边界
//							oi.setMarker(boundCenter(markerf));
//							break;
//						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//				mGeoList.add(oi);
//			}
//			if (this.traces.size() == 1 && currentfalg == 1) {
//				mCurrent_h = mGeoList.get(0);
//				if (mCurrent_h != null) {
//					if (singleitem.size() == 0
//							|| (singleitem.size() > 0 && (mCurrent_h.getPoint()
//									.getLatitudeE6() != singleitem
//									.get(singleitem.size() - 1).getPoint()
//									.getLatitudeE6() && mCurrent_h.getPoint()
//									.getLongitudeE6() != singleitem
//									.get(singleitem.size() - 1).getPoint()
//									.getLongitudeE6()))) {
//						singleitem.add(mCurrent_h);
//					}
//				}
//				mMapView.getController().animateTo(mCurrent_h.getPoint());
//				new initAddress(0).execute();
//			} else {
//				singleitem.clear();
//				// CommonUtils.showProcessDialog(MyTrace_More.this, "正在获取数据...");
//				new initAddress(mCurrentindex).execute();
//			}
//			handler.post(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					mMapView.postInvalidate();
//					populate();
//				}
//			});
//		}
//
//		List<MyOverLayItem> singleitem = new ArrayList<MyOverLayItem>();
//
//		@Override
//		protected MyOverLayItem createItem(int i) {
//			// TODO Auto-generated method stub
//			return mGeoList.get(i);
//		}
//
//		@Override
//		public int size() {
//			// TODO Auto-generated method stub
//			return mGeoList.size();
//		}
//
//		@Override
//		// 处理当点击事件
//		protected boolean onTap(int i) {
//			// CommonUtils.showProcessDialog(MyTrace_More.this, "正在获取数据...");
//			setFocus(mGeoList.get(i));
//
//			new initAddress(i).execute();
//			return true;
//		}
//
//		public class initAddress extends AsyncTask<Void, Void, String> {
//			int i = 0;
//
//			public initAddress(int p) {
//				i = p;
//				if (i > mGeoList.size() - 1) {
//					return;
//				}
//				GeoPoint pt = mGeoList.get(i).getPoint();
//				MyTrace_More.mMapView.getController().animateTo(pt);
//				showPop(pt);
//				Trace t = traces.get(i);
//				mCurrentindex = i;
//				// CommonUtils.dismissProcessDialog();
//				// Item item = TeamActivity.items_map.get(t.id);
//				// int type = Integer.parseInt(item.on);
//				String typename = "";
//				int type = Integer.parseInt(TeamActivity.allitem.get(t.id).on);
//				switch (type) {
//				case 0:
//					// 总的停车时长=当前时间（手机系统时间）-位置时间+停车时长(单位为秒)
//					typename = "未开通";
//					break;
//				case 1:
//					typename = "离线";
//					break;
//				case 2:
//					typename = "在线";
//					if ("0".equals(t.speed)) {
//						long du = 0;
//						try {
//							long b_time = sdf2.parse(t.time.trim()).getTime() / 1000;
//							long e_time = new Date().getTime() / 1000;
//							du = Math.abs(e_time - b_time)
//									+ Integer.parseInt(t.parktime);
//						} catch (ParseException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						typename = "静止        " + SecondToDateDiff(du);
//					} else {
//						typename = "运动         " + t.speed + "km/h";
//					}
//
//					break;
//				case 3:
//					typename = "运动           " + t.speed + "km/h";
//					break;
//				}
//				Item i_c = new Item();
//				i_c.name = TeamActivity.allitem.get(t.id).name;
//				pop_title.setText(i_c.name);
//				pop_state.setText(typename);
//				i_c.id = t.id;
//				i_c.lag = traces.get(i).r_lag / 1e6 + "";
//				i_c.lng = traces.get(i).r_lng / 1e6 + "";
//				text_name.setText(i_c.name);
//				pop_address.setText("正在地址数据...");
//				i_c.stype = t.a_type;
//				pop_title.setTag(i_c);
//				item = i_c;
//			}
//
//			@Override
//			protected String doInBackground(Void... params) {
//				// TODO Auto-generated method stub
//				try {
//					List<NameValuePair> param = new ArrayList<NameValuePair>();
//					// param.add(new BasicNameValuePair("mobile", Build.MODEL));
//					param.add(new BasicNameValuePair("serviceid", PrefHelper
//							.getStringData(PrefHelper.P_USER_ID)));
//					param.add(new BasicNameValuePair("lng",
//							(traces.get(i).r_lng / 1e6) + ""));
//					param.add(new BasicNameValuePair("lat",
//							(traces.get(i).r_lag / 1e6) + ""));
//					String json = MySSLSocketFactory.getinfo(Urls.car_position,
//							param);
//					JSONObject obj = new JSONObject(json);
//					return obj.getString("addr");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(String result) {
//				// TODO Auto-generated method stub
//				// 更新气泡位置,并使之显示
//
//				// Toast.makeText(this.mContext, mGeoList.get(i).getSnippet(),
//				// Toast.LENGTH_SHORT).show();
//				if (mCurrentindex == i) {
//					pop_address.setText(result);
//					item.address = result;
//					pop_title.setTag(item);
//					// item = i_c;
//				}
//				super.onPostExecute(result);
//			}
//		}
//
//		@Override
//		public boolean onTap(GeoPoint arg0, MapView arg1) {
//			// TODO Auto-generated method stub
//			// 消去弹出的气泡
//			MyTrace_More.mPopView.setVisibility(View.GONE);
//			try {
//				super.onTap(arg0, arg1);
//			} catch (Exception e) {
//			}
//			return false;
//		}
//	}
//
//	int curren;
//	Handler pro_handler = new Handler() {
//
//		public void handleMessage(Message msg) {
//			// if (prog.getProgress() == 120 && msg.what < 120) {
//			// return;
//			// }
//			// if (msg.what > 120) {
//			// prog.setVisibility(View.GONE);
//			// CommonUtils.showToast("相片上传异常");
//			// return;
//			// }
//			// if (curren % 10 == 0) {
//			// new initData().execute();
//			// }
//			// prog.setProgress(curren);
//			// if (msg.what <= 120) {
//			// pro_handler.sendEmptyMessageDelayed(curren, 1000);
//			// curren++;
//		}
//	};
//
//	// public void initHistory(Intent intent) {
//	// currentfalg = 2;
//	// // if (oh != null) {
//	// // return;
//	// // }
//	// if (timer != null) {
//	// timer.cancel();
//	// }
//	// title.setText("历史回放");
//	// next_content.setVisibility(View.GONE);
//	// date_b = intent.getStringExtra("date_b");
//	// date_e = intent.getStringExtra("date_e");
//	// play = (SeekBar) findViewById(R.id.paly);
//	// findViewById(R.id.seek_content).setVisibility(View.VISIBLE);
//	// mMapView.postInvalidate();
//	// // mMapView.getOverlays().clear();
//	// // mMapView.getOverlays().add(mLocationOverlay);
//	// drable_marker = getResources().getDrawable(R.drawable.map_move);
//	// drable_marker.setBounds(0, 0, (int) (drable_marker.getIntrinsicWidth() / 1.5),
//	// (int) (drable_marker.getIntrinsicHeight() / 1.5));
//	// try {
//	// begin = sdf1.parse(date_b);
//	// end = sdf1.parse(date_e);
//	// int i = (int) ((end.getTime() - begin.getTime()) / 3600);
//	// play.setMax(i);
//	// play.setProgress(0);
//	// } catch (ParseException e) {
//	// // TODO Auto-generated catch block
//	// e.printStackTrace();
//	// }
//	// oh = new OverItemH(drable_marker, MyTrace_More_google.this);
//	// timer_text.setText("正在下载...");
//	// }
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		mMapView.onResume();
//		setUpMapIfNeeded();
//	}
//
//	public void initMap() {
//		// ////////////////////开启
//		if (currentfalg != 2) {
//			isback = false;
//			timer_text.setText("正在刷新...");
//			findViewById(R.id.seek_content).setVisibility(View.GONE);
//			new initData().execute();
//		} else {
//			isback = false;
//			// ispaly = true;
//			// initHistory(getIntent());
//		}
//	}
//
//	private void setUpMapIfNeeded() {
//		if (mMap == null) {
//			mMap = ((MapView) findViewById(R.id.map)).getMap();
//			if (mMap != null) {
//				setUpMap();
//			}
//		}
//	}
//
//	private void setUpMap() {
//		mMap.addMarker(new MarkerOptions().position(new LatLng(25, 80)).title(
//				"Marker"));
//	}
//
//	@Override
//	protected void onPause() {
//		mMapView.onPause();
//		super.onPause();
//	}
//
//	@Override
//	protected void onDestroy() {
//		mMapView.onDestroy();
//		super.onDestroy();
//	}
//
//	@Override
//	public void onLowMemory() {
//		super.onLowMemory();
//		mMapView.onLowMemory();
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		mMapView.onSaveInstanceState(outState);
//		// outState.putInt(CURRENT_FLAG_KEY, currentfalg);
//		// outState.putInt(CURRENT_INDEX_KEY, mCurrentindex);
//		// outState.putBoolean(CURRENT_INITLOAD_KEY, initload);
//	}
// }
