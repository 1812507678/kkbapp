package com.ttxgps.utils;

import java.io.File;

import android.os.Environment;

public class Constants {


	/** ����洢·�� */
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

	public static final boolean GETLOCATION = false; //trueȡĬ�Ͼ�γ�ȣ� falseȥ�ٶȾ�γ��

	/**�Ƿ��֧��ģ��*/
	public static final boolean PAY_STATUE = true;

	/** ��ʼ */
	public static final int DOWNLOAD_START = 1;
	/** ������ */
	public static final int DOWNLOAD_DOWNLOAD = 2;
	/** ��ͣ */
	public static final int DOWNLOAD_SPACE = 3;
	/** ���� */
	public static final int DOWNLOAD_GO = 4;
	/** ȡ�� */
	public static final int DOWNLOAD_CANCEL = 5;
	/** ���� */
	public static final int DOWNLOAD_ERROR = 6;
	/** ������� */
	public static final int DOWNLOAD_SUCCESS = 7;
	/** ���������� */
	public static final int DOWNLOAD_ERROR_SERVER = 8;

	public static final String STATUS = "Status";
	public static final String MSG = "Msg";
	public static final String KEY_IS_ADMIN = "isAdmin";

	/**�Խ�*/
	public static final String ACTION_TALK_BACK = "action_talk_back";
	/**¼��*/
	public static final String ACTION_RECORD = "action_record";
	/**APP����*/
	public static final String ACTION_LOGINOUT = "action_loginout";
	/**�豸����*/
	public static final String ACTION_DEVICEONOFF = "action_DeviceOnOff";
	/**�໤�˽��*/
	public static final String ACTION_GUARDIANDEL = "action_GuardianDel";
	/**����Աת��*/
	public static final String ACTION_MNSCHANGE = "action_Mngchange";
	/**��λ*/
	public static final String ACTION_LOCATION = "action_Location";
	/**����Χ�����ѱ�����SOS������*/
	public static final String ACTION_PUSH_MSG = "action_push_msg";

	/**��ǰ�豸id*/
	public static final String CUR_DEVICE_ID = "CUR_DEVICE_ID";

	/**��ֵ�ɹ���ˢ�±����������*/
	public static final String ACTION_UPDATE_BABYDETAIL = "action_update_babydetail";


	public static final String NAME_TALK_BACK = "name_talk_back";
	public static final String NAME_RECORD = "name_record";



}
