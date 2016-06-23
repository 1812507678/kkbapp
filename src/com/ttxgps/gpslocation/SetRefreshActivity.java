package com.ttxgps.gpslocation;
import com.xtst.gps.R;
import com.ttxgps.utils.PrefHelper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

public class SetRefreshActivity extends PreferenceActivity implements
OnSharedPreferenceChangeListener {
	public static final String REFRESH_DISABLE = "refresh_disable"; // if true, disable refresh.
	public static final String REFRESH_PERIOD_SINGLE = "refresh_period_single";
	public static final String REFRESH_PERIOD_TEAM = "refresh_period_team";
	public static final String REFRESH_PERIOD_DEFAULT_SINGLE = "10";
	public static final String REFRESH_PERIOD_DEFAULT_TEAM = "60";
	ListPreference refreshPeriods_Single = null;
	ListPreference refreshPeriods_Team = null;
	//	CheckBoxPreference refreshDisable = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refresh_period);
		PreferenceManager prefManager = getPreferenceManager();
		prefManager.setSharedPreferencesName(PrefHelper.GPSLOCATION_PREFS_FILENAME);
		SharedPreferences prefs = prefManager.getSharedPreferences();
		prefs.registerOnSharedPreferenceChangeListener(this);
		// NOTICE:
		// This line must be call after registerOnSharedPreferenceChangeListener().
		addPreferencesFromResource(R.xml.set_refresh);

		refreshPeriods_Single = (ListPreference) findPreference(REFRESH_PERIOD_SINGLE);
		refreshPeriods_Team = (ListPreference) findPreference(REFRESH_PERIOD_TEAM);
		// We have to set summary by ourselves.
		refreshPeriods_Single.setSummary(refreshPeriods_Single.getEntry());
		refreshPeriods_Team.setSummary(refreshPeriods_Team.getEntry());

		/*
		 * Initialize time setting for refresh period.
		 * TODO: Since we are using shared PreferenceManager, the prefs of
		 * refresh_period_single and refresh_period_team will be saved into
		 * the default preference file by default.
		 *  (/data/data/com.ttxgps.gpslocation_preferences.xml)
		 */

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

		/* This will be done by Android automatically.
		if (key.equals("disable_refresh")) {
			if (refreshDisable.isChecked()) {
				PrefHelper.setInfo(REFRESH_DISABLE, true);
			} else {
				PrefHelper.setInfo(REFRESH_DISABLE, false);
			}
		}
		 */

		// refresh period for single card was changed.
		if (key.equals(REFRESH_PERIOD_SINGLE)) {
			// Update summary
			refreshPeriods_Single.setSummary(refreshPeriods_Single.getEntry());
			return;
		} else if (key.equals(REFRESH_PERIOD_TEAM)) { // refresh period for a team was changed.
			// Update summary
			refreshPeriods_Team.setSummary(refreshPeriods_Team.getEntry());
			return;
		}

	}

	public void things(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
