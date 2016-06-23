package com.ttxgps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ttxgps.gpslocation.MapAPP;

public class ReflushReceiver extends BroadcastReceiver {
	static int i = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (MapAPP.handler_list != null) {
			MapAPP.handler_list.sendEmptyMessage(1);
		}
		// String msg = intent.getStringExtra("msg");
		// Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
