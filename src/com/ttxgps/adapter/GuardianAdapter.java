package com.ttxgps.adapter;

import java.util.List;

import com.ttxgps.bean.GuardianBean;
import com.ttxgps.bean.HomeGridviewBean;
import com.ttxgps.utils.ViewHolderUtils;
import com.xtst.gps.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class GuardianAdapter extends MyBaseAdapter<GuardianBean>{
	private final Context context;
	private final int clickTemp = -1;

	public GuardianAdapter(Context context, List<GuardianBean> list) {
		super(list);
		this.context = context;
	}

	@Override
	protected View getViewItem(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_guardian, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.head_icon = (ImageView)convertView.findViewById(R.id.head_icon_iv);
			viewHolder.relation = (TextView)convertView.findViewById(R.id.relation_tv);
			viewHolder.account = (TextView)convertView.findViewById(R.id.account_tv);
			viewHolder.mark_type = (TextView)convertView.findViewById(R.id.mark_tv);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		GuardianBean homeGridviewBean = getItem(position);

		return convertView;
	}


	public class ViewHolder{
		public ImageView head_icon;
		public TextView relation;
		public TextView account;
		public TextView mark_type;
	}
}
