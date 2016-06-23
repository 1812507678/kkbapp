package com.ttxgps.gpslocation;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.palmtrends.loadimage.Utils;
import com.utils.RegularUtils;
import com.xtst.gps.R;

public class BabyDetailUpdateActivity extends BaseActivity{

	private Button btnNext;
	private EditText edtInput;
	private int flag;
	private int maxLength;
	private String text;
	private TextView tvUnit;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_detail_update);
		initTitle();
		initView();
	}
	private void initTitle(){
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mBack();
			}
		});
	}
	private void initView(){
		this.edtInput = (EditText) findViewById(R.id.input_edt);
		this.btnNext = (Button) findViewById(R.id.next_btn);
		this.tvUnit = (TextView) findViewById(R.id.unit_tv);
		this.flag = getIntent().getIntExtra("flag", -1);
		this.text = getIntent().getStringExtra("text");
		this.maxLength = getIntent().getIntExtra("maxLength", 12);
		String title = "";
		String hint = "";
		switch (this.flag) {
		case 0 /*0*/:
			title = getString(R.string.update_nick_name);
			hint = getString(R.string.please_input_nickname);
			this.edtInput.setInputType(1);
			break;
		case 1 /*1*/:
			title = getString(R.string.update_height);
			hint = getString(R.string.please_input_height);
			this.edtInput.setInputType(2);
			this.tvUnit.setText(R.string.cm);
			this.tvUnit.setVisibility(View.VISIBLE);
			break;
		case 2/*2*/:
			title = getString(R.string.update_weight);
			hint = getString(R.string.please_input_weight);
			this.edtInput.setInputType(2);
			this.tvUnit.setText(R.string.kg);
			this.tvUnit.setVisibility(View.VISIBLE);
			break;
		case 3/*3*/:
			title = getString(R.string.update_watch_num);
			hint = getString(R.string.please_input_phone_num);
			this.edtInput.setInputType(3);
			break;
		}
		((TextView) findViewById(R.id.title_tv)).setText(title);
		this.edtInput.setHint(hint);
		this.edtInput.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(this.maxLength)});
		this.edtInput.setText(this.text);

	}

	private void mBack() {
		String value = this.edtInput.getText().toString().trim();
		if (this.flag == 3) {
			//改为设备类型字段，不需要手机号码正则表达式的验证
			/*if (value.length() > 0 && !RegularUtils.getPhone(value)) {
				Utils.showToast(R.string.input_correct_phone_num);
				return;
			}*/
		} else if (TextUtils.isEmpty(value)) {
			Utils.showToast(edtInput.getHint().toString());
			return;
		}
		Intent data = new Intent();
		data.putExtra("flag", this.flag);
		data.putExtra("value", value);
		setResult(666, data);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			mBack();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
