package com.ttxgps.utils;

import android.content.Context;
import android.view.WindowManager;

public class DensityUtil {
	public static int dip2px(Context context, float dpValue) {
		return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		return (int) ((pxValue / context.getResources().getDisplayMetrics().density) + 0.5f);
	}

	public static int getScreenWidth(Context context) {
		return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth();
	}

	public static int getScreenHeight(Context context) {
		return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight();
	}

}
