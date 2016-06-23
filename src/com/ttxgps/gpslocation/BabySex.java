package com.ttxgps.gpslocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ttxgps.gpslocation.view.MultiLayoutRadioGroup;
import com.ttxgps.gpslocation.view.MultiLayoutRadioGroup.OnCheckedChangeListener;
import com.xtst.gps.R;
public class BabySex extends BaseActivity{
	private int sex_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_sex);
		initTitle();
		initView();
	}
	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.sex);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent data = new Intent();
				data.putExtra("sex", BabySex.this.sex_status);
				BabySex.this.setResult(667, data);
				BabySex.this.finish();
			}
		});
	}
	private void initView(){
		int sexId;
		this.sex_status = getIntent().getIntExtra("sex", 1);
		MultiLayoutRadioGroup sex_group = (MultiLayoutRadioGroup) findViewById(R.id.sex_group);
		sex_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(MultiLayoutRadioGroup group, int checkedId) {
				if (checkedId == R.id.man_rb) {
					BabySex.this.sex_status = 1;
				} else {
					BabySex.this.sex_status = 0;
				}
			}
		});
		if (this.sex_status == 1) {
			sexId = R.id.man_rb;
		} else {
			sexId = R.id.female_rb;
		}
		sex_group.check(sexId);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent data = new Intent();
			data.putExtra("sex", BabySex.this.sex_status);
			BabySex.this.setResult(667, data);
			BabySex.this.finish();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
