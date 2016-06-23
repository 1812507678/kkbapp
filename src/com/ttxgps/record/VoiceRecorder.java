package com.ttxgps.record;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;

public class VoiceRecorder {
	public static final int DEFULAT_RECORDMAXDURATION = 10000;
	static final String EXTENSION = ".amr";
	public static final int INVALID_FILE = -1011;
	static final String PREFIX = "voice";
	public static final int WHAT_STOP = 2;
	public static final int WHAT_UPDATE_MIC = 1;
	private File file;
	private final Handler handler;
	private boolean isRecording;
	Runnable mRunnable;
	private int recordMaxDuration;
	MediaRecorder recorder;
	private long startTime;

	public VoiceRecorder(Handler paramHandler) {
		this.recordMaxDuration = 0;
		this.isRecording = false;
		this.mRunnable = new Runnable() {
			@Override
			public void run() {
				VoiceRecorder.this.isRecording = true;
				Message msg = VoiceRecorder.this.handler.obtainMessage();
				msg.what = VoiceRecorder.WHAT_STOP;
				msg.obj = VoiceRecorder.this.getVoiceFilePath();
				msg.arg1 = VoiceRecorder.this.stopRecoding();
				VoiceRecorder.this.handler.sendMessage(msg);
			}
		};
		this.handler = paramHandler;
	}

	public String startRecording(File file, Context paramContext) {
		this.file = null;
		try {
			if (this.recorder != null) {
				this.recorder.release();
				this.recorder = null;
			}
			this.recordMaxDuration = DEFULAT_RECORDMAXDURATION;
			this.recorder = new MediaRecorder();
			this.recorder.setAudioSource(WHAT_UPDATE_MIC);
			this.recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			this.recorder.setAudioEncoder(WHAT_UPDATE_MIC);
			this.recorder.setAudioChannels(WHAT_UPDATE_MIC);
			this.recorder.setAudioSamplingRate(8000);
			this.recorder.setAudioEncodingBitRate(WHAT_UPDATE_MIC);
			this.file = file;
			this.recorder.setOutputFile(this.file.getAbsolutePath());
			this.recorder.prepare();
			this.isRecording = true;
			this.recorder.start();
			this.handler.removeCallbacks(this.mRunnable);
			this.handler.postDelayed(this.mRunnable, this.recordMaxDuration);
		} catch (IOException e) {
			Log.e(PREFIX, "prepare() failed");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (VoiceRecorder.this.isRecording) {
					try {
						Message msg = VoiceRecorder.this.handler.obtainMessage();
						msg.what = VoiceRecorder.WHAT_UPDATE_MIC;
						msg.arg1 = (VoiceRecorder.this.recorder.getMaxAmplitude() * 13) / 32767;
						VoiceRecorder.this.handler.sendMessage(msg);
						SystemClock.sleep(100);
					} catch (Exception localException) {
						Log.e(VoiceRecorder.PREFIX, localException.toString());
						return;
					}
				}
			}
		}).start();
		this.startTime = new Date().getTime();
		Log.d(PREFIX, "start voice recording to file:" + this.file.getAbsolutePath());
		if (this.file == null) {
			return null;
		}
		return this.file.getAbsolutePath();
	}

	public void discardRecording() {
		this.handler.removeCallbacks(this.mRunnable);
		if (this.recorder != null) {
			try {
				this.recorder.stop();
				this.recorder.release();
				this.recorder = null;
				if (!(this.file == null || !this.file.exists() || this.file.isDirectory())) {
					this.file.delete();
				}
			} catch (IllegalStateException e) {
			} catch (RuntimeException e2) {
			}
			this.isRecording = false;
		}
	}

	public int stopRecoding() {
		if (this.recorder == null) {
			return 0;
		}
		this.handler.removeCallbacks(this.mRunnable);
		this.recorder.stop();
		this.recorder.release();
		this.recorder = null;
		if (this.file != null && this.file.exists() && this.file.isFile() && this.file.length() == 0) {
			this.file.delete();
			return INVALID_FILE;
		}
		int i = ((int) (new Date().getTime() - this.startTime)) / 1000;
		Log.d(PREFIX, "voice recording finished. seconds:" + i + " file length:" + this.file.length());
		this.isRecording = false;
		return i;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (this.recorder != null) {
			this.recorder.release();
		}
	}

	public String getVoiceFileName(String paramString) {
		Time localTime = new Time();
		localTime.setToNow();
		return new StringBuilder(String.valueOf(paramString)).append(localTime.toString().substring(0, 15)).append(EXTENSION).toString();
	}

	public String getVoiceFilePath() {
		return this.file.getAbsolutePath();
	}

	public boolean isRecording() {
		return this.isRecording;
	}

}
