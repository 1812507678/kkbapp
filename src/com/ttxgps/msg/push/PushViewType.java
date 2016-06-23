package com.ttxgps.msg.push;

public enum PushViewType {
	SETGUARDIAN(0),
	SETADMIN(0),
	UNBIND(0),
	OFFLINE(1),
	RELOGIN(2);

	private int mPriority;

	private PushViewType(int priority) {
		this.mPriority = priority;
	}

	public int getPriority() {
		return this.mPriority;
	}
}
