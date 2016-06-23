package com.ttxgps.adapter;

import java.util.List;

import com.ttxgps.msg.MsgInfoDetail;
import com.ttxgps.msg.MsgUtil;
import com.xtst.gps.R;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MsgsAdapter extends BaseAdapter{
	public static final int FLAG_BOTTOM = 2;
	public static final int FLAG_CENTER = 1;
	public static final int FLAG_ONLY_ONE = 3;
	public static final int FLAG_TOP = 0;
	private static final int TYPE_COUNT = 2;
	public static final int TYPE_DATE = 0;
	public static final int TYPE_MSG = 1;
	private final Context context;
	List<MsgInfoDetail> datas;
	int otherTextColor;
	int sosTextColor;

	class DateViewHolder {
		TextView tvDate;

		DateViewHolder() {
		}
	}

	class MsgViewHolder {
		ImageView ivTime;
		ImageView ivType;
		TextView tvAddress;
		TextView tvContent;
		TextView tvTime;
		TextView tvType;
		View vBottomLine;
		View vTopLine;

		MsgViewHolder() {
		}
	}

	public MsgsAdapter(Context context, List<MsgInfoDetail> datas) {
		this.sosTextColor = Color.rgb(244, 116, 115);
		this.otherTextColor = Color.rgb(31, 147, 235);
		this.datas = datas;
		this.context = context;
	}

	@Override
	public int getCount() {
		return this.datas.size();
	}

	@Override
	public MsgInfoDetail getItem(int position) {
		return this.datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (this.datas.get(position).flag == 0) {
			return TYPE_DATE;
		}
		return TYPE_MSG;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MsgInfoDetail bean = getItem(position);
		DateViewHolder dateHolder = null;
		MsgViewHolder msgHolder = null;
		int type = getItemViewType(position);
		if (convertView != null) {
			switch (type) {
			case TYPE_DATE /*0*/:
				dateHolder = (DateViewHolder) convertView.getTag();
				break;
			case TYPE_MSG /*1*/:
				msgHolder = (MsgViewHolder) convertView.getTag();
				break;
			default:
				break;
			}
		}
		switch (type) {
		case TYPE_DATE /*0*/:
			convertView = LayoutInflater.from(this.context).inflate(R.layout.item_msg_date, null);
			dateHolder = new DateViewHolder();
			dateHolder.tvDate = (TextView) convertView.findViewById(R.id.date_tv);
			convertView.setTag(dateHolder);
			break;
		case TYPE_MSG /*1*/:
			convertView = LayoutInflater.from(this.context).inflate(R.layout.msg_category_item, null);
			msgHolder = new MsgViewHolder();
			msgHolder.tvTime = (TextView) convertView.findViewById(R.id.msg_time_tv);
			msgHolder.vTopLine = convertView.findViewById(R.id.msg_top_line);
			msgHolder.vBottomLine = convertView.findViewById(R.id.msg_bottom_line);
			msgHolder.ivTime = (ImageView) convertView.findViewById(R.id.msg_time_iv);
			msgHolder.ivType = (ImageView) convertView.findViewById(R.id.msg_type_iv);
			msgHolder.tvType = (TextView) convertView.findViewById(R.id.msg_type_tv);
			msgHolder.tvContent = (TextView) convertView.findViewById(R.id.msg_content_tv);
			msgHolder.tvAddress = (TextView) convertView.findViewById(R.id.msg_address_tv);
			convertView.setTag(msgHolder);
			break;
		}
		if (type == 0) {
			dateHolder.tvDate.setText(bean.date);
		} else {
			int topStatus;
			int bottomStatus;
			String time;
			int drawableId;
			int typeNameId;
			switch (bean.lineFlag) {
			case TYPE_DATE /*0*/:
				topStatus = 4;
				bottomStatus = TYPE_DATE;
				break;
			case TYPE_MSG /*1*/:
				topStatus = TYPE_DATE;
				bottomStatus = TYPE_DATE;
				break;
			case TYPE_COUNT /*2*/:
				topStatus = TYPE_DATE;
				bottomStatus = 4;
				break;
			case FLAG_ONLY_ONE /*3*/:
				topStatus = 4;
				bottomStatus = 4;
				break;
			default:
				topStatus = TYPE_DATE;
				bottomStatus = TYPE_DATE;
				break;
			}
			msgHolder.vTopLine.setVisibility(topStatus);
			msgHolder.vBottomLine.setVisibility(bottomStatus);
			if (TextUtils.isEmpty(bean.locTime)) {
				time = "00:00";
			} else {
				time = bean.locTime;
			}
			msgHolder.tvTime.setText(time);
			int msgType = Integer.parseInt(bean.msgType);
			if (msgType == MsgUtil.TYPE_SOS_WARN) {
				msgHolder.ivTime.setImageResource(R.drawable.icon_sos_msg);
			} else {
				msgHolder.ivTime.setImageResource(R.drawable.icon_other_msg);
			}
			switch (msgType) {
			case MsgUtil.TYPE_SOS_WARN /*101013*/:
				drawableId = R.drawable.icon_sos;
				typeNameId = R.string.msg_sos_warn;
				break;
			case MsgUtil.TYPE_EXCISE_DEVICE_WARN /*101014*/:
				drawableId = R.drawable.icon_excise_device;
				typeNameId = R.string.msg_excise_device_warn;
				break;
			case MsgUtil.TYPE_TUMBLE_WARN /*101015*/:
				drawableId = R.drawable.icon_tumble;
				typeNameId = R.string.msg_tumble_warn;
				break;
			case MsgUtil.TYPE_LOW_VOLTAGE_WARN /*101016*/:
				drawableId = R.drawable.icon_low_voltage;
				typeNameId = R.string.msg_low_voltage_warn;
				break;
			case MsgUtil.TYPE_ENTER_FENCE_WARN /*102004*/:
				drawableId = R.drawable.icon_enter_fence;
				typeNameId = R.string.msg_enter_fence_warn;
				break;
			case MsgUtil.TYPE_OUT_FENCE_WARN /*102005*/:
				drawableId = R.drawable.icon_out_fence;
				typeNameId = R.string.msg_out_fence_warn;
				break;
			case MsgUtil.TYPE_COMMONLY_WARN:
				drawableId = R.drawable.icon_out_fence;
				typeNameId = R.string.msg_commonly_warn;
				break;
			default:
				drawableId = R.drawable.icon_other;
				typeNameId = R.string.other_msg;
				break;
			}
			if (msgType == MsgUtil.TYPE_SOS_WARN) {
				msgHolder.tvType.setTextColor(this.sosTextColor);
			} else {
				msgHolder.tvType.setTextColor(this.otherTextColor);
			}
			msgHolder.ivType.setImageResource(drawableId);
			msgHolder.tvType.setText(typeNameId);
			if(msgType == MsgUtil.TYPE_COMMONLY_WARN){
				msgHolder.tvContent.setText(context.getString(R.string.app_name));
				msgHolder.tvAddress.setText(bean.msgContent);
			}else{
				msgHolder.tvContent.setText(bean.msgContent);
				msgHolder.tvAddress.setText(bean.address);
			}
		}
		return convertView;
	}

}
