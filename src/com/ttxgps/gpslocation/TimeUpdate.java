package com.ttxgps.gpslocation;
  
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TimePicker;

import com.xtst.gps.R;
import com.palmtrends.loadimage.Utils;

public class TimeUpdate extends BaseActivity {
	private Button sure = null;
	private Button cancle = null;
	private final Calendar c = null;
	TimePicker tp_begin;
	TimePicker tp_end;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//禁止界面弹出软键盘
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		setContentView(R.layout.time);
		tp_begin = (TimePicker) findViewById(R.id.begin_time);
		tp_end = (TimePicker) findViewById(R.id.end_time);
		Calendar cal = Calendar.getInstance();
		tp_begin.setIs24HourView(true);
		tp_begin.setCurrentHour(0);
		tp_begin.setCurrentMinute(0);
		tp_end.setIs24HourView(true);
		tp_end.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		sure = (Button) findViewById(R.id.sure);
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int b1 = tp_begin.getCurrentHour();
				int e1 = tp_end.getCurrentHour();
				int b2 = tp_begin.getCurrentMinute();
				int e2 = tp_end.getCurrentMinute();
				if (e1 < b1 || (e1 == b1 && e2 <= b2)) {
					Utils.showToast("请重新设置开始时间");
					return;
				}
				Intent intent = getIntent();
				intent.putExtra("date_b", String.format("%02d", b1) + ":" + String.format("%02d", b2) + ":00");
				intent.putExtra("date_e", String.format("%02d", e1) + ":" + String.format("%02d", e2) + ":00");
				setResult(SelectReplayDate.success, intent);
				finish();
			}
		});
		cancle = (Button) findViewById(R.id.cancle);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	public boolean compareDate(Date date1, Date date2) {
		return date1.before(date2);

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
