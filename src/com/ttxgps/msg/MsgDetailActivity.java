package com.ttxgps.msg;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.android.gms.maps.model.LatLng;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.gpslocation.BaseActivity;
import com.ttxgps.gpslocation.MapAPP;
import com.ttxgps.utils.CommonUtils;
import com.xtst.gps.R;


public class MsgDetailActivity extends BaseActivity implements OnClickListener{
	private double lat;
	private double lng;
	private MapView mMapView;
	BDLocationListener mLocationListener = null;// onResume时注册此listener，onPause时需要Remove
	MyLocationOverlay mLocationOverlay = null; // 定位图层
	private int zoom = 15;
	private GeoPoint point;
	/**
	 * 用户位置信息
	 */
	private LocationData mLocData;
	private View mPopView = null; // 点击mark时弹出的气泡View

	private Button delete_btn;
	private int id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_detail);
		initTitle();
		initView();
		setListener();
		initmap();
		initInfoWindow();
	}

	private void initTitle(){
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		this.mMapView = (MapView) findViewById(R.id.mapview);
		delete_btn = (Button) findViewById(R.id.right_btn);
		delete_btn.setBackgroundResource(R.drawable.del_msg_new);
		delete_btn.setOnClickListener(this);
		findViewById(R.id.traffic_cbx).setVisibility(View.GONE);
		findViewById(R.id.device_bt).setVisibility(View.GONE);

	}

	private void initmap(){
		MapAPP app = (MapAPP) this.getApplication();
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(getApplication());
			app.mBMapMan.init(new MapAPP.MyGeneralListener());
		}
		app.mBMapMan.start();
		// 添加定位图层
		mLocationOverlay = new MyLocationOverlay(mMapView);
		// 实例化定位数据，并设置在我的位置图层
		mLocData = new LocationData();
		mLocationOverlay.setData(mLocData);
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(mLocationOverlay);
		mMapView.getController().setZoom(zoom);

	}

	private void initInfoWindow() {
		id = getIntent().getIntExtra("id", -1);
		String msgType = getIntent().getStringExtra("type");
		String lat = getIntent().getStringExtra("lat");
		String lng = getIntent().getStringExtra("lng");
		String time = getIntent().getStringExtra("time");
		String content = getIntent().getStringExtra("content");
		String address = getIntent().getStringExtra("address");
		((TextView) findViewById(R.id.title_tv)).setText(MsgUtil.getMsgCategoryName(Integer.parseInt(msgType)));
		if(id!=-1){
			delete_btn.setVisibility(View.VISIBLE);
		}
		mPopView = super.getLayoutInflater().inflate(R.layout.map_windowinfo, null);
		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		mPopView.setVisibility(View.INVISIBLE);
		((TextView) mMapView.findViewById(R.id.pop_title)).setText(getString(R.string.location_time, new Object[]{time}));
		((TextView) mMapView.findViewById(R.id.pop_state)).setText(getString(R.string.warn_mobile, new Object[]{content}));
		((TextView) mMapView.findViewById(R.id.pop_address)).setText(getString(R.string.warn_address, new Object[]{address}));

		View contentv = mPopView.findViewById(R.id.content);
		int w = 320 * getResources().getDisplayMetrics().widthPixels / 480;
		int h = 155 * getResources().getDisplayMetrics().widthPixels / 480;
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(w, h);
		contentv.setLayoutParams(llp);
		this.lat = CommonUtils.toDouble(lat);
		this.lng = CommonUtils.toDouble(lng);
		point = new GeoPoint((int) (this.lat * 1e6), (int) (this.lng * 1e6));
		mMapView.getController().animateTo(point);
		showPop(point);

		mLocData.latitude = this.lat;
		mLocData.longitude = this.lng;
		// 如果不显示定位精度圈，将accuracy赋值为0即可
		//		mLocData.accuracy = location.getRadius();
		//		mLocData.direction = location.getDerect();

		// 将定位数据设置到定位图层里
		mLocationOverlay.setData(mLocData);
		// 更新图层数据执行刷新后生效
		mMapView.refresh();
	}

	public void showPop(GeoPoint gp) {
		int w = 320 * getResources().getDisplayMetrics().widthPixels / 480;
		MapView.LayoutParams ml = new MapView.LayoutParams(w,
				MapView.LayoutParams.WRAP_CONTENT, gp,
				MapView.LayoutParams.CENTER_HORIZONTAL
				| MapView.LayoutParams.BOTTOM);
		ml.y -= 40;
		mMapView.updateViewLayout(mPopView, ml);
		mPopView.setVisibility(View.VISIBLE);
	}

	protected void setListener() {
		findViewById(R.id.location_bt).setOnClickListener(this);
		findViewById(R.id.zoomin_ib).setOnClickListener(this);
		findViewById(R.id.zoomout_ib).setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.location_bt:
			if (this.lat >= 0.0d && this.lng >= 0.0d) {
				mMapView.getController().animateTo(point);
				// 更新图层数据执行刷新后生效
				mMapView.refresh();
			}
			break;
		case R.id.zoomin_ib:
			if ((zoom + 1) > 18) {
				Utils.showToast("Already in max zoom in.");
			} else {
				mMapView.getController().setZoom(++zoom);
			}
			break;
		case R.id.zoomout_ib:
			if ((zoom - 1) < 3) {
				Utils.showToast("Already in min zoon out.");
			} else {
				mMapView.getController().setZoom(--zoom);
			}
			break;
		case R.id.right_btn:
			int ret = MsgDBOper.getInstance(MsgDetailActivity.this).deleteByID(this.id);
			if (ret == -1 || ret == 0) {
				Utils.showToast(R.string.delete_fail);
				return;
			}
			Utils.showToast(R.string.deleted_success);
			MsgDetailActivity.this.setResult(-1);
			MsgDetailActivity.this.finish();

			break;
		default:
		}


	}

}
