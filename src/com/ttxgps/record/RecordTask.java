package com.ttxgps.record;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

public class RecordTask  implements Runnable {
	private boolean canRecord;
	boolean isCancel;
	private final RecordTaskCallback mCallBack;
	private final File mRecordFile;
	private final MediaRecorder mRecorder;
	public boolean recordState;
	long time;

	protected interface RecordTaskCallback {
		void onCancelRecord();

		void onStart();

		void onStop(String str);
	}

	public RecordTask(RecordListener listener, File file, long startTime, RecordTaskCallback callback) {
		this.canRecord = true;
		this.time = 0;
		this.isCancel = false;
		this.mRecordFile = file;
		this.time = startTime;
		this.isCancel = false;
		this.mCallBack = callback;
		if (this.mRecorder != null) {
			this.mRecorder.release();
		}
		this.mRecorder = new MediaRecorder();
		this.mRecorder.setAudioSource(1);
		this.mRecorder.setOutputFormat(3);
		this.mRecorder.setAudioEncoder(1);
		this.mRecorder.setAudioEncodingBitRate(1);
	}

	@Override
	public void run() {
		if (this.mCallBack != null) {
			this.mCallBack.onStart();
		}
		this.recordState = true;
		this.mRecorder.setOutputFile(this.mRecordFile.getAbsolutePath());
		try {
			this.mRecordFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.mRecorder.prepare();
		} catch (IllegalStateException e2) {
			e2.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e4) {
			e4.printStackTrace();
		}
		Log.e("locke", "===>" + (System.currentTimeMillis() - this.time));
		if (this.isCancel) {
			this.mCallBack.onCancelRecord();
			return;
		}
		try {
			this.mRecorder.start();
			do {
			} while (this.canRecord);
			if (this.mRecorder != null) {
				this.mRecorder.stop();
				this.mRecorder.release();
				if (this.mCallBack != null) {
					this.mCallBack.onStop(this.mRecordFile.getAbsolutePath());
				}
			}
		} catch (Exception e5) {
		}
	}

	public synchronized void stopRecord() {
		Log.e("locke", "-->" + (System.currentTimeMillis() - this.time));
		if (System.currentTimeMillis() - this.time < 800) {
			this.isCancel = true;
		}
		this.canRecord = false;
	}

}
