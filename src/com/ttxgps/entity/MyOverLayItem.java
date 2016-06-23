package com.ttxgps.entity;

//import com.baidu.mapapi.GeoPoint;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.mapapi.map.OverlayItem;

public class MyOverLayItem extends OverlayItem {
	public String date;
	public String speed;
	public int course;
	public String alarm;

	public MyOverLayItem(GeoPoint arg0, String arg1, String arg2, int course,
			String speed, String date, String alarm) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
		this.course = course;
		this.speed = speed;
		this.date = date;
		this.alarm = alarm;
	}
}
