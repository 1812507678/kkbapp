package com.ttxgps.gpslocation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.bean.GuardianBean;
import com.ttxgps.bean.GuardianInfo;
import com.ttxgps.bean.HomeGridviewBean;
import com.ttxgps.entity.Trace;
import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.MyBabyActivity.ViewHolder;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.DensityUtil;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.utils.RegularUtils;
import com.xtst.gps.R;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GuardianActivity extends BaseActivity implements OnClickListener{

	private BaseAdapter adapter;
	private View addGuarV;
	Button btnAddGuardian;
	private Button btnCancel;
	private Button btnConfir;
	Button btnFooter;
	private Button btnUnbindCancel;
	private Button btnUnbindConfir;
	private EditText edtNickName;
	private EditText edtPhoneNum;
	private View footer;
	int index;
	private boolean isAdmin = false;
	private ImageView ivPhone;
	private final List<GuardianInfo> list= new ArrayList();
	private ListView listView;
	private final int mCurPage = 1;
	private final int mPageSize = 50;
	private TextView tvMessage;
	private View unbindDialog;
	private TextView tvTitle;


	private final ArrayList<GuardianBean> mList = new ArrayList<GuardianBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guardian);
		initTitle();
		initView();
	}

	private void initTitle(){
		tvTitle = (TextView)findViewById(R.id.title_tv);
		tvTitle.setText(HomeGridviewBean.NAME_GUARDER);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		this.isAdmin =PrefHelper.getBooleanData(PrefHelper.P_ISADMIN);
		this.listView = (ListView) findViewById(R.id.guardian_lv);
		this.addGuarV = findViewById(R.id.add_item_rl);
		this.edtNickName = (EditText) findViewById(R.id.nick_name_edt);
		this.edtPhoneNum = (EditText) findViewById(R.id.phone_num_edt);
		this.btnConfir = (Button) findViewById(R.id.add_confir_btn);
		this.btnCancel = (Button) findViewById(R.id.add_cancel_btn);
		this.ivPhone = (ImageView) findViewById(R.id.get_phone_num_iv);
		this.unbindDialog = findViewById(R.id.shutdown_v);
		this.tvMessage = (TextView) findViewById(R.id.dialog_message_tv);
		this.tvMessage.setText(R.string.guardian_unbind_message);
		this.btnUnbindConfir = (Button) findViewById(R.id.confir_btn);
		this.btnUnbindCancel = (Button) findViewById(R.id.cancel_btn);
		isAdmin = PrefHelper.getBooleanData(PrefHelper.P_ISADMIN);
		if (this.isAdmin) {
			this.footer = LayoutInflater.from(this).inflate(R.layout.footer_guardian, null);
			this.btnFooter = (Button) this.footer.findViewById(R.id.footer_btn);
			this.btnAddGuardian = (Button) this.footer.findViewById(R.id.footer1_btn);
			this.btnAddGuardian.setVisibility(View.VISIBLE);
			this.btnFooter.setOnClickListener(this);
			this.btnAddGuardian.setOnClickListener(this);
			this.listView.addFooterView(this.footer);
		}

		this.btnConfir.setOnClickListener(this);
		this.btnCancel.setOnClickListener(this);
		this.addGuarV.setOnClickListener(this);
		this.ivPhone.setOnClickListener(this);
		this.btnUnbindConfir.setOnClickListener(this);
		this.btnUnbindCancel.setOnClickListener(this);

		getGuardian();
	}

	private void getGuardian(){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		WebServiceTask wsk = new WebServiceTask("GetUserList", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				String msg = null;
				if(result!=null){//错误信息
					msg=result;
					Utils.showToast(msg);
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=jsonObject.optString("Msg");
						msg=str;

						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							JSONArray GuardianArr = jsonObject.getJSONArray("GuardianList");
							parseJSON(GuardianArr);
						}
						else{
							Utils.showToast(msg);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//

					}
				}
				//				Utils.showToast(msg);

			}
		});
		wsk.execute("GetUserListResult");
	}

	private void parseJSON(JSONArray GuardianArr){
		for (int i = 0; i < GuardianArr.length(); i++) {
			GuardianInfo bean = new GuardianInfo();
			JSONObject Guardian;
			try {
				Guardian = GuardianArr.getJSONObject(i);
				bean.setHeadIconPath(Guardian.optString("URL"));
				bean.setIsAdmin(Guardian.optBoolean("IsAdmin"));
				bean.setMobileno(Guardian.optString("Mob"));
				bean.setRelation(Guardian.optString("RNick"));
				bean.setUserId(Guardian.optString("UserID"));
				list.add(bean);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		setAdapter();
	}


	private void setAdapter(){
		adapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				final ViewHolder viewHolder;
				if (convertView == null) {
					convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_guardian, null);
					viewHolder = new ViewHolder();
					viewHolder.Icon = (ImageView)convertView.findViewById(R.id.head_icon_iv);
					viewHolder.mark = (ImageView)convertView.findViewById(R.id.mark_tv);
					viewHolder.relation = (TextView)convertView.findViewById(R.id.relation_tv);
					viewHolder.account = (TextView)convertView.findViewById(R.id.account_tv);
					convertView.setTag(viewHolder);
				}else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				final GuardianInfo bean = list.get(position);

				if(!TextUtils.isEmpty(bean.getHeadIconPath())){
					CommonUtils.downloadphoto(getBaseContext(), bean.getHeadIconPath(), viewHolder.Icon);
				}

				if (bean.getIsAdmin()) {
					viewHolder.mark.setImageResource(R.drawable.icon_admin);
				} else if (GuardianActivity.this.isAdmin) {
					viewHolder.mark.setImageResource(R.drawable.icon_unbind);
				} else {
					viewHolder.mark.setImageBitmap(null);
				}
				if (String.valueOf(User.id).equals(bean.getUserId())) {
					viewHolder.relation.setText(new StringBuilder(String.valueOf(GuardianActivity.this.getString(R.string.me))).append("(").append(bean.getRelation()).append(")").toString());
				} else {
					viewHolder.relation.setText(bean.getRelation());
				}
				viewHolder.account.setText(bean.getMobileno());
				viewHolder.mark.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if(!bean.getIsAdmin()){
							GuardianActivity.this.btnUnbindConfir.setTag(bean);
							GuardianActivity.this.unbindDialog.setVisibility(View.VISIBLE);
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
		listView.setAdapter(adapter);

	}
	public class ViewHolder{
		public ImageView Icon;
		public ImageView mark;
		public TextView relation;
		public TextView account;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.confir_btn:
			GuardianInfo bean = (GuardianInfo) v.getTag();
			if (bean != null) {
				unBindGuardian(bean);
			} else {
				Utils.showToast(R.string.mbb_unbind_fail);
			}
			this.unbindDialog.setVisibility(View.GONE);

			break;
		case R.id.add_confir_btn:
			if(TextUtils.isEmpty(edtNickName.getText().toString())){
				Utils.showToast("请输入关系昵称");
				return;
			}
			if(TextUtils.isEmpty(edtPhoneNum.getText().toString())){
				Utils.showToast("请输入手机号");
				return;
			}
			if(!RegularUtils.getPhone(edtPhoneNum.getText().toString())){
				Utils.showToast("手机号码有误");
				return;
			}
			AddGuarV();
			break;
		case R.id.add_cancel_btn:
			hideAddGuarV();
			break;
		case R.id.cancel_btn:
			this.unbindDialog.setVisibility(View.GONE);
			break;
		case R.id.footer1_btn:
			this.addGuarV.setVisibility(View.VISIBLE);
			break;
		case R.id.footer_btn:
			showDialog();
			break;
		default:
		}

	}

	private void hideAddGuarV() {
		this.edtNickName.setText(null);
		this.edtPhoneNum.setText(null);
		this.addGuarV.setVisibility(View.GONE);
	}


	private void showDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_guardian, null);
		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.guardian_rg);
		for (int i = 0; i < this.list.size(); i++) {
			GuardianInfo info = this.list.get(i);
			if (!info.getIsAdmin()) {
				RadioButton radioButton = new RadioButton(this);
				radioButton.setButtonDrawable(R.drawable.login_check_btn_s);
				radioButton.setText(info.getRelation() + "(" + info.getMobileno() + ")");
				radioButton.setTextColor(Color.rgb(0, 0, 0));
				radioButton.setLayoutParams(new LayoutParams(-1, DensityUtil.dip2px(this, 40.0f)));
				radioButton.setId(i);
				radioGroup.addView(radioButton);
				if (i < this.list.size() - 1) {
					ImageView line = new ImageView(this);
					line.setImageResource(R.drawable.main_keyline);
					line.setScaleType(ScaleType.FIT_XY);
					radioGroup.addView(line);
				}
			}
		}
		final PopupWindow popup = new PopupWindow(view, -2, -2);
		view.findViewById(R.id.confir_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int index = radioGroup.getCheckedRadioButtonId();
				if(index >= 0){
					transferAdmin(list.get(index).getUserId());
				}
				popup.dismiss();
			}
		});
		view.findViewById(R.id.cancel_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popup.dismiss();
			}
		});
		popup.setBackgroundDrawable(new ColorDrawable(0));
		popup.setOutsideTouchable(true);
		popup.showAtLocation(this.tvTitle, 17, 0, 0);
	}
	/**
	 * 添加监护人
	 */
	private void AddGuarV(){
		final String phone=edtPhoneNum.getText().toString();
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("Mob",edtPhoneNum.getText().toString()));
		linkedlist.add(new WebServiceProperty("RelashionNick", edtNickName.getText().toString()));
		WebServiceTask wsk = new WebServiceTask("GuardianAdd", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				String msg = null;
				if(result!=null){//错误信息
					msg=result;
					Utils.showToast(msg);
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=jsonObject.optString("Msg");
						msg=str;

						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							hideAddGuarV();
							list.clear();
							getGuardian();
							String content = jsonObject.optString("Content");
							if (!TextUtils.isEmpty(content)){
								sendSMS(phone,content);
							}
						}


					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//

					}
				}
				Utils.showToast(msg);

			}
		});
		wsk.execute("GuardianAddResult");
	}
	/**
	 * 转移管理员
	 */
	private void transferAdmin(String id){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("ChangeUserID",id));
		linkedlist.add(new WebServiceProperty("DeviceID",User.curBabys.getId()));
		WebServiceTask wsk = new WebServiceTask("MngChange", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				String msg = null;
				if(result!=null){//错误信息
					msg=result;
					Utils.showToast(msg);
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=jsonObject.optString("Msg");
						msg=str;

						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							PrefHelper.setInfo(PrefHelper.P_ISADMIN, false);
							Utils.showToast(msg);
							finish();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//

					}
				}
				Utils.showToast(msg);

			}
		});
		wsk.execute("MngChangeResult");
	}
	/**
	 * 解绑监护人
	 */
	private void unBindGuardian(final GuardianInfo bean){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID",bean.getUserId()));
		linkedlist.add(new WebServiceProperty("GuardianRelashion",0));
		WebServiceTask wsk = new WebServiceTask("GuardianDel", linkedlist, WebService.URL_OTHER,this.getApplicationContext(),new WebServiceResult() {

			@Override
			public void webServiceResult(String result, String data) {
				// TODO Auto-generated method stub
				String msg = null;
				if(result!=null){//错误信息
					msg=result;
					Utils.showToast(msg);
				}
				else{//正确信息

					try {
						JSONObject jsonObject = new JSONObject(data);
						String str=jsonObject.optString("Msg");
						msg=str;

						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							list.remove(bean);
							adapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//

					}
				}
				Utils.showToast(msg);

			}
		});
		wsk.execute("GuardianDelResult");
	}

	protected void sendSMS(String phone,String content) {
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.setData(Uri.parse("smsto:"+phone));
		smsIntent.setType("vnd.android-dir/mms-sms");
		smsIntent.putExtra("address"  , phone);
		smsIntent.putExtra("sms_body"  , content);
		try {
			startActivity(smsIntent);

		} catch (android.content.ActivityNotFoundException ex) {

		}
	}

}
