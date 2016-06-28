package com.ttxgps.gpslocation;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.google.gson.JsonObject;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.palmtrends.app.ShareApplication;
import com.palmtrends.loadimage.ImageFetcher;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.TalkBackBean;
import com.ttxgps.entity.User;
import com.ttxgps.msg.MsgActivity;
import com.ttxgps.msg.MsgInfoDetail;
import com.ttxgps.msg.MsgUtil;
import com.ttxgps.talkback.TalkBackDBOper;
import com.ttxgps.utils.AsyncHttpUtil;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.xtst.gps.R;

public class MapAPP extends ShareApplication {
	public static Handler handler_list;
	public static Handler handler_map;
	static MapAPP mDemoApp;
	public static String LOGPATH;
	// �ٶ�MapAPI�Ĺ�����
	public static BMapManager mBMapMan = null;
	public static ImageFetcher mImageWorker;
	// ��ȨKey
	// Old key [v2.1.2 and older]: http://developer.baidu.com/map/android-mobile-apply-key.htm
	// New key [v2.1.3 and newer]: http://lbsyun.baidu.com/apiconsole/key
	// Nestor CarLocation old key: 3CB1F74BE1517DE3B9B4597C7DAFF7E54C175D31
	// Nestor GpsLocation old key: AB676540A8C5F8FDDFDD14407C40E0B1D48741A1
	// Nestor com.ttxgps.carlocation: E7669da7218cb78ed10858ef0040fcca [New Key for v2.1.3 and later]
	// TODO: ����������Key,
	public String mStrKey = "Ga9sUMiScOUfrOIhKsn2UzcA";
	boolean m_bKeyRight = true; // ��ȨKey��ȷ����֤ͨ��
	private PushAgent mPushAgent;
	List <TalkBackBean> talkBackBeans = new ArrayList();
	private final List<Activity> activities = new ArrayList<Activity>();

	public static MapAPP getInstance() {
		return mDemoApp;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mDemoApp = this;
		Utils.h = new Handler();
		mBMapMan = new BMapManager(this);
		//mBMapMan.init(this.mStrKey, new MyGeneralListener());
		// 2.4.1 �汾��key����ͨ���˽ӿڴ��룬key��д��AndroidManifest.xm��mata-data��,
		// ���������������ָ��

		//mBMapMan.init(new MyGeneralListener());
		LOGPATH = this.getFilesDir().getAbsolutePath() + File.separator + "log";
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.setDebugMode(true);
		initUM();
		if(TextUtils.isEmpty(User.id)){
			User.GetUserData(getApplicationContext());
		}
	}

