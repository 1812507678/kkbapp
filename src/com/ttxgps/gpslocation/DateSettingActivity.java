package com.ttxgps.gpslocation;
  
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.xtst.gps.R;

public class DateSettingActivity extends BaseActivity implements OnClickListener{
	public static final int FLAG_DATE_MODE = 1;
	public static final int FLAG_WEEK_MODE = 0;
	private final int DIALOG_SETDATE = 0;
	private final int END_DATE_ID = 1;
	private final int MODE_EVERYDAY = 0;
	private final int MODE_SETDATE = 3;
	private final int MODE_WEEKEND = 2;
	private final int MODE_WORKDAY = 1;
	private final int START_DATE_ID = 0;
	private MyAdarpter adapter;
	private Button btn_back;
	//	private RelativeLayout btn_end_date;
	private RadioButton btn_everyday;
	private RadioGroup btn_group;
	//	private RelativeLayout btn_start_date;
	private RadioButton btn_weekend;
	private RadioButton btn_workday;
	private boolean[] checkeds;
	private int choice_mode;
	private Calendar endDate;
	private int flag;
	private SimpleDateFormat formatDate;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
	//	private LinearLayout layout_setdate;
	private LinearLayout layout_week;
	private LayoutInflater mInflater;
	private HashMap<Integer, WeekOfDAY> mListData;
	private ListView mListView;
	private int nowday;
	private int nowmonth;
	private int nowyear;
	private String oldData;
	private Calendar startDate;
	private final int startEnd = -1;
	private String strEndDate = "";
	private String strStartDate = "";
	private String[] weekOfDays;


	private class MyAdarpter extends BaseAdapter {
		private boolean isEnable;

		private class ViewHolder {
			CheckBox ckbox;
			TextView txv;

			private ViewHolder() {
			}
		}

		private MyAdarpter() {
			this.isEnable = true;
		}

		@Override
		public int getCount() {
			return DateSettingActivity.this.mListData.size();
		}

		@Override
		public Object getItem(int position) {
			return DateSettingActivity.this.mListData.get(Integer.valueOf(position));
		}

		@Override
		public long getItemId(int position) {
			return DateSettingActivity.this.mListData.get(Integer.valueOf(position)).hashCode();
		}

