package com.ttxgps.record;

import java.io.File;

import android.os.Handler;
import android.os.HandlerThread;

import com.ttxgps.record.RecordTask.RecordTaskCallback;

public class Recorder {
	private static final int INTERVAL = 1000;
	private static Recorder instance;
	private int duration;
	private Handler mHandler;
	private RecordListener mListener;
	private RecordTask mRecordTask;
	private HandlerThread mThread;
	Runnable recodeRunnable;

	public Recorder() {
		this.duration = 0;
		this.recodeRunnable = new Runnable() {
			@Override
			public void run() {
				Recorder recorder = Recorder.this;
				recorder.duration = recorder.duration + 1;
				if (Recorder.this.duration <= 30) {
					RecordListener listener = Recorder.this.mListener;
					if (listener != null) {
						listener.onRecording(Recorder.this.duration);
					}
					Recorder.this.mHandler.postDelayed(this, 1000);
				}
			}
		};
		this.mThread = new HandlerThread("record_thread");
		this.mThread.start();
		this.mHandler = new Handler(this.mThread.getLooper());
	}

	public static synchronized Recorder getInstance() {
		Recorder recorder;
		synchronized (Recorder.class) {
			if (instance == null) {
				instance = new Recorder();
			}
			recorder = instance;
		}
		return recorder;
	}

	public void startRecord(RecordListener listener, File file, long startTime) {
		this.mListener = listener;
		this.duration = 0;
		if (this.mRecordTask != null) {
			this.mRecordTask.stopRecord();
			this.mRecordTask = null;
		}
		this.mRecordTask = new RecordTask(listener, file, startTime, new RecordTaskCallback() {
			@Override
			public void onStop(String filePath) {
				if (Recorder.this.mListener != null) {
					Recorder.this.mListener.onStop(filePath, Recorder.this.duration);
				}
			}

			@Override
			public void onStart() {
				if (Recorder.this.mListener != null) {
					Recorder.this.mListener.onStart();
				}
			}

			@Override
			public void onCancelRecord() {
				if (Recorder.this.mListener != null) {
					Recorder.this.mListener.onCancelRecord();
				}
			}
		});
		new Thread(this.mRecordTask).start();
		this.mHandler.postDelayed(this.recodeRunnable, 0);
	}

	public void stopRecord() {
		if (this.mRecordTask != null) {
			this.mHandler.removeCallbacks(this.recodeRunnable);
			this.mRecordTask.stopRecord();
		}
	}

	public void destroy() {
		if (this.mRecordTask != null && this.mRecordTask.recordState) {
			this.mRecordTask.stopRecord();
		}
		this.mThread.quit();
		this.mThread = null;
		this.mHandler = null;
		instance = null;
	}

}
