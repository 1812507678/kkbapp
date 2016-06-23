package com.ttxgps.gpslocation;

import com.ttxgps.entity.User;
import com.ttxgps.utils.AsyncImageLoader;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.AsyncImageLoader.ImageCallback;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xtst.gps.R;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PersonalCenterFragment extends Fragment {
	private final AsyncImageLoader imageLoader = new AsyncImageLoader();
	private ImageView iv_personcenter_iocn;
	private TextView tv_personcenter_nickname;
	private TextView tv_personcenter_sign;
	private RelativeLayout rl_personcenter_persondata;
	private View inflate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		inflate = inflater.inflate(R.layout.fragment_person_center, null);


		initView();
		initIcon();
		initViewData();

		return inflate;
	}



	private void initView() {
		rl_personcenter_persondata = (RelativeLayout) inflate.findViewById(R.id.rl_personcenter_persondata);
		iv_personcenter_iocn = (ImageView) inflate.findViewById(R.id.iv_personcenter_iocn);
		tv_personcenter_nickname = (TextView) inflate.findViewById(R.id.tv_personcenter_nickname);
		tv_personcenter_sign = (TextView) inflate.findViewById(R.id.tv_personcenter_sign);

		PersonDataOnclickListener onclickListener = new PersonDataOnclickListener();
		rl_personcenter_persondata.setOnClickListener(onclickListener);

		inflate.findViewById(R.id.device_ll).setOnClickListener(onclickListener);
		inflate.findViewById(R.id.pwd_ll).setOnClickListener(onclickListener);
		inflate.findViewById(R.id.up_ll).setOnClickListener(onclickListener);
		inflate.findViewById(R.id.feedback_ll).setOnClickListener(onclickListener);
		inflate.findViewById(R.id.exit_ll).setOnClickListener(onclickListener);
	}

	private void initViewData() {
		tv_personcenter_nickname.setText(User.niceName);
		tv_personcenter_sign.setText(User.Signature);
	}

	private void initIcon(){
		if(!User.headerurl.isEmpty()){
			String PhotoPath = imageLoader.getCacheImgFileName(User.headerurl);
			if (PhotoPath == null)
			{
				Drawable cachedImage = null;
				cachedImage = imageLoader.loadDrawable(User.headerurl, getActivity().getBaseContext(),
						true, new ImageCallback()
				{

					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl)
					{
						if (imageDrawable != null)
						{
							iv_personcenter_iocn.setImageDrawable(imageDrawable);
						}
						else
						{
						}
					}
				});
				if (cachedImage != null)
				{
					iv_personcenter_iocn.setImageDrawable(cachedImage);

				}
			}
			else
			{
				iv_personcenter_iocn.setImageBitmap(BitmapFactory.decodeFile(PhotoPath));
			}
		}else{
			iv_personcenter_iocn.setImageResource(R.drawable.home_icon_head);
		}
	}

	class PersonDataOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_personcenter_persondata:
				startActivity(new Intent(getActivity(),AccountActivity.class));
				break;
			case R.id.device_ll:
				startActivityForResult(new Intent(getActivity(), MyBabyActivity.class),100);
				break;
			case R.id.pwd_ll:
				startActivityForResult(new Intent(getActivity(), ModifyPassword.class),111);
				break;
			case R.id.up_ll:
				gotoCheckVersion();
				break;
			case R.id.feedback_ll:
				Intent system = new Intent(getActivity(), ActivityInfoCentre.class);
				system.putExtra("web_url","http://112.74.130.65:8001/feedback.aspx?UserID="+User.id+"&DeviceID="+User.curBabys.getId());//
				system.putExtra("web_title", "意见反馈");
				startActivity(system);
				break;
			case R.id.exit_ll:
				User.CleanUMData(getActivity());
				User.isLogin = false;
				User.SaveUserSharedBoolean(getActivity(), "IsorNoLogin", false);
				startActivity(new Intent(getActivity(), LoginActivity.class));
				getActivity().finish();
				break;
			default:
				break;
			}

		}

	}

	private void gotoCheckVersion(){
		try
		{
			CommonUtils.showProgress(getActivity(), "正在检查版本···");
			UmengUpdateAgent.setUpdateOnlyWifi(false); // 目前我们默认在Wi-Fi接入情况下才进行自动提醒。如需要在其他网络环境下进行更新自动提醒，则请添加该行代码
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener()
			{
				@Override
				public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo)
				{
					//连续点击多次检测新版本后，退出[关于我们]界面，会导致空指针。因这个回调是异步。
					try
					{
						CommonUtils.closeProgress();
						switch (updateStatus)
						{
						case UpdateStatus.Yes: // has update
							UmengUpdateAgent.showUpdateDialog(getActivity(), updateInfo);
							break;
						case UpdateStatus.No: // has no update
							//  newView.setVisibility(View.GONE);
							Toast.makeText(getActivity(),
									getActivity().getString(R.string.UMAbout_New_Version),
									Toast.LENGTH_SHORT).show();
							break;
						case UpdateStatus.NoneWifi: // none wifi
							//newView.setVisibility(View.GONE);
							Toast.makeText(getActivity(),
									getActivity().getString(R.string.UMAbout_Wifi),
									Toast.LENGTH_SHORT).show();
							break;
						case UpdateStatus.Timeout: // time out
							//  newView.setVisibility(View.GONE);
							Toast.makeText(
									getActivity(),
									getActivity()
									.getString(R.string.UMAbout_Network_Timeout),
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
					catch (NullPointerException ex)
					{
						//
					}
				}
			});
			//          UmengUpdateAgent.setDefault();
			UmengUpdateAgent.update(getActivity());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			CommonUtils.closeProgress();
		}
	}

}
