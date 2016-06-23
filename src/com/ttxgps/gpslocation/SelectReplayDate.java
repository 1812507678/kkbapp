package com.ttxgps.gpslocation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.xtst.gps.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.TeamMember;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class SelectReplayDate extends BaseActivity {
	private Button sure = null;
	private Button cancle = null;
	private TextView date_et = null;
	private TextView time_et = null;
	public static final int success = 1;
	public static final int fall = 2;
	private final static int DATE_DIALOG = 0;
	private final static int TIME_DIALOG = 1;
	private Calendar c = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.date);

		date_et = (TextView) findViewById(R.id.date_et);
		time_et = (TextView) findViewById(R.id.time_et);
		date = sdf.format(new Date());
		date_et.setText(date);
		sure = (Button) findViewById(R.id.sure);
		cancle = (Button) findViewById(R.id.cancle);
		date_et.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG);
			}
		});
		time_et.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SelectReplayDate.this, TimeUpdate.class);
				startActivityForResult(intent, 1);
			}
		});
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if (date == null
							|| !compareDate(sdf.parse(date), new Date())) {
						Utils.showToast(R.string.reselect_date);
						return;
					}

					if (end == null) {
						begin = "00:00:00";
						end = "23:59:59";
					}
					int mapType = getIntent().getIntExtra("mapType", 0);
					TeamMember item = (TeamMember) getIntent().getSerializableExtra("item");
					if (item != null) {
						Intent intent = new Intent();
						intent.putExtra("item", item);
						intent.putExtra("date_b", date + " " + begin);
						intent.putExtra("date_e", date + " " + end);
						intent.putExtra(MyTrace_More_Baidu.CURRENT_TAB_STR,MyTrace_More_Baidu.LOCATION_TAB);

						if (mapType == 0) {
							intent.setClass(SelectReplayDate.this, MyTrace_More_Baidu.class);
						} else {
							intent.setClass(SelectReplayDate.this, MyTrace_More_Google.class);
						}
						startActivity(intent);
					} else {
						Intent intent = new Intent();
						intent.putExtra("date_b", date + " " + begin);
						intent.putExtra("date_e", date + " " + end);
						intent.putExtra("type", 2);
						setResult(success, intent);
					}
					finish();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public boolean compareDate(Date date1, Date date2) {
		return date1.before(date2);

	}

	String end = null;
	String begin = null;
	String date = null;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == success) {
			end = data.getStringExtra("date_e");
			begin = data.getStringExtra("date_b");
			time_et.setText(begin + "  -  " + end);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 创建日期及时间选择对话框
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DATE_DIALOG:
			c = Calendar.getInstance();
			dialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker dp, int year,
						int month, int dayOfMonth) {
					date = year + "-" + (month + 1) + "-" + dayOfMonth;
					date_et.setText(date);
				}
			}, c.get(Calendar.YEAR), // 传入年份
			c.get(Calendar.MONTH), // 传入月份
			c.get(Calendar.DAY_OF_MONTH) // 传入天数
					);
			break;
		case TIME_DIALOG:
			c = Calendar.getInstance();
			dialog = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay,
						int minute) {
					date_et.setText(hourOfDay + getString(R.string.hour)
							+ minute + getString(R.string.minute));
				}
			}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
			true);
			break;
		}
		return dialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
