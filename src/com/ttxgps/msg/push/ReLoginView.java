package com.ttxgps.msg.push;

import com.xtst.gps.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ReLoginView extends PushView implements OnClickListener {
	private Button btnConfir;
	private View mRootView;

	public ReLoginView(Activity context, PushViewType type) {
		super(context, type);
	}

	@Override
	protected void onShow(String content) {
		if (this.mRootView == null) {
			this.mRootView = LayoutInflater.from(this.mActivity).inflate(R.layout.popup_login_again, null);
			this.btnConfir = (Button) this.mRootView.findViewById(R.id.confir_btn);
			this.btnConfir.setOnClickListener(this);
		}
		this.btnConfir.setText(R.string.ok);
		super.realityShow(this.mRootView);
	}

	@Override
	protected View onDismiss() {
		return this.mRootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confir_btn:
			if (this.mListener != null) {
				this.mListener.onListener(0);
			}
		default:
		}
	}
}
