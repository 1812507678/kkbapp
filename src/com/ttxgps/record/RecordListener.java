package com.ttxgps.record;

public interface RecordListener {
	void onCancelRecord();

	void onRecording(int i);

	void onStart();

	void onStop(String str, int i);

}
