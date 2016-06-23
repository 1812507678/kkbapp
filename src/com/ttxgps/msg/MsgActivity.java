package com.ttxgps.msg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.adapter.MsgTypeAdapter;
import com.ttxgps.adapter.MsgsAdapter;
import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.BaseActivity;
import com.ttxgps.utils.Constants;
import com.xtst.gps.R;

public class MsgActivity extends BaseActivity implements OnItemClickListener{

	private static final String TAG = "MsgsActivity";
	public static MsgActivity instance;
	private MsgsAdapter adapter;
	private final int curMsgType = 0;
	List<MsgInfoDetail> datas = new ArrayList();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	LayoutInflater inflater;
	ListView listview;
	//	ListView lvMsgType;
	TextView no_msg;
	//	public Button btnMsgRight;
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	//	MsgTypeAdapter typeAdapter;
	private final List<Integer> typeIds = new ArrayList();
	String userId;
	//	View vMsgType;
	String deviceID;
	private final MsgReceiver receiver = new MsgReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msgs);
		instance = this;
		deviceID = getIntent().getStringExtra("deviceID");
		initTitle();
		initView();
		initReceiver();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideNotification();
	}
	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText("信息中心");
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		this.no_msg = (TextView) findViewById(R.id.no_msg);
		this.inflater = getLayoutInflater();
		this.listview = (ListView) findViewById(R.id.listview);
		this.listview.setEmptyView(this.no_msg);
		this.listview.setOnItemClickListener(this);
		this.adapter = new MsgsAdapter(this, this.datas);
		this.listview.setAdapter(this.adapter);
		//		this.vMsgType = findViewById(R.id.msg_type_layout);
		//		this.vMsgType.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				MsgActivity.this.vMsgType.setVisibility(View.GONE);
		//			}
		//		});
		//		this.btnMsgRight = (Button) findViewById(R.id.right_btn);
		//		this.btnMsgRight.setVisibility(View.VISIBLE);
		//		this.btnMsgRight.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		// TODO Auto-generated method stub
		//				if (vMsgType.getVisibility() == View.GONE) {
		//					vMsgType.setVisibility(View.VISIBLE);
		//				} else {
		//					vMsgType.setVisibility(View.GONE);
		//				}

		//			}
		//		});
		//		inittypeIds();
		//		this.lvMsgType = (ListView) findViewById(R.id.msg_type_listview);
		//		this.typeAdapter = new MsgTypeAdapter(this, this.typeIds);
		//		this.lvMsgType.setAdapter( this.typeAdapter);
		//		this.lvMsgType.setOnItemClickListener(new OnItemClickListener() {
		//
		//			@Override
		//			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		//				// TODO Auto-generated method stub
		//				MsgActivity.this.initDatas(MsgActivity.this.typeIds.get(position).intValue());
		//				MsgActivity.this.vMsgType.setVisibility(View.GONE);
		//
		//			}
		//		});

		initDatas(this.curMsgType);


	}

	private void inittypeIds(){
		this.typeIds.add(Integer.valueOf(0));
		this.typeIds.add(MsgUtil.TYPE_SOS_WARN);
		this.typeIds.add(MsgUtil.TYPE_EXCISE_DEVICE_WARN);
		this.typeIds.add(MsgUtil.TYPE_ENTER_FENCE_WARN);
		this.typeIds.add(MsgUtil.TYPE_OUT_FENCE_WARN);
		this.typeIds.add(MsgUtil.TYPE_COMMONLY_WARN);
	}

	private void initDatas(int msgType) {
		List<MsgInfoDetail> tempList = null;
		this.datas.clear();
		Log.i(TAG, "initDatas MsgType=" + msgType);
		if (msgType == 0) {
			if(TextUtils.isEmpty(deviceID)){
				tempList = MsgDBOper.getInstance(this).queryAllMsg(User.id,User.curBabys.getId());
			}else{
				tempList = MsgDBOper.getInstance(this).queryAllMsg(User.id,deviceID);
			}

		} else {
			if(TextUtils.isEmpty(deviceID)){
				tempList = MsgDBOper.getInstance(this).queryMsgByType(String.valueOf(User.id),User.curBabys.getId(), String.valueOf(msgType));
			}else{
				tempList = MsgDBOper.getInstance(this).queryMsgByType(String.valueOf(User.id),deviceID, String.valueOf(msgType));
			}
		}
		if (tempList != null) {
			for (MsgInfoDetail item : getGroupDatas(tempList)) {
				this.datas.add(item);
			}
		}
		this.adapter.notifyDataSetChanged();
	}




	private List<MsgInfoDetail> getGroupDatas(List<MsgInfoDetail> tempList) {
		List<MsgInfoDetail> list1 = new ArrayList();
		for (MsgInfoDetail item : tempList) {
			item.date = getDate(item.time);
			item.locTime = getTime(item.time);
			list1.add(item);
		}
		List<MsgInfoDetail> list2 = new ArrayList();
		int length = list1.size();
		MsgInfoDetail date;
		if (length == 1) {
			MsgInfoDetail info = list1.get(0);
			date = new MsgInfoDetail();
			date.flag = 0;
			date.date = info.date;
			list2.add(date);
			info.flag = 1;
			info.lineFlag = 3;
			list2.add(info);
		} else {
			for (int i = 0; i < length; i++) {
				MsgInfoDetail curInfo;
				MsgInfoDetail nextInfo;
				if (i == 0) {
					curInfo = list1.get(i);
					nextInfo = list1.get(i + 1);
					date = new MsgInfoDetail();
					date.flag = 0;
					date.date = curInfo.date;
					list2.add(date);
					curInfo.flag = 1;
					if (curInfo.date.equals(nextInfo.date)) {
						curInfo.lineFlag = 0;
					} else {
						curInfo.lineFlag = 3;
					}
					list2.add(curInfo);
				} else if (i + 1 < length) {
					curInfo = list1.get(i);
					nextInfo = list1.get(i + 1);
					if (list1.get(i - 1).date.equals(curInfo.date)) {
						curInfo.flag = 1;
						if (curInfo.date.equals(nextInfo.date)) {
							curInfo.lineFlag = 1;
						} else {
							curInfo.lineFlag = 2;
						}
						list2.add(curInfo);
					} else {
						date = new MsgInfoDetail();
						date.flag = 0;
						date.date = curInfo.date;
						list2.add(date);
						curInfo.flag = 1;
						if (curInfo.date.equals(nextInfo.date)) {
							curInfo.lineFlag = 0;
						} else {
							curInfo.lineFlag = 3;
						}
						list2.add(curInfo);
					}
				} else {
					curInfo = list1.get(i);
					if (list1.get(i - 1).date.equals(curInfo.date)) {
						curInfo.flag = 1;
						curInfo.lineFlag = 2;
						list2.add(curInfo);
					} else {
						date = new MsgInfoDetail();
						date.flag = 0;
						date.date = curInfo.date;
						list2.add(date);
						curInfo.flag = 1;
						curInfo.lineFlag = 3;
						list2.add(curInfo);
					}
				}
			}
		}
		return list2;
	}

	private String getTime(String time) {
		String timeStr = "";
		try {
			timeStr = this.timeFormat.format(this.simpleDateFormat.parse(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStr;
	}

	private String getDate(String time) {
		String dateStr = "";
		try {
			dateStr = this.dateFormat.format(this.simpleDateFormat.parse(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}





	class MsgAdapter extends BaseAdapter {
		int otherTextColor;
		int sosTextColor;

		class ViewHolder {
			ImageView ivTime;
			ImageView ivType;
			TextView tvAddress;
			TextView tvContent;
			TextView tvTime;
			TextView tvType;
			View vBottomLine;
			View vTopLine;

			ViewHolder() {
			}
		}

		MsgAdapter() {
			this.sosTextColor = Color.rgb(244, 116, 115);
			this.otherTextColor = Color.rgb(31, 147, 235);
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public MsgInfoDetail getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup group) {
			ViewHolder holder;
			if (v == null) {
				v = MsgActivity.this.inflater.inflate(R.layout.msg_category_item, null);
				holder = new ViewHolder();
				holder.tvTime = (TextView) v.findViewById(R.id.msg_time_tv);
				holder.vTopLine = v.findViewById(R.id.msg_top_line);
				holder.vBottomLine = v.findViewById(R.id.msg_bottom_line);
				holder.ivTime = (ImageView) v.findViewById(R.id.msg_time_iv);
				holder.ivType = (ImageView) v.findViewById(R.id.msg_type_iv);
				holder.tvType = (TextView) v.findViewById(R.id.msg_type_tv);
				holder.tvContent = (TextView) v.findViewById(R.id.msg_content_tv);
				holder.tvAddress = (TextView) v.findViewById(R.id.msg_address_tv);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			if (position == 0) {
				if (getCount() > 1) {
					holder.vTopLine.setVisibility(View.INVISIBLE);
					holder.vBottomLine.setVisibility(View.VISIBLE);
				} else {
					holder.vTopLine.setVisibility(View.INVISIBLE);
					holder.vBottomLine.setVisibility(View.INVISIBLE);
				}
			} else if (position == getCount() - 1) {
				holder.vTopLine.setVisibility(View.VISIBLE);
				holder.vBottomLine.setVisibility(View.INVISIBLE);
			} else {
				holder.vTopLine.setVisibility(View.VISIBLE);
				holder.vBottomLine.setVisibility(View.VISIBLE);
			}
			MsgInfoDetail info = getItem(position);
			if (TextUtils.isEmpty(info.time)) {
				holder.tvTime.setText("00:00");
			} else {
				String time = null;
				try {
					Date date = MsgActivity.this.simpleDateFormat.parse(info.time);
					if (date != null) {
						time = MsgActivity.this.timeFormat.format(date);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				holder.tvTime.setText(time);
			}
			int msgType = Integer.parseInt(info.msgType);
			if (msgType == MsgUtil.TYPE_SOS_WARN) {
				holder.ivTime.setImageResource(R.drawable.icon_sos_msg);
			} else {
				holder.ivTime.setImageResource(R.drawable.icon_other_msg);
			}
			holder.ivType.setVisibility(View.VISIBLE);
			switch (msgType) {
			case MsgUtil.TYPE_SOS_WARN /*101013*/:
				holder.ivType.setImageResource(R.drawable.icon_sos);
				holder.tvType.setText(R.string.msg_sos_warn);
				holder.tvType.setTextColor(this.sosTextColor);
				break;
			case MsgUtil.TYPE_EXCISE_DEVICE_WARN /*101014*/:
				holder.ivType.setImageResource(R.drawable.icon_excise_device);
				holder.tvType.setText(R.string.msg_excise_device_warn);
				holder.tvType.setTextColor(this.otherTextColor);
				break;
			case MsgUtil.TYPE_TUMBLE_WARN /*101015*/:
				holder.ivType.setImageResource(R.drawable.icon_tumble);
				holder.tvType.setText(R.string.msg_tumble_warn);
				holder.tvType.setTextColor(this.otherTextColor);
				break;
			case MsgUtil.TYPE_LOW_VOLTAGE_WARN /*101016*/:
				holder.ivType.setImageResource(R.drawable.icon_low_voltage);
				holder.tvType.setText(R.string.msg_low_voltage_warn);
				holder.tvType.setTextColor(this.otherTextColor);
				break;
			case MsgUtil.TYPE_ENTER_FENCE_WARN /*102004*/:
				holder.ivType.setImageResource(R.drawable.icon_enter_fence);
				holder.tvType.setText(R.string.msg_enter_fence_warn);
				holder.tvType.setTextColor(this.otherTextColor);
				break;
			case MsgUtil.TYPE_OUT_FENCE_WARN /*102005*/:
				holder.ivType.setImageResource(R.drawable.icon_out_fence);
				holder.tvType.setText(R.string.msg_out_fence_warn);
				holder.tvType.setTextColor(this.otherTextColor);
				break;
			case MsgUtil.TYPE_COMMONLY_WARN/*101017*/:
				holder.ivType.setImageResource(R.drawable.icon_other);
				holder.tvType.setText(R.string.msg_commonly_warn);
				holder.tvType.setTextColor(this.otherTextColor);
				break;
			default:
				holder.ivType.setImageResource(R.drawable.icon_out_fence);
				holder.ivType.setVisibility(View.INVISIBLE);
				holder.tvType.setText(R.string.other_msg);
				holder.tvType.setTextColor(this.otherTextColor);
				break;
			}
			holder.tvContent.setText(info.msgContent);
			holder.tvAddress.setText(info.address);
			return v;
		}
	}




	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
		// TODO Auto-generated method stub
		MsgInfoDetail temp = this.datas.get(position);
		if(Integer.parseInt(temp.msgType) != MsgUtil.TYPE_COMMONLY_WARN){
			if (!TextUtils.isEmpty(temp.lat) && !TextUtils.isEmpty(temp.lng)) {
				Intent intent = new Intent(this, MsgDetailActivity.class);
				intent.putExtra("id", temp.id);
				intent.putExtra("type", temp.msgType);
				intent.putExtra("lat", temp.lat);
				intent.putExtra("lng", temp.lng);
				intent.putExtra("time", temp.time);
				intent.putExtra("content", temp.msgContent);
				intent.putExtra("address", temp.address);
				startActivityForResult(intent, 0);
			}
		}else{
			//一般信息
			AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
			//			builder.setTitle(temp.msgContent);
			builder.setMessage(temp.msgContent);
			builder.setPositiveButton("确定",  new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.create().show();
		}

	}
	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PUSH_MSG);
		registerReceiver(this.receiver, filter);
	}

	private void unRegistReceiver() {
		unregisterReceiver(this.receiver);
	}

	class MsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.ACTION_PUSH_MSG.equals(intent.getAction())) {
				MsgActivity.this.hideNotification();
				MsgActivity.this.initDatas(0);
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		instance = null;
		unRegistReceiver();
	}

	private void hideNotification() {
		((NotificationManager) getSystemService("notification")).cancel(2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			initDatas(this.curMsgType);
		}

	}

}
