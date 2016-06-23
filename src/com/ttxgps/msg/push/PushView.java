package com.ttxgps.msg.push;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public abstract class PushView implements PushViewInterface {
	protected Activity mActivity;
	protected boolean mIsShowing;
	protected PushViewEventListener mListener;
	private final PushViewType mPushViewType;
	private WindowManager wmManager;

	protected abstract View onDismiss();

	protected abstract void onShow(String str);

	public PushView(Activity context, PushViewType type) {
		this.mIsShowing = false;
		this.mActivity = context;
		this.mPushViewType = type;
	}

	@Override
	public void show(String content, PushViewEventListener listener) {
		this.mListener = listener;
		if (this.wmManager == null) {
			onShow(content);
		}
	}

	protected void realityShow(View view) {
		this.wmManager = (WindowManager) this.mActivity.getSystemService("window");
		LayoutParams wmParams = new LayoutParams();
		wmParams.type = 99;
		wmParams.format = 1;
		wmParams.flags = 40;
		wmParams.gravity = 17;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = -1;
		wmParams.height = -1;
		this.wmManager.addView(view, wmParams);
	}

	@Override
	public void dismiss() {
		this.mIsShowing = false;
		if (onDismiss() != null) {
			this.wmManager.removeView(onDismiss());
		}
	}

	@Override
	public PushViewType getType() {
		return this.mPushViewType;
	}

	@Override
	public boolean isShowing() {
		return this.mIsShowing;
	}
}
