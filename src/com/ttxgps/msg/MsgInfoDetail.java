package com.ttxgps.msg;

import org.json.JSONException;
import org.json.JSONObject;

import com.ttxgps.utils.Constants;

import android.content.Context;
import android.util.Log;

public class MsgInfoDetail extends MsgInfo{
	public String address;
	public String date;
	public int flag;
	public String gpsTime;
	public int id;
	public int isRead;
	public String lat;
	public int lineFlag;
	public String lng;
	public String locTime;
	public long locaTimeMillis;
	public String msgContent;
	public String msgType;
	public String deviceid;

	@Override
	public String toString() {
		return "MsgInfoDetail [id=" + this.id + ", msgContent=" + this.msgContent + ", msgType=" + this.msgType + ", lat=" + this.lat + ", lng=" + this.lng + ", address=" + this.address + ", isRead=" + this.isRead + ", locaTimeMillis=" + this.locaTimeMillis + ", deviceid=" + this.deviceid+ ", gpsTime=" + this.gpsTime + "]";
	}

	public void parserContent(Context context, String content) {
		//		this.content = msg.content;
		//		this.type = msg.type;
		//		this.time = msg.time;
		try {
			JSONObject msgObj = new JSONObject(content);
			this.msgContent = msgObj.optString("Msg");
			this.msgType = msgObj.optString("Type");
			this.lat = msgObj.optString(Constants.GETLOCATION?"Lat":"BLat");
			this.lng = msgObj.optString(Constants.GETLOCATION?"Lon":"BLon");
			this.address = msgObj.optString("Address");
			this.gpsTime = msgObj.optString("DateTime");
			this.deviceid = msgObj.optString("DeviceID");
			this.locaTimeMillis = System.currentTimeMillis();
			Log.e("locke", "≤Â»Î ˝æ›£∫" + MsgDBOper.getInstance(context).insertMsg(this));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
