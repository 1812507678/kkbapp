package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.Linkman;
import com.ttxgps.entity.User;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class WhiteListActivity extends BaseActivity implements OnClickListener{
	private static final int DEF = 4;
	private static final int MAX = 10;
	private final String _id = "";
	private final List<EditText> aliass = new ArrayList();
	private Button btnAdd;
	private Button btnConfir;
	private final boolean hasRepeat = false;
	private boolean isAdmin = false;
	private final List<Linkman> list = new ArrayList();
	private final List<Linkman> liststr = new ArrayList();
	private LinearLayout mScrollView;
	private TextView noDataTv;
	private final List<EditText> phones = new ArrayList();
	private final HashSet<String> set = new HashSet();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView( R.layout.family_num);
		initTitle();
		initView();
	}
	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.whitelist_set);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView() {
		this.mScrollView = (LinearLayout) findViewById(R.id.srcoll_view);
		this.noDataTv = (TextView) findViewById(R.id.no_data_tv);
		this.btnConfir = (Button) findViewById(R.id.fn_confir_btn);
		this.btnAdd = (Button) findViewById(R.id.fn_add_btn);
		this.btnConfir.setOnClickListener(this);
		this.btnAdd.setOnClickListener(this);
		this.isAdmin = PrefHelper.getBooleanData(PrefHelper.P_ISADMIN);
		if (this.isAdmin) {
			this.btnAdd.setVisibility(View.VISIBLE);
			this.btnConfir.setVisibility(View.VISIBLE);
		} else {
			this.btnAdd.setVisibility(View.GONE);
			this.btnConfir.setVisibility(View.GONE);
		}
		initData();
	}

	private void initData() {

		getNum();
	}

	private void initWhiteList(String listStr){
		String s = listStr.substring(4, listStr.length()-2);
		String []ss = s.split("&&");
		if(ss != null)
			for(int i =0; i < ss.length;i++){
				if(i%2==0){//偶数
					Linkman linkman = new Linkman(1,ss[i],ss[i+1]);
					list.add(linkman);
				}else{
				}
			}
	}

	private void setAdapter() {
		int i;
		int len = this.list.size();
		if (len >= 0 && len < DEF) {
			for (i = 0; i < 4 - len; i++) {
				Linkman linkman = new Linkman(-1, "", "");
				this.list.add(linkman);
			}
		}
		i = this.mScrollView.getChildCount();
		while (i < this.list.size() && i < MAX) {
			Linkman item = this.list.get(i);
			View itemV = LayoutInflater.from(this).inflate(R.layout.item_draglv_demo, null);
			EditText edtPhone = (EditText) itemV.findViewById(R.id.phone_num);
			EditText edtAlias = (EditText) itemV.findViewById(R.id.use_name);
			edtPhone.setHint(getString(R.string.phone) + (i + 1));
			edtAlias.setHint(R.string.name);
			this.phones.add(edtPhone);
			this.aliass.add(edtAlias);
			edtPhone.setText(item.getNumber());
			edtAlias.setText(item.getAlias());
			if (!this.isAdmin) {
				edtPhone.setEnabled(false);
				edtAlias.setEnabled(false);
			}
			this.mScrollView.addView(itemV);
			i++;
		}
		if (this.list.size() >= MAX || !this.isAdmin) {
			this.btnAdd.setVisibility(View.GONE);
		} else {
			this.btnAdd.setVisibility(View.VISIBLE);
		}
	}
	private void addItem() {
		Linkman linkman = new Linkman(-1,"","");
		this.list.add(linkman);
		setAdapter();
	}

	private void createData()
	{
		liststr.clear();
		String FamilyNumListStr = "WN";
		for (int i = 0; i < phones.size(); i++) {

			String str1 = this.aliass.get(i).getText().toString();
			String str2 = this.phones.get(i).getText().toString();
			if (!TextUtils.isEmpty(str1) && (!TextUtils.isEmpty(str2)))
			{
				Linkman family = new Linkman(1,str1,str2);
				liststr.add(family);
				FamilyNumListStr =FamilyNumListStr+"&&"+liststr.get(i).getAlias()+"&&"+liststr.get(i).getNumber() ;
			}else if(!TextUtils.isEmpty(str1) && (TextUtils.isEmpty(str2))){
				Utils.showToast(R.string.please_input_phone_num);
				return;
			}else if(TextUtils.isEmpty(str1) && !(TextUtils.isEmpty(str2))){
				Utils.showToast(R.string.please_input_name);
				return;
			}else{
				Linkman family = new Linkman(1,str1,str2);
				liststr.add(family);
				FamilyNumListStr = FamilyNumListStr+"&&&&";
			}
		}
		FamilyNumListStr = FamilyNumListStr+"##";
		setNum(FamilyNumListStr);
		Log.e("FamilyNumListStr", FamilyNumListStr);
	}

	private void setNum(String str){
		CommonUtils.showProgress(this, "正在提交···");
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("WhiteList", str));
		WebServiceTask wsk = new WebServiceTask("GetOrSetWhiteList", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
						String str=(String) jsonObject.get("Msg");
						msg=str;
						if (jsonObject.has("Status") && jsonObject.getString("Status").equals("0")) {
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
		wsk.execute("GetOrSetWhiteListResult");
	}
	private void getNum(){
		CommonUtils.showProgress(this, "加载中···");
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("WhiteList", ""));
		WebServiceTask wsk = new WebServiceTask("GetOrSetWhiteList", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

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
							String WhiteList = jsonObject.optString("WhiteList");
							initWhiteList(WhiteList);
						}
						else{
							Utils.showToast(str);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg="数据出错";
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
					}
				}
				setAdapter();
			}
		});
		wsk.execute("GetOrSetWhiteListResult");
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!this.isAdmin) {
			//			ToastUtils.toast(this, getString(R.string.unAdmin_cant_change));
		} else if (v.getId() == R.id.fn_confir_btn) {
			createData();
		} else if (v.getId() == R.id.fn_add_btn) {
			addItem();
		}

	}



}
