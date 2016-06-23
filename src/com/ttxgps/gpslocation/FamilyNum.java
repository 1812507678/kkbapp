package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.User;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class FamilyNum extends BaseActivity{
	private Button btnConfir;
	private LinearLayout mScrollView;
	private final List<familyInfo> familyList = new ArrayList();
	private final List<familyInfo> familyListstr = new ArrayList();
	private final List<EditText> name = new ArrayList();
	private final List<EditText> phone = new ArrayList();

	private boolean isAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.family_num);
		initTitle();
		initView();
		initDate();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.family_num_set);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	private void initView(){
		isAdmin = PrefHelper.getBooleanData(PrefHelper.P_ISADMIN);
		mScrollView = (LinearLayout)findViewById(R.id.srcoll_view);
		btnConfir = (Button)findViewById(R.id.fn_confir_btn);
		if(isAdmin)
			btnConfir.setVisibility(View.VISIBLE);
		btnConfir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				createData();
			}
		});
	}

	private void initDate(){
		getNum();
	}
	private void initfamilyList(String listStr){
		String s = listStr.substring(4, listStr.length()-2);
		String []ss = s.split("&&");
		if(ss != null)
			for(int i =0; i < ss.length;i++){
				if(i%2==0){//偶数
					familyInfo family = new familyInfo();
					family.name = ss[i];
					family.phone = ss[i+1];
					familyList.add(family);
				}else{
				}
			}
	}

	private void setAdapter()
	{
		for (int i = 0; i < 4; i++) {
			View localView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_draglv_demo, null);
			EditText localEditText1 = (EditText)localView.findViewById(R.id.use_name);
			EditText localEditText2 = (EditText)localView.findViewById(R.id.phone_num);
			localEditText1.setHint("姓名");
			localEditText2.setHint("亲情号码"+ (i + 1));
			this.name.add(localEditText1);
			this.phone.add(localEditText2);
			if( i < familyList.size()){
				familyInfo family = familyList.get(i);
				localEditText1.setText(family.name);
				localEditText2.setText(family.phone);
			}
			if (!this.isAdmin)
			{
				localEditText1.setEnabled(false);
				localEditText2.setEnabled(false);
			}
			this.mScrollView.addView(localView);
		}

	}

	private void createData()
	{

		familyListstr.clear();
		String FamilyNumListStr = "FN";
		for (int i = 0; i < 4; i++) {
			familyInfo family = new familyInfo();
			String str1 = this.name.get(i).getText().toString();
			String str2 = this.phone.get(i).getText().toString();
			if (!TextUtils.isEmpty(str1) && (!TextUtils.isEmpty(str2)))
			{
				family.name = str1;
				family.phone = str2;
				familyListstr.add(family);
				FamilyNumListStr =FamilyNumListStr+"&&"+familyListstr.get(i).name+"&&"+familyListstr.get(i).phone ;
			}else if(!TextUtils.isEmpty(str1) && (TextUtils.isEmpty(str2))){
				Utils.showToast(R.string.please_input_phone_num);
				return;
			}else if(TextUtils.isEmpty(str1) && !(TextUtils.isEmpty(str2))){
				Utils.showToast(R.string.please_input_name);
				return;
			}else{
				family.name = str1;
				family.phone = str2;
				familyListstr.add(family);
				FamilyNumListStr = FamilyNumListStr+"&&&&";
			}
			//			if(i <familyListstr.size()) {
			//				FamilyNumListStr =FamilyNumListStr+"&&"+familyListstr.get(i).name+"&&"+familyListstr.get(i).phone ;
			//			}else{
			//				FamilyNumListStr = FamilyNumListStr+"&&&&";
			//			}
		}
		FamilyNumListStr = FamilyNumListStr+"##";
		setNum(FamilyNumListStr);
		Log.e("FamilyNumListStr", FamilyNumListStr);
	}

	private class familyInfo{
		String name;
		String phone;
	}

	private void getNum(){
		CommonUtils.showProgress(this, "加载中···");
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("FamilyList", ""));
		WebServiceTask wsk = new WebServiceTask("GetOrSetFamilyList", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				CommonUtils.closeProgress();
				String msg;
				if(result!=null){//错误信息
					msg=result;
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=jsonObject.optString("Msg");
						msg=str;
						if (jsonObject.has("Status") && jsonObject.getString("Status").equals("0")) {
							String FamilyNumber = jsonObject.optString("FamilyNumber");
							initfamilyList(FamilyNumber);
						}
						else{
							Utils.showToast(str);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg="数据出错！";
					}
				}
				setAdapter();
				//				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
		wsk.execute("GetOrSetFamilyListResult");
	}


	private void setNum(String str){
		CommonUtils.showProgress(this, "正在提交···");
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID",User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("FamilyList", str));
		WebServiceTask wsk = new WebServiceTask("GetOrSetFamilyList", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				CommonUtils.closeProgress();
				String msg;
				if(result!=null){//错误信息
					msg=result;
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=jsonObject.optString("Msg");
						msg=str;
						if (jsonObject.has("Status") && jsonObject.optString("Status").equals("0")) {
							Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
							finish();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
		wsk.execute("GetOrSetFamilyListResult");
	}
}
