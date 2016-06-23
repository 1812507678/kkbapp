package com.ttxgps.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.ttxgps.entity.User;
import com.ttxgps.gpslocation.TalkBackActivity;
import com.ttxgps.msg.MsgDBOper;
import com.ttxgps.talkback.TalkBackDBOper;

import android.content.Context;
import android.util.Log;

public class TalkBackBean implements Serializable{
	private static final long serialVersionUID = 1;
	private String command;
	private String date;
	private int duration;
	private String headPath;
	private String id;
	private boolean playing;
	private String senderId;
	private String deviceid;
	private int status;
	private Object tag;
	private int type;
	private int uploadstatus;
	private int soundtype; //1ÊÇ¶Ô½²2ÊÇÂ¼Òô
	private String url;
	private String userId;

	public TalkBackBean() {
		this.playing = false;
	}

	@Override
	public String toString() {
		return "id:" + this.id + "  deviceid:" + this.deviceid + "  date:" + this.date + "  duration:" + this.duration + "  url:" + this.url + "  status:" + this.status + "  userId:" + this.userId + "  headPath:" + this.headPath + "  senderId:" + this.senderId + "  type:" + this.type+ "  soundtype:" + this.soundtype + "  uploadstatus:" + this.uploadstatus;
	}
	public void parserContent(Context context, String content,int soundtype) {
		try {
			JSONObject msgObj = new JSONObject(content);
			this.headPath = msgObj.optString("HeaderPic");
			this.url = msgObj.optString("Url");
			this.senderId = msgObj.optString("SendUserId");
			this.deviceid = msgObj.optString("DeviceID");
			this.status = 1;
			this.type = TalkBackActivity.WHAT_SINGLE_ADD;
			this.duration = msgObj.optInt("SoundTime");
			this.date = msgObj.optString("DateTime");
			this.uploadstatus = 2;
			this.soundtype = soundtype;
			this.userId = User.id;
			TalkBackDBOper.getInstance(context).insert(this);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getdeviceid() {
		return this.deviceid;
	}

	public void setdeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHeadPath() {
		return this.headPath;
	}

	public void setHeadPath(String headPath) {
		this.headPath = headPath;
	}

	public String getSenderId() {
		return this.senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUploadstatus() {
		return this.uploadstatus;
	}

	public void setUploadstatus(int uploadstatus) {
		this.uploadstatus = uploadstatus;
	}

	public Object getTag() {
		return this.tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public boolean isPlaying() {
		return this.playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getSoundtype() {
		return this.soundtype;
	}

	public void setSoundtype(int type) {
		this.soundtype = type;
	}

}
