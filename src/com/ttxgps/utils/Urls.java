package com.ttxgps.utils;

public class Urls {
	// www.gps1010.com:8080， gps1010.com:8081，
	// www.gps9988.com:8081 www.ydlbs.net:8081
	public static final String mainurl = "http://112.74.130.65:8001";//"http://www.gps1010.com:8081";
	// www.gps1010.com:8081
	public static final String car_list = mainurl + "/team_bcs_load.do";
	/**获取手机验证码*/
	public static final String reg_url_phone_code = mainurl + "/Other.asmx/SendNumToMobile";
	/**注册*/
	public static final String reg_url = mainurl + "/Other.asmx/Regist";
	/**添加设备*/
	public static final String add_device = mainurl + "/Other.asmx/DeviceAdd";
	/**获取设备列表*/
	public static final String get_device_list = mainurl + "/OpenAPIV2.asmx/GetDeviceList";
	/**设备个人信息修改*/
	public static final String edit_device_info = mainurl + "/Other.asmx/DeviceEdit";
	/**设备个人信息头像修改*/
	public static final String edit_device_header = mainurl + "/UploadHeaderPic.ashx";
	/**获取设备个人信息*/
	public static final String get_device_info = mainurl + "/Other/GetDeviceDetail";
	/**登录*/
	public static final String login = mainurl + "/OpenAPIV2.asmx/Login";
	/**修改密码*/
	public static final String edit_pwd = mainurl + "/Other.asmx/PwdEdit";
	/**修改用户个人信息*/
	public static final String edit_user_info = mainurl + "/Other.asmx/InfoEdit";

	/**获取监护人列表*/
	public static final String get_guardian_list = mainurl + "/Other.asmx/GetUserList";
	/**监护人添加*/
	public static final String add_guardian = mainurl + "/Other.asmx/GuardianAdd";
	/**解除监护人绑定*/
	public static final String delete_guardian = mainurl + "/Other.asmx/GuardianDel";
	/**管理员转移*/
	public static final String mng_change = mainurl + "/Other.asmx/MngChange";

	/**读取计步信息*/
	public static final String get_runinfo = mainurl + "/Other.asmx/GetRunInfo";

	/**设置,读取亲情号码*/
	public static final String setORget_family_list = mainurl + "/Other.asmx/GetOrSetFamilyList";
	/**设置，读取隐身时段*/
	public static final String setORget_stealth_time = mainurl + "/Other.asmx/GetOrSetStealthTime";

	/**设置，读取白名单*/
	public static final String setORget_white_list = mainurl + "/Other.asmx/GetOrSetWhiteList";
	/**摘表，定位模式设置与读取*/
	public static final String locationMode_isoffwatch = mainurl + "/Other.asmx/GetOrSetModelIsOffWatch";

	/**录音,找手表，远程关机，聆听，驱蚊  下发指令*/
	public static final String send_device_cmd = mainurl + "/Other.asmx/SendCmdToDevice";

	/**获取电子围栏列表*/
	public static final String get_geofence_list = mainurl + "/GetGeofenceList";
	/**设置电子围栏*/
	public static final String set_geofence = mainurl + "/GeofenceListEdit";
	/**删除电子围栏*/
	public static final String delete_geofence = mainurl + "/GeofenceDel";
	/**添加电子围栏*/
	public static final String add_geofence = mainurl + "/GeofenceListAdd";

	/**历史轨迹信息*/
	public static final String get_device_history = mainurl + "/GetDeviceHistory";

	/**语音发送*/
	public static final String sen_audio = mainurl + "/SendAudio.ashx";



	public static final String vip_bcs_addmember = mainurl
			+ "/vip_bcs_addmember.do";// 成员添加接口
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
	public static final String car_bcs_setsafedist = mainurl// 安全距离设置
			+ "/car_bcs_setsafedist.do";
	public static final String car_bcs_sethousing = mainurl// 离家设置
			+ "/car_bcs_sethousing.do";

	public static final String vip_bcs_delmember = mainurl
			+ "/vip_bcs_delmember.do";// 成员删除
	public static final String vip_bcs_editmember = mainurl
			+ "/vip_bcs_editmember.do";// 成员修改
	public static final String vip_bcs_getmember = mainurl
			+ "/vip_bcs_getmember.do";// 成员信息获取
	public static final String car_bcs_setalarm = mainurl
			+ "/car_bcs_setalarm.do";// 定时提醒设置
}
