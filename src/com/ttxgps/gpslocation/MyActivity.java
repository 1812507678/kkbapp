package com.ttxgps.gpslocation;
  
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtst.gps.R;

public class MyActivity extends BaseActivity implements OnClickListener{

	private ImageView IvHasNew;
	private TextView curVersionNameTv;
	private ImageView headIconIv;
	private TextView nickNameTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		//		initTitle();
		initView();
	}

	private void initTitle(){
		((TextView) findViewById(R.id.title_tv)).setText("ÎÒµÄ");
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView(){
		IvHasNew = (ImageView)findViewById(R.id.head_icon_iv);
		curVersionNameTv = (TextView)findViewById(R.id.cur_versionName_tv);
		nickNameTv = (TextView)findViewById(R.id.nick_name_tv);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.terminal_ll:

			break;
		case R.id.account_ll:

			break;
		case R.id.updPwd_ll:

			break;
		case R.id.checkUp_ll:

			break;
		case R.id.feedback_ll:

			break;
		case R.id.exit_ll:

			break;

		default:
			break;
		}

	}

}
