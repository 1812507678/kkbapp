package com.ttxgps.gpslocation;
  
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.PedometerBean;
import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.view.CircleProgressBar;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class PedometerActivity extends BaseActivity implements OnItemClickListener{
	private static final int TOTAL = 4000;
	private BaseAdapter adapter;
	private final List<PedometerBean> list = new ArrayList();
	private ListView listView;
	private CircleProgressBar progress;
	private TextView tvDate;
	private TextView tvNoData;
	private TextView tvScale;
	private TextView tvStepNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedometer);
		initTitle();
		initView();
	}

	private void initTitle() {
		((TextView) findViewById(R.id.title_tv)).setText(R.string.dis_step_num_text);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		this.tvStepNum = (TextView) findViewById(R.id.step_num_tv);
		this.listView = (ListView) findViewById(R.id.pedometer_lv);
		this.tvNoData = (TextView) findViewById(R.id.no_data_tv);
		this.listView.setEmptyView(this.tvNoData);
		this.tvScale = (TextView) findViewById(R.id.scale_tv);
		this.tvDate = (TextView) findViewById(R.id.date_tv);
		this.progress = (CircleProgressBar) findViewById(R.id.progress);
		this.progress.setMaxProgress(TOTAL);
		this.listView.setEmptyView(this.tvNoData);
		getRunInfo();

	}


	private void setAdapter() {
		updateUI(0);
		this.adapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final ViewHolder viewHolder;
				if (convertView == null) {
					convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_pedometer, null);
					viewHolder = new ViewHolder();
					viewHolder.date = (TextView)convertView.findViewById(R.id.item_pedom_date_tv);
					viewHolder.stepNum = (TextView)convertView.findViewById(R.id.item_pedom_stepNum_tv);
					viewHolder.bottom = (ImageView)convertView.findViewById(R.id.bottom);
					convertView.setTag(viewHolder);
				}else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				PedometerBean pedometerBean = list.get(position);
				viewHolder.date.setText(pedometerBean.getDate());
				viewHolder.stepNum.setText(pedometerBean.getStepNum()+getString(R.string.step));
				if (position == PedometerActivity.this.list.size() - 1) {
					viewHolder.bottom.setVisibility(View.VISIBLE);
				} else {
					viewHolder.bottom.setVisibility(View.GONE);
				}


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
	public class ViewHolder{
		public TextView date;
		public TextView stepNum;
		public ImageView bottom;
	}


	private void updateUI(int position) {
		if (this.list.size() > 0) {
			PedometerBean bean = this.list.get(position);
			if (!TextUtils.isEmpty(bean.getStepNum())) {
				int num = Integer.parseInt(bean.getStepNum());
				String scale = "0%";
				if (num < 0) {
					scale = "0%";
					num = 0;
				} else if (num <= TOTAL) {
					scale = getPercent(num, 4000.0d);
				} else if (num > TOTAL) {
					scale = "100%";
					num = TOTAL;
				}
				this.tvDate.setText(bean.getDate());
				this.tvScale.setText(scale);
				this.progress.setProgress(num);
			}
			this.tvStepNum.setText(bean.getStepNum());
			return;
		}
		this.tvStepNum.setText("0");
	}

	public String getPercent(double x, double total) {
		return new DecimalFormat("0.00%").format(x / total);
	}

	private void getRunInfo(){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID",User.curBabys.getId()));
		//		linkedlist.add(new WebServiceProperty("DateTime",""));
		//		linkedlist.add(new WebServiceProperty("Step",0));
		WebServiceTask wsk = new WebServiceTask("GetRunInfo", linkedlist, WebService.URL_OTHER,this,new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				String msg;
				if(result!=null){//错误信息
					msg=result;
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=jsonObject.optString("Msg");
						msg=str;
						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							JSONArray userInfoStr = jsonObject.getJSONArray("RunInfo");
							int count = userInfoStr.length();
							for (int i = 0; i < count; i++) {
								PedometerBean bean = new PedometerBean();
								JSONObject member = userInfoStr.getJSONObject(i);
								bean.setDate(member.optString("DateTime"));
								bean.setStepNum(member.optString("Step"));

								list.add(bean);
							}
						}
						else{
							Utils.showToast(str);
						}
						setAdapter();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				//				Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
		wsk.execute("GetRunInfoResult");
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		// TODO Auto-generated method stub
		updateUI(position);

	}



}
