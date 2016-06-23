
package com.ttxgps.adapter;

import java.util.List;

import com.ttxgps.bean.HomeGridviewBean;
import com.ttxgps.utils.DensityUtil;
import com.ttxgps.utils.ViewHolderUtils;
import com.xtst.gps.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;



public class HomeAdapter extends MyBaseAdapter<HomeGridviewBean> {

	private final Context context;
	private int clickTemp = -1;
	private final GridView gridview;

	public HomeAdapter(Context context, List<HomeGridviewBean> list,GridView gridview) {
		super(list);
		this.context = context;
		this.gridview = gridview;
	}

	@Override
	protected View getViewItem(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false);
		}
		AbsListView.LayoutParams param = new AbsListView.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				gridview.getHeight()/4-DensityUtil.dip2px(context, 4));

		ImageView ItemImage = ViewHolderUtils.get(convertView, R.id.ItemImage);
		TextView ItemText = ViewHolderUtils.get(convertView, R.id.ItemText);

		HomeGridviewBean homeGridviewBean = getItem(position);
		//		if (clickTemp == position) {
		//			ItemImage.setBackgroundResource(homeGridviewBean.getOnclickdrawableId());
		//		} else {
		ItemImage.setImageResource(homeGridviewBean.getNormaldrawableId());
		//		}
		convertView.setBackgroundResource(homeGridviewBean.getBackground());
		ItemText.setText(homeGridviewBean.getDrawableName());

		convertView.setLayoutParams(param);

		return convertView;
	}
	public void setSeclection(int position) {
		clickTemp =position;
	}

}
