
package com.ttxgps.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.utils.Deviceinf;
import com.ttxgps.utils.DevicesUtils;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;
import com.ttxgps.utils.WebServiceTask;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.umeng.message.PushAgent;
import com.xtst.gps.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * �û���Ϣ
 */
public class User
{

	public static final String NAME = "ttxgps_UserInfo";

	private final Context mContext;
	public static List<BabyInfoBean> babyslist = new ArrayList<BabyInfoBean>();;
	public static BabyInfoBean curBabys;

	public User(Context context)
	{
		mContext = context;
	}

	/**
	 * ID
	 */
	public static String id;
	/**
	 * ��¼��
	 */
	public static String loginName = "";

	/**
	 * ����
	 */
	public static String LoginPwd = "";
	/**
	 * �ǳ�
	 */
	public static String niceName = "";
	/**
	 * �Ա�
	 */
	public static String sex = "0";
	/**
	 * ����
	 */
	public static String email = "";
	/**
	 * ͷ���ַ
	 */
	public static String headerurl;
	/**
	 * �ֻ�����
	 */
	public static String phone = "";
	/**
	 * ����ǩ��
	 */
	public static String Signature = "";
	/**
	 * ʱ��
	 */
	public static String timeZone = "";
	/**
	 * �豸��
	 */
	public static String SN = "";
	/**
	 * �豸ID
	 */
	public static String DeviceID ="";
	/**
	 * �Ƿ��¼
	 */
	public static boolean isLogin = false;

	/**
	 * �Ƿ񱣴�����
	 */
	public static boolean isSavepwd = false;
	/**
	 * �Ƿ����»�ȡ��ǰ�豸����
	 */
	public static boolean isGetBabyDdtail = false;

	/**
	 * �����û�����
	 */
	public static void SaveUserData(Context context)
	{
		SharedPreferences mySharedPreferences = context.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putBoolean("IsorNoLogin", isLogin);
		editor.putString("ID", id);
		editor.putString("DeviceID", DeviceID);
		editor.putString("UserName", niceName);
		editor.putString("SEX", sex);
		editor.putString("Email", email);
		editor.putString("LoginName",loginName);
		editor.putString("Signature", Signature);
		editor.putString("Phone", phone);
		editor.putString("Headerurl", headerurl);
		editor.putString("timeZone", timeZone);
		editor.putString("PassWord", LoginPwd);
		editor.putBoolean("IsSavePwd", isSavepwd);
		editor.commit();
	}
	/**
	 * �����û�����
	 */
	public static void SaveUserShared(Context context,String key,String value)
	{
		SharedPreferences mySharedPreferences = context.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	/**
	 * ��ȡ����
	 */
	public static String GetUserShared(Context context,String key)
	{
		SharedPreferences sp = context
				.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	/**
	 * �����û�����
	 */
	public static void SaveUserSharedBoolean(Context context,String key,boolean value)
	{
		SharedPreferences mySharedPreferences = context.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void putUser(User user)
	{
		SharedPreferences settings = mContext.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		Editor edit = settings.edit();
		edit.putString("ID", user.id);
		edit.putString("DeviceID", DeviceID);
		edit.putString("LoginName", user.loginName);
		edit.putString("UserName", user.niceName);
		edit.putString("SEX", user.sex);
		edit.putString("Signature", user.Signature);
		edit.putString("Email", user.email);
		edit.putBoolean("IsorNoLogin", user.isLogin);
		edit.putString("Phone", user.phone);
		edit.putString("Headerurl", user.headerurl);
		edit.putString("timeZone", timeZone);
		edit.putString("PassWord", user.LoginPwd);
		edit.putBoolean("IsSavePwd", user.isSavepwd);
		edit.commit();
	}

	/**
	 * ��ȡ�û�����
	 */
	public static void GetUserData(Context context)
	{
		SharedPreferences sp = context
				.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
		isLogin = sp.getBoolean("IsorNoLogin", false);
		id = sp.getString("ID", "0");
		DeviceID =sp.getString("DeviceID", "-1");
		loginName = sp.getString("LoginName", "");
		LoginPwd = sp.getString("PassWord", "");
		niceName = sp.getString("UserName", "");
		sex = sp.getString("SEX", "");
		Signature = sp.getString("Signature", "");
		email = sp.getString("Email", "");
		phone = sp.getString("Phone", "");
		headerurl = sp.getString("Headerurl", "");
		timeZone = sp.getString("timeZone", "");
		isSavepwd = sp.getBoolean("IsSavePwd", false);

	}

	// ����û���Ϣ
	public static void CleanUserData(Context context)
	{
		try
		{
			PushAgent.getInstance(context).removeAlias( User.id,"userid");
			if(User.phone!=""){
				PushAgent.getInstance(context).removeAlias( User.phone,"phone");
			}
			String imei =  Deviceinf.getDeviceId(context);
			String imsi = Deviceinf.getSubscriberId(context);
			if(imei!=null)
				PushAgent.getInstance(context).removeAlias(imei,"imei");
			if(imsi!=null)
				PushAgent.getInstance(context).removeAlias(imsi,"imsi" );


			SharedPreferences mySharedPreferences = context.getSharedPreferences(NAME,
					Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			editor.clear();
			editor.commit();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//�����������
	public static void CleanUMData(final Context context){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					PushAgent.getInstance(context).removeAlias( User.id,"userid");
					if(User.phone!=""){
						PushAgent.getInstance(context).removeAlias( User.phone,"phone");
					}
					String imei =  Deviceinf.getDeviceId(context);
					String imsi = Deviceinf.getSubscriberId(context);
					if(imei!=null)
						PushAgent.getInstance(context).removeAlias(imei,"imei");
					if(imsi!=null)
						PushAgent.getInstance(context).removeAlias(imsi,"imsi" );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	//��ȡ��ǰ�û��Ƿ�ѡ�񱣴�����
	public static boolean getIsSavePwd(Context context)
	{
		SharedPreferences sp = context
				.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
		return sp.getBoolean("IsSavePwd", false);
	}

	//��ȡ��ǰ�û�����
	public static String getPwd(Context context)
	{
		SharedPreferences sp = context
				.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
		return sp.getString("PassWord", "");
	}

	//��ȡ��ǰ�û���
	public static String getLoginName(Context context)
	{
		SharedPreferences sp = context
				.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
		return sp.getString("LoginName", "");
	}

	//��ȡ��ǰ�û�id
	public static String getUserId(Context context)
	{
		SharedPreferences sp = context
				.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
		return sp.getString("ID", "0");
	}

	//��ȡ��ǰ�û�����
	public static boolean getIsNoLogin(Context context)
	{
		SharedPreferences sp = context
				.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
		return sp.getBoolean("IsorNoLogin", false);
	}

	public static Boolean getDevicesList(Context context){
		List<BabyInfoBean> list= DevicesUtils.getDevicesList(context);
		if(list!=null){
			babyslist=list;
			return true;
		}
		else{
			return	false;
		}

	}

}
