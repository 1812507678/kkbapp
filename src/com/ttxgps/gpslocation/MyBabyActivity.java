package com.ttxgps.gpslocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.entity.TeamMember;
import com.ttxgps.entity.Trace;
import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.StealthTimeSet.ViewHolder;
import com.ttxgps.gpslocation.TeamActivity.MyExpandableListAdapter;
import com.ttxgps.utils.AsyncImageLoader;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.PrefHelper;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.AsyncImageLoader.ImageCallback;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MyBabyActivity extends BaseActivity implements OnClickListener{


	public static HashMap<String, Trace> memberTraces = new HashMap<String, Trace>();
	public int curMapType = 0; // 0:baidu, 1:google

	private ListAdapter adapter;
	private final boolean isLoading = false;
	private List<BabyInfoBean> bblist =null;
	private final int mCurPage = 1;
	private final int mPageSize = 50;
	private android.widget.ListView ListView;
	private TextView tvNoData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mybaby);

		initTitle();
		initView();
		getBabylist();

	}
	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.my_baby_list);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	private void initView(){
		this.tvNoData = (TextView) findViewById(R.id.id_nodata);
		Button btnFooter = (Button) findViewById(R.id.footer_btn);
		btnFooter.setText(R.string.mbb_add);
		btnFooter.setOnClickListener(this);
	}

	private void setAdapter(){
		adapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				final ViewHolder viewHolder;
				if (convertView == null) {
					convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_mybaby, null);
					viewHolder = new ViewHolder();
					viewHolder.iconView = (ImageView)convertView.findViewById(R.id.head_icon_iv);
					viewHolder.nickName = (TextView)convertView.findViewById(R.id.relation_tv);
					viewHolder.imei = (TextView)convertView.findViewById(R.id.imei_tv);
					viewHolder.role = (TextView)convertView.findViewById(R.id.role_tv);
					viewHolder.mark = (TextView)convertView.findViewById(R.id.mark_tv);
					convertView.setTag(viewHolder);
				}else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				final BabyInfoBean bean = bblist.get(position);
				if(!TextUtils.isEmpty(bean.getHeadIconPath())){
					loadIcon(viewHolder.iconView, bean);
				}else if(bean.getSex()==1){
					viewHolder.iconView.setImageResource(R.drawable.icon_boy);
				}else{
					viewHolder.iconView.setImageResource(R.drawable.icon_girl);
				}
				viewHolder.nickName.setText(bean.getNickName());
				viewHolder.imei.setText(bean.getPhoneNum());
				if(bean.getIsAdmin()){
					if(bean.getId() == User.curBabys.getId()){
						User.SaveUserSharedBoolean(getBaseContext(), Constants.KEY_IS_ADMIN, true);
					}
					viewHolder.role.setText("(" + MyBabyActivity.this.getString(R.string.admin) + ")");
				}else{
					viewHolder.role.setText("(" + MyBabyActivity.this.getString(R.string.guardian) + ")");
				}
				if(bean.getId().equals(User.curBabys.getId())){
					viewHolder.mark.setText(R.string.mbb_cur);
					viewHolder.mark.setBackgroundResource(R.drawable.icon_cur);

				}else{
					viewHolder.mark.setText(R.string.mbb_cut);
					viewHolder.mark.setBackgroundResource(R.drawable.icon_cut);

				}
				viewHolder.mark.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0){
						// TODO Auto-generated method stub
						if(!bean.getId().equals(User.curBabys.getId())){
							User.curBabys = bean;
							User.isGetBabyDdtail = true;
							User.SaveUserShared(getBaseContext(), Constants.CUR_DEVICE_ID, bean.getId());
							if(User.curBabys.getIsAdmin()){
								User.SaveUserSharedBoolean(getBaseContext(), Constants.KEY_IS_ADMIN, true);
							}
							else{
								User.SaveUserSharedBoolean(getBaseContext(), Constants.KEY_IS_ADMIN, false);
							}
							finish();
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
				return bblist.get(arg0);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return bblist.size();
			}
		};
		ListView = (android.widget.ListView)findViewById(R.id.id_listview);
		ListView.setAdapter(adapter);
	}
	public class ViewHolder{
		public ImageView iconView;
		public TextView nickName;
		public TextView imei;
		public TextView role;
		public TextView mark;
	}
	private final AsyncImageLoader imageLoader = new AsyncImageLoader();
	private void loadIcon(final ImageView icon,final BabyInfoBean baby){
		String PhotoPath = imageLoader.getCacheImgFileName(baby.getHeadIconPath());
		if (PhotoPath == null)
		{
			Drawable cachedImage = null;
			cachedImage = imageLoader.loadDrawable(baby.getHeadIconPath(), getBaseContext(),
					true, new ImageCallback()
			{

				@Override
				public void imageLoaded(Drawable imageDrawable,
						String imageUrl)
				{
					if (imageDrawable != null)
					{
						icon.setBackgroundDrawable(imageDrawable);
					}
					else
					{
						if(baby.getSex() == 1)
							icon.setImageResource(R.drawable.icon_boy);
						else
							icon.setImageResource(R.drawable.icon_girl);
					}
				}
			});
			if (cachedImage != null)
			{
				icon.setBackgroundDrawable(cachedImage);

			}
		}
		else
		{
			icon.setImageBitmap(BitmapFactory.decodeFile(PhotoPath));
		}
	}




	private void getBabylist(){
		//		bblist = User.babyslist;
		CommonUtils.showProgress(this, "正在加载···");
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		//linkedlist.add(new WebServiceProperty("DeviceID",User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserID",User.id));

		WebServiceTask wsk = new WebServiceTask("GetDeviceList", linkedlist, WebService.URL_OTHER,this,new WebServiceResult() {

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
						if (jsonObject.has(Constants.STATUS) && jsonObject.getString(Constants.STATUS).equals("0")) {
							User.babyslist = DevicesUtils.parseJSON(data);
							bblist = User.babyslist;
							setAdapter();
						}
						else{
							Utils.showToast(jsonObject.optString(Constants.MSG));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
			}
		});
		wsk.execute("GetDeviceListResult");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(getBaseContext(), LoginBindDeviceActivity.class);
		startActivityForResult(intent, 100);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100){
			setResult(100);
			finish();
		}
	}


}
