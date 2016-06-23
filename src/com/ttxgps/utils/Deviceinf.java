package com.ttxgps.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Deviceinf {

	
	public static  String keyStr="JASONHEU";
	/**
	 * sim卡状态
	 * @param context
	 * @return
	 */
	public static int getSimState(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);//取得相关系统服务
		return tm.getSimState();
//		switch(tm.getSimState()){ //getSimState()取得sim的状态 有下面6中状态
//		case TelephonyManager.SIM_STATE_ABSENT :sb.append("无卡");break; 
//		case TelephonyManager.SIM_STATE_UNKNOWN :sb.append("未知状态");break;
//		case TelephonyManager.SIM_STATE_NETWORK_LOCKED :sb.append("需要NetworkPIN解锁");break;
//		case TelephonyManager.SIM_STATE_PIN_REQUIRED :sb.append("需要PIN解锁");break;
//		case TelephonyManager.SIM_STATE_PUK_REQUIRED :sb.append("需要PUK解锁");break;
//		case TelephonyManager.SIM_STATE_READY :sb.append("良好");break;
		}

	//TelephonyManager.PHONE_TYPE_NONE//PHONE_TYPE_GSM//PHONE_TYPE_CDMA//PHONE_TYPE_SIP 
	/**
	 * 网络类型
	 * @param context
	 * @return
	 */
	public static int getPhoneType(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	
		return tm.getPhoneType();
	}
	
	/**
	 * imei
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	
	/**
	 * imsi
	 * @param context
	 * @return
	 */
	public static String getSubscriberId(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}
	
	/**
	 * mobile
	 * @param context
	 * @return
	 */
	public static String getLine1Number(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
	
	/**
	 * 终端型号
	 * @param context
	 * @return
	 */
	public static String getModel(Context context){
		return android.os.Build.MODEL;
	}
	
	
	/**
	 * 网络类型
	 * @param context
	 * @return
	 */
	public static int getNetworkType(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkType();
	}
	
	

    /**
     * 检查网络连接环境
     * @param context
     * @return
     */
    public static String getAPNType(Context context) {
        String netType = null;
        boolean isAPN = false;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connMgr.getActiveNetworkInfo();
        if (networkinfo == null) {// 网络没连接
            isAPN = false;
//            Log.v("NOAPN", "------------->NOAPN");
            return netType;
        } else {
            int nType = networkinfo.getType();
            if (nType == ConnectivityManager.TYPE_MOBILE) {
                isAPN = true;
                netType =networkinfo.getExtraInfo().toUpperCase();
            }
            else if (nType == ConnectivityManager.TYPE_WIFI) {
                netType = "WIFI";
//                Log.v("wifi", "this is ----->WIFI");

            }
        }
        return netType;
    }

    /**
     *  检查网络是不是3G,
     * @param context
     * @return
     */
    public static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telemgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        switch (telemgr.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
                // case TelephonyManager.NETWORK_TYPE_EHRPD:
                // return true; // ~ 1-2 Mbps
                // case TelephonyManager.NETWORK_TYPE_EVDO_B:
                // return true; // ~ 5 Mbps
                // case TelephonyManager.NETWORK_TYPE_HSPAP:
                // return true; // ~ 10-20 Mbps
                // case TelephonyManager.NETWORK_TYPE_IDEN:
                // return false; // ~25 kbps
                // case TelephonyManager.NETWORK_TYPE_LTE:
                // return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
    
    
    public static void setSmsCenter(Context context,String smsc){
    	SharedPreferences ms = context.getSharedPreferences("mobile_pay_imsi_smsc", 0);
    	Editor edit =ms.edit();
    	edit.putString("smsc", smsc);
    		edit.commit();	
    }
    
    
    /**
     * 取得短信中心号码 
     * @param context
     * @return
     */
    public static String getSmsCenter(Context context,String imsi)
    {
    	SharedPreferences ms = context.getSharedPreferences("mobile_pay_imsi_smsc", 0);
    	Editor edit=null;
    	if(ms.contains("smsc")){
    		return ms.getString("smsc", null);
    	}
    	else{
    		 edit =ms.edit();
    		edit.clear();
    		
    	}
        String[] projection = new String[] {"service_center"};
        StringBuilder str=new StringBuilder();
        try{
     //获取所有短信，按时间倒序
        	ContentResolver csLoder = context.getContentResolver();	
        Cursor cur = csLoder.query(Uri.parse("content://sms/inbox"),
          projection,
          null, null , "date desc");
        if(cur!=null){
        	 String smscenter=null;
        	 Map<String, Integer> map = new HashMap<String, Integer>();
        	 int index=0;
             while(cur.moveToNext()) {
                   String smsc;
                   int smscColumn = cur.getColumnIndex("service_center");
                       smsc = cur.getString(smscColumn);
                       Integer reg = map.get(smsc);
                       map.put(smsc, reg == null ? 1 : reg + 1);
                       index++;
                       if(index>10){
                    	   break;
                       }
               }
             Integer max=0;
             Entry<String, Integer> maxet=null;
          for(Entry<String, Integer> et: map.entrySet()){
        	  if(et.getValue()>max){
        		  max=et.getValue();
        		  maxet = et;
        	  }
          }
          if(maxet!=null){
          smscenter=maxet.getKey();
          if(smscenter.startsWith("+86")){
        	  smscenter= smscenter.substring(3); 
          }
          edit.putString("smsc", smscenter);
          edit.commit();
          }
             cur.close();
             
             return smscenter;
        }
        return null;
       
        
        
       // return doCursor(myCursor);
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        }
        
        return null;
    }
    
    
    /**判断当前手机是否有ROOT权限
     * @return */ 
    public static boolean isRoot(){ 
    	boolean bool = false;
    	try{ 
    		if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())){
                bool=false ;   //否
            } else {
                bool=true;  //是
            }
        } catch (Exception e) {  } return bool;
    }

	
}
