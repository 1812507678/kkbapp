package com.ttxgps.utils;

import android.util.SparseArray;
import android.view.View;

public class ViewHolderUtils {
	private ViewHolderUtils() {
	}
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHodler = (SparseArray<View>) view.getTag();
		if (viewHodler == null) {
			viewHodler = new SparseArray<View>();
			view.setTag(viewHodler);
		}
		View childView = viewHodler.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHodler.put(id, childView);
		}
		return (T) childView;
	}
}
