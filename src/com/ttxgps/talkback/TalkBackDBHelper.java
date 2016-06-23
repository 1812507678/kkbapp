package com.ttxgps.talkback;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TalkBackDBHelper  extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "talkback.db";
	private static final int DATABASE_VERSION = 3;

	public TalkBackDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TalkBackDBOper.TABLE_NAME + "(" + TalkBackDBOper.ID + " TEXT , " + TalkBackDBOper.USERID + " TEXT, " + TalkBackDBOper.SENDERID + " TEXT, " + TalkBackDBOper.URL + " TEXT, " + TalkBackDBOper.DEVICEID + " TEXT, " + TalkBackDBOper.DATE + " TEXT, " + TalkBackDBOper.DURATION + " INTEGER, " + TalkBackDBOper.HEADPATH + " TEXT, " + TalkBackDBOper.STATUS + " INTEGER," + TalkBackDBOper.TYPE + " INTEGER," + TalkBackDBOper.UPLOAD_STATUS+ " INTEGER," + TalkBackDBOper.SOUND_TYPE + " INTEGER " + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TalkBackDBOper.TABLE_NAME);
		onCreate(db);
	}

}
