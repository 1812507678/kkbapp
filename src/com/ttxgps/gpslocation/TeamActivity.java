package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.xtst.gps.R;
import com.ttxgps.entity.TeamMember;
import com.ttxgps.entity.Trace;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.palmtrends.app.ShareApplication;
import com.palmtrends.loadimage.Utils;


public class TeamActivity extends BaseActivity {
	public static Map<String, TeamMember> memberList = new HashMap<String, TeamMember>();
	public static HashMap<String, Trace> memberTraces = new HashMap<String, Trace>();

	private MyExpandableListAdapter teamMemberAdapter;
	private ExpandableListView teamMemberList;
	public List<TeamMember> groups = null;
	public Map<Integer, List<TeamMember>> children = null;
	public View load;
	public TeamMember current_item;
	public static boolean googleServiceSupport = true;
	public int curMapType = 0; // 0:baidu, 1:google
	private static PendingIntent pendingReceiver = null;
	private int refresh_period;
	// {"userID":"165","userName":"nestorzhang","loginName":"nestor", ... }
	private static int userID = LoginActivity.userID;
	private static int deviceID = LoginActivity.deviceID;
	private static int typeID = 0;
	private static String userName = "";
	private static String loginName = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team);

		//typeID = PrefHelper.getIntData(PrefHelper.P_TYPE_ID);
		//userName = PrefHelper.getStringData(PrefHelper.P_USER_NAME);
		//loginName = PrefHelper.getStringData(PrefHelper.P_LOGIN_NAME);

		teamMemberList = (ExpandableListView) findViewById(R.id.expandableListView);
		teamMemberList.setGroupIndicator(null);
		teamMemberList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				current_item = children.get(groupPosition).get(childPosition);

				v.setSelected(true);
				return false;
			}
		});

		load = findViewById(R.id.loading);
		//load.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (ShareApplication.flag) {
			ShareApplication.flag = false;
			menu_move = findViewById(R.id.move);
			View trackView = findViewById(R.id.menu_track);
			trackView.measure(init_w, init_h);
			int width = trackView.getMeasuredWidth();
			int height = trackView.getMeasuredHeight();
			RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
					getResources().getDisplayMetrics().widthPixels / 4, height);
			menu_move.setLayoutParams(rlp);
			menu_move.clearAnimation();
			current_item = null;
			if (old != null) {
				old.setBackgroundResource(R.drawable.select_btn);
			}
			oldview = null;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// load.setVisibility(View.VISIBLE);

		// Check if loginName is changed.
		// This can happen when user switch account.
		if (LoginActivity.newLogin) {
			userID = LoginActivity.userID;
			deviceID = LoginActivity.deviceID;
			typeID = PrefHelper.getIntData(PrefHelper.P_TYPE_ID);
			userName = PrefHelper.getStringData(PrefHelper.P_USER_NAME);
			loginName = PrefHelper.getStringData(PrefHelper.P_LOGIN_NAME);

			memberTraces.clear();
			memberList.clear();

			if (groups != null) {
				groups.clear();
				groups = null;
			}
			if (children != null) {
				children.clear();
				children = null;
			}

			teamMemberAdapter = null;

			LoginActivity.newLogin = false;

			load.setVisibility(View.VISIBLE);
		}

		String periodString;
		if (PrefHelper.hasKey(SetRefreshActivity.REFRESH_PERIOD_TEAM)) {
			periodString = PrefHelper.getStringData(SetRefreshActivity.REFRESH_PERIOD_TEAM);
		} else { // Use default value
			periodString = SetRefreshActivity.REFRESH_PERIOD_DEFAULT_TEAM;
		}
		refresh_period = Integer.parseInt(periodString);

		// Check if Google Play Service is available which supports Google Map.
		int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(TeamActivity.this);
		if (ConnectionResult.SUCCESS != errorCode) {
			googleServiceSupport = false;
		}

		// Check if the user ever selected a map type.
		if (PrefHelper.hasKey(PrefHelper.P_SELECTED_MAP)) {
			curMapType = PrefHelper.getIntData(PrefHelper.P_SELECTED_MAP);
			// Force to baidu if not support google.
			if (curMapType < 0 || curMapType > 1 ||
					!googleServiceSupport) {
				curMapType = 0;
			}
		} else { // map type is not set by user.
			// Default to google map if the phone supports.
			curMapType = googleServiceSupport ? 1 : 0;
		}

		MapAPP.handler_list = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				new InitData().execute();
			}
		};

		new InitData().execute();

		if (teamMemberAdapter != null) {
			teamMemberAdapter.notifyDataSetChanged();
		}

		if (pendingReceiver == null) {
			setTimer();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MapAPP.handler_list = null;
	}

	public class InitData extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// Now init Geography information for all members.
			// TODO: DON'T MOVE THIS LINE TO OTHER WHERE.
			initGeoPoint();

			// Get team member list from server.
			try {
				WebService webservice = new WebService(TeamActivity.this, "GetDeviceList");
				LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
				if (typeID == 0) {
					linkedlist.add(new WebServiceProperty("ID", userID));
				} else {
					linkedlist.add(new WebServiceProperty("ID", deviceID));
				}
				linkedlist.add(new WebServiceProperty("PageNo", Integer.valueOf(1)));
				linkedlist.add(new WebServiceProperty("PageCount", Integer.valueOf(0x186a0)));
				linkedlist.add(new WebServiceProperty("TypeID", typeID));
				linkedlist.add(new WebServiceProperty("IsAll", true));
				webservice.SetProperty(linkedlist);
				return webservice.Get("GetDeviceListResult",WebService.URL_OPEN);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}

		private List<TeamMember> parseJSON(String parseJson) throws Exception {
			/*
			if (pendingReceiver == null) {
				setTimer();
			}*/
			// items_map.clear();
			List<TeamMember> teamList = new ArrayList<TeamMember>();
			JSONObject json = new JSONObject(parseJson);
			if (json.has("Status") && (json.getInt("Status") == 0)) {

				// TODO: We have only one team.
				// {"userID":"165","userName":"nestorzhang","loginName":"nestor"," ...}
				TeamMember team = new TeamMember();
				team.id = Integer.toString(userID);
				team.name = loginName;
				team.nickname = userName;

				JSONArray memberArray = json.getJSONArray("arr");
				int count = memberArray.length();
				children = new HashMap<Integer, List<TeamMember>>();
				List<TeamMember> childs = new ArrayList<TeamMember>();
				int online_members = 0;
				// {"id":"206","name":"TR02-42339","groupID":"-1","car":"车牌","status":"0","icon":"27"}
				for (int i = 0; i < count; i++) {
					TeamMember teamMember = new TeamMember();
					JSONObject member = memberArray.getJSONObject(i);
					teamMember.id = member.getString("id");
					teamMember.name = member.getString("name");
					teamMember.pid = member.getString("groupID");
					teamMember.status = member.getString("status");

					/*
					 * status:
					 * 0 LogOff; 1 Move; 2 Stop; 3 Offline; 4 Arrears
					 */
					if ("1".equals(teamMember.status) || "2".equals(teamMember.status)) {
						online_members++;
					}
					// items_map.put(item_c.id, item_c);
					memberList.put(teamMember.id, teamMember);
					childs.add(teamMember);
				}
				team.onlinecount = online_members + "";
				team.childcount = childs.size() + "";
				children.put(0, childs);
				teamList.add(team);

			} else {
				return null;
			}
			return teamList;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			// Remove loading view once we get data from server.
			load.setVisibility(View.GONE);
			Utils.dismissProcessDialog();

			List<TeamMember> items = null;
			try {
				items = parseJSON(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (items == null) {
				Utils.showToast(R.string.error_fail_load);
				return;
			}
			if (items.size() == 0) {
				Utils.showToast(R.string.error_no_member);
				return;
			}

			groups = items;
			super.onPostExecute(result);

			if (teamMemberAdapter == null) {
				teamMemberAdapter = new MyExpandableListAdapter();
				teamMemberList.setAdapter(teamMemberAdapter);
			} else {
				teamMemberAdapter.notifyDataSetChanged();
			}

			for (int i = 0; i < items.size(); i++) {
				teamMemberList.expandGroup(i);
			}

			/* Select the first member by default.
			teamMemberAdapter.getChildView(0, 0, false, null, null).setSelected(true);
			current_item = children.get(0).get(0);
			 */
		}
	}

	boolean initGeoPoint() {
		// myTraces.clear();
		try {

			String result = "";

			WebService webservice = new WebService(TeamActivity.this, "GetTrackingByUserID");
			LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
			linkedlist.add(new WebServiceProperty("UserID", userID));
			if (typeID == 0) { // User account
				linkedlist.add(new WebServiceProperty("DeviceID", -1)); // Get all devices
			} else { // Device account
				linkedlist.add(new WebServiceProperty("DeviceID", deviceID)); // Get specific devices
			}
			linkedlist.add(new WebServiceProperty("MapType",
					(curMapType == 1) ? "Google" : "Baidu"));
			webservice.SetProperty(linkedlist);
			result = webservice.Get("GetTrackingByUserIDResult",WebService.URL_OPEN);

			JSONObject jsonObj = new JSONObject(result);
			if (jsonObj.has("Status") && (jsonObj.getInt("Status") == 0)) {
				JSONArray deviceArr = jsonObj.getJSONArray("devices");

				// !!!WARNING!!! JSONArray.toString() will change the
				// ";" separator into ","
				String[] strs = deviceArr.toString().replace("[", "")
						.replace("]", "").replace("\"", "").split(",");
				int count = strs.length;
				for (int i = 0; i < count; i++) {
					if (strs[i] == null || strs[i].equals("")) {
						continue;
					}
					Trace t = new Trace();

					String[] info = strs[i].split(";");
					t.initTrace(info);
					memberTraces.put(t.id, t);
				}
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Utils.showToast("获取设备列表错误");
			e.printStackTrace();
		}
		return false;
	}

	public void onTopMenuClick(View view) {
		switch (view.getId()) {
		case R.id.reflush:
			Utils.showProcessDialog(this, R.string.refreshing);
			new InitData().execute();
			break;
		case R.id.map:
			Intent intent = new Intent();
			if (current_item == null) {
				intent.putExtra("type", 0);  // Team monitor
			} else {
				intent.putExtra("item", current_item);
				intent.putExtra("type", 1);  // Single car track
			}

			if (curMapType == 1) {
				intent.setClass(TeamActivity.this, MyTrace_More_Google.class);
			} else {
				intent.setClass(TeamActivity.this, MyTrace_More_Baidu.class);
			}
			startActivity(intent);
			break;
		}
	}

	View menu_move;
	int init_w = View.MeasureSpec.makeMeasureSpec(0,
			View.MeasureSpec.UNSPECIFIED);
	int init_h = View.MeasureSpec.makeMeasureSpec(0,
			View.MeasureSpec.UNSPECIFIED);

	int number;
	View oldview;

	/**
	 * 导行条滑动执行
	 * 
	 * @param current_View
	 */
	public void startSlip(final View current_View, final View current_MoveView) {
		if (oldview == null) {
			number = 0;
		} else {
			number = oldview.getLeft();
		}
		Animation a = new TranslateAnimation(number, current_View.getLeft(),
				0.0f, 0.0f);

		a.setDuration(400);
		a.setFillAfter(true);
		a.setFillBefore(true);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				switch (current_View.getId()) {
				case R.id.menu_track:
					if (current_item != null) {
						Intent intent = new Intent();
						intent.putExtra("item", current_item);
						intent.putExtra("type", 1);
						if (curMapType == 1) {
							intent.setClass(TeamActivity.this, MyTrace_More_Google.class);
						} else {
							intent.setClass(TeamActivity.this, MyTrace_More_Baidu.class);
						}
						startActivity(intent);
					} else {
						Utils.showToast(getString(R.string.select_member));
					}
					break;
				case R.id.menu_replay:
					if (current_item != null) {
						Intent intent = new Intent();
						intent.putExtra("item", current_item);
						intent.putExtra("mapType", curMapType);
						intent.setClass(TeamActivity.this, SelectReplayDate.class);
						startActivity(intent);
					} else {
						Utils.showToast(getString(R.string.select_member));
					}
					break;
				case R.id.menu_monitor:
					Intent intent = new Intent();
					intent.putExtra("type", 0);
					if (curMapType == 1) {
						intent.setClass(TeamActivity.this, MyTrace_More_Google.class);
					} else {
						intent.setClass(TeamActivity.this, MyTrace_More_Baidu.class);
					}
					startActivity(intent);
					break;
				case R.id.menu_options:
					Intent setting = new Intent();
					setting.setClass(TeamActivity.this, SettingActivity.class);
					startActivity(setting);
					break;
				}
				oldview = current_View;
			}
		});
		menu_move.startAnimation(a);
	}

	/**
	 * onClick method for menu imageView.
	 * @param view
	 */
	public void things(View view) {
		menu_move.setVisibility(View.VISIBLE);
		startSlip(view, oldview);
	}

	public View old;

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		// Sample data set. children[i] contains the children (String[]) for
		// groups[i].

		@Override
		public TeamMember getChild(int groupPosition, int childPosition) {
			return children.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return children.get(groupPosition).size();
		}

		public TextView getGenericView(boolean isExpanded) {
			// Layout parameters for the ExpandableListView

			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			TextView textView = new TextView(TeamActivity.this);
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(36, 0, 0, 0);
			return textView;
		}

		public View optionview;
		/* TODO
		class Delete extends AsyncTask<String, String, Boolean> {

			@Override
			protected Boolean doInBackground(String... params) {
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				// param.add(new BasicNameValuePair("mobile", Build.MODEL));
				param.add(new BasicNameValuePair("serviceid", PrefHelper
						.getStringData(PrefHelper.P_USER_ID)));
				param.add(new BasicNameValuePair("memberid", params[0]));

				try {
					String json = MySSLSocketFactory.getinfo(
							Urls.vip_bcs_delmember, param);
					JSONObject jsonobj = new JSONObject(json);
					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
						CommonUtils.showToast("删除成功");
						current_item = null;
					} else {
						CommonUtils.showToast("删除失败");
					}
				} catch (Exception e) {
					CommonUtils.showToast("删除失败");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				CommonUtils.dismissProcessDialog();
				new InitData().execute();
			}
		}
		 */

		GestureDetector gd = new GestureDetector(TeamActivity.this,
				new GestureDetector.SimpleOnGestureListener() {

			/* TODO
					@Override
					public void onLongPress(MotionEvent e) {
						// TODO Auto-generated method stub
						if (old != null) {
							old.setSelected(false);
							old.setBackgroundResource(R.drawable.d_list_bg);
						}
						optionview.setSelected(true);
						old = optionview;
						current_item = (TeamMember) old.getTag();
						optionview.setBackgroundResource(R.drawable.d_list_selector_bg);
						teamMemberAdapter.notifyDataSetChanged();
						new AlertDialog.Builder(TeamActivity.this)
						.setTitle("成员操作")
						.setItems(new String[] { "编辑", "删除" },
								new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									Intent intent = new Intent();
									intent.setClass(
											TeamActivity.this,
											AddChildActivity.class);
									intent.putExtra("mid",
											current_item.id);
									startActivityForResult(
											intent, 1);
									break;
								case 1:
									CommonUtils.showProcessDialog(
											TeamActivity.this,
											"正在删除...");
									new Delete()
									.execute(current_item.id);
									break;
								}
							}
						}).setNegativeButton("取消", null).show();
					}
			 */
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				if (old != null) {
					old.setSelected(false);
					old.setBackgroundResource(R.drawable.d_list_bg);
				}
				optionview.setSelected(true);
				old = optionview;
				current_item = (TeamMember) old.getTag();
				optionview
				.setBackgroundResource(R.drawable.d_list_selector_bg);
				teamMemberAdapter.notifyDataSetChanged();
				return super.onSingleTapUp(e);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				if (old != null) {
					old.setSelected(false);
				}
				optionview.setSelected(true);
				old = optionview;
				current_item = (TeamMember) old.getTag();
				Intent intent = new Intent();
				intent.putExtra("item", current_item);
				intent.putExtra(MyTrace_More_Baidu.CURRENT_TAB_STR,MyTrace_More_Baidu.LOCATION_TAB);
				if (curMapType == 1) {
					intent.setClass(TeamActivity.this, MyTrace_More_Google.class);
				} else {
					intent.setClass(TeamActivity.this, MyTrace_More_Baidu.class);
				}

				startActivity(intent);
				return true;
			}

		});

		int drawableid[][] = new int[][] {
				{ R.drawable.group_car_h, R.drawable.group_car },
				{ R.drawable.child_icon, R.drawable.child_icon } }; // TODO

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(TeamActivity.this).inflate(
						R.layout.listitem_child, null);

				convertView.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						optionview = v;
						gd.onTouchEvent(event);
						return true;
					}
				});
			}
			TeamMember item = getChild(groupPosition, childPosition);
			// convertView.setBackgroundDrawable(null);
			convertView.setTag(item);

			/*
			 * Fill/updates this child's information. Including:
			 * Head image, name, state and other info associated to
			 * current state.
			 */
			ImageView iconView = (ImageView) convertView
					.findViewById(R.id.child_head);
			TextView titleView = (TextView) convertView
					.findViewById(R.id.child_title);
			TextView stateView = (TextView) convertView
					.findViewById(R.id.child_state);
			TextView otherStateView = (TextView) convertView
					.findViewById(R.id.child_other);

			// Set title(name) for this child.
			titleView.setText(item.name);

			Trace trace = memberTraces.get(item.id);

			int typeIndex = Integer.parseInt(item.type) - 1;
			int stateIndex = Integer.parseInt(item.status);

			// Set child's state and other info.
			otherStateView.setText("");
			String stateText = "";
			try {
				// 0 LogOff; 1 Move; 2 Stop; 3 Offline; 4 Arrears
				switch (stateIndex) {
				case 0:
					iconView.setImageResource(drawableid[typeIndex][0]);
					stateText = getString(R.string.unregistered);
					break;
				case 1:
					stateText = getString(R.string.running);
					iconView.setImageResource(R.drawable.group_car);
					otherStateView.setText(trace.speed + "km/h");
					break;
				case 2:
					iconView.setImageResource(drawableid[typeIndex][1]);
					stateText = getString(R.string.stop);
					otherStateView.setText(minToDateDiff(Long.parseLong(trace.pos_time)));
					break;
				case 3:
					iconView.setImageResource(drawableid[typeIndex][0]);
					stateText = getString(R.string.offline);
					break;
				case 4:
					iconView.setImageResource(drawableid[typeIndex][0]);
					stateText = getString(R.string.arrears);
					break;
				default:
					iconView.setImageResource(drawableid[typeIndex][0]);
					stateText = getString(R.string.unregistered);
					break;
				}
				stateView.setText(stateText);
			} catch (Exception e) {
				Utils.showToast("Exception during set car status: " + stateIndex);
			}

			if (current_item != null && item.id.equals(current_item.id)) {
				convertView
				.setBackgroundResource(R.drawable.d_list_selector_bg);
			} else {
				convertView.setBackgroundResource(R.drawable.d_list_bg);
			}
			return convertView;
		}

		@Override
		public TeamMember getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return groups.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(TeamActivity.this).inflate(
					R.layout.listitem_group, null);
			ImageView view = (ImageView) convertView
					.findViewById(R.id.group_head);
			TextView state = (TextView) convertView
					.findViewById(R.id.group_state);
			TeamMember item = getGroup(groupPosition);
			if (!"0".equals(item.childcount)) {
				state.setText("(" + item.onlinecount + "/" + item.childcount
						+ ")");
			}
			if (isExpanded) {
				view.setImageResource(R.drawable.arrow);
			} else {
				view.setImageResource(R.drawable.arrow_h);
			}
			TextView textView = (TextView) convertView
					.findViewById(R.id.group_title);
			textView.setText(item.name);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	public void setTimer() {
		Intent intent = new Intent("com.ttxgps.receiver");
		// TODO: intent.putExtra("msg", "21");
		pendingReceiver = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		// 按设定时间间隔，通过PendingIntent pendingReceiver对象发送广播
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				refresh_period * 1000, pendingReceiver);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (pendingReceiver != null) {
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			am.cancel(pendingReceiver);
		}
		MapAPP.handler_list = null;

		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
			.setTitle(R.string.exit)
			.setMessage(R.string.exit_message)
			.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					finish();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							android.os.Process
							.killProcess(android.os.Process
									.myPid());
						}
					}, 1000);
				}
			})
			.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0,
						int arg1) {
				}
			}).show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
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
}
