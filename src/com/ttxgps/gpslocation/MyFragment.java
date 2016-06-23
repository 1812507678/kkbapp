package com.ttxgps.gpslocation;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.User;
import com.ttxgps.pay.PayActivity;
import com.ttxgps.utils.CommonUtils;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xtst.gps.R;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragment extends Fragment implements OnClickListener{


	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_discover, null);
		initView();
		return view;
	}


	private void initView(){
		view.findViewById(R.id.book_ll).setOnClickListener(this);
		view.findViewById(R.id.education_ll).setOnClickListener(this);
		view.findViewById(R.id.about_ll).setOnClickListener(this);
		view.findViewById(R.id.Medical_ll).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.feedback_ll:
			Intent system = new Intent(getActivity(), ActivityInfoCentre.class);
			system.putExtra("web_url","http://112.74.130.65:8001/feedback.aspx?UserID="+User.id+"&DeviceID="+User.curBabys.getId());//
			system.putExtra("web_title", "�������");
			startActivity(system);
			break;
		case R.id.Medical_ll:
			Intent Medical = new Intent(getActivity(), ActivityInfoCentre.class);
			Medical.putExtra("web_url","http://info.kokobao.com/fun/andy.aspx?fid=jiankang");
			Medical.putExtra("web_title", "����ҽ��");
			startActivity(Medical);
			break;
		case R.id.book_ll:
			Intent book = new Intent(getActivity(), ActivityInfoCentre.class);
			book.putExtra("web_url","http://info.kokobao.com/fun/andy.aspx?fid=yuedu");//
			book.putExtra("web_title", "�Ķ�");
			startActivity(book);
			break;
		case R.id.education_ll:
			Intent education = new Intent(getActivity(), ActivityInfoCentre.class);
			education.putExtra("web_url","http://info.kokobao.com/fun/andy.aspx?fid=jiaoyu");//
			education.putExtra("web_title", "����");
			startActivity(education);
			break;
		case R.id.about_ll:
			String versionName = "1.0.1.20160105";
			try {
				versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent aboutint = new Intent(getActivity(), ActivityInfoCentre.class);
			aboutint.putExtra("web_url","http://api.kokobao.com:8001/AboutUs.aspx?channel=1&version="+versionName+"&platform=android&uid="+User.id);//
			aboutint.putExtra("web_title", "����");
			startActivity(aboutint);
			break;

		default:
			break;
		}

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 111){
			Utils.showToast("�����µ�¼");
			startActivity(new Intent(getActivity(), LoginActivity.class));
			getActivity().finish();
		}else if(resultCode == 100){
			getActivity().finish();
		}
	}

	private void gotoCheckVersion()
	{
		try
		{
			CommonUtils.showProgress(getActivity(), "���ڼ��汾������");
			UmengUpdateAgent.setUpdateOnlyWifi(false); // Ŀǰ����Ĭ����Wi-Fi��������²Ž����Զ����ѡ�����Ҫ���������绷���½��и����Զ����ѣ�������Ӹ��д���
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener()
			{
				@Override
				public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo)
				{
					//���������μ���°汾���˳�[��������]���棬�ᵼ�¿�ָ�롣������ص����첽��
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
