package com.ttxgps.utils;

public class Urls {
	// www.gps1010.com:8080�� gps1010.com:8081��
	// www.gps9988.com:8081 www.ydlbs.net:8081
	public static final String mainurl = "http://112.74.130.65:8001";//"http://www.gps1010.com:8081";
	// www.gps1010.com:8081
	public static final String car_list = mainurl + "/team_bcs_load.do";
	/**��ȡ�ֻ���֤��*/
	public static final String reg_url_phone_code = mainurl + "/Other.asmx/SendNumToMobile";
	/**ע��*/
	public static final String reg_url = mainurl + "/Other.asmx/Regist";
	/**����豸*/
	public static final String add_device = mainurl + "/Other.asmx/DeviceAdd";
	/**��ȡ�豸�б�*/
	public static final String get_device_list = mainurl + "/OpenAPIV2.asmx/GetDeviceList";
	/**�豸������Ϣ�޸�*/
	public static final String edit_device_info = mainurl + "/Other.asmx/DeviceEdit";
	/**�豸������Ϣͷ���޸�*/
	public static final String edit_device_header = mainurl + "/UploadHeaderPic.ashx";
	/**��ȡ�豸������Ϣ*/
	public static final String get_device_info = mainurl + "/Other/GetDeviceDetail";
	/**��¼*/
	public static final String login = mainurl + "/OpenAPIV2.asmx/Login";
	/**�޸�����*/
	public static final String edit_pwd = mainurl + "/Other.asmx/PwdEdit";
	/**�޸��û�������Ϣ*/
	public static final String edit_user_info = mainurl + "/Other.asmx/InfoEdit";

	/**��ȡ�໤���б�*/
	public static final String get_guardian_list = mainurl + "/Other.asmx/GetUserList";
	/**�໤�����*/
	public static final String add_guardian = mainurl + "/Other.asmx/GuardianAdd";
	/**����໤�˰�*/
	public static final String delete_guardian = mainurl + "/Other.asmx/GuardianDel";
	/**����Աת��*/
	public static final String mng_change = mainurl + "/Other.asmx/MngChange";

	/**��ȡ�Ʋ���Ϣ*/
	public static final String get_runinfo = mainurl + "/Other.asmx/GetRunInfo";

	/**����,��ȡ�������*/
	public static final String setORget_family_list = mainurl + "/Other.asmx/GetOrSetFamilyList";
	/**���ã���ȡ����ʱ��*/
	public static final String setORget_stealth_time = mainurl + "/Other.asmx/GetOrSetStealthTime";

	/**���ã���ȡ������*/
	public static final String setORget_white_list = mainurl + "/Other.asmx/GetOrSetWhiteList";
	/**ժ����λģʽ�������ȡ*/
	public static final String locationMode_isoffwatch = mainurl + "/Other.asmx/GetOrSetModelIsOffWatch";

	/**¼��,���ֱ�Զ�̹ػ�������������  �·�ָ��*/
	public static final String send_device_cmd = mainurl + "/Other.asmx/SendCmdToDevice";

	/**��ȡ����Χ���б�*/
	public static final String get_geofence_list = mainurl + "/GetGeofenceList";
	/**���õ���Χ��*/
	public static final String set_geofence = mainurl + "/GeofenceListEdit";
	/**ɾ������Χ��*/
	public static final String delete_geofence = mainurl + "/GeofenceDel";
	/**��ӵ���Χ��*/
	public static final String add_geofence = mainurl + "/GeofenceListAdd";

	/**��ʷ�켣��Ϣ*/
	public static final String get_device_history = mainurl + "/GetDeviceHistory";

	/**��������*/
	public static final String sen_audio = mainurl + "/SendAudio.ashx";



	public static final String vip_bcs_addmember = mainurl
			+ "/vip_bcs_addmember.do";// ��Ա��ӽӿ�
	public static final String car_last_pos = mainurl
			+ "/team_bcs_loadUpdatedPos.do"; // Latest position

	public static final String car_position = mainurl
			+ "/Addr_bcs_Translate.do";
	public static final String car_lock = mainurl + "/car_bcs_shefang.do";
	public static final String car_unlock = mainurl + "/car_bcs_cefang.do";
	public static final String car_break = mainurl + "/car_bcs_dyd.do";
	public static final String car_unbreak = mainurl + "/car_bcs_hfyd.do";
	public static final String car_location = mainurl + "/car_bcs_call.do";

	public static final String car_history_load_baidu = mainurl +
			"/carhis_bcs_rolload.do?mt=baidu&delta=true&serviceid=";

	public static final String car_history_load_google = mainurl +
			"/carhis_bcs_rolload.do?mt=google&serviceid=";

	public static final String car_bcs_listen = mainurl + "/car_bcs_listen.do";
	public static final String car_bcs_photo = mainurl + "/car_bcs_photo.do";
	public static final String car_bcs_phonebook = mainurl
			+ "/car_bcs_phonebook.do";
	public static final String car_bcs_setsafedist = mainurl// ��ȫ��������
			+ "/car_bcs_setsafedist.do";
	public static final String car_bcs_sethousing = mainurl// �������
			+ "/car_bcs_sethousing.do";

	public static final String vip_bcs_delmember = mainurl
			+ "/vip_bcs_delmember.do";// ��Աɾ��
	public static final String vip_bcs_editmember = mainurl
			+ "/vip_bcs_editmember.do";// ��Ա�޸�
	public static final String vip_bcs_getmember = mainurl
			+ "/vip_bcs_getmember.do";// ��Ա��Ϣ��ȡ
	public static final String car_bcs_setalarm = mainurl
			+ "/car_bcs_setalarm.do";// ��ʱ��������
}
