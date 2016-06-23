package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.StealthTimeBean;
import com.ttxgps.entity.User;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class UpdateStealthTime extends BaseActivity implements OnClickListener{

	private static final int WEEK_SET = 3;
	private final List<TextView> beginTimes = new ArrayList();
	private Button btnAddItem;
	private Button btnConfir;
	private final List<TextView> endTimes = new ArrayList();
	private  List<StealthTimeBean> list = new ArrayList();
	private LinearLayout mScrollView;
	private final int result_flag = 0;
	private TextView tv_set;
	private String[] weekOfDays;
	private final  List<TextView> weeks = new ArrayList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//禁止界面弹出软键盘
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_stealth_time);
		initTitle();
		initView();
		initDate();
		setAdapter();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.edit);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	private void initView(){
		this.mScrollView = ((LinearLayout)findViewById(R.id.linear_layout));
		this.btnAddItem = ((Button)findViewById(R.id.add_item_btn));
		this.btnConfir = ((Button)findViewById(R.id.confir_btn));
		this.weekOfDays = getResources().getStringArray(R.array.week_array);
		list = (List)getIntent().getSerializableExtra("StealthTimeBeanList");
		btnAddItem.setOnClickListener(this);
		btnConfir.setOnClickListener(this);
	}
	private void initDate(){

	}

	private void setAdapter(){
		if(list.size()>0)
			for (int i = 0; i < list.size(); i++) {
				final StealthTimeBean Bean = this.list.get(i);
				String weekName = getWeekName(Bean.getWeeks());
				final View view = LayoutInflater.from(this).inflate(R.layout.item_update_stealth, null);
				final TextView edtBeginTime = (TextView) view.findViewById(R.id.begin_time_edt);
				final TextView edtEndTime = (TextView) view.findViewById(R.id.end_time_edt);
				final TextView tvWeek = (TextView) view.findViewById(R.id.fortify_date_set_result);
				edtBeginTime.setTag(Bean);
				edtEndTime.setTag(Bean);
				tvWeek.setTag(Bean);

				this.beginTimes.add(edtBeginTime);
				this.endTimes.add(edtEndTime);
				this.weeks.add(tvWeek);
				tvWeek.setText(weekName);

				ImageView del_btn = (ImageView)view.findViewById(R.id.remove);
				del_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						//						if (TextUtils.isEmpty(Bean.getId())) {
						UpdateStealthTime.this.beginTimes.remove(edtBeginTime);
						UpdateStealthTime.this.endTimes.remove(edtEndTime);
						UpdateStealthTime.this.weeks.remove(tvWeek);
						UpdateStealthTime.this.list.remove(Bean);
						UpdateStealthTime.this.mScrollView.removeView(view);
						UpdateStealthTime.this.mScrollView.invalidate();
						//							return;
						//						}else{
						//
						//						}

					}
				});
				view.findViewById(R.id.fortify_layout_date).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(UpdateStealthTime.this, DateSettingActivity.class);
						UpdateStealthTime.this.tv_set = tvWeek;
						intent.addFlags(0);
						intent.putExtra("isVisible", false);
						intent.putExtra("DataOfDate", tvWeek.getText().toString());
						UpdateStealthTime.this.startActivityForResult(intent, UpdateStealthTime.WEEK_SET);

					}
				});
				edtBeginTime.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						UpdateStealthTime.this.tv_set = edtBeginTime;
						UpdateStealthTime.this.showStartDialog();
					}
				});
				edtEndTime.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						UpdateStealthTime.this.tv_set = edtEndTime;
						UpdateStealthTime.this.showEndDialog();
					}
				});

				edtBeginTime.setText(Bean.getBegintime());
				edtEndTime.setText(Bean.getEndtime());
				this.mScrollView.addView(view);


			}
	}

	private void AddNewStealth() {
		if (this.list.size() > 0) {
			for (int i = this.list.size() - 1; i < this.list.size(); i++) {
				StealthTimeBean item = this.list.get(i);
				item.setBegintime(this.beginTimes.get(i).getText().toString());
				item.setEndtime(this.endTimes.get(i).getText().toString());
				item.setWeeks(getWeekCode(this.weeks.get(i).getText().toString()));
			}
		}
		StealthTimeBean bean = new StealthTimeBean();
		bean.setId("");
		bean.setOpenflag("1");
		bean.setWeeks("0011111");
		bean.setSource("1");
		bean.setBegintime("");
		bean.setEndtime("");
		this.list.add(bean);
		this.beginTimes.clear();
		this.endTimes.clear();
		this.weeks.clear();
		this.mScrollView.removeAllViews();
		setAdapter();
	}

	private void updateStealth() {
		for (int i = 0; i < this.list.size(); i++) {
			StealthTimeBean item = this.list.get(i);
			String beginTime = this.beginTimes.get(i).getText().toString();
			String endTime = this.endTimes.get(i).getText().toString();
			String week = this.weeks.get(i).getText().toString();
			if (!TextUtils.isEmpty(beginTime) && !TextUtils.isEmpty(endTime)) {
				item.setBegintime(beginTime);
				item.setEndtime(endTime);
				item.setWeeks(getWeekCode(week));
			} else if (TextUtils.isEmpty(beginTime)) {
				Utils.showToast(R.string.ust_input_start_time);
				return;
			} else if (TextUtils.isEmpty(endTime)) {
				Utils.showToast(R.string.ust_input_end_time);
				return;
			}
		}
		String str=DevicesUtils.saveStealthTimeToStr(list);
		Log.e("saveStealthTimeToStr", str);
		if(!str.equals(User.curBabys.StealthTimeStr)){
			//dialogShow("正在下发指令...");
			//startSaveStealthTimeList(this,str);


			Intent intent = new Intent();
			intent.putExtra("listStr", str);
			setResult(Activity.RESULT_OK,intent);
			finish();
		}else{
			setResult(Activity.RESULT_CANCELED);
			finish();
		}

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.confir_btn:
			updateStealth();
			break;
		case R.id.back_btn:
			setResult(Activity.RESULT_CANCELED);

			finish();
			break;
		case R.id.add_item_btn:
			AddNewStealth();
			break;
		}

	}

	public String getWeekCode(String paramString)
	{
		StringBuilder sb = new StringBuilder();
		if (getString(R.string.everyday).equals(paramString)) {
			return "0000000";
		}

		if (!paramString.contains(this.weekOfDays[6])) {
			sb.append("1");
		} else {
			sb.append("0");
		}
		if (!paramString.contains(this.weekOfDays[5])) {
			sb.append("1");
		} else {
			sb.append("0");
		}
		if (!paramString.contains(this.weekOfDays[4])) {
			sb.append("1");
		} else {
			sb.append("0");
		}
		if (!paramString.contains(this.weekOfDays[3])) {
			sb.append("1");
		} else {
			sb.append("0");
		}
		if (!paramString.contains(this.weekOfDays[2])) {
			sb.append("1");
		} else {
			sb.append("0");
		}
		if (!paramString.contains(this.weekOfDays[1])) {
			sb.append("1");
		} else {
			sb.append("0");
		}

		if (!paramString.contains(this.weekOfDays[0])) {
			sb.append("1");
		} else {
			sb.append("0");
		}
		return sb.toString();

	}

	public String getWeekName(String paramString)
	{
		if ("0000000".equals(paramString))
			return getString(R.string.everyday);
		if ("1100000".equals(paramString))
			return this.weekOfDays[0] + "," + this.weekOfDays[1] + "," + this.weekOfDays[2] + "," + this.weekOfDays[3] + "," + this.weekOfDays[4];
		if ("0011111".equals(paramString))
			return this.weekOfDays[5] + "," + this.weekOfDays[6];
		if (("1111111".equals(paramString)) || ("".equals(paramString)) || (paramString == null))
			return getString(R.string.never);
		StringBuilder localStringBuilder = new StringBuilder();
		if (paramString.charAt(0) == '0')
		{
			localStringBuilder.append(this.weekOfDays[6]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(1) == '0')
		{
			localStringBuilder.append(this.weekOfDays[0]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(2) == '0')
		{
			localStringBuilder.append(this.weekOfDays[1]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(3) == '0')
		{
			localStringBuilder.append(this.weekOfDays[2]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(4) == '0')
		{
			localStringBuilder.append(this.weekOfDays[3]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(5) == '0')
		{
			localStringBuilder.append(this.weekOfDays[4]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(6) == '0')
		{
			localStringBuilder.append(this.weekOfDays[5]);
			localStringBuilder.append(",");
		}

		return localStringBuilder.substring(0, localStringBuilder.length() - 1);
	}

	private void showStartDialog() {
		int hour;
		int min;
		String str=tv_set.getText().toString();
		if(str!=null&&!str.isEmpty()){
			String[] ss =str.split("\\:");
			hour=Integer.parseInt(ss[0]);
			min=Integer.parseInt(ss[1]);
		}
		else{
			hour=7;
			min=0;
		}
		TimePickerDialog timePickerDialog = new TimePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int mHour, int mMinute) {
				// TODO Auto-generated method stub
				String str=new StringBuilder()
				.append(mHour < 10 ? "0" + mHour : mHour).append(":")
				.append(mMinute < 10 ?  "0"+mMinute : mMinute).toString();
				StealthTimeBean bean = (StealthTimeBean) tv_set.getTag();
				bean.setBegintime(str);
				UpdateStealthTime.this.tv_set.setText(str);
			}
		}, hour, min, true);
		timePickerDialog.show();
	}
	private void showEndDialog(){
		int hour;
		int min;
		String str=tv_set.getText().toString();
		if(str!=null&&!str.isEmpty()){
			String[] ss =str.split("\\:");
			hour=Integer.parseInt(ss[0]);
			min=Integer.parseInt(ss[1]);
		}
		else{
			hour=7;
			min=0;
		}
		TimePickerDialog timePickerDialog = new TimePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int mHour, int mMinute) {
				// TODO Auto-generated method stub
				String str=new StringBuilder()
				.append(mHour < 10 ? "0" + mHour : mHour).append(":")
				.append(mMinute < 10 ? "0"+mMinute : mMinute).toString();
				StealthTimeBean bean = (StealthTimeBean) tv_set.getTag();
				bean.setEndtime(str);
				UpdateStealthTime.this.tv_set.setText(str);
			}
		}, hour, min, true);
		timePickerDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 222 && data != null) {
			String str=data.getStringExtra("choiceData");
			this.tv_set.setText(str);
			StealthTimeBean bean = (StealthTimeBean) tv_set.getTag();
			bean.setWeeks(getWeekCode(str));
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	/*	AlertDialog.Builder builder;
	AlertDialog alerDialog;
	protected void dialogShow(String msg) {
		if(builder==null){
			builder = new Builder(this);
			alerDialog=builder.create();
		}
		alerDialog.setCancelable(false);
		alerDialog.setMessage(msg);
		alerDialog.setTitle("提示");


		alerDialog.show();
	}*/

}
