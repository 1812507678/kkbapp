
package com.ttxgps.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;


public class NetUtil {
	public static enum netType {
		wifi, CMNET, CMWAP, noneNet
	}
	public static final int NETWORN_NONE = 0;

	public static final int NETWORN_WIFI = 1;

	public static final int NETWORN_MOBILE = 2;

	/**
	 * @Description: ��ȡ������������
	 * @param context
	 * @return int
	 */
	public static int getNetworkState(Context context) {
		State state = null;
		ConnectivityManager connManager =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if(null != wifiNetworkInfo){
			state = wifiNetworkInfo.getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return NETWORN_WIFI;
			}
		}

		NetworkInfo mobileNetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(null != mobileNetworkInfo){
			state = mobileNetworkInfo.getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return NETWORN_MOBILE;
			}
		}
		return NETWORN_NONE;
	}

	/**
	 * �����Ƿ����
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context != null) {
			ConnectivityManager mgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] info = mgr.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * �ж��Ƿ���������
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * �ж�WIFI�����Ƿ�����
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager =
					(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo =
					mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * �ж�MOBILE�����Ƿ�����
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager =
					(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo =
					mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * ��ȡ��ǰ������״ͬgetNetworkState()����)
	 * @param context
	 * @return NetUtil.netType
	 */
	public static netType getConnectType(Context context) {
		if (context == null)
			return netType.noneNet;
		ConnectivityManager connMgr =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType.noneNet;
		}
		int nType = networkInfo.getType();

		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				return netType.CMNET;
			}
			else {
				return netType.CMWAP;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			return netType.wifi;
		}
		return netType.noneNet;
	}
}
