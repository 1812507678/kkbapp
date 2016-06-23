package com.ttxgps.entity;

import java.io.Serializable;

public class TeamMember implements Serializable {
	public String id;
	public String name;
	public String status;  // raw state from server
	public String state = "未知"; // Detailed readable state
	public String device_state;
	public String nickname;
	public String pid;
	public String address;
	public String lat;
	public String lng;
	//	public String lat_real;
	//	public String lng_real;
	public String childcount;
	public String onlinecount;
	public String type = "1";// 1表示汽车 2表示人
	public String gender; // Only human has gender
}