	// �����¼���������������ͨ�������������Ȩ��֤�����
	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Toast.makeText(MapAPP.mDemoApp.getApplicationContext(),
					R.string.error_network_state,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// ��ȨKey����
				Toast.makeText(MapAPP.mDemoApp.getApplicationContext(),
						"����BMapApiDemoApp.java�ļ�������ȷ����ȨKey��", Toast.LENGTH_LONG)
						.show();
				MapAPP.mDemoApp.m_bKeyRight = false;
			}
		}
	}

	@Override
	// ��������app���˳�֮ǰ����mapadpi��destroy()�����������ظ���ʼ��������ʱ������
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}

	private void initUM(){
		/**
		 * ��Handler����IntentService�б����ã���
		 * 1. ���������Activity�������Intent.FLAG_ACTIVITY_NEW_TASK
		 * 2. IntentService���onHandleIntent�����ǲ����������߳��У���ˣ��������õ����̣߳���������ʾ;
		 *        ���߿���ֱ������Service
		 * */
		UmengMessageHandler messageHandler = new UmengMessageHandler(){
			@Override
			public void dealWithCustomMessage(final Context context, final UMessage msg) {
				new Handler(getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String Type = null;
						String content;
						JSONObject conJson;
						UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
						JSONObject json;
						try {

							if(TextUtils.isEmpty(User.id)){
								User.GetUserData(getApplicationContext());
							}

							Log.e("msg.custom", msg.custom);
							json = new JSONObject(msg.custom);
							Type = json.optString("Type");
							content = json.optString("Data");
							conJson = new JSONObject(content);
							if(Type.equals("Info")){ //һ����Ϣ
								MsgInfoDetail detail = new MsgInfoDetail();
								detail.parserContent(getApplicationContext(),content);
								if(MsgActivity.instance == null){
									sendNitification(2, detail.msgContent, detail.msgContent,detail.deviceid, MsgActivity.class);
								}else{
									sendBroadcast(new Intent(Constants.ACTION_PUSH_MSG));
								}
							}else if(Type.equals("DeviceOnOff")){//�豸����
								Intent intent = new Intent(Constants.ACTION_DEVICEONOFF);
								intent.putExtra("content", content);
								sendBroadcast(intent);
								sendNitification(0, "�豸����", conJson.optString("Msg"));
							}
							else if(Type.equals("Mngchange")){//����Աת��
								if(MainFragmentActivity.instance!=null){
									Intent intent = new Intent(Constants.ACTION_MNSCHANGE);
									intent.putExtra("content", content);
									sendBroadcast(intent);
								}else{
									sendNitification(1, MapAPP.this.getString(R.string.admin_transfer), conJson.optString("Msg"), LoginActivity.class);
								}
							}
							else if(Type.equals("OffLine")){ //APP����
								User.SaveUserSharedBoolean(getBaseContext(), "IsorNoLogin", false);
								User.CleanUMData(getBaseContext());
								if(MainFragmentActivity.instance!=null){
									Intent intent = new Intent(Constants.ACTION_LOGINOUT);
									intent.putExtra("content", content);
									sendBroadcast(intent);
								}else{
									sendNitification(3, "�ʺ��ڱ𴦵�¼", conJson.optString("Msg"), LoginActivity.class);
								}
							}
							else if(Type.equals("GuardianDel")){//����໤��
								if(MainFragmentActivity.instance!=null){
									Intent intent = new Intent(Constants.ACTION_GUARDIANDEL);
									intent.putExtra("content", content);
									sendBroadcast(intent);
								}else{
									sendNitification(4, "����໤��", conJson.optString("Msg"), LoginActivity.class);
								}
							}
							else if(Type.equals("DW")){//��λ
								Intent intent = new Intent(Constants.ACTION_LOCATION);
								intent.putExtra("content", content);
								sendBroadcast(intent);
							}
							else if(Type.equals("InGeofence")||Type.equals("OutGeofence")||Type.equals("OffWatch")||Type.equals("SOS")){
								//����Χ�����ѱ�����SOS������
								MsgInfoDetail detail = new MsgInfoDetail();
								detail.parserContent(getApplicationContext(),content);
								if(MsgActivity.instance!=null){
									sendBroadcast(new Intent(Constants.ACTION_PUSH_MSG));
								}else /*if(MainFragmentActivity.instance!=null)*/{
									sendNitification(2, getString(MsgUtil.getMsgCategoryName(Integer.parseInt(detail.msgType))), detail.msgContent,detail.deviceid, MsgActivity.class);
								}

							}else if(Type.equals("Talk")){
								//����
								talkBackBeans = new ArrayList();
								TalkBackBean talkBackBean = new TalkBackBean();
								talkBackBean.parserContent(getApplicationContext(),content,1);
								talkBackBeans.add(talkBackBean);
								downTalkMedia(talkBackBean);
							}else if(Type.equals("Record")){
								//¼��
								talkBackBeans = new ArrayList();
								TalkBackBean talkBackBean = new TalkBackBean();
								talkBackBean.parserContent(getApplicationContext(),content,2);
								talkBackBeans.add(talkBackBean);
								downMedia(talkBackBean);
								/*if(RecordActivity.instance!=null){
									sendBroadcast(new Intent(Constants.ACTION_RECORD).putExtra(Constants.NAME_RECORD,(Serializable) talkBackBeans));
								}else{
									sendNitification(6, MapAPP.this.getString(R.string.voice_msg), MapAPP.this.getString(R.string.you_have_new_voice_msg), RecordActivity.class);
								}*/
							}


						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

		};
		mPushAgent.setMessageHandler(messageHandler);
	}

	private void sendNitification(int type, String title, String message, Class clazz) {
		boolean isAutoLogin = User.getIsNoLogin(getBaseContext());
		//		Log.i(TAG, "sendNitification()========>" + String.valueOf(isAutoLogin));
		if (!isAutoLogin) {
			clazz = LoginActivity.class;
		}
		Builder mBuilder = new Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(message);
		mBuilder.setTicker(message);
		//		mBuilder.setNumber(1);
		mBuilder.setAutoCancel(true);
		Intent resultIntent = new Intent(this, clazz);
		mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, resultIntent, 134217728));
		((NotificationManager) getSystemService("notification")).notify(type, mBuilder.build());
	}
	private void sendNitification(int type, String title, String message) {
		boolean isAutoLogin = User.getIsNoLogin(getBaseContext());
		//		Log.i(TAG, "sendNitification()========>" + String.valueOf(isAutoLogin));
		Builder mBuilder = new Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(message);
		mBuilder.setTicker(message);
		//		mBuilder.setNumber(1);
		mBuilder.setAutoCancel(true);
		Intent resultIntent = new Intent();
		mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, resultIntent, 134217728));
		((NotificationManager) getSystemService("notification")).notify(type, mBuilder.build());
	}

	private void sendNitification(int type, String title, String message,String deviceID, Class clazz) {
		boolean isAutoLogin = User.getIsNoLogin(getBaseContext());

		//		Log.i(TAG, "sendNitification()========>" + String.valueOf(isAutoLogin));
		if (!isAutoLogin) {
			clazz = LoginActivity.class;
		}
		Builder mBuilder = new Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(message);
		mBuilder.setTicker(message);
		//		mBuilder.setNumber(1);
		mBuilder.setAutoCancel(true);
		Intent resultIntent = new Intent(this, clazz);
		resultIntent.putExtra("deviceID", deviceID);
		mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, resultIntent, 134217728));
		((NotificationManager) getSystemService("notification")).notify(type, mBuilder.build());
	}


	private void downMedia(final TalkBackBean bean) {
		AsyncHttpUtil.get(bean.getUrl(), null, new FileAsyncHttpResponseHandler(new File(CommonUtils.getCacheVoiceFileName(bean.getUrl()))) {

			@Override
			public void onSuccess(int arg0, Header[] arg1, File file) {
				try {
					long s = CommonUtils.getAmrDuration(file);
					bean.setDuration((int)s/1000);
					TalkBackDBOper.getInstance(getBaseContext()).updateItemByTalkBackBean(bean);
					if(RecordActivity.instance!=null){
						sendBroadcast(new Intent(Constants.ACTION_RECORD).putExtra(Constants.NAME_RECORD,(Serializable) talkBackBeans));
					}else{
						sendNitification(6, MapAPP.this.getString(R.string.voice_msg), MapAPP.this.getString(R.string.you_have_new_voice_msg), RecordActivity.class);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
				if(RecordActivity.instance!=null){
					sendBroadcast(new Intent(Constants.ACTION_RECORD).putExtra(Constants.NAME_RECORD,(Serializable) talkBackBeans));
				}else{
					sendNitification(6, MapAPP.this.getString(R.string.voice_msg), MapAPP.this.getString(R.string.you_have_new_voice_msg), RecordActivity.class);
				}
			}

			@Override
			public void onFinish() {
			}

		});
	}

	private void downTalkMedia(final TalkBackBean bean) {
		AsyncHttpUtil.get(bean.getUrl(), null, new FileAsyncHttpResponseHandler(new File(CommonUtils.getCacheVoiceFileName(bean.getUrl()))) {

			@Override
			public void onSuccess(int arg0, Header[] arg1, File file) {
				try {
					long s = CommonUtils.getAmrDuration(file);
					bean.setDuration((int)s/1000);
					TalkBackDBOper.getInstance(getBaseContext()).updateItemByTalkBackBean(bean);
					if(TalkBackActivity.instance!=null){
						sendBroadcast(new Intent(Constants.ACTION_TALK_BACK).putExtra(Constants.NAME_TALK_BACK,(Serializable) talkBackBeans));
					}else{
						sendNitification(5, MapAPP.this.getString(R.string.voice_msg), MapAPP.this.getString(R.string.you_have_new_voice_msg), TalkBackActivity.class);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
				if(TalkBackActivity.instance!=null){
					sendBroadcast(new Intent(Constants.ACTION_TALK_BACK).putExtra(Constants.NAME_TALK_BACK,(Serializable) talkBackBeans));
				}else{
					sendNitification(5, MapAPP.this.getString(R.string.voice_msg), MapAPP.this.getString(R.string.you_have_new_voice_msg), TalkBackActivity.class);
				}
			}

			@Override
			public void onFinish() {
			}

		});
	}

	/**
	 * @Description ���activity
	 * @param activity
	 * @return void
	 */
	public void add(Activity activity){
		activities.add(activity);
	}
	/**
	 * @Description �Ƴ�activity
	 * @param activity
	 * @return void
	 */
	public void remove(Activity activity){
		activities.remove(activity);
	}
	/**
	 * @Description �˳�����activity
	 * @return void
	 */
	public void quitApp(){
		//����catch���쳣��Ȼ��Activity destroy timeout for HistoryRecord����
		try {
			for (Activity activity : activities) {
				if (activity != null) {
					activity.finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

}
