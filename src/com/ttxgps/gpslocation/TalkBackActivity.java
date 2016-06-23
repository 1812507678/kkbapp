package com.ttxgps.gpslocation;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
import org.apache.http.Header;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.loopj.android.http.RequestParams;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.adapter.TalkBackAdapter;
import com.ttxgps.adapter.TalkBackAdapter.MyMediaPlayerListener;
import com.ttxgps.bean.TalkBackBean;
import com.ttxgps.entity.User;
import com.ttxgps.record.VoiceRecorder;
import com.ttxgps.talkback.TalkBackDBOper;
import com.ttxgps.utils.AsyncHttpUtil;
import com.ttxgps.utils.AsyncHttpUtil.AsyHttpHandler;
import com.ttxgps.utils.CallBackInterface;
import com.ttxgps.utils.CommonUtils;
import com.ttxgps.utils.Constants;
import com.ttxgps.utils.HttpUploadfile;
import com.ttxgps.utils.Urls;
import com.xtst.gps.R;

public class TalkBackActivity extends BaseActivity implements MyMediaPlayerListener{
	private static final String TAG = "TalkBackActivity";
	public static TalkBackActivity instance;
	private static final int WHAT_BATCH_ADD = 2;
	private static final int WHAT_BATCH_DELE = 4;
	public static final int WHAT_SINGLE_ADD = 1;
	private static final int WHAT_SINGLE_DELE = 3;
	public static final int WHAT_SEN_FAIL = 66;
	private boolean _flag;
	private TalkBackAdapter adapter;
	private MapAPP application;
	private Button btnTalk;
	private final int curPage = 1;
	private long currentTime = 0L;
	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//	private View headerV;
	private double lat;
	private Handler handler;
	private Handler micImageHandler;
	private ListView listView;
	private double lon;
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
		((TextView) findViewById(R.id.title_tv)).setText(R.string.tback_title);
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
		this.tvNoData = (TextView) findViewById(R.id.no_data_tv);
		this.mRecordAnimationV = findViewById(R.id.recording_container);
		this.mPlayer = new MediaPlayer();
		this.mPlayer.setAudioStreamType(WHAT_SINGLE_ADD);
		this.adapter = new TalkBackAdapter(this, this.mList);
		this.adapter.setPlayerListener(this);
		this.listView.setAdapter(this.adapter);
		this.voiceRecorder = new VoiceRecorder(this.micImageHandler);

		adapter.setSendSound(new CallBackInterface() {

			@Override
			public void onSendSound(TalkBackBean talkBackBean) {
				// TODO Auto-generated method stub
				if(TextUtils.isEmpty(talkBackBean.getUrl())){
					Utils.showToast("语音文件地址不存在");
					return;
				}
				if(!new File(talkBackBean.getUrl()).exists()){
					Utils.showToast("语音文件不存在");
				}
				uploadFile(talkBackBean);
				TalkBackActivity.this.sendMessage(talkBackBean, TalkBackActivity.WHAT_SEN_FAIL);
			}
		});

