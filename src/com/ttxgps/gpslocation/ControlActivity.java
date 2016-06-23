package com.ttxgps.gpslocation;

import java.util.LinkedList;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.xtst.gps.R;
import com.palmtrends.loadimage.Utils;
import com.ttxgps.entity.TeamMember;
import com.ttxgps.utils.WebService;
import com.ttxgps.utils.WebServiceProperty;

public class ControlActivity extends BaseActivity {

	private TeamMember item;
	private static int userID = LoginActivity.userID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		item = (TeamMember) getIntent().getSerializableExtra("item");
	}

	@Override
	protected void onResume() {
		super.onResume();
		userID = LoginActivity.userID;
	}


	public void things(View view) {
		String actionName = null;
		String cmd = null;

		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.location_btn:
			actionName = getString(R.string.renew_position);
			cmd = "LJDW";
			break;
		case R.id.lock_btn:
			actionName = getString(R.string.remote_fortify);
			cmd = "SF";
			break;
		case R.id.unlock_btn:
			actionName = getString(R.string.remote_disarm);
			cmd = "CF";
			break;
		case R.id.break_btn:
			actionName = getString(R.string.remote_cutoff);
			cmd = "DYD";
			break;
		case R.id.unbreak_btn:
			actionName = getString(R.string.remote_recover);
			cmd = "HFYD";
			break;
		case R.id.reset_btn:
			actionName = getString(R.string.renew_position);
			cmd = "CQSB";
			break;
		}

		if (actionName != null && cmd != null) {
			SendCommandToDevice(actionName, cmd);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void SendCommandToDevice(final String action, final String cmd) {
		new Thread() {
			@Override
			public void run() {
				String result = "";
				String failMsg = action + getString(R.string.failed);
				try {
					WebService webservice = new WebService(
							ControlActivity.this, "SendCmdToDevice");
					LinkedList<WebServiceProperty> linkedlist =
							new LinkedList<WebServiceProperty>();
					linkedlist.add(new WebServiceProperty("UserID", userID));
					linkedlist.add(new WebServiceProperty("DeviceID", Integer.parseInt(item.id)));
					linkedlist.add(new WebServiceProperty("Command", cmd));
					webservice.SetProperty(linkedlist);
					result = webservice.Get("SendCmdToDeviceResult","");

					JSONObject jsonObj = new JSONObject(result);
					if (jsonObj.has("success") && jsonObj.getBoolean("success")) {
						Utils.showToast(R.string.send_cmd_done);
					} else {
						/* TODO
						 * Error Code:
						 * -1: Command not supported; 1: Device has no SN; 2: Device is offline.
						 */
						Utils.showToast(failMsg);
					}
				} catch (Exception e) {
					Utils.showToast(failMsg);
					e.printStackTrace();
				} finally {
					Utils.dismissProcessDialog();
				}
			};
		}.start();
	}
}