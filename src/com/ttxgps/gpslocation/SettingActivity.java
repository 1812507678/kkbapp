package com.ttxgps.gpslocation;
  
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.xtst.gps.R;
import com.ttxgps.utils.PrefHelper;
import com.palmtrends.loadimage.Utils;


public class SettingActivity extends BaseActivity {
	static TextView curMapView;
	static int mapIndex = 0;

	private static String[] mapList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		// mapList = { // Incorrect?!
		mapList = new String[] {
				// We have to reference resource in/after
				// OnCreate!
				getString(R.string.baidu_map),
				getString(R.string.google_map)
		};

		curMapView = (TextView) findViewById(R.id.current_map);
		/*
		 *  If SELECTED_MAP is not set, the default value returned by getIntData
		 *  is 0, which is right for the baidu map. Here we also want to sanity
		 *  check for the return value of getIntData in case we reference beyond
		 *  the scope of mapList.
		 */
		if (PrefHelper.hasKey(PrefHelper.P_SELECTED_MAP)) {
			mapIndex = PrefHelper.getIntData(PrefHelper.P_SELECTED_MAP);
			// Force to baidu if not support google, and update share preference
			// accordingly.
			if (mapIndex < 0 || mapIndex > 1 ||
					!TeamActivity.googleServiceSupport) {
				mapIndex = 0;
				PrefHelper.setInfo(PrefHelper.P_SELECTED_MAP, mapIndex);
			}
			curMapView.setText(mapList[mapIndex]);
		} else {
			mapIndex = -1;
			curMapView.setText(R.string.unset);
		}
	}

	public void things(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.setting_about:
			Intent about = new Intent();
			about.setClass(this, AboutActivity.class);
			startActivity(about);
			break;
		case R.id.setting_account:
			Intent account = new Intent();
			account.setClass(this, AccountActivity.class);
			startActivity(account);
			break;
		case R.id.setting_refresh:
			Intent refresh = new Intent();
			refresh.setClass(this, SetRefreshActivity.class);
			startActivity(refresh);
			break;
		case R.id.setting_map:
			popup_map_selections();
			break;
			/*
		case R.id.setting_mylocation:
			Intent myLocation = new Intent();
			myLocation.setClass(this, MyLocationActivity.class);
			startActivity(myLocation);
			break;
			 */
		}
	}

	///////////////////////////////////
	// popup selection menu for map type
	///////////////////////////////////

	private static RadioOnClick radioMapOnClick;

	private final void popup_map_selections() {
		// mapIndex: 0(baidu), 1(google);
		radioMapOnClick = new RadioOnClick(mapIndex);

		AlertDialog ad = new AlertDialog.Builder(this)
		.setTitle(R.string.select_map_title)
		.setSingleChoiceItems(mapList, radioMapOnClick.getIndex(),
				radioMapOnClick).create();
		ad.show();
	}

	class RadioOnClick implements DialogInterface.OnClickListener {
		private int index = 0;

		public RadioOnClick(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub

			// If Google Map is selected, warn user if this phone
			// doesn't have Google Service support.
			if (which == 1 && !TeamActivity.googleServiceSupport) {
				Utils.showToast(R.string.error_no_google_service);
			} else {
				mapIndex = which;
				curMapView.setText(mapList[which]);
				PrefHelper.setInfo(PrefHelper.P_SELECTED_MAP, mapIndex);
			}

			dialog.dismiss();
		}
	}
}
