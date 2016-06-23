package com.ttxgps.entity;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.xtst.gps.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.palmtrends.app.ShareApplication;

public class MyMarker {
	public int angle;
	public String speed;
	public String date;
	public String a_type;// 报警类型
	public int type_icon;// 图标显示类型,车1，人2
	public int type_icon_line;// 离线在线图标
	public int icon;//
	public String point_la;
	public String point_ln;
	public Marker marker;
	public String title;
	public String id;
	public TeamMember item;
	public String address;

	public MyMarker() {
	}

	public LatLng getLatlng() {
		return new LatLng(Double.parseDouble(point_la),
				Double.parseDouble(point_ln));
	}

	public MyMarker(String point_la, String point_ln, int angle, String speed,
			String date, String a_type, int type_icon, int type_icon_line,
			String title, String id) {
		// TODO Auto-generated constructor stub
		this.angle = angle;
		this.speed = speed;
		this.date = date;
		this.a_type = a_type;
		this.type_icon = type_icon;
		this.type_icon_line = type_icon_line;
		this.point_la = point_la;
		this.point_ln = point_ln;
		this.title = title;
		this.id = id;
	}

	public MarkerOptions buildMarker(int index) {
		if (icon == R.drawable.location_gps) {
			return buildMarker_one(index);
		} else {
			MarkerOptions mk = new MarkerOptions().position(getLatlng())
					.title("" + index).snippet(toString())
					.icon(BitmapDescriptorFactory.fromResource(icon));
			return mk;
		}

	}

	public MarkerOptions buildMarker_one(int index) {
		MarkerOptions mk = new MarkerOptions()
				.position(getLatlng())
				.title("" + index)
				.snippet(toString())
				.icon(BitmapDescriptorFactory
						.fromBitmap(initMarker(angle, icon)));
		return mk;
	}

	public static MyMarker buildMyMarker(String snippet) throws Exception {
		MyMarker mm = new MyMarker();
		JSONObject obj = new JSONObject(snippet);
		mm.angle = obj.getInt("angle");
		mm.speed = obj.getString("speed");
		mm.date = obj.getString("date");
		mm.a_type = obj.getString("a_type");
		mm.type_icon = obj.getInt("type_icon");
		mm.type_icon_line = obj.getInt("type_icon_line");
		mm.icon = obj.getInt("icon");
		mm.point_ln = obj.getString("point_ln");
		mm.point_la = obj.getString("point_la");
		mm.address = obj.getString("address");
		mm.title = obj.getString("title");
		mm.id = obj.getString("id");
		return mm;
	}

	public Bitmap initMarker(int angle, int icon) {// 箭头方向改变
		BitmapDrawable bd = (BitmapDrawable) ShareApplication.share
				.getResources().getDrawable(icon);
		Bitmap bit = bd.getBitmap();
		Matrix m = new Matrix();
		m.setRotate(angle, bit.getWidth() / 2, bit.getHeight() / 2);
		bit = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(),
				m, true);
		return bit;
	}

	@Override
	public String toString() {
		return "{angle=\"" + angle + "\", speed=\"" + speed + "\", date=\""
				+ date + "\", a_type=\"" + a_type + "\", type_icon="
				+ type_icon + ", type_icon_line=" + type_icon_line + ", icon="
				+ icon + ", point_la=\"" + point_la + "\", point_ln=\""
				+ point_ln + "\",title=\"" + title + "\", address=\"" + address
				+ "\", id=\"" + id + "\"}";
	}

}
