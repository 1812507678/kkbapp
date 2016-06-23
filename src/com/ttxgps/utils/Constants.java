package com.ttxgps.utils;

import java.io.File;

import android.os.Environment;

public class Constants {


	/** 缓存存储路径 */
	public static final String SDCARD = Environment
			.getExternalStorageDirectory().getAbsolutePath();


	public static final String HEALTH_ROOD = SDCARD+"/ttxgps/";
	public static final String HEADER_DIR = HEALTH_ROOD+"icon/";
	public static final String VOICE_DIR = HEALTH_ROOD+"voice/";

	public static final int PHOTO_PICKED_WITH_DATA = 3021;
	public static final int CAMERA_WITH_DATA = 3023;
	public static final int ICON_SIZE = 218;
	public static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");

	public static final boolean GETLOCATION = false; //true取默认经纬度， false去百度经纬度

	/**是否打开支付模块*/
	public static final boolean PAY_STATUE = true;

	/** 开始 */
	public static final int DOWNLOAD_START = 1;
	/** 下载中 */
	public static final int DOWNLOAD_DOWNLOAD = 2;
	/** 暂停 */
	public static final int DOWNLOAD_SPACE = 3;
	/** 继续 */
	public static final int DOWNLOAD_GO = 4;
	/** 取消 */
	public static final int DOWNLOAD_CANCEL = 5;
	/** 错误 */
	public static final int DOWNLOAD_ERROR = 6;
	/** 下载完成 */
	public static final int DOWNLOAD_SUCCESS = 7;
	/** 服务器错误 */
	public static final int DOWNLOAD_ERROR_SERVER = 8;

	public static final String STATUS = "Status";
	public static final String MSG = "Msg";
	public static final String KEY_IS_ADMIN = "isAdmin";

	/**对讲*/
	public static final String ACTION_TALK_BACK = "action_talk_back";
	/**录音*/
	public static final String ACTION_RECORD = "action_record";
	/**APP下线*/
	public static final String ACTION_LOGINOUT = "action_loginout";
	/**设备下线*/
	public static final String ACTION_DEVICEONOFF = "action_DeviceOnOff";
	/**监护人解除*/
	public static final String ACTION_GUARDIANDEL = "action_GuardianDel";
	/**管理员转让*/
	public static final String ACTION_MNSCHANGE = "action_Mngchange";
	/**定位*/
	public static final String ACTION_LOCATION = "action_Location";
	/**电子围栏、脱表报警、SOS报警、*/
	public static final String ACTION_PUSH_MSG = "action_push_msg";

	/**当前设备id*/
	public static final String CUR_DEVICE_ID = "CUR_DEVICE_ID";

	/**充值成功，刷新宝贝详情界面*/
	public static final String ACTION_UPDATE_BABYDETAIL = "action_update_babydetail";


	public static final String NAME_TALK_BACK = "name_talk_back";
	public static final String NAME_RECORD = "name_record";



}
