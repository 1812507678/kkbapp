package com.ttxgps.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

	public static final String TAG = MainViewPagerAdapter.class.getSimpleName();
	private List<Fragment> fragments;

	public MainViewPagerAdapter(FragmentManager fragmentManager,List<Fragment> fragments) {
		super(fragmentManager);
		this.fragments = fragments;
	}
	
	@Override
	public android.support.v4.app.Fragment getItem(int arg0) {
		
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		if (fragments != null) {
			return fragments.size();
		}
		return 0;
	}

}
