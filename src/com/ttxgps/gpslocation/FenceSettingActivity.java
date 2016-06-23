package com.ttxgps.gpslocation;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.core.PoiInfo;
//import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
//import com.baidu.mapapi.search.geocode.GeoCodeResult;
//import com.baidu.mapapi.search.geocode.GeoCoder;
//import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
//import com.baidu.mapapi.search.poi.PoiResult;















import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.FenceBaseInfo;
import com.ttxgps.entity.User;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class FenceSettingActivity extends BaseActivity

{
	public static final int FLAG_ADD_FENCE = 1;
	public static final int FLAG_DEL_FENCE = 2;
	EditText addr_tv;
	Button btnSave;
	String city;
	Context context;
	Button del_btn;
	CheckBox drive_in;
	CheckBox drive_out;
	int flag;
	FenceBaseInfo info;
	TextView lat_tv;
	TextView lon_tv;
	EditText name_tv;
	EditText r_tv;
	//GeoCoder search;
	String action;
	int index;
	@Override
	protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
	{

	}

	@Override
	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.fence_dialog_layout);
		index = this.getIntent().getIntExtra("FenceListInedx", -1);
		info = (FenceBaseInfo) this.getIntent().getSerializableExtra("fence");
		action = this.getIntent().getStringExtra("action");
		if(action=="edit"){
			//			del_btn = (Button) findViewById(R.id.del_fence_btn);
			//			del_btn.setVisibility(View.VISIBLE);
			//			del_btn.setOnClickListener(new OnClickListener() {
			//
			//				@Override
			//				public void onClick(View v) {
			//					// TODO Auto-generated method stub
			//					Intent intent =new Intent();
			//					intent.putExtra("FenceListInedx", index);
			//					intent.putExtra("action", "del");
			//					setResult(Activity.RESULT_OK, intent);
			//					finish();
			//				}
			//			});

		}

		Button backBtn=(Button) findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				saveAndBack();
			}
		});


		this.name_tv = ((EditText)findViewById(R.id.name_tv));
		this.r_tv = ((EditText)findViewById(R.id.r_tv));
		this.drive_in = ((CheckBox)findViewById(R.id.drive_in_cbx));
		this.drive_out = ((CheckBox)findViewById(R.id.drive_out_cbx));
		this.lon_tv = ((TextView)findViewById(R.id.lon_tv));
		this.lat_tv = ((TextView)findViewById(R.id.lat_tv));
		this.addr_tv = ((EditText)findViewById(R.id.addr_tv));
		this.name_tv.setText(this.info.fenceName);
		this.r_tv.setText(String.valueOf(this.info.radius));
		this.lon_tv.setText(String.valueOf(this.info.longitude));
		this.lat_tv.setText(String.valueOf(this.info.latitude));

		if(info.fenceType==0){
			drive_in.setChecked(true);
			drive_out.setChecked(true);
		}
		else
			if(info.fenceType==1){
				drive_in.setChecked(true);
				drive_out.setChecked(false);
			}
			else
				if(info.fenceType==2) {
					drive_in.setChecked(false);
					drive_out.setChecked(true);
				}
				else{
					drive_in.setChecked(false);
					drive_out.setChecked(false);
				}





		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Lat",String.valueOf(info.latitude)));
		linkedlist.add(new WebServiceProperty("Lng",String.valueOf(info.longitude)));
		linkedlist.add(new WebServiceProperty("MapType", "Baidu"));
		String language = "zh-cn";
		if (! LoginActivity.timeZone.equals("China Standard Time")) {
			language = "en-us";
		}
		linkedlist.add(new WebServiceProperty("Language", language));

		WebServiceTask wtask = new WebServiceTask("GetAddressByLatlng", linkedlist, WebService.URL_OPEN, this,new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				if(result==null){
					String[] str = data.split("\\.");
					if (str.length == 0)
						str = data.split(",");
					if (str.length == 0)
						str = data.split("°£");
					if (str.length == 0)
						str = data.split("£¨");
					if (str.length != 0)
						data = str[0];
					addr_tv.setText(data);
				}
				else{
					addr_tv.setText("µÿ÷∑ªÒ»° ß∞‹£°");
					Utils.showToast(result);
				}
			}
		});
		wtask.execute("GetAddressByLatlngResult");



	}


	void saveAndBack(){
		info.fenceName = name_tv.getText().toString();
		info.radius = Integer.parseInt(r_tv.getText().toString());
		info.remark = addr_tv.getText().toString();

		if(drive_in.isChecked() && drive_out.isChecked()){
			info.fenceType=0;
		}
		else
			if(drive_in.isChecked() ){
				info.fenceType=1;
			}
			else
				if(drive_out.isChecked() )
				{
					info.fenceType=2;
				}
				else{
					info.fenceType=3;
				}

		Intent intent =new Intent();
		intent.putExtra("FenceListInedx", index);
		intent.putExtra("action", "save");
		intent.putExtra("fence", info);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}




	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		saveAndBack();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//		this.search.destroy();
	}



	@Override
	protected void onPause()
	{
		super.onPause();

	}

	public void things(View view){
		switch(view.getId()){
		case R.id.back_btn:
			finish();
			break;
		}
	}
}

