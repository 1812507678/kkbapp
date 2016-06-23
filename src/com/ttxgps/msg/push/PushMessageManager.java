package com.ttxgps.msg.push;

import android.app.Activity;
import android.util.Log;
import com.baidu.location.BDLocation;

public class PushMessageManager {
	private static /* synthetic */ int[] $SWITCH_TABLE$com$hy$hywatch$msg$push$PushViewType;
	private static PushMessageManager instance;
	private final String TAG;
	private PushViewInterface mPushView;

	static /* synthetic */ int[] $SWITCH_TABLE$com$hy$hywatch$msg$push$PushViewType() {
		int[] iArr = $SWITCH_TABLE$com$hy$hywatch$msg$push$PushViewType;
		if (iArr == null) {
			iArr = new int[PushViewType.values().length];
			try {
				iArr[PushViewType.OFFLINE.ordinal()] = 4;
			} catch (NoSuchFieldError e) {
			}
			try {
				iArr[PushViewType.RELOGIN.ordinal()] = 5;
			} catch (NoSuchFieldError e2) {
			}
			try {
				iArr[PushViewType.SETADMIN.ordinal()] = 2;
			} catch (NoSuchFieldError e3) {
			}
			try {
				iArr[PushViewType.SETGUARDIAN.ordinal()] = 1;
			} catch (NoSuchFieldError e4) {
			}
			try {
				iArr[PushViewType.UNBIND.ordinal()] = 3;
			} catch (NoSuchFieldError e5) {
			}
			$SWITCH_TABLE$com$hy$hywatch$msg$push$PushViewType = iArr;
		}
		return iArr;
	}

	private PushMessageManager() {
		this.TAG = "PushMessageManager";
	}

	public static PushMessageManager getInstance() {
		if (instance == null) {
			instance = new PushMessageManager();
		}
		return instance;
	}

	public void show(Activity activity, PushViewType type, PushViewEventListener listener) {
		show(activity, type, null, listener);
	}

	public void show(Activity activity, PushViewType type, String content, PushViewEventListener listener) {
		if (type != null && activity != null && !activity.isFinishing()) {
			if (this.mPushView != null) {
				if (this.mPushView.getType().getPriority() > type.getPriority()) {
					return;
				}
			}
			dismiss();
			switch ($SWITCH_TABLE$com$hy$hywatch$msg$push$PushViewType()[type.ordinal()]) {
			case 1 /*1*/:
				this.mPushView = new PushViewGuardian(activity, type);
				break;
			case 2 /*2*/:
				this.mPushView = new PushViewSetAdmin(activity, type);
				break;
			case 3 /*3*/:
				this.mPushView = new PushViewUnBind(activity, type);
				break;
			case 4 /*4*/:
				this.mPushView = new PushViewOffLine(activity, type);
				break;
			case 5 /*5*/:
				this.mPushView = new ReLoginView(activity, type);
				break;
			}
			if (this.mPushView != null) {
				this.mPushView.show(content, listener);
			} else {
				Log.e(this.TAG, "unknow type");
			}
		}
	}

	public void dismiss() {
		if (this.mPushView != null) {
			this.mPushView.dismiss();
			this.mPushView = null;
		}
	}
}
