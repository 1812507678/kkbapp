package com.ttxgps.gpslocation;import com.xtst.gps.R;import android.content.Intent;import android.os.Bundle;import android.view.View;import android.view.View.OnClickListener;import android.widget.Button;import android.widget.EditText;import android.widget.TextView;public class AddDeviceActivity extends BaseActivity implements OnClickListener{	private EditText code;	private View scan_code;	private Button next_btn;	@Override	protected void onCreate(Bundle savedInstanceState) {		// TODO Auto-generated method stub		super.onCreate(savedInstanceState);		setContentView(R.layout.activity_add_device);		initTitle();		initView();	}	private void initView(){		/*code = (EditText)findViewById(R.id.imei_edt);		scan_code = findViewById(R.id.scan_code);		next_btn = (Button)findViewById(R.id.next_btn);*/		scan_code.setOnClickListener(this);		next_btn.setOnClickListener(this);	}	private void initTitle(){		((TextView) findViewById(R.id.title_tv)).setText("绑定设备");		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {			@Override			public void onClick(View arg0) {				// TODO Auto-generated method stub				finish();			}		});	}	@Override	public void onClick(View arg0) {		// TODO Auto-generated method stub		switch (arg0.getId()) {		/*case R.id.scan_code:			break;*/		case R.id.next_btn:			break;		default:			break;		}	}	@Override	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		// TODO Auto-generated method stub		super.onActivityResult(requestCode, resultCode, data);	}}