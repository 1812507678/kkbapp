package com.ttxgps.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.palmtrends.app.ShareApplication;

/**
 * @author Nestor.Zhang@Oracle.Com
 * 
 * Contains names for all global settings which will
 * write to the application preferences file.
 * 
 * NOTE: All settings which write to the application shared preference
 * file should be put into this class in an order of naming in case
 * duplicate names. And do best to never reuse one used name for other
 * purpose in case the application corrupts in future updated releases.
 * 
 */

public class PrefHelper {
	public static final String GPSLOCATION_PREFS_FILENAME = "ttxgps_UserInfo";

	public static final String P_AUTOLOGIN_STATE = "sp_autologin_state";
	public static final String P_USER_ID = "ID";
	public static final String P_USER_TIMEZONE = "timeZone";
	public static final String P_TYPE_ID = "sp_type_id";
	public static final String P_USER_NAME = "UserName";
	public static final String P_LOGIN_NAME = "LoginName";
	public static final String P_GPS = "sp_location_setting";
	public static final String P_PUSH = "sp_push_setting";
	public static final String P_PHONE_W = "Phone";
	public static final String P_PHONE_H = "sp_phone_h";
	public static final String P_LOGIN_STATE = "IsorNoLogin";
	public static final String P_PWD_TEXT = "sp_pwd_text";
	public static final String P_PWD_STATE = "sp_pwd_state";
	public static final String P_ISADMIN = "isAdmin";

	public static final String P_SELECTED_MAP = "sp_selected_map";


	private static SharedPreferences sp;
	private static PrefHelper ph;

	private PrefHelper() {

	}

	public static PrefHelper getPreference(Context a) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}
		return ph;
	}

	public static PrefHelper getPreference() {
		return ph;
	}

	/**
	 * Check if the setting represented by "key" was ever set.
	 * @param key
	 * @return true if set; otherwise false
	 */
	public static boolean hasKey(String key) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}

		return sp.contains(key);
	}

	public static void setInfo(String name, String data) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}
		SharedPreferences.Editor e = sp.edit().putString(name, data);
		e.commit();
	}

	public static void setInfo(String name, int data) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}
		SharedPreferences.Editor e = sp.edit().putInt(name, data);
		e.commit();
	}

	public static void setInfo(String name, boolean data) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}

		SharedPreferences.Editor e = sp.edit().putBoolean(name, data);
		e.commit();
	}

	public static int getIntData(String name) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}
		return sp.getInt(name, -1);
	}

	public static String getStringData(String name) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}
		return sp.getString(name, "");
	}

	public static boolean getBooleanData(String name) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}
		return sp.getBoolean(name, false);
	}

	public static void setInfo(String name, long data) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}
		SharedPreferences.Editor e = sp.edit().putLong(name, data);
		e.commit();
	}

	public static long getLongData(String name) {
		if (ph == null) {
			ph = new PrefHelper();
			sp = ShareApplication.share.getSharedPreferences(GPSLOCATION_PREFS_FILENAME, 0);
		}
		return sp.getLong(name, 0);
	}
}
