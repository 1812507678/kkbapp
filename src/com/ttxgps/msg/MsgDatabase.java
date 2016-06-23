package com.ttxgps.msg;

import com.ttxgps.talkback.TalkBackDBOper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MsgDatabase  extends SQLiteOpenHelper{
	public static final String DB_NAME = "msg_db";
	public static int DB_VERSION;
	public static int m_refNums;
	public Context context;

	static {
		m_refNums = 0;
		DB_VERSION = 4;
	}

	public MsgDatabase(Context context, CursorFactory factory) {
		super(context, DB_NAME, factory, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS _msg_table (_id INTEGER PRIMARY KEY,_userid TEXT,_type TEXT,_time TEXT,_localmillis long,_msgtype TEXT,_msgcontent TEXT,_lat TEXT,_lng TEXT,_address TEXT,_deviceid TEXT,_isread INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS _msg_table");
		onCreate(db);
	}

	private void inseartPlugin(SQLiteDatabase db) {
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "1", "电子围栏消息1", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "1", "电子围栏消息2", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "1", "电子围栏消息3", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "2", "震动预警消息1", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "2", "震动预警消息2", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "3", "异常移位预警1", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "4", "电瓶断开预警1", 1));
		db.insert("_msg_table", null, getValues("12345", "2", "2014-12-31 15:20:11", "4", "电瓶断开预警2", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "5", "超速预警1", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "5", "超速预警2", 0));
		db.insert("_msg_table", null, getValues("1234", "2", "2014-12-31 15:20:11", "5", "超速预警3", 1));
	}

	private ContentValues getValues(String userid, String type, String time, String msgType, String msgContent, int isread) {
		ContentValues values = new ContentValues();
		values.put(TalkBackDBOper.USERID, userid);
		values.put(TalkBackDBOper.TYPE, type);
		values.put(MsgDBOper.TIME, time);
		values.put(MsgDBOper.MSGTYPE, msgType);
		values.put(MsgDBOper.MSGCONTENT, msgContent);
		values.put(MsgDBOper.LOCALMILLIS, Long.valueOf(System.currentTimeMillis()));
		values.put(MsgDBOper.ISREAD, Integer.valueOf(isread));
		return values;
	}

}
