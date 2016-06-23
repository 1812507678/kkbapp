package com.ttxgps.msg;

import java.util.ArrayList;

import com.ttxgps.entity.User;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class MsgDBOper {
	public static final String ADDRESS = "_address";
	public static final String ID = "_id";
	public static final String ISREAD = "_isread";
	public static final String LAT = "_lat";
	public static final String LNG = "_lng";
	public static final String LOCALMILLIS = "_localmillis";
	public static final String MSGCONTENT = "_msgcontent";
	public static final String MSGTYPE = "_msgtype";
	public static final String TABLE_NAME = "_msg_table";
	public static final String TIME = "_time";
	public static final String TYPE = "_type";
	public static final String USERID = "_userid";
	public static final String DEVICEID = "_deviceid";
	private static MsgDBOper dbOper;
	private final String[] column;
	private final String[] column_msgtype;
	private final Context m_context;
	private SQLiteDatabase m_db;
	private final MsgDatabase m_dbHelper;
	//	private String userId;

	static {
		dbOper = null;
	}

	//	public void setUserId(String userId) {
	//		this.userId = userId;
	//	}

	public static MsgDBOper getInstance(Context context) {
		if (dbOper == null) {
			dbOper = new MsgDBOper(context);
		}
		return dbOper;
	}

	private MsgDBOper(Context context) {
		this.column = new String[]{ID, TYPE, TIME, MSGTYPE, MSGCONTENT, LAT, LNG, ADDRESS, LOCALMILLIS, ISREAD,DEVICEID};
		this.column_msgtype = new String[]{MSGTYPE, "count(_msgtype)", MSGCONTENT, LOCALMILLIS};
		this.m_context = context;
		this.m_dbHelper = new MsgDatabase(this.m_context, null);
	}

	public boolean isOpen() {
		if (this.m_dbHelper == null || this.m_db == null) {
			return false;
		}
		return this.m_db.isOpen();
	}

	public synchronized boolean openDataBase() {
		if (!isOpen()) {
			this.m_db = this.m_dbHelper.getWritableDatabase();
		}
		return isOpen();
	}

	public void closeDataBase() {
		if (this.m_db != null && this.m_db.isOpen()) {
			this.m_db.close();
		}
	}

	public synchronized ArrayList<MsgInfoDetail> queryAllMsg(String userId,String deviceId) {
		ArrayList<MsgInfoDetail> result;
		result = new ArrayList();
		if (openDataBase()) {
			Cursor cursor = this.m_db.query(TABLE_NAME, this.column, "_userid =? and _deviceid=? or _deviceid=?", new String[]{userId,deviceId,""}, null, null, "_time desc");
			parseMsgInfo(cursor, result);
			if (cursor != null) {
				cursor.close();
			}
			closeDataBase();
		}
		return result;
	}

	public synchronized ArrayList<MsgInfoDetail> queryMsgByType(String userid,String deviceId, String msgType) {
		ArrayList<MsgInfoDetail> result;
		result = new ArrayList();
		if (openDataBase()) {
			Cursor cursor = this.m_db.query(TABLE_NAME, this.column, "_deviceid =? or _userid =? and _deviceid=? and _type=? and _msgtype in (" + msgType + ")", new String[]{"",userid,deviceId, "0"}, null, null, "_time desc");
			parseMsgInfo(cursor, result);
			if (cursor != null) {
				cursor.close();
			}
			closeDataBase();
		}
		return result;
	}

	public synchronized ArrayList<MsgGroupInfo> queryMsgGroupbyMsgType(String userid) {
		ArrayList<MsgGroupInfo> result;
		result = new ArrayList();
		if (openDataBase()) {
			Cursor cursor = this.m_db.query(TABLE_NAME, new String[]{MSGTYPE, MSGCONTENT, LOCALMILLIS}, "_type=? and _userid=?", new String[]{"2",userid}, MSGTYPE, null, TIME);
			parseGroupInfo(cursor, result, 1);
			if (cursor != null) {
				cursor.close();
			}
			closeDataBase();
		}
		return result;
	}

	public synchronized ArrayList<MsgGroupInfo> queryGroupbyMsgTypeOfNoreadMsg(String userid) {
		ArrayList<MsgGroupInfo> result;
		result = new ArrayList();
		if (openDataBase()) {
			Cursor cursor = this.m_db.query(TABLE_NAME, this.column_msgtype, "_isread=? and _userid=? and _type=?", new String[]{"0", userid, "2"}, MSGTYPE, null, null);
			parseGroupInfo(cursor, result, 2);
			if (cursor != null) {
				cursor.close();
			}
			closeDataBase();
		}
		return result;
	}

	private void parseGroupInfo(Cursor cursor, ArrayList<MsgGroupInfo> result, int flag) {
		if (cursor != null && result != null) {
			while (cursor.moveToNext()) {
				MsgGroupInfo info = new MsgGroupInfo();
				if (flag == 1) {
					info.msgType = cursor.getString(0);
					info.lastMsg = cursor.getString(1);
					info.timeMillis = cursor.getLong(2);
				} else {
					info.msgType = cursor.getString(0);
					info.msgCount = cursor.getInt(1);
					info.lastMsg = cursor.getString(2);
					info.timeMillis = cursor.getLong(3);
				}
				if (!TextUtils.isEmpty(info.msgType)) {
					result.add(info);
				}
			}
			cursor.close();
		}
	}

	private void parseMsgInfo(Cursor cursor, ArrayList<MsgInfoDetail> list) {
		if (cursor != null && list != null) {
			while (cursor.moveToNext()) {
				MsgInfoDetail info = new MsgInfoDetail();
				info.id = cursor.getInt(cursor.getColumnIndex(ID));
				info.type = cursor.getInt(cursor.getColumnIndex(TYPE));
				info.time = cursor.getString(cursor.getColumnIndex(TIME));
				info.msgType = cursor.getString(cursor.getColumnIndex(MSGTYPE));
				info.msgContent = cursor.getString(cursor.getColumnIndex(MSGCONTENT));
				info.lat = cursor.getString(cursor.getColumnIndex(LAT));
				info.lng = cursor.getString(cursor.getColumnIndex(LNG));
				info.address = cursor.getString(cursor.getColumnIndex(ADDRESS));
				info.locaTimeMillis = cursor.getLong(cursor.getColumnIndex(LOCALMILLIS));
				info.isRead = cursor.getInt(cursor.getColumnIndex(ISREAD));
				info.deviceid = cursor.getString(cursor.getColumnIndex(DEVICEID));
				list.add(info);
			}
			cursor.close();
		}
	}

	public synchronized int insertMsg(MsgInfoDetail info) {
		int i = -1;
		synchronized (this) {
			if (openDataBase()) {
				if (!(this.m_db == null || info == null)) {
					ContentValues values = new ContentValues();
					values.put(TYPE, Integer.valueOf(info.type));
					values.put(TIME, info.gpsTime);
					values.put(MSGTYPE, info.msgType);
					values.put(MSGCONTENT, info.msgContent);
					values.put(LAT, info.lat);
					values.put(LNG, info.lng);
					values.put(ADDRESS, info.address);
					values.put(LOCALMILLIS, Long.valueOf(info.locaTimeMillis));
					values.put(USERID, User.id);
					values.put(DEVICEID, info.deviceid);
					values.put(ISREAD, Integer.valueOf(0));
					i = (int) this.m_db.insert(TABLE_NAME, null, values);
				}
			}
		}
		return i;
	}

	public synchronized int updataMsg(int id, int isRead) {
		int res;
		if (openDataBase()) {
			ContentValues values = new ContentValues();
			values.put(ISREAD, Integer.valueOf(isRead));
			res = 0;
			if (values.size() > 0) {
				res = this.m_db.update(TABLE_NAME, values, "_id = " + id, null);
			}
			closeDataBase();
		} else {
			res = -1;
		}
		return res;
	}

	public synchronized int updataAddress(int id, String addr) {
		int res;
		if (openDataBase()) {
			ContentValues values = new ContentValues();
			values.put(ADDRESS, addr);
			res = 0;
			if (values.size() > 0) {
				res = this.m_db.update(TABLE_NAME, values, "_id = " + id, null);
			}
			closeDataBase();
		} else {
			res = -1;
		}
		return res;
	}

	public synchronized int deleteByID(int id) {
		int ret;
		if (openDataBase()) {
			ret = this.m_db.delete(TABLE_NAME, "_id = " + id, null);
			closeDataBase();
		} else {
			ret = -1;
		}
		return ret;
	}

	public synchronized int deleteByType(String type) {
		int count;
		if (openDataBase()) {
			count = this.m_db.delete(TABLE_NAME, "_msgtype in (" + type + ")", null);
			closeDataBase();
		} else {
			count = -1;
		}
		return count;
	}


}