		this.btnTalk.setOnTouchListener(new PressToSpeakListen());
		this.mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (TalkBackActivity.this.adapter != null) {
					TalkBackActivity.this.adapter.stopOtherAnima();
				}
			}
		});
		this.mPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				if (TalkBackActivity.this.adapter != null) {
					TalkBackActivity.this.adapter.stopOtherAnima();
				}
				mMediaStop();
				return false;
			}
		});
		registerReceiver();
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
				TalkBackActivity.this.totalPage = TalkBackDBOper.getInstance(TalkBackActivity.this).queryPageCount();
				List<TalkBackBean> hisMessages = TalkBackDBOper.getInstance(TalkBackActivity.this).queryByPage(TalkBackActivity.this.curPage,1);
				if (hisMessages != null && hisMessages.size() > 0) {
					TalkBackActivity.this.sendMessage(hisMessages, TalkBackActivity.WHAT_BATCH_ADD, isScroller);
				}

			}
		}).start();
	}

	private void registerReceiver() {
		registerReceiver(this.receiver, new IntentFilter(Constants.ACTION_TALK_BACK));
	}

	private void unRegistReceiver() {
		unregisterReceiver(this.receiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideNotification();
	}

	private void initHander(){
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				boolean z = true;
				TalkBackBean _bean;
				switch (msg.what) {
				case TalkBackActivity.WHAT_SINGLE_ADD /*1*/:
					TalkBackActivity.this.addMessageToList((TalkBackBean) msg.obj);
					TalkBackActivity.this.refreshUI(true);
					break;
				case TalkBackActivity.WHAT_BATCH_ADD /*2*/:
					List<TalkBackBean> beans = (List<TalkBackBean>) msg.obj;
					if (beans != null) {
						for (TalkBackBean _bean2 : beans) {
							TalkBackActivity.this.addMessageToList(_bean2);
						}
						TalkBackActivity.this.sort();
						TalkBackActivity talkBackActivity = TalkBackActivity.this;
						if (msg.arg1 != TalkBackActivity.WHAT_SINGLE_ADD) {
							z = false;
						}
						talkBackActivity.refreshUI(z);
					}
					break;
				case TalkBackActivity.WHAT_SINGLE_DELE /*3*/:
					String path = null;
					TalkBackBean _bean2 = (TalkBackBean) msg.obj;
					TalkBackActivity.this.removeMessageFromList(_bean2);
					TalkBackDBOper.getInstance(TalkBackActivity.this).deleteItem(_bean2.getUrl());
					if (_bean2.getType() == TalkBackActivity.WHAT_SINGLE_ADD) {
						path = _bean2.getUrl();
					} else {
						//						path = MediaUitl.getNetworkVoicePath(TalkBackActivity.this, _bean2.getUrl());
					}
					TalkBackActivity.this.deleteFile(new File(path));
					TalkBackActivity.this.refreshUI(false);
					break;
				case TalkBackActivity.WHAT_BATCH_DELE /*4*/:
					for (TalkBackBean talkBackBean : TalkBackActivity.this.mList) {
						String path1 = null;
						if (talkBackBean.getType() == TalkBackActivity.WHAT_SINGLE_ADD) {
							path1 = talkBackBean.getUrl();
						} else {
							//							path1 = MediaUitl.getNetworkVoicePath(TalkBackActivity.this, talkBackBean.getUrl());
						}
						TalkBackActivity.this.deleteFile(new File(path1));
					}
					TalkBackDBOper.getInstance(TalkBackActivity.this).delAll(1);
					TalkBackActivity.this.mList.clear();
					TalkBackActivity.this.refreshUI(true);
					break;
				case 6666 /*6666*/:
					LocationInfo info = (LocationInfo) msg.obj;
					TalkBackActivity.this.lat = info.lat;
					TalkBackActivity.this.lon = info.lon;
					break;
				case WHAT_SEN_FAIL:
					TalkBackActivity.this.addMessageToList((TalkBackBean) msg.obj);
					TalkBackActivity.this.refreshUI(true);
					break;
				default:
				}
			}
		};

		this.micImageHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case TalkBackActivity.WHAT_SINGLE_ADD /*1*/:
					int index = msg.arg1;
					if (index > -1 && index < TalkBackActivity.this.micImages.length) {
						TalkBackActivity.this.micImage.setImageDrawable(TalkBackActivity.this.micImages[msg.arg1]);
					}
					break;
				case TalkBackActivity.WHAT_BATCH_ADD /*2*/:
					TalkBackActivity.this.btnTalk.setPressed(false);
					TalkBackActivity.this.mRecordAnimationV.setVisibility(View.INVISIBLE);
					if (TalkBackActivity.this.wakeLock.isHeld()) {
						TalkBackActivity.this.wakeLock.release();
					}
					TalkBackBean item = TalkBackActivity.this.getTalkBackBean(msg.arg1, (String) msg.obj);
					TalkBackActivity.this.uploadFile(item);
					TalkBackActivity.this.sendMessage(item, TalkBackActivity.WHAT_SINGLE_ADD);
					break;
				default:
				}
			}
		};

	}



	class PressToSpeakListen implements OnTouchListener {
		PressToSpeakListen() {
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case BDLocation.TypeNone /*0*/:
				if (TalkBackActivity.this.voiceRecorder.isRecording()) {
					return true;
				}
				TalkBackActivity.this._flag = true;
				if ((System.currentTimeMillis() - TalkBackActivity.this.currentTime) < 400.0d) {
					TalkBackActivity.this._flag = false;
					return true;
				}
				TalkBackActivity.this._flag = true;
				if (TalkBackActivity.isExitsSdcard()) {
					try {
						v.setPressed(true);
						TalkBackActivity.this.wakeLock.acquire();
						TalkBackActivity.this.mMediaStop();
						TalkBackActivity.this.mRecordAnimationV.setVisibility(View.VISIBLE);
						TalkBackActivity.this.recordingHint.setText(TalkBackActivity.this.getString(R.string.move_up_to_cancel));
						TalkBackActivity.this.recordingHint.setBackgroundColor(0);
						TalkBackActivity.this.media_path = new StringBuilder(Constants.VOICE_DIR+(new SimpleDateFormat("yyyyMMddHHmmss").format(Long.valueOf(System.currentTimeMillis())))).append(".amr").toString();
						File saveFilePath = new File(TalkBackActivity.this.media_path);
						saveFilePath.createNewFile();
						TalkBackActivity.this.voiceRecorder.startRecording(saveFilePath, TalkBackActivity.this.getApplicationContext());
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						v.setPressed(false);
						if (TalkBackActivity.this.wakeLock.isHeld()) {
							TalkBackActivity.this.wakeLock.release();
						}
						if (TalkBackActivity.this.voiceRecorder != null) {
							TalkBackActivity.this.voiceRecorder.discardRecording();
						}
						TalkBackActivity.this.mRecordAnimationV.setVisibility(View.INVISIBLE);
						Utils.showToast(R.string.recoding_fail);
						return true;
					}
				}
				Toast.makeText(TalkBackActivity.this, "发送语音需要sdcard支持！", 0).show();
				return false;
			case TalkBackActivity.WHAT_SINGLE_ADD /*1*/:
				if (!TalkBackActivity.this.voiceRecorder.isRecording()) {
					return true;
				}
				TalkBackActivity.this.currentTime = System.currentTimeMillis();
				if (!TalkBackActivity.this._flag) {
					return true;
				}
				v.setPressed(false);
				TalkBackActivity.this.mRecordAnimationV.setVisibility(View.INVISIBLE);
				if (TalkBackActivity.this.wakeLock.isHeld()) {
					TalkBackActivity.this.wakeLock.release();
				}
				if (event.getY() < 0.0f) {
					TalkBackActivity.this.voiceRecorder.discardRecording();
					return true;
				}
				int length = TalkBackActivity.this.voiceRecorder.stopRecoding();
				Log.i(TalkBackActivity.TAG, "record length:" + length);
				if (length > 0) {
					TalkBackBean item = TalkBackActivity.this.getTalkBackBean(length, TalkBackActivity.this.voiceRecorder.getVoiceFilePath());
					TalkBackActivity.this.uploadFile(item);
					TalkBackActivity.this.sendMessage(item, TalkBackActivity.WHAT_SINGLE_ADD);
					return true;
				} else if (length == VoiceRecorder.INVALID_FILE) {
					Toast.makeText(TalkBackActivity.this, "无录音权限", 0).show();
					return true;
				} else {
					Utils.showToast("录音时间太短");
					return true;
				}
			case TalkBackActivity.WHAT_BATCH_ADD /*2*/:
				if (!TalkBackActivity.this.voiceRecorder.isRecording() || !TalkBackActivity.this._flag) {
					return true;
				}
				if (event.getY() < 0.0f) {
					TalkBackActivity.this.recordingHint.setText(TalkBackActivity.this.getString(R.string.release_to_cancel));
					TalkBackActivity.this.recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
					return true;
				}
				TalkBackActivity.this.recordingHint.setText(TalkBackActivity.this.getString(R.string.move_up_to_cancel));
				TalkBackActivity.this.recordingHint.setBackgroundColor(0);
				return true;
			default:
				if (!TalkBackActivity.this._flag) {
					return true;
				}
				TalkBackActivity.this.mRecordAnimationV.setVisibility(View.INVISIBLE);
				if (TalkBackActivity.this.voiceRecorder != null) {
					TalkBackActivity.this.voiceRecorder.discardRecording();
				}
				return false;
			}
		}
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
		item.setSoundtype(1);
		item.toString();
		TalkBackDBOper.getInstance(this).insert(item);
		return item;
	}


	private void uploadFile(final TalkBackBean bean){
		if (bean != null) {
			Log.i("TalkBackActivity", "正在上传对讲音频文件...");
			Log.i("TalkBackActivity", "文件本地路径：" + bean.getUrl());
			byte[] content = CommonUtils.getBytesFromFile(new File(bean.getUrl()));
			new HttpUploadfile(Urls.sen_audio, User.id, User.curBabys.getId(), content,"0",String.valueOf(bean.getDuration()), new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case BabyDetailActivity.UPLOAD_USERINFO_SUCCESS:
						bean.setUploadstatus(TalkBackActivity.WHAT_BATCH_ADD);
						//						bean.setDate("");
						TalkBackDBOper.getInstance(TalkBackActivity.this).updateItemByTalkBackBean(bean);
						TalkBackActivity.this.sendMessage(bean, TalkBackActivity.WHAT_SINGLE_ADD);
						break;
					case BabyDetailActivity.UPLOAD_USERINFO_FAIL:
						Utils.showToast(R.string.tback_upload_fail);
						bean.setUploadstatus(TalkBackActivity.WHAT_SEN_FAIL);
						TalkBackDBOper.getInstance(TalkBackActivity.this).updateItemByTalkBackBean(bean);
						TalkBackActivity.this.sendMessage(bean, TalkBackActivity.WHAT_SEN_FAIL);
						break;
					case 15:
						Utils.showToast(R.string.tback_upload_fail_no);
						bean.setUploadstatus(TalkBackActivity.WHAT_SEN_FAIL);
						TalkBackDBOper.getInstance(TalkBackActivity.this).updateItemByTalkBackBean(bean);
						TalkBackActivity.this.sendMessage(bean, TalkBackActivity.WHAT_SEN_FAIL);
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
		if (!checkTalkBackBeanEx(bean)) {
			Log.e("locke", "add");
			if (isHttp(bean.getUrl())&& bean.getDuration() == 0) {
				//				downMedia(bean);
			}
			this.mList.add(bean);
		}
	}
	private void downMedia(final TalkBackBean bean) {
		AsyncHttpUtil.get(bean.getUrl(), null, new FileAsyncHttpResponseHandler(new File(CommonUtils.getCacheVoiceFileName(bean.getUrl()))) {

			@Override
			public void onSuccess(int arg0, Header[] arg1, File file) {
				try {
					long s = getAmrDuration(file);
					bean.setDuration((int)s/1000);
					TalkBackDBOper.getInstance(getBaseContext()).updateItemByTalkBackBean(bean);
					adapter.notifyDataSetChanged();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

	/**
	 * 得到amr的时长
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long getAmrDuration(File file) throws IOException {
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();//文件的长度
			int pos = 6;//设置初始位置
			int frameCount = 0;//初始帧数
			int packedPos = -1;
			/////////////////////////////////////////////////////
			byte[] datas = new byte[1];//初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			/////////////////////////////////////////////////////
			duration += frameCount * 20;//帧数*20
		} finally {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		}
		return duration;
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



	public class LocationInfo implements Serializable {
		private static final long serialVersionUID = 4019861593241923427L;
		public String addr;
		public String city;
		public double lat;
		public double lon;
		public String name;
		public float radius;
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



	class TalkBackReceiver extends BroadcastReceiver {
		TalkBackReceiver() {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e(TalkBackActivity.TAG, "Receiver:" + action);
			if (Constants.ACTION_TALK_BACK.equals(action)) {
				TalkBackActivity.this.hideNotification();
				TalkBackActivity.this.sendMessage(intent.getSerializableExtra(Constants.NAME_TALK_BACK), TalkBackActivity.WHAT_BATCH_ADD, TalkBackActivity.WHAT_SINGLE_ADD);
			}
		}
	}
	private void hideNotification() {
		((NotificationManager) getSystemService("notification")).cancel(5);
	}




}
