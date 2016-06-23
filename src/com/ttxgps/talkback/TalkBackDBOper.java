package com.ttxgps.talkback;

import java.util.ArrayList;
import java.util.List;

import com.ttxgps.bean.TalkBackBean;
import com.ttxgps.entity.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TalkBackDBOper {
	public static final String DATE = "_date";
	public static final String DURATION = "_duration";
	public static final String HEADPATH = "_headpath";
	public static final String ID = "_id";
	public static final String SENDERID = "_senderid";
	public static final String DEVICEID = "_deviceid";
	public static final String STATUS = "_status";
	public static String TABLE_NAME = null;
	private static final String TAG = "TalkBackDBOper";
	public static final String TYPE = "_type";
	public static final String UPLOAD_STATUS = "_uploadstatus";
	public static final String URL = "_url";
	public static final String USERID = "_userid";
	public static final String SOUND_TYPE = "_soundtype";
	private static TalkBackDBOper dbOper;
	//	private User ack;
	private int curPage;
	private SQLiteDatabase m_db;
	private final TalkBackDBHelper m_dbHelper;
	private final int pageSize;
	private int totlePage;

	static {
		TABLE_NAME = "_talkback_table";
	}

	private TalkBackDBOper(Context context) {
		this.pageSize = 10;
		this.curPage = 1;
		this.m_dbHelper = new TalkBackDBHelper(context);
	}

	public static TalkBackDBOper getInstance(Context context) {
		if (dbOper == null) {
			dbOper = new TalkBackDBOper(context);
		}
		return dbOper;
	}

	private boolean isOpen() {
		if (this.m_db == null || this.m_dbHelper == null) {
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

	public synchronized ArrayList<TalkBackBean> queryByPage(int page, int type) {
		ArrayList<TalkBackBean> list;
		if (page > 0) {
			this.curPage = page;
		}
		list = new ArrayList();
		try {
			if (openDataBase()) {
				String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERID + " = '" + User.id + "' AND " + DEVICEID + " = '" + User.curBabys.getId() + "' AND " + SOUND_TYPE + " = '" + type + "'" + "ORDER BY " + DATE + " DESC" ;//+ " LIMIT " + this.pageSize + " OFFSET " + (this.pageSize * (this.curPage - 1));
				Log.e(TAG, "sql:" + sql);
				Cursor cursor = this.m_db.rawQuery(sql, null);
				int _count = cursor.getCount() / 10;
				int count;
				if (cursor.getCount() / 10 == 0) {
					count = _count;
				} else {
					count = _count + 1;
				}
				parseTalkback(cursor, list);
				if (cursor != null) {
					cursor.close();
				}
				closeDataBase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public synchronized int queryPageCount() {
		int count;
		count = 0;
		ArrayList<TalkBackBean> list = new ArrayList();
		try {
			if (openDataBase()) {
				Cursor cursor = this.m_db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERID + " = '" + User.id + "' AND " + DEVICEID + " = '" + User.curBabys.getId() + "'", null);
				int _count = cursor.getCount();
				if (_count / 10 == 0) {
					count = _count / 10;
				} else {
					count = (_count / 10) + 1;
				}
				parseTalkback(cursor, list);
				if (cursor != null) {
					cursor.close();
				}
				closeDataBase();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	private void parseTalkback(Cursor cursor, ArrayList<TalkBackBean> list) {
		if (cursor != null && list != null) {
			while (cursor.moveToNext()) {
				TalkBackBean bean = new TalkBackBean();
				bean.setId(cursor.getString(cursor.getColumnIndex(ID)));
				bean.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				bean.setDuration(cursor.getInt(cursor.getColumnIndex(DURATION)));
				bean.setHeadPath(cursor.getString(cursor.getColumnIndex(HEADPATH)));
				bean.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS)));
				bean.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
				bean.setdeviceid(cursor.getString(cursor.getColumnIndex(DEVICEID)));
				bean.setUserId(cursor.getString(cursor.getColumnIndex(USERID)));
				bean.setSenderId(cursor.getString(cursor.getColumnIndex(SENDERID)));
				bean.setUploadstatus(cursor.getInt(cursor.getColumnIndex(UPLOAD_STATUS)));
				bean.setType(cursor.getInt(cursor.getColumnIndex(TYPE)));
				bean.setSoundtype(cursor.getInt(cursor.getColumnIndex(SOUND_TYPE)));
				Log.e("locke", "qure:" + bean.toString());
				list.add(bean);
			}
			cursor.close();
		}
	}

	public synchronized void insertList(List<TalkBackBean> list) {
		if (!(!openDataBase() || this.m_db == null || list == null)) {
			this.m_db.beginTransaction();
			for (TalkBackBean bean : list) {
				Log.e("locke", "insertList:" + bean.toString());
				this.m_db.insert(TABLE_NAME, null, getValues(bean));
			}
			this.m_db.setTransactionSuccessful();
			this.m_db.endTransaction();
		}
	}

	public synchronized void insert(TalkBackBean bean) {
		if (openDataBase() && this.m_db != null) {
			try {
				Log.e("locke", "insert:" + bean.toString());
				Log.e("locke", "插入" + this.m_db.insert(TABLE_NAME, null, getValues(bean)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ContentValues getValues(TalkBackBean bean) {
		ContentValues values = new ContentValues();
		values.put(ID, bean.getId());
		values.put(DATE, bean.getDate());
		values.put(DURATION, Integer.valueOf(bean.getDuration()));
		values.put(HEADPATH, bean.getHeadPath());
		values.put(STATUS, Integer.valueOf(bean.getStatus()));
		values.put(URL, bean.getUrl());
		values.put(DEVICEID, bean.getdeviceid());
		values.put(USERID, bean.getUserId());
		values.put(SENDERID, bean.getSenderId());
		values.put(TYPE, Integer.valueOf(bean.getType()));
		values.put(UPLOAD_STATUS, Integer.valueOf(bean.getUploadstatus()));
		values.put(SOUND_TYPE, bean.getSoundtype());
		return values;
	}

	public synchronized int deleteItem(String url) {
		int ret;
		if (openDataBase()) {
			ret = this.m_db.delete(TABLE_NAME, "_url = '" + url + "'", null);
			closeDataBase();
		} else {
			ret = -1;
		}
		return ret;
	}

	public synchronized int delAll(int type) {
		int ret;
		if (openDataBase()) {
			ret = this.m_db.delete(TABLE_NAME, "_userid = '" + User.id + "' and " + DEVICEID + " = '" + User.curBabys.getId() + "' AND " + SOUND_TYPE + " = '" + type + "'", null);
			closeDataBase();
		} else {
			ret = -1;
		}
		return ret;
	}

	public synchronized int updateItem(String url) {
		int res;
		res = 0;
		if (openDataBase()) {
			ContentValues values = new ContentValues();
			values.put(STATUS, Integer.valueOf(0));
			res = this.m_db.update(TABLE_NAME, values, "_url = ?", new String[]{url});
			Log.i(TAG, "修改语音状态（受影响的行数）：res =" + res);
			closeDataBase();
		}
		return res;
	}

	public synchronized void updateItemByTalkBackBean(TalkBackBean bean) {
		Log.e("locke", "updateItemByTalkBackBean:" + bean.toString());
		if (openDataBase()) {
			bean.toString();
			Log.i(TAG, "修改语音状态（受影响的行数）：res =" + this.m_db.update(TABLE_NAME, getValues(bean), "_url=?", new String[]{bean.getUrl()}));
			closeDataBase();
		}
	}

}
