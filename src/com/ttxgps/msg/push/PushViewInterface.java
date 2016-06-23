package com.ttxgps.msg.push;

public interface PushViewInterface {
	void dismiss();

	PushViewType getType();

	boolean isShowing();

	void show(String str, PushViewEventListener pushViewEventListener);
}
