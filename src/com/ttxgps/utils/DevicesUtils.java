package com.ttxgps.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.transport.HttpResponseException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.bean.BabyInfoBean;
import com.ttxgps.bean.StealthTimeBean;
import com.ttxgps.entity.TeamMember;
import com.ttxgps.entity.Trace;
import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.LoginActivity;
import com.ttxgps.utils.WebServiceTask.WebServiceResult;
import com.xtst.gps.R;

public class DevicesUtils {

	//经纬度，换成地址
	public static void getAddrByLatLng(Context context,String Lat,String Lng,WebServiceResult wsr){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("Lat",Lat));
		linkedlist.add(new WebServiceProperty("Lng",Lng));
		linkedlist.add(new WebServiceProperty("MapType", "Baidu"));
		String language = "zh-cn";
		if (! LoginActivity.timeZone.equals("China Standard Time")) {
			language = "en-us";
		}
		linkedlist.add(new WebServiceProperty("Language", language));

		WebServiceTask wtask = new WebServiceTask("GetAddressByLatlng", linkedlist, WebService.URL_OPEN,context, wsr);
		wtask.execute("GetAddressByLatlngResult");

	}

	public static void sendCMDToDevice(Context context,String UserId,String DeviceID, String action,String content,WebServiceResult wsr){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("DeviceID", User.curBabys.getId()));
		linkedlist.add(new WebServiceProperty("UserId", User.id));
		linkedlist.add(new WebServiceProperty("Action", action));
		linkedlist.add(new WebServiceProperty("Content", content));

		WebServiceTask wsk = new WebServiceTask("SendToDevice", linkedlist, WebService.URL_OPEN,context,wsr);
		wsk.execute("SendToDeviceResult");
	}

	//取得 deviceID 位置信息
	public static	void  initGeoPoint(Context context,String deviceID,String mapType,WebServiceResult wsResult ) {
		// myTraces.clear();

		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();

		linkedlist.add(new WebServiceProperty("UserID", User.id));
		linkedlist.add(new WebServiceProperty("DeviceID", deviceID));
		linkedlist.add(new WebServiceProperty("MapType",mapType ));
		WebServiceTask wsk = new WebServiceTask("GetTrackingByUserID", linkedlist, WebService.URL_OPEN,context,wsResult);
		wsk.execute("GetTrackingByUserIDResult");


	}


	public static List<BabyInfoBean> getDevicesList(Context context){
		List<BabyInfoBean> list = null;
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();

		linkedlist.add(new WebServiceProperty("UserID", User.id));

		WebService webservice = new WebService(context, "GetDeviceList");
		webservice.SetProperty(linkedlist);
		try {
			String result = webservice.Get("GetDeviceListResult",WebService.URL_OTHER);
			//			JSONObject jsonObject = new JSONObject(result);

			list = parseJSON(result);
			Log.e("GetDeviceList", result);



		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}


	public static List<BabyInfoBean> parseJSON(String parseJson) throws Exception {
		/*
		if (pendingReceiver == null) {
			setTimer();
		}*/
		// items_map.clear();
		List<BabyInfoBean> teamList = new ArrayList<BabyInfoBean>();
		JSONObject json = new JSONObject(parseJson);
		if (json.has("Status") && (json.getInt("Status") == 0)) {


			JSONArray memberArray = json.getJSONArray("GuardianList");
			int count = memberArray.length();

			for (int i = 0; i < count; i++) {
				BabyInfoBean teamMember = new BabyInfoBean();
				JSONObject member = memberArray.getJSONObject(i);
				teamMember.setId(member.getString("DeviceID"));
				teamMember.setNickName(member.getString("DeviceNick"));
				teamMember.setIsAdmin(member.getBoolean("IsAdmin"));
				teamMember.setHeadIconPath(member.getString("URL"));
				teamMember.setPhoneNum(member.getString("Mob"));
				teamMember.setSex(member.optInt("Sex"));

				/*
				 * status:
				 * 0 LogOff; 1 Move; 2 Stop; 3 Offline; 4 Arrears
				 */
				if ("1".equals(teamMember.getStatus()) || "2".equals(teamMember.getStatus())) {
					//					online_members++;
				}

				teamList.add(teamMember);
			}


		} else {
			return null;
		}
		return teamList;
	}


	public static void getDevicePenceList(Context context,String deviceID,String timeZone,String mapType,WebServiceResult wsResult ){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("TimeZone", timeZone));
		linkedlist.add(new WebServiceProperty("DeviceID", deviceID));
		linkedlist.add(new WebServiceProperty("mapType",mapType));

		WebServiceTask wsk = new WebServiceTask("GetGeofence", linkedlist, WebService.URL_GEOFENCE,context,wsResult);
		wsk.execute("GetGeofenceResult");
	}

	public static void saveDevicePence(Context context,String UserID,String DeviceID,String GeofenceName,String Remark,double Lat,double Lng,int Radius,int GeofenceID,int TypeID,String mapType,WebServiceResult wsResult ){
		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("UserID", UserID));
		linkedlist.add(new WebServiceProperty("DeviceID", DeviceID));
		linkedlist.add(new WebServiceProperty("GeofenceName", GeofenceName));
		linkedlist.add(new WebServiceProperty("Remark", Remark));
		linkedlist.add(new WebServiceProperty("Lat", String.valueOf(Lat)));
		linkedlist.add(new WebServiceProperty("Lng", String.valueOf(Lng)));
		linkedlist.add(new WebServiceProperty("Radius", Radius));
		linkedlist.add(new WebServiceProperty("GeofenceID", GeofenceID));
		linkedlist.add(new WebServiceProperty("TypeID", TypeID));
		linkedlist.add(new WebServiceProperty("mapType",mapType));

		WebServiceTask wsk = new WebServiceTask("SaveGeofence", linkedlist, WebService.URL_GEOFENCE,context,wsResult);
		wsk.execute("SaveGeofenceResult");
	}


	public static void delFence(Context context,String deviceID,String geofenceID,WebServiceResult wsResult){

		LinkedList<WebServiceProperty> linkedlist = new LinkedList<WebServiceProperty>();
		linkedlist.add(new WebServiceProperty("GeofenceID", geofenceID));
		linkedlist.add(new WebServiceProperty("DeviceID", deviceID));


		WebServiceTask wsk = new WebServiceTask("DelGeofence", linkedlist, WebService.URL_GEOFENCE,context,wsResult);
		wsk.execute("DelGeofenceResult");

	}



	/**
	 *
	 * @param ID 根据id删除设备
	 */
	public static void deleteDevice(String ID){
		for (int i = 0; i < User.babyslist.size(); i++) {
			if(User.babyslist.get(i).getId().equals(ID)){
				User.babyslist.remove(i);
				break;
			}
		}
	}

	static String getBinaryStr(String num){ //10进度转2进度字串
		String b=Integer.toBinaryString(Integer.parseInt(num,10));
		for(int i=0;i<8;i++){
			if(b.length()<8)
				b="0"+b;
			else
				break;
		}
		return b;
	}

	static String binaryToStr(String bstr){  //2进度转10进度字串
		int num=Integer.parseInt(bstr,2);
		/*	String result= Integer.toHexString(num);
		if(result.length()%2==1)
			result="0"+result;
		return result;*/
		return String.valueOf(num);
	}

	static String decToHexStr(String bstr){  //10进度转16进度字串
		int num=Integer.parseInt(bstr,10);
		String result= Integer.toHexString(num);
		if(result.length()%2==1)
			result="0"+result;
		return result;
	}

	//取得隐身时间列表
	public static List<StealthTimeBean> strToBeanparseList(final String timesStr) {
		//TIME|00002359,0|00002359,1|00002359,10|00002359,10}
		List<StealthTimeBean> teamList = new ArrayList<StealthTimeBean>();

		if(timesStr!=null){
			//			timesStr.replace("time", "");
			String str=timesStr.replace("}", "");
			String[] slist = str.split("\\|");

			for (int i = 1; i < slist.length; i++) {
				StealthTimeBean tb = new StealthTimeBean();
				String[] tmp = slist[i].split("\\,");
				if(tmp.length==2){
					tb.setId(String.valueOf(i-1));
					//tb.setBegintime(String.format("%02d",Integer.parseInt(tmp[0].substring(0, 2),16))+":"+String.format("%02d",Integer.parseInt(tmp[0].substring(2, 4),16)));
					//tb.setEndtime(String.format("%02d",Integer.parseInt(tmp[0].substring(4, 6),16))+":"+String.format("%02d",Integer.parseInt(tmp[0].substring(6, 8),16)));
					tb.setBegintime(tmp[0].substring(0, 2)+":"+tmp[0].substring(2, 4));
					tb.setEndtime(tmp[0].substring(4, 6)+":"+tmp[0].substring(6, 8));
					String ss=getBinaryStr(tmp[1]);
					tb.setWeeks(ss.substring(0,7));
					tb.setOpenflag(ss.substring(7,8));
					teamList.add(tb);
				}
			}

		}

		return teamList;
	}


	//生成隐身时间字串
	public static String saveStealthTimeToStr(List<StealthTimeBean> list){
		StringBuilder result=new StringBuilder();
		if(list!=null){
			result.append("TIME");
			for(int i=0;i<list.size();i++){
				StealthTimeBean stb = list.get(i);
				result.append("|");
				String[] tSE=stb.getBegintime().split(":");
				result.append(tSE[0]+tSE[1]);
				tSE=stb.getEndtime().split(":");
				result.append(tSE[0]+tSE[1]);
				result.append(",");
				result.append(binaryToStr(stb.getWeeks()+stb.getOpenflag()));
			}
			//			result.append("}");
		}

		return result.toString();
	}


	public static boolean getcurDevice(Context context){
		String deviceID = User.GetUserShared(context, Constants.CUR_DEVICE_ID);
		if(TextUtils.isEmpty(deviceID))
			return false;
		for(int i=User.babyslist.size()-1;i>=0;i--){
			BabyInfoBean babyInfoBean = User.babyslist.get(i);
			if (babyInfoBean.getId().equals(deviceID)) {
				User.curBabys = babyInfoBean;
				return true;
			}
		}
		/*for(BabyInfoBean babyInfoBean : User.babyslist){
			if(babyInfoBean.getId().equals(deviceID)){
				User.curBabys = babyInfoBean;
				return true;
			}
		}*/
		return false;

	}



}
