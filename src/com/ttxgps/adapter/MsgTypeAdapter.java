package com.ttxgps.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ttxgps.msg.MsgUtil;
import com.xtst.gps.R;

public class MsgTypeAdapter extends BaseAdapter{
	private final Context mContext;
	private final List<Integer> typeIds;

	class ViewHolder {
		TextView tvTypeName;

		ViewHolder() {
		}
	}

	public MsgTypeAdapter(Context context, List<Integer> types) {
		this.mContext = context;
		this.typeIds = types;
	}

	@Override
	public int getCount() {
		return this.typeIds.size();
	}

	@Override
	public Integer getItem(int position) {
		return this.typeIds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(R.layout.item_msg_type, null);
			holder = new ViewHolder();
			holder.tvTypeName = (TextView) convertView.findViewById(R.id.msg_type_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		switch (getItem(position).intValue()) {
		case MsgUtil.TYPE_SOS_WARN /*101013*/:
			holder.tvTypeName.setText(R.string.msg_sos_warn);
			break;
		case MsgUtil.TYPE_EXCISE_DEVICE_WARN /*101014*/:
			holder.tvTypeName.setText(R.string.msg_excise_device_warn);
			break;
			//		case MsgUtil.TYPE_TUMBLE_WARN /*101015*/:
			//			holder.tvTypeName.setText(R.string.msg_tumble_warn);
			//			break;
			//		case MsgUtil.TYPE_LOW_VOLTAGE_WARN /*101016*/:
			//			holder.tvTypeName.setText(R.string.msg_low_voltage_warn);
			//			break;
		case MsgUtil.TYPE_ENTER_FENCE_WARN /*102004*/:
			holder.tvTypeName.setText(R.string.msg_enter_fence_warn);
			break;
		case MsgUtil.TYPE_OUT_FENCE_WARN /*102005*/:
			holder.tvTypeName.setText(R.string.msg_out_fence_warn);
			break;
		case MsgUtil.TYPE_COMMONLY_WARN /*102005*/:
			holder.tvTypeName.setText(R.string.msg_commonly_warn);
			break;
		default:
			holder.tvTypeName.setText(R.string.all);
			break;
		}
		return convertView;
	}

}
