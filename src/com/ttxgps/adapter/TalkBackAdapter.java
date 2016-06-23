package com.ttxgps.adapter;

import java.util.ArrayList;
import java.util.List;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.TalkBackBean;
import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.TalkBackActivity;
import com.ttxgps.talkback.TalkBackDBOper;
import com.ttxgps.utils.AsyncImageLoader;
import com.ttxgps.utils.CallBackInterface;
import com.ttxgps.utils.CommonUtils;
import com.xtst.gps.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TalkBackAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener {
	private static final String TAG = "TalkBackAdapter";
	private static final int TYPE_CHAT_FROM = 1;
	private static final int TYPE_CHAT_TO = 0;
	private static final int TYPE_COUNT = 2;
	private static final int WHAT_ADD_ITEM = 2;
	private static final int WHAT_DEL_ALL = 3;
	private static final int WHAT_DEL_LAST = 1;
	private static final int WHAT_PLAY_NEXT = 0;
	private TalkBackActivity activity;
	private Context context;
	private LayoutInflater inflater;
	private AnimationDrawable lastAnima;
	private ViewHolder lastHolder;
	private List<TalkBackBean> list;
	private MyMediaPlayerListener listener;
	private Handler mHandler;
	private List<TalkBackBean> mNeedPlays;
	private int mPlayIndex;
	private CallBackInterface callBackInterface;

	public TalkBackAdapter(Context context, List<TalkBackBean> list) {
		this.mNeedPlays = new ArrayList();
		this.mPlayIndex = -1;
		this.mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case TalkBackAdapter.TYPE_CHAT_TO /*0*/:
					TalkBackBean bean = TalkBackAdapter.this.mNeedPlays.get(TalkBackAdapter.this.mPlayIndex);
					int lastVisiblePosition = ((Integer) msg.obj).intValue();
					if (bean == null) {
						return;
					}
					if (lastVisiblePosition < TalkBackAdapter.this.list.indexOf(bean)) {
						//						TalkBackAdapter.this.toPlayVoice(bean);
						return;
					}
					LinearLayout llMessage = (LinearLayout) bean.getTag();
					if (llMessage != null) {
						llMessage.performClick();
					}
					break;
				case TalkBackAdapter.WHAT_DEL_LAST /*1*/:
					TalkBackAdapter.this.mNeedPlays.remove(msg.obj);
					break;
				case TalkBackAdapter.WHAT_ADD_ITEM /*2*/:
					TalkBackAdapter.this.mNeedPlays.add((TalkBackBean) msg.obj);
					Log.i(TalkBackAdapter.TAG, "ADD_ITEM:" + TalkBackAdapter.this.mNeedPlays.size());
					break;
				case TalkBackAdapter.WHAT_DEL_ALL /*3*/:
					TalkBackAdapter.this.mNeedPlays.clear();
				default:
				}
			}
		};
		this.context = context;
		this.activity = (TalkBackActivity) context;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}
	public void setPlayerListener(MyMediaPlayerListener listener) {
		this.listener = listener;
	}


	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final TalkBackBean item = getItem(position);
		ViewHolder holderTo = null;
		ViewHolder holderFrom = null;
		int type = getItemViewType(position);
		if (view != null) {
			switch (type) {
			case TYPE_CHAT_TO /*0*/:
				holderTo = (ViewHolder) view.getTag();
				break;
			case WHAT_DEL_LAST /*1*/:
				holderFrom = (ViewHolder) view.getTag();
				break;
			default:
				break;
			}
		}
		switch (type) {
		case TYPE_CHAT_TO /*0*/:
			view = this.inflater.inflate(R.layout.item_right_talkback, null);
			holderTo = new ViewHolder();
			holderTo.tvDate = (TextView)view.findViewById(R.id.item_chatto_date_tv);
			holderTo.imgHead = (ImageView)view.findViewById(R.id.item_chatto_head_iv);
			holderTo.imgsenfail = (ImageView)view.findViewById(R.id.sen_fail_img);
			holderTo.llMessage = (LinearLayout)view.findViewById(R.id.item_chatto_message_ll);
			holderTo.imgVoice = (ImageView)view.findViewById(R.id.item_chatto_voice_img);
			holderTo.tvText = (TextView)view.findViewById( R.id.item_chatto_text_tv);
			holderTo.tvDuration = (TextView)view.findViewById(R.id.item_chatto_duration_tv);
			holderTo.isNew =view.findViewById(R.id.item_chatto_new_img);
			holderTo.voiceBg = view.findViewById(R.id.item_chatto_voice_bg);
			holderTo.pb_uploadstate = (ProgressBar)view.findViewById(R.id.pb_uploadstate);
			view.setTag(holderTo);
			break;
		case WHAT_DEL_LAST /*1*/:
			view = this.inflater.inflate(R.layout.item_left_talkback, null);
			holderFrom = new ViewHolder();
			holderFrom.tvDate = (TextView)view.findViewById(R.id.item_chatfrom_date_tv);
			holderFrom.imgHead = (ImageView)view.findViewById(R.id.item_chatfrom_head_iv);
			holderFrom.llMessage = (LinearLayout)view.findViewById(R.id.item_chatfrom_message_ll);
			holderFrom.imgVoice = (ImageView)view.findViewById(R.id.item_chatfrom_voice_img);
			holderFrom.tvText = (TextView)view.findViewById(R.id.item_chatfrom_text_tv);
			holderFrom.tvDuration = (TextView)view.findViewById(R.id.item_chatfrom_duration_tv);
			holderFrom.isNew = view.findViewById( R.id.item_chatfrom_new_img);
			holderFrom.voiceBg = view.findViewById( R.id.item_chatfrom_voice_bg);
			view.setTag(holderFrom);
			break;
		}
		int i;
		AnimationDrawable anima;
		switch (type) {
		case TYPE_CHAT_TO /*0*/:
			holderTo.tvDate.setText(item.getDate());
			StringBuilder sb1 = new StringBuilder();
			i = TYPE_CHAT_TO;
			while (i < item.getDuration() && i < 10) {
				sb1.append("AA");
				i += WHAT_DEL_LAST;
			}
			holderTo.tvText.setText(sb1.toString());
			holderTo.tvDuration.setText(item.getDuration() + "\"");
			if (holderTo.pb_uploadstate != null) {
				if (item.getUploadstatus() == 0) {
					holderTo.pb_uploadstate.setVisibility(View.VISIBLE);
					holderTo.tvDuration.setVisibility(View.GONE);
					holderTo.imgsenfail.setVisibility(View.GONE);
				} else if (item.getUploadstatus() == WHAT_DEL_LAST) {
					holderTo.pb_uploadstate.setVisibility(View.GONE);
					holderTo.tvDuration.setVisibility(View.VISIBLE);
					holderTo.imgsenfail.setVisibility(View.GONE);
				}else if (item.getUploadstatus() == TalkBackActivity.WHAT_SEN_FAIL){
					holderTo.pb_uploadstate.setVisibility(View.GONE);
					holderTo.tvDuration.setVisibility(View.GONE);
					holderTo.imgsenfail.setVisibility(View.VISIBLE);
				}else {
					holderTo.pb_uploadstate.setVisibility(View.GONE);
					holderTo.tvDuration.setVisibility(View.VISIBLE);
					holderTo.imgsenfail.setVisibility(View.GONE);
				}
			}
			holderTo.imgsenfail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					callBackInterface.onSendSound(item);
				}
			});
			holderTo.imgVoice.setBackgroundResource(R.anim.chat_to_voice);
			anima = (AnimationDrawable) holderTo.imgVoice.getBackground();
			anima.setOneShot(false);
			Temp temp1 = new Temp(holderTo, item, anima);
			setHeadIcon(false, User.headerurl, holderTo);
			holderTo.llMessage.setTag(temp1);
			holderTo.llMessage.setOnClickListener(this);
			holderTo.llMessage.setOnLongClickListener(this);
			if (!item.isPlaying()) {
				holderTo.voiceBg.setVisibility(View.VISIBLE);
				holderTo.imgVoice.setVisibility(View.GONE);
				anima.stop();
				break;
			}
			holderTo.voiceBg.setVisibility(View.GONE);
			holderTo.imgVoice.setVisibility(View.VISIBLE);
			anima.start();
			break;
		case WHAT_DEL_LAST /*1*/:
			holderFrom.tvDate.setText(item.getDate());
			StringBuilder sb2 = new StringBuilder();
			i = TYPE_CHAT_TO;
			while (i < item.getDuration() && i < 10) {
				sb2.append("AA");
				i += WHAT_DEL_LAST;
			}
			holderFrom.tvText.setText(sb2.toString());
			holderFrom.tvDuration.setText(item.getDuration() + "\"");
			if (item.getStatus() == WHAT_DEL_LAST) {
				holderFrom.isNew.setVisibility(View.VISIBLE);
			} else {
				holderFrom.isNew.setVisibility(View.GONE);
			}
			holderFrom.imgVoice.setBackgroundResource(R.anim.chat_from_voice);
			anima = (AnimationDrawable) holderFrom.imgVoice.getBackground();
			anima.setOneShot(false);
			Temp temp2 = new Temp(holderFrom, item, anima);
			if (User.curBabys.getId().equals(item.getSenderId())) {
				setHeadIcon(true, User.curBabys.getHeadIconPath(), holderFrom);
			} else {
				setHeadIcon(false, item.getHeadPath(), holderFrom);
			}
			holderFrom.llMessage.setTag(temp2);
			item.setTag(holderFrom.llMessage);
			holderFrom.llMessage.setOnClickListener(this);
			holderFrom.llMessage.setOnLongClickListener(this);
			if (!item.isPlaying()) {
				holderFrom.voiceBg.setVisibility(TYPE_CHAT_TO);
				holderFrom.imgVoice.setVisibility(8);
				anima.stop();
				break;
			}
			holderFrom.voiceBg.setVisibility(8);
			holderFrom.imgVoice.setVisibility(TYPE_CHAT_TO);
			anima.start();
			break;
		}
		return view;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public TalkBackBean getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public int getItemViewType(int position) {
		if (String.valueOf(User.id).equals(getItem(position).getSenderId())) {
			return TYPE_CHAT_TO;
		}
		return WHAT_DEL_LAST;
	}

	@Override
	public int getViewTypeCount() {
		return WHAT_ADD_ITEM;
	}


	private void setHeadIcon(boolean isDeviceIcon, String url, ViewHolder holder) {
		if (!TextUtils.isEmpty(url) && !"null".equals(url)) {
			//			holder.imgHead.setImageBitmap(BitmapFactory.decodeFile(AsyncImageLoader.getCacheImgFileName(url)));
			CommonUtils.downloadphoto(activity, url, holder.imgHead);
		} else if (!isDeviceIcon) {
			holder.imgHead.setImageResource(R.drawable.home_icon_head);
		} else if (User.curBabys.getSex() == WHAT_DEL_LAST) {
			holder.imgHead.setImageResource(R.drawable.icon_boy);
		} else {
			holder.imgHead.setImageResource(R.drawable.icon_girl);
		}
	}

	public void playNext(int lastVisiblePosition) {
		stopOtherAnima();
	}



	public interface MyMediaPlayerListener {
		void mMediaPlay(TalkBackBean talkBackBean);

		void mMediaStop();
	}

	class Temp {
		AnimationDrawable anima;
		TalkBackBean bean;
		ViewHolder holder;

		public Temp(ViewHolder holder, TalkBackBean bean, AnimationDrawable anima) {
			this.holder = holder;
			this.bean = bean;
			this.anima = anima;
		}
	}

	private class ViewHolder {
		public ImageView imgHead;
		public ImageView imgVoice;
		public ImageView imgsenfail;
		public View isNew;
		public LinearLayout llMessage;
		public ProgressBar pb_uploadstate;
		public TextView tvDate;
		public TextView tvDuration;
		public TextView tvText;
		public View voiceBg;
	}


	public void stopOtherAnima() {
		if (this.lastAnima != null && this.lastHolder != null) {
			this.lastAnima.stop();
			this.lastHolder.voiceBg.setVisibility(TYPE_CHAT_TO);
			this.lastHolder.imgVoice.setVisibility(8);
		}
	}





	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		final Temp temp = (Temp) v.getTag();
		View view = LayoutInflater.from(this.context).inflate(R.layout.talkback_del_pop, null);
		final PopupWindow pw = new PopupWindow(view, -2, -2);
		pw.setBackgroundDrawable(new BitmapDrawable());
		pw.setOutsideTouchable(true);
		view.findViewById(R.id.del_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TalkBackAdapter.this.activity.delTalkback(temp.bean);
				TalkBackAdapter.this.sendMessage(temp.bean, TalkBackAdapter.WHAT_DEL_LAST);
				pw.dismiss();

			}
		});
		view.findViewById(R.id.del_all_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TalkBackAdapter.this.activity.delAllTalkback();
				TalkBackAdapter.this.sendMessage(null, TalkBackAdapter.WHAT_DEL_ALL);
				pw.dismiss();

			}
		});
		pw.showAsDropDown(v, 10, -((v.getHeight() * WHAT_DEL_ALL) / WHAT_ADD_ITEM));
		return true;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Temp temp = (Temp) v.getTag();
		if (TextUtils.isEmpty(temp.bean.getUrl())) {
			Utils.showToast(R.string.tback_file_null);
		} else if (temp.anima.isRunning() && temp.holder.hashCode() == this.lastHolder.hashCode()) {
			temp.holder.voiceBg.setVisibility(TYPE_CHAT_TO);
			temp.holder.imgVoice.setVisibility(View.GONE);
			temp.anima.stop();
			this.listener.mMediaStop();
			this.mPlayIndex = -1;
		} else {
			stopOtherAnima();
			temp.holder.voiceBg.setVisibility(View.GONE);
			temp.holder.imgVoice.setVisibility(View.VISIBLE);
			temp.holder.isNew.setVisibility(View.GONE);
			temp.anima.start();
			this.lastAnima = temp.anima;
			this.lastHolder = temp.holder;
			toPlayVoice(temp.bean);
		}

	}
	private void toPlayVoice(TalkBackBean bean) {
		bean.setStatus(TYPE_CHAT_TO);
		this.mPlayIndex = this.mNeedPlays.indexOf(bean);
		if (this.mPlayIndex != -1) {
			sendMessage(bean, WHAT_DEL_LAST);
		}
		this.listener.mMediaPlay(bean);
		if (TalkBackDBOper.getInstance(this.context).updateItem(bean.getUrl()) <= 0) {
			Log.i("TalkBackAdapter", "ÓïÒô×´Ì¬Î´ÐÞ¸Ä³É¹¦");
		} else {
			Log.i("TalkBackAdapter", "ÓïÒô×´Ì¬ÐÞ¸Ä³É¹¦");
		}
	}
	private void sendMessage(Object obj, int what) {
		Message msg = this.mHandler.obtainMessage();
		msg.what = what;
		msg.obj = obj;
		this.mHandler.sendMessage(msg);
	}

	public void setSendSound(CallBackInterface callBackInterface){
		this.callBackInterface = callBackInterface;
	}

}
