package com.ttxgps.gpslocation;
  
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.palmtrends.loadimage.Utils;
import com.ttxgps.gpslocation.view.MultiLayoutRadioGroup;
import com.ttxgps.gpslocation.view.MultiLayoutRadioGroup.OnCheckedChangeListener;
import com.xtst.gps.R;

public class BabyRelationActivity extends BaseActivity{

	private EditText edtOther;
	private String relation;
	private MultiLayoutRadioGroup relation_group;
	private View vOther;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_relation);
		initTitle();
		initView();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText(R.string.update_relation);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String name = BabyRelationActivity.this.edtOther.getText().toString().trim();
				Intent data = new Intent();
				if (BabyRelationActivity.this.relation_group.getCheckedRadioButtonId() != R.id.other_rb) {
					data.putExtra("relation", BabyRelationActivity.this.relation);
				} else if (TextUtils.isEmpty(name)) {
					Utils.showToast(R.string.please_affirm_relation);
					return;
				} else {
					data.putExtra("relation", name);
				}
				BabyRelationActivity.this.setResult(668, data);
				BabyRelationActivity.this.finish();
			}

		});
	}
	private void initView(){
		int relationId;
		this.relation = getIntent().getStringExtra("relation");
		final String mather_relation = getString(R.string.mather);
		final String father_relation = getString(R.string.father);
		this.edtOther = (EditText) findViewById(R.id.other_edt);
		this.vOther = findViewById(R.id.other_img);
		this.relation_group = (MultiLayoutRadioGroup) findViewById(R.id.relation_group);
		this.relation_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MultiLayoutRadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.mather_rb) {
					BabyRelationActivity.this.relation = mather_relation;
				} else if (checkedId == R.id.father_rb) {
					BabyRelationActivity.this.relation = father_relation;
				} else {
					BabyRelationActivity.this.relation = BabyRelationActivity.this.edtOther.getText().toString();
				}
				BabyRelationActivity.this.changeStatus(checkedId);
			}
		});
		if (mather_relation.equals(this.relation)) {
			relationId = R.id.mather_rb;
		} else if (father_relation.equals(this.relation)) {
			relationId = R.id.father_rb;
		} else {
			relationId = R.id.other_rb;
			this.edtOther.setText(this.relation);
		}
		this.relation_group.check(relationId);

	}

	private void changeStatus(int id) {
		switch (id) {
		case R.id.father_rb:
		case R.id.mather_rb:
			this.edtOther.setVisibility(View.GONE);
			this.vOther.setVisibility(View.GONE);
			break;
		case R.id.other_rb:
			this.edtOther.setVisibility(View.VISIBLE);
			this.vOther.setVisibility(View.VISIBLE);
			break;
		default:
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			String name = BabyRelationActivity.this.edtOther.getText().toString().trim();
			Intent data = new Intent();
			if (BabyRelationActivity.this.relation_group.getCheckedRadioButtonId() != R.id.other_rb) {
				data.putExtra("relation", BabyRelationActivity.this.relation);
			} else if (TextUtils.isEmpty(name)) {
				Utils.showToast(R.string.please_affirm_relation);
				return false;
			} else {
				data.putExtra("relation", name);
			}
			BabyRelationActivity.this.setResult(668, data);
			BabyRelationActivity.this.finish();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
