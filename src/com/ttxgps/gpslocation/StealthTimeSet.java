package com.ttxgps.gpslocation;
  
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.bean.DataSyncDto;
import com.ttxgps.bean.ResultVo;
import com.ttxgps.bean.StealthTimeBean;
import com.ttxgps.entity.User;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class StealthTimeSet extends BaseActivity implements OnItemClickListener{
	private BaseAdapter adapter;
	private boolean isAdmin = true;
	private final HashMap<Integer, Boolean> isSelected = new HashMap();
	private List<StealthTimeBean> list ;
	private ListView listView;
	private String[] weekOfDays;
	private Button btnFooter,backBtn;
	private BabyInfoBean babyInfoBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stealth_time_set);
		babyInfoBean=User.curBabys;
		list=DevicesUtils.strToBeanparseList(babyInfoBean.StealthTimeStr);
		initTitle();
		initView();
		setAdapter();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.stealth_set);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String str=DevicesUtils.saveStealthTimeToStr(list);
				if(!str.equals(User.curBabys.StealthTimeStr)){
					CommonUtils.showProgress(StealthTimeSet.this, "正在提交...",null);
					User.curBabys.StealthTimeStr =str;
					startSaveStealthTimeList(getBaseContext(),str);
				}
				else{
					finish();
				}


			}
		});
	}

	private void initView(){
		View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.footer_guardian, null);
		listView = (ListView)findViewById(R.id.stealth_lv);
		btnFooter = (Button)view.findViewById(R.id.footer_btn);
		btnFooter.setText("编辑");
		btnFooter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toUpdateStealth();
			}
		});
		listView.addFooterView(view);
		weekOfDays = getResources().getStringArray(R.array.week_array);
		isAdmin = PrefHelper.getBooleanData(PrefHelper.P_ISADMIN);
		if(!isAdmin)
			btnFooter.setVisibility(View.GONE);

		this.listView.setOnItemClickListener(this);
	}


	private void setAdapter()
	{
		adapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final ViewHolder viewHolder;
				// TODO Auto-generated method stub
				if (convertView == null) {
					convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_stealth_time, null);
					viewHolder = new ViewHolder();
					viewHolder.Begintime = (TextView)convertView.findViewById(R.id.beginTime_tv);
					viewHolder.Endtime = (TextView)convertView.findViewById(R.id.endTime_tv);
					viewHolder.WeekName = (TextView)convertView.findViewById(R.id.week_tv);
					viewHolder.chk = (CheckBox)convertView.findViewById(R.id.checkbox);
					convertView.setTag(viewHolder);
				}else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				final StealthTimeBean bean = list.get(position);
				viewHolder.Begintime.setText(bean.getBegintime());
				viewHolder.Endtime.setText(bean.getEndtime());
				viewHolder.WeekName.setText(getWeekName(bean.getWeeks()));
				viewHolder.chk.setChecked(bean.getOpenflag().equals("0")?true:false);
				viewHolder.chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						if(isAdmin){

							bean.setOpenflag(arg1?"0":"1");
						}
					}
				});
				return convertView;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return list.get(arg0);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}
		};
		this.listView.setAdapter(this.adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.chk.toggle();
		boolean openFlag = holder.chk.isChecked();
		if (openFlag) {
			this.list.get(position).setOpenflag("0");
		} else {
			this.list.get(position).setOpenflag("1");
		}
	}


	private void parseResult(JSONObject arg0) {
		ResultVo<DataSyncDto<StealthTimeBean>> resultVo = new ResultVo<DataSyncDto<StealthTimeBean>>();
		Gson gson = new Gson();
		resultVo = gson.fromJson(arg0.toString(), new TypeToken<ResultVo<DataSyncDto<StealthTimeBean>>>() {
		}.getType());

		if (resultVo == null || resultVo.getData() == null) {
			return;
		}

		DataSyncDto<StealthTimeBean> syncDto = resultVo.getData();

		list = syncDto.getResult();
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
			localStringBuilder.append(this.weekOfDays[5]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(2) == '0')
		{
			localStringBuilder.append(this.weekOfDays[4]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(3) == '0')
		{
			localStringBuilder.append(this.weekOfDays[3]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(4) == '0')
		{
			localStringBuilder.append(this.weekOfDays[2]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(5) == '0')
		{
			localStringBuilder.append(this.weekOfDays[1]);
			localStringBuilder.append(",");
		}
		if (paramString.charAt(6) == '0')
		{
			localStringBuilder.append(this.weekOfDays[0]);
			localStringBuilder.append(",");
		}

		return localStringBuilder.substring(0, localStringBuilder.length() - 1);
	}

	public class ViewHolder{
		public TextView Begintime;
		public TextView Endtime;
		public TextView WeekName;
		public CheckBox chk;
	}

	private void toUpdateStealth()
	{
		Intent localIntent = new Intent(this, UpdateStealthTime.class);
		localIntent.putExtra("StealthTimeBeanList", (Serializable)this.list);
		startActivityForResult(localIntent, 1001);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==1001&&resultCode==Activity.RESULT_OK){
			String str=data.getStringExtra("listStr");
			list=DevicesUtils.strToBeanparseList(str);
			adapter.notifyDataSetChanged();

		}
		super.onActivityResult(requestCode, resultCode, data);
	}






	void startSaveStealthTimeList(final Context context,final String sdata){

		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("StealthTime",sdata));
		WebServiceTask timeWeb = new WebServiceTask("GetOrSetStealthTime", linkedlist, WebService.URL_OTHER, context, new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				if(StealthTimeSet.this.isFinishing())
					return;
				CommonUtils.closeProgress();
				if(result!=null){//错误信息

					Toast.makeText(context, result,Toast.LENGTH_SHORT).show();
				}
				else{//正确信息
					try {
						JSONObject json = new JSONObject(data);
						if (json.has("Msg"))
							Toast.makeText(context, json.getString("Msg"),Toast.LENGTH_SHORT).show();

						if (json.has("Status") && (json.getInt("Status") == 0)) {


						}
						finish();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		timeWeb.execute("GetOrSetStealthTimeResult");
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			String str=DevicesUtils.saveStealthTimeToStr(list);
			if(!str.equals(User.curBabys.StealthTimeStr)){
				CommonUtils.showProgress(StealthTimeSet.this, "正在提交...",null);
				User.curBabys.StealthTimeStr =str;
				startSaveStealthTimeList(getBaseContext(),str);
			}
			else{
				finish();
			}
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
