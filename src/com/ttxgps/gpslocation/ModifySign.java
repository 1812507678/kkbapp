package com.ttxgps.gpslocation;
import com.ttxgps.entity.User;
import com.xtst.gps.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ModifySign extends BaseActivity{
	EditText input_edt;
	private String text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_sign);
		initTitle();
		initView();
	}
	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.update_sign);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String signature = ModifySign.this.input_edt.getText().toString();
				if (!(TextUtils.isEmpty(signature) || signature.trim().equals(User.Signature))) {
					Intent data = new Intent();
					data.putExtra("key", "signature");
					data.putExtra("value", signature);
					ModifySign.this.setResult(1000, data);
				}
				ModifySign.this.finish();

			}
		});
	}

	private void initView(){
		this.input_edt = (EditText) findViewById(R.id.input_edt);
		this.text = getIntent().getStringExtra("text");
		this.input_edt.setText(text);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			String signature = ModifySign.this.input_edt.getText().toString();
			Intent data = new Intent();
			data.putExtra("key", "signature");
			data.putExtra("value", signature);
			ModifySign.this.setResult(1000, data);
			finish();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
