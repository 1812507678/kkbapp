package com.ttxgps.gpslocation;
import java.util.LinkedList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.palmtrends.loadimage.Utils;
import com.xtst.gps.R;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.entity.TeamMember;
import com.ttxgps.entity.User;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;

public class InfoActivity extends BaseActivity {
	private TextView name;
	private TextView state;
	private TextView device_state;
	private TextView address_point;
	private TextView address_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		BabyInfoBean member =User.curBabys;//= (BabyInfoBean) getIntent().getSerializableExtra("item");
		name = (TextView) findViewById(R.id.diver_name);
		state = (TextView) findViewById(R.id.state);
		device_state = (TextView) findViewById(R.id.device_state);
		address_point = (TextView) findViewById(R.id.address_point);
		address_text = (TextView) findViewById(R.id.address_text);
		initTitle();
		name.setText(getString(R.string.device) + member.getNickName());
		state.setText(getString(R.string.state) + member.getStatus());
		device_state.setText(getString(R.string.device_state) + member.getStatusInf());
		address_point.setText(getString(R.string.lng_lat)
				+ member.getLng() + ", " + member.getLat());

		if (member.getAddress() == null || member.getAddress().equals("")) {
			address_text.setText(getString(R.string.address) + "\n" +
					getString(R.string.translating));
			new initAddress().execute(member.getLat(), member.getLng());
		} else {
			address_text.setText(getString(R.string.address) + "\n" + member.getAddress());
		}
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.detailed_info);
		((Button) findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	public class initAddress extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... latlng) {
			// TODO Auto-generated method stub
			try {
				WebService webservice = new WebService(
						InfoActivity.this, "GetAddressByLatlng");
				LinkedList<WebServiceProperty> linkedlist =
						new LinkedList<WebServiceProperty>();
				linkedlist.add(new WebServiceProperty("Lat", latlng[0]));
				linkedlist.add(new WebServiceProperty("Lng", latlng[1]));
				linkedlist.add(new WebServiceProperty("MapType", "Baidu"));

				String language = "zh-cn";
				if (!(LoginActivity.timeZone.equals("China Standard Time"))) {
					language = "en-us";
				}
				linkedlist.add(new WebServiceProperty("Language", language));
				webservice.SetProperty(linkedlist);
				return webservice.Get("GetAddressByLatlngResult","");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// super.onPostExecute(result);
			address_text.setText(getString(R.string.address) + "\n" + result);
		}
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
