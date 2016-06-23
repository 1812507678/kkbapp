package com.ttxgps.msg.push;

import com.xtst.gps.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PushViewUnBind extends PushView implements OnClickListener {
	private Button btnCancel;
	private Button btnConfir;
	private View mRootView;
	private TextView tvMsg;

	public PushViewUnBind(Activity context, PushViewType type) {
		super(context, type);
	}

	@Override
	protected void onShow(String content) {
		if (this.mRootView == null) {
			this.mRootView = LayoutInflater.from(this.mActivity).inflate(R.layout.activity_push_msg, null);
			this.tvMsg = (TextView) this.mRootView.findViewById(R.id.message_tv);
			this.btnConfir = (Button) this.mRootView.findViewById(R.id.confir_btn);
			this.btnCancel = (Button) this.mRootView.findViewById(R.id.cancel_btn);
			this.btnConfir.setOnClickListener(this);
			this.btnCancel.setOnClickListener(this);
		}
		this.tvMsg.setText(content);
		this.btnConfir.setText(R.string.ok);
		this.btnCancel.setText(R.string.cancel);
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
		case R.id.cancel_btn:
			if (this.mListener != null) {
				this.mListener.onListener(1);
			}
		default:
		}
	}
}
