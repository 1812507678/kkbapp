package com.ttxgps.utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.xtst.gps.R;

public class DateTimeButton extends Button {
	public Calendar time = Calendar.getInstance(Locale.CHINA);
	public static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private DatePicker datePicker;
	private TimePicker timePicker;

	private Button dataView;
	private AlertDialog dialog;

	// private Activity activity;
	public DateTimeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DateTimeButton(Context context) {
		super(context);
		init();
	}

	// ���ӹ�����
	public DateTimeButton(Context context, Button dataView) {
		super(context);
		this.dataView = dataView;
	}

	public AlertDialog dateTimePickerDialog() {
		LinearLayout dateTimeLayout = (LinearLayout) LayoutInflater.from(
				getContext()).inflate(R.layout.date_time, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.DatePicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.TimePicker);
		if (dataView == null)
			init();
		timePicker.setIs24HourView(true);

		OnTimeChangedListener timeListener = new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				time.set(Calendar.HOUR_OF_DAY, hourOfDay);
				time.set(Calendar.MINUTE, minute);

			}
		};

		timePicker.setOnTimeChangedListener(timeListener);

		OnDateChangedListener dateListener = new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub
				time.set(Calendar.YEAR, year);
				time.set(Calendar.MONTH, monthOfYear);
				time.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			}
		};

		datePicker.init(time.get(Calendar.YEAR), time.get(Calendar.MONTH),
				time.get(Calendar.DAY_OF_MONTH), dateListener);
		timePicker.setCurrentHour(time.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(time.get(Calendar.MINUTE));

		dialog = new AlertDialog.Builder(getContext())
				.setTitle("��������ʱ��")
				.setView(dateTimeLayout)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						time.set(Calendar.YEAR, datePicker.getYear());
						time.set(Calendar.MONTH, datePicker.getMonth());
						time.set(Calendar.DAY_OF_MONTH,
								datePicker.getDayOfMonth());
						time.set(Calendar.HOUR_OF_DAY,
								timePicker.getCurrentHour());
						time.set(Calendar.MINUTE, timePicker.getCurrentMinute());

						updateLabel();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();
		return dialog;
	}

	/**
	 * 
	 */
	private void init() {
		this.setGravity(Gravity.LEFT);
		this.setTextColor(Color.BLACK);
		this.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ����һ��DatePickerDialog���󣬲���ʾ����ʾ��DatePickerDialog�ؼ�����ѡ�������գ�������
				dateTimePickerDialog();
			}
		});

		// updateLabel();
	}

	/**
	 * ���±�ǩ
	 */
	public void updateLabel() {
		if (dataView != null) {
			dataView.setText(format.format(time.getTime()));
		}
		this.setText(format.format(time.getTime()));

	}

	/**
	 * @return ���ʱ���ַ���"yyyy-MM-dd HH:mm:ss"
	 */
	public String getDateString() {
		return format.format(time.getTime());
	}

	public void setDate(String datestr) {
		try {
			time.setTime(format.parse(datestr));
			updateLabel();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
