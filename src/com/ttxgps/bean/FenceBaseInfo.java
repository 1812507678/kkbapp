package com.ttxgps.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class FenceBaseInfo
implements Serializable
{
	private static final long serialVersionUID = 71212121971047L;

	public String endDate;
	public String endTime;
	public String startDate;
	public String startTime;
	public int fenceId;
	public int fenceType;
	public double latitude;
	public double longitude;
	public int radius=300;
	public String createTime;
	public String fenceName;
	public String remark;



	public FenceBaseInfo(){
		radius=300;
		SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd HH:mm:ss");
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
		createTime    =    formatter.format(curDate);
		fenceType=2;
		fenceName="";
		fenceId=0;

	}

	public static FenceBaseInfo getFenceByJson(String jstr,FenceBaseInfo fbi){
		JSONObject jo;
		try {
			jo = new JSONObject(jstr);

			if(fbi==null)
				fbi = new FenceBaseInfo();
			try {
				fbi.fenceId = jo.getInt("geofenceID");
				fbi.fenceName = jo.getString("fenceName");
				fbi.fenceType=jo.getInt("fenceType");
				fbi.latitude = jo.getDouble("latitude");
				fbi.longitude = jo.getDouble("longitude");
				fbi.remark = jo.getString("remark");
				fbi.radius = jo.getInt("radius");
				fbi.createTime = jo.getString("createTime");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		return fbi;
	}

	public static String FenceToJSONStr(FenceBaseInfo fbi){
		JSONObject jo = new JSONObject();

		try {
			jo.put("geofenceID", fbi.fenceId);
			jo.put("fenceName", fbi.fenceName);
			jo.put("fenceType", fbi.fenceType);
			jo.put("latitude", fbi.latitude);
			jo.put("longitude", fbi.longitude);
			jo.put("remark", fbi.remark);
			jo.put("radius", fbi.radius);
			jo.put("createTime", fbi.createTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jo.toString();
	}
}