		@Override
		public boolean isEnabled(int position) {
			return this.isEnable;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = DateSettingActivity.this.mInflater.inflate(R.layout.fortify_setdate_list_item, null);
				holder.txv = (TextView) convertView.findViewById(R.id.day_of_week);
				holder.ckbox = (CheckBox) convertView.findViewById(R.id.haschecked);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			WeekOfDAY day = (WeekOfDAY) getItem(position);
			holder.txv.setText(day.day);
			holder.ckbox.setChecked(day.isChoice);
			return convertView;
		}
	}
	private class WeekOfDAY {
		private String day;
		private boolean isChoice;
		private WeekOfDAY() {
		}
	}





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fortify_datesetting);
		this.choice_mode = 0;
		this.formatDate = new SimpleDateFormat("yyyy.MM.dd");
		this.startDate = Calendar.getInstance();
		this.endDate = Calendar.getInstance();
		this.weekOfDays = getResources().getStringArray(R.array.week_array);
		this.checkeds = new boolean[]{true, true, true, true, true, true, true};

		initTitle();
		initListData();
		initView();
		initRadioGroup();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.warn_date);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				saveDate();
			}
		});
	}

	private void initListData(){
		int i;
		this.mListData = new HashMap();
		this.flag = getIntent().getFlags();
		this.oldData = getIntent().getStringExtra("DataOfDate");
		if (this.flag == 0 && !getString(R.string.everyday).equals(this.oldData)) {
			for (i = 0; i < 7; i += FLAG_DATE_MODE) {
				this.checkeds[i] = false;
			}
			String[] arrs = this.oldData.split(",");
			for (i = 0; i < arrs.length; i += FLAG_DATE_MODE) {
				if (this.weekOfDays[0].equals(arrs[i])) {
					this.checkeds[0] = true;
				} else if (this.weekOfDays[FLAG_DATE_MODE].equals(arrs[i])) {
					this.checkeds[FLAG_DATE_MODE] = true;
				} else if (this.weekOfDays[2].equals(arrs[i])) {
					this.checkeds[2] = true;
				} else if (this.weekOfDays[3].equals(arrs[i])) {
					this.checkeds[3] = true;
				} else if (this.weekOfDays[4].equals(arrs[i])) {
					this.checkeds[4] = true;
				} else if (this.weekOfDays[5].equals(arrs[i])) {
					this.checkeds[5] = true;
				} else if (this.weekOfDays[6].equals(arrs[i])) {
					this.checkeds[6] = true;
				}
			}
		}
		for (i = 0; i < 7; i += FLAG_DATE_MODE) {
			WeekOfDAY day = new WeekOfDAY();
			day.day = this.weekOfDays[i];
			day.isChoice = this.checkeds[i];
			this.mListData.put(Integer.valueOf(i), day);
		}

	}
	private void initView(){
		Calendar calendar = Calendar.getInstance();
		this.nowyear = calendar.get(FLAG_DATE_MODE);
		this.nowmonth = calendar.get(2);
		this.nowday = calendar.get(5);
		this.mInflater = LayoutInflater.from(this);
		this.btn_group = (RadioGroup) findViewById(R.id.btn_group);
		//		this.layout_setdate = (LinearLayout) findViewById(R.id.layout_setdate);
		this.layout_week = (LinearLayout) findViewById(R.id.layout_week);
		this.mListView = (ListView) findViewById(R.id.week_list);
		this.btn_everyday = (RadioButton) findViewById(R.id.btn_everyday);
		this.btn_weekend = (RadioButton) findViewById(R.id.btn_weekend);
		this.btn_workday = (RadioButton) findViewById(R.id.btn_workday);
		this.btn_group.check(R.id.btn_everyday);
		this.btn_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.btn_everyday:
					DateSettingActivity.this.choice_mode = 0;
					DateSettingActivity.this.layout_week.setVisibility(View.VISIBLE);
					DateSettingActivity.this.adapter.isEnable = true;
					DateSettingActivity.this.changeListData();
					break;
				case R.id.btn_workday:
					DateSettingActivity.this.choice_mode = DateSettingActivity.FLAG_DATE_MODE;
					DateSettingActivity.this.layout_week.setVisibility(View.VISIBLE);
					DateSettingActivity.this.adapter.isEnable = true;
					DateSettingActivity.this.changeListData();
					break;
				case R.id.btn_weekend:
					DateSettingActivity.this.choice_mode = 2;
					DateSettingActivity.this.layout_week.setVisibility(View.VISIBLE);
					DateSettingActivity.this.adapter.isEnable = true;
					DateSettingActivity.this.changeListData();
					break;
				default:
				}
			}
		});
		this.adapter = new MyAdarpter();
		this.mListView.setAdapter(this.adapter);
		this.mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				WeekOfDAY day = (WeekOfDAY) parent.getItemAtPosition(position);
				if (day.isChoice) {
					day.isChoice = false;
				} else {
					day.isChoice = true;
				}
				DateSettingActivity.this.adapter.notifyDataSetChanged();
			}
		});
		String format = this.formatDate.format(new Date());
		this.strEndDate = format;
		this.strStartDate = format;
		if (this.flag == FLAG_DATE_MODE) {
			changeViews(false);
			String[] arrs = this.oldData.split("-");
			if (arrs.length >= 2) {
				this.strStartDate = arrs[0];
				this.strEndDate = arrs[FLAG_DATE_MODE];
			}
			try {
				this.startDate.setTimeInMillis(this.formatDate.parse(this.strStartDate).getTime());
				this.endDate.setTimeInMillis(this.formatDate.parse(this.strEndDate).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (this.flag == 0) {
			changeViews(true);
		}

	}

	private void changeListData() {
		int i = 0;
		while (i < 7) {
			WeekOfDAY day = this.mListData.get(Integer.valueOf(i));
			switch (this.choice_mode) {
			case BDLocation.TypeNone /*0*/:
				day.isChoice = true;
				break;
			case FLAG_DATE_MODE /*1*/:
				if (i != 5 && i != 6) {
					day.isChoice = true;
					break;
				} else {
					day.isChoice = false;
					break;
				}
			case 2 /*2*/:
				if (i != 5 && i != 6) {
					day.isChoice = false;
					break;
				} else {
					day.isChoice = true;
					break;
				}
			default:
				break;
			}
			i += FLAG_DATE_MODE;
		}
		this.adapter.notifyDataSetChanged();
	}

	private void changeViews(boolean isChecked) {
		if (isChecked) {
			this.flag = 0;
			this.choice_mode = 0;
			this.layout_week.setVisibility(View.VISIBLE);
			this.btn_group.setVisibility(View.VISIBLE);
			return;
		}
		this.flag = FLAG_DATE_MODE;
		this.choice_mode = 3;
		this.layout_week.setVisibility(View.GONE);
		this.btn_group.setVisibility(View.GONE);
	}



	private void initRadioGroup() {
		if (this.checkeds[0] && this.checkeds[FLAG_DATE_MODE] && this.checkeds[2] && this.checkeds[3] && this.checkeds[4] && !this.checkeds[5] && !this.checkeds[6]) {
			this.btn_group.check(R.id.btn_workday);
		} else if (!this.checkeds[0] && !this.checkeds[FLAG_DATE_MODE] && !this.checkeds[2] && !this.checkeds[3] && !this.checkeds[4] && this.checkeds[5] && this.checkeds[6]) {
			this.btn_group.check(R.id.btn_weekend);
		} else if (this.checkeds[0] && this.checkeds[FLAG_DATE_MODE] && this.checkeds[2] && this.checkeds[3] && this.checkeds[4] && this.checkeds[5] && this.checkeds[6]) {
			this.btn_group.check(R.id.btn_everyday);
		}
	}

	private void saveDate() {
		Intent data = new Intent();
		switch (this.choice_mode) {
		case BDLocation.TypeNone /*0*/:
		case FLAG_DATE_MODE /*1*/:
		case 2/*2*/:
			StringBuilder sb = new StringBuilder();
			boolean flag = true;
			int num = 0;
			for (int i = 0; i < 7; i += FLAG_DATE_MODE) {
				if (this.mListData.get(Integer.valueOf(i)).isChoice) {
					num += FLAG_DATE_MODE;
					if (flag) {
						flag = false;
						setStrWeekOfDay(sb, i);
					} else {
						sb.append(',');
						setStrWeekOfDay(sb, i);
					}
				}
			}
			if (num != 0) {
				if (num != 7) {
					data.putExtra("choiceData", sb.toString());
					break;
				} else {
					data.putExtra("choiceData", getString(R.string.everyday));
					break;
				}
			}
			data.putExtra("choiceData", getString(R.string.never));
			break;
		case 3 /*3*/:
			if (!"".equals(this.strStartDate)) {
				if (!"".equals(this.strEndDate)) {
				}
				Toast.makeText(this, getString(R.string.set_end_date),Toast.LENGTH_SHORT).show();
				return;
			}
			Toast.makeText(this, getString(R.string.set_start_date),Toast.LENGTH_SHORT).show();

			return;
		}
		data.putExtra("choiceMode", this.flag);
		setResult(222, data);
		finish();
	}
	private void setStrWeekOfDay(StringBuilder sb, int i) {
		String str = "";
		sb.append(this.weekOfDays[i]);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			saveDate();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
