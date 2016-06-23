package com.ttxgps.gpslocation;
  
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.adapter.RecordAdapter;
import com.ttxgps.adapter.RecordAdapter.MyMediaPlayerListener;
import com.ttxgps.bean.TalkBackBean;
import com.ttxgps.entity.User;
import com.ttxgps.record.VoiceRecorder;
import com.ttxgps.talkback.TalkBackDBOper;
import com.ttxgps.utils.AsyncHttpUtil;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.HttpUploadfile;
import com.ttxgps.utils.Urls;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class RecordActivity extends BaseActivity implements MyMediaPlayerListener{
	private static final String TAG = "RecordActivity";
	public static RecordActivity instance;
	private static final int WHAT_BATCH_ADD = 2;
	private static final int WHAT_BATCH_DELE = 4;
	public static final int WHAT_SINGLE_ADD = 1;
	private static final int WHAT_SINGLE_DELE = 3;
	private boolean _flag;
	private RecordAdapter adapter;
	private Button btnTalk;
	private final int curPage = 1;
	private final long currentTime = 0L;
	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//	private View headerV;
	private Handler handler;
	private Handler micImageHandler;
	private ListView listView;
	private final List<TalkBackBean> mList = new ArrayList();
	private MediaPlayer mPlayer;
	private View mRecordAnimationV;
	private String media_path;
	private ImageView micImage;
	private Drawable[] micImages;
	private TextView recordingHint;
	private int totalPage = 0;
	private TextView tvNoData;
	private VoiceRecorder voiceRecorder;
	private PowerManager.WakeLock wakeLock;
	private final TalkBackReceiver receiver = new TalkBackReceiver();

	private int times = 15;
	private Runnable reSendRunnable;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talkback);
		instance = this;
		initTitle();
		initHander();
		initView();
		initData();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.dis_sound_record_text);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		this.micImage = (ImageView) findViewById(R.id.mic_image);
		this.recordingHint = (TextView) findViewById(R.id.recording_hint);
		this.listView = (ListView) findViewById(R.id.talk_lv);
		//		this.headerV = LayoutInflater.from(this).inflate(R.layout.list_header, null);
		//		this.listView.addHeaderView(this.headerV);
		//		this.headerV.setOnClickListener(this);
		this.btnTalk = (Button) findViewById(R.id.talk_btn);
		btnTalk.setText(R.string.monitor_record);
		this.tvNoData = (TextView) findViewById(R.id.no_data_tv);
		this.mRecordAnimationV = findViewById(R.id.recording_container);
		this.mPlayer = new MediaPlayer();
		this.mPlayer.setAudioStreamType(WHAT_SINGLE_ADD);
		this.adapter = new RecordAdapter(this, this.mList);
		this.adapter.setPlayerListener(this);
		this.listView.setAdapter(this.adapter);
		this.voiceRecorder = new VoiceRecorder(this.micImageHandler);

		this.btnTalk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendCMDToDevice("Record", "");
			}
		});
		this.mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (RecordActivity.this.adapter != null) {
					RecordActivity.this.adapter.stopOtherAnima();
				}
			}
		});

		this.mPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				if (RecordActivity.this.adapter != null) {
					RecordActivity.this.adapter.stopOtherAnima();
				}
				mMediaStop();
				return false;
			}
		});
		registerReceiver();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideNotification();
	}

	private void initData(){
		this.wakeLock = ((PowerManager) getSystemService("power")).newWakeLock(6, "talklock");
		this.micImages = new Drawable[]{getResources().getDrawable(R.drawable.record_animate_01), getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03), getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05), getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07), getResources().getDrawable(R.drawable.record_animate_08), getResources().getDrawable(R.drawable.record_animate_09), getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11), getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13), getResources().getDrawable(R.drawable.record_animate_14)};
		loadHistoricalMessageFromLocal(WHAT_SINGLE_ADD);

	}

	private void loadHistoricalMessageFromLocal(final int isScroller) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				RecordActivity.this.totalPage = TalkBackDBOper.getInstance(RecordActivity.this).queryPageCount();
				List<TalkBackBean> hisMessages = TalkBackDBOper.getInstance(RecordActivity.this).queryByPage(RecordActivity.this.curPage,2);
				if (hisMessages != null && hisMessages.size() > 0) {
					RecordActivity.this.sendMessage(hisMessages, RecordActivity.WHAT_BATCH_ADD, isScroller);
				}

			}
		}).start();
	}

	private void registerReceiver() {
		registerReceiver(this.receiver, new IntentFilter(Constants.ACTION_RECORD));
	}

	private void unRegistReceiver() {
		unregisterReceiver(this.receiver);
	}

	private void initHander(){
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				boolean z = true;
				TalkBackBean _bean;
				switch (msg.what) {
				case RecordActivity.WHAT_SINGLE_ADD /*1*/:
					RecordActivity.this.addMessageToList((TalkBackBean) msg.obj);
					RecordActivity.this.refreshUI(true);
					break;
				case RecordActivity.WHAT_BATCH_ADD /*2*/:
					List<TalkBackBean> beans = (List<TalkBackBean>) msg.obj;
					if (beans != null) {
						for (TalkBackBean _bean2 : beans) {
							RecordActivity.this.addMessageToList(_bean2);
						}
						RecordActivity.this.sort();
						RecordActivity RecordActivity = RecordActivity.this;
						if (msg.arg1 != RecordActivity.WHAT_SINGLE_ADD) {
							z = false;
						}
						RecordActivity.refreshUI(z);
					}
					break;
				case RecordActivity.WHAT_SINGLE_DELE /*3*/:
					String path = null;
					TalkBackBean _bean2 = (TalkBackBean) msg.obj;
					RecordActivity.this.removeMessageFromList(_bean2);
					TalkBackDBOper.getInstance(RecordActivity.this).deleteItem(_bean2.getUrl());
					if (_bean2.getType() == RecordActivity.WHAT_SINGLE_ADD) {
						path = _bean2.getUrl();
					} else {
						//						path = MediaUitl.getNetworkVoicePath(RecordActivity.this, _bean2.getUrl());
					}
					RecordActivity.this.deleteFile(new File(path));
					RecordActivity.this.refreshUI(false);
					break;
				case RecordActivity.WHAT_BATCH_DELE /*4*/:
					for (TalkBackBean talkBackBean : RecordActivity.this.mList) {
						String path1 = null;
						if (talkBackBean.getType() == RecordActivity.WHAT_SINGLE_ADD) {
							path1 = talkBackBean.getUrl();
						} else {
							//							path1 = MediaUitl.getNetworkVoicePath(RecordActivity.this, talkBackBean.getUrl());
						}
						RecordActivity.this.deleteFile(new File(path1));
					}
					TalkBackDBOper.getInstance(RecordActivity.this).delAll(2);
					RecordActivity.this.mList.clear();
					RecordActivity.this.refreshUI(true);
					break;
				case 6666 /*6666*/:
					break;

				default:
				}
			}
		};

		this.micImageHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case RecordActivity.WHAT_SINGLE_ADD /*1*/:
					int index = msg.arg1;
					if (index > -1 && index < RecordActivity.this.micImages.length) {
						RecordActivity.this.micImage.setImageDrawable(RecordActivity.this.micImages[msg.arg1]);
					}
					break;
				case RecordActivity.WHAT_BATCH_ADD /*2*/:
					RecordActivity.this.mRecordAnimationV.setVisibility(View.INVISIBLE);
					if (RecordActivity.this.wakeLock.isHeld()) {
						RecordActivity.this.wakeLock.release();
					}
					TalkBackBean item = RecordActivity.this.getTalkBackBean(msg.arg1, (String) msg.obj);
					RecordActivity.this.uploadFile(item);
					RecordActivity.this.sendMessage(item, RecordActivity.WHAT_SINGLE_ADD);
					break;
				default:
				}
			}
		};

	}




	private TalkBackBean getTalkBackBean(int duration, String path) {
		long curTime = System.currentTimeMillis();
		TalkBackBean item = new TalkBackBean();
		item.setDate(this.format.format(Long.valueOf(curTime)));
		item.setHeadPath(User.headerurl);
		item.setDuration(duration);
		item.setStatus(0);
		item.setUrl(path);
		item.setSenderId(String.valueOf(User.id));
		item.setUserId(String.valueOf(User.id));
		item.setdeviceid(User.curBabys.getId());
		item.setType(WHAT_SINGLE_ADD);
		item.setUploadstatus(0);
		item.toString();
		TalkBackDBOper.getInstance(this).insert(item);
		return item;
	}


	private void uploadFile(final TalkBackBean bean){
		if (bean != null) {
			Log.i("RecordActivity", "正在上传对讲音频文件...");
			Log.i("RecordActivity", "文件本地路径：" + this.media_path);
			byte[] content = CommonUtils.getBytesFromFile(new File(media_path));
			new HttpUploadfile(Urls.sen_audio, User.id, User.curBabys.getId(), content,"0",String.valueOf(bean.getDuration()), new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case BabyDetailActivity.UPLOAD_USERINFO_SUCCESS:
						bean.setUploadstatus(RecordActivity.WHAT_BATCH_ADD);
						//						bean.setDate("");
						TalkBackDBOper.getInstance(RecordActivity.this).updateItemByTalkBackBean(bean);
						RecordActivity.this.sendMessage(bean, RecordActivity.WHAT_SINGLE_ADD);
						break;
					case BabyDetailActivity.UPLOAD_USERINFO_FAIL:
						Utils.showToast(R.string.tback_upload_fail);
						bean.setUploadstatus(RecordActivity.WHAT_SINGLE_ADD);
						TalkBackDBOper.getInstance(RecordActivity.this).updateItemByTalkBackBean(bean);
						RecordActivity.this.sendMessage(bean, RecordActivity.WHAT_SINGLE_ADD);
						break;
					}
				}
			});
		}

	}

	private boolean removeMessageFromList(TalkBackBean bean) {
		return this.mList.remove(bean);
	}

	private void sendMessage(Object obj, int what) {
		sendMessage(obj, what, -1);
	}

	private void sendMessage(Object obj, int what, int arg) {
		Message msg = this.handler.obtainMessage();
		msg.what = what;
		msg.obj = obj;
		msg.arg1 = arg;
		this.handler.sendMessage(msg);
	}


	private void sort() {
		int i = 0;
		while (true) {
			if (i < this.mList.size() - 1) {
				int j = WHAT_SINGLE_ADD;
				while (true) {
					if (j >= this.mList.size() - i) {
						break;
					}
					TalkBackBean tin1 = this.mList.get(j - 1);
					TalkBackBean tin2 = this.mList.get(j);
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dt1 = null;
					try {
						dt1 = sdf1.parse(tin1.getDate());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					long time1 = 0;
					if (dt1 != null) {
						time1 = dt1.getTime() / 1000;
					}
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dt2 = null;
					try {
						dt2 = sdf2.parse(tin2.getDate());
					} catch (ParseException e2) {
						e2.printStackTrace();
					}
					long time2 = 0;
					if (dt2 != null) {
						time2 = dt2.getTime() / 1000;
					}
					if (time1 > time2) {
						TalkBackBean temp = this.mList.get(j - 1);
						this.mList.set(j - 1, this.mList.get(j));
						this.mList.set(j, temp);
					}
					j += WHAT_SINGLE_ADD;
				}
				i += WHAT_SINGLE_ADD;
			} else {
				return;
			}
		}
	}

	private void addMessageToList(TalkBackBean bean) {
		bean.toString();
		if (!checkTalkBackBeanEx(bean)) {
			Log.e("locke", "add");
			if (bean.getType() == WHAT_BATCH_ADD) {
				downMedia(bean.getUrl());
			}
			this.mList.add(bean);
		}
	}
	private void downMedia(String path) {
		AsyncHttpUtil.get(path, null, new FileAsyncHttpResponseHandler(new File(media_path)) {

			@Override
			public void onSuccess(int arg0, Header[] arg1, File file) {

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
			}

			@Override
			public void onFinish() {
			}

		});
	}
	private boolean checkTalkBackBeanEx(TalkBackBean bean) {
		for (TalkBackBean _bean : this.mList) {
			if (_bean.getUrl().equals(bean.getUrl())) {
				return true;
			}
		}
		return false;
	}

	private void refreshUI(boolean isScroller) {
		Log.e("locke", "curPage:" + this.curPage + "  totalPage:" + this.totalPage);
		if (this.curPage == this.totalPage || this.totalPage == 0) {
			//			this.headerV.setVisibility(8);
		}
		if (this.mList.size() > 0) {
			this.tvNoData.setVisibility(View.GONE);
			this.listView.setVisibility(View.VISIBLE);
		} else {
			this.tvNoData.setVisibility(View.VISIBLE);
			this.listView.setVisibility(View.GONE);
		}
		this.adapter.notifyDataSetChanged();
		if (isScroller) {
			this.listView.setSelection(this.listView.getBottom());
		}
	}


	public static boolean isExitsSdcard() {
		if (Environment.getExternalStorageState().equals("mounted")) {
			return true;
		}
		return false;
	}
	private boolean deleteFile(File file) {
		if (file != null) {
			return file.delete();
		}
		return false;
	}
	public void delTalkback(TalkBackBean bean) {
		if (bean.getType() == WHAT_SINGLE_ADD) {
			sendMessage(bean, WHAT_SINGLE_DELE);
		} else if (!TextUtils.isEmpty(bean.getId())) {
		}
	}

	public void delAllTalkback() {
		String ids = "";
		for (int i = 0; i < this.mList.size(); i += WHAT_SINGLE_ADD) {
			TalkBackBean bean = this.mList.get(i);
			if (bean.getType() != WHAT_SINGLE_ADD) {
				if (i == this.mList.size() - 1) {
					ids = new StringBuilder(String.valueOf(ids)).append(bean.getId()).toString();
				} else {
					ids = new StringBuilder(String.valueOf(ids)).append(bean.getId()).append(",").toString();
				}
			}
		}
		if (TextUtils.isEmpty(ids)) {
			sendMessage(null, WHAT_BATCH_DELE);
			return;
		}
		ids = ids.substring(0, ids.length() - 1);
	}

	public boolean isHttp(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		return path.contains("http://");
	}



	@Override
	public void mMediaPlay(TalkBackBean talkBackBean) {
		// TODO Auto-generated method stub
		try {
			this.mPlayer.reset();
			Log.i(TAG, "path:" + talkBackBean.getUrl());
			String str = CommonUtils.getCacheVoiceFileName(talkBackBean.getUrl());
			if (isHttp(talkBackBean.getUrl())&& new File(str).exists()) {
				Log.e(TAG, "Media:" + str);
				this.mPlayer.setDataSource(str);
			} else {
				this.mPlayer.setDataSource(talkBackBean.getUrl());
			}
			this.mPlayer.prepare();
			this.mPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
			if (this.adapter != null) {
				this.adapter.stopOtherAnima();
			}
		}

	}

	@Override
	public void mMediaStop() {
		// TODO Auto-generated method stub
		if (this.mPlayer != null && this.mPlayer.isPlaying()) {
			this.mPlayer.stop();
		}


	}
	public boolean isPlayering() {
		if (this.mPlayer != null) {
			return this.mPlayer.isPlaying();
		}
		return false;
	}


	@Override
	protected void onDestroy() {
		instance = null;
		if (this.mPlayer != null && this.mPlayer.isPlaying()) {
			this.mPlayer.stop();
			this.mPlayer.release();
		}
		if (this.mPlayer != null) {
			this.mPlayer.release();
		}
		unRegistReceiver();
		super.onDestroy();
	}


	private void sendCMDToDevice(String action,String phone){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserId", User.id));
		linkedlist.add(new WebServiceProperty("Action", action));
		linkedlist.add(new WebServiceProperty("Content", phone));

		WebServiceTask wsk = new WebServiceTask("SendToDevice", linkedlist, WebService.URL_OPEN,getBaseContext(),new WebServiceResult() {

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
						String str=(String) jsonObject.get("Msg");
						msg=str;
						if (jsonObject.has("Status") && (jsonObject.getInt("Status") == 0)) {
							setReSendBtn();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg=e.getMessage();
					}
				}
				Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
		wsk.execute("SendToDeviceResult");
	}

	private void setReSendBtn()
	{
		this.btnTalk.setText(times+"");
		this.btnTalk.setClickable(false);
		this.reSendRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				RecordActivity localRecordActivity = RecordActivity.this;
				localRecordActivity.times -= 1;
				if (RecordActivity.this.times <= 0)
				{
					RecordActivity.this.btnTalk.setText(R.string.monitor_record);
					RecordActivity.this.btnTalk.setClickable(true);
					RecordActivity.this.times = 15;
					return;
				}
				RecordActivity.this.btnTalk.setText(times+"");
				RecordActivity.this.btnTalk.setClickable(false);
				RecordActivity.this.btnTalk.postDelayed(this, 1000L);
			}
		};
		this.btnTalk.postDelayed(this.reSendRunnable, 1000L);
	}

	class TalkBackReceiver extends BroadcastReceiver {
		TalkBackReceiver() {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e(TAG, "Receiver:" + action);
			if (Constants.ACTION_RECORD.equals(action)) {
				hideNotification();
				sendMessage(intent.getSerializableExtra(Constants.NAME_RECORD), WHAT_BATCH_ADD, RecordActivity.WHAT_SINGLE_ADD);
			}
		}
	}
	private void hideNotification() {
		((NotificationManager) getSystemService("notification")).cancel(6);
	}

}
