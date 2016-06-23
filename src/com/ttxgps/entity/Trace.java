package com.ttxgps.entity;

public class Trace {
	public String id; // Device ID
	public String pos_time;
	public String pos_type; // //定位方式，0为基站定位，1为GPS定位，2为wifi定位，3为多混合定位
	public String speed;
	public String course;
	public String car_state;  // 1: online 0: offline
	public String alarm_state;
	public String direction="0";
	// public int is_stop;

	// public String online;// 1: online 2: offline
	public int lat;
	public int lng;
	// Original offset for backup.
	public String real_lat;
	public String real_lng;

	String[] postype={"基站定位","GPS定位","wifi定位","混合定位"};

	public void initTrace(String[] param) {
		id = param[0];
		speed = (param[1] == null || param[1].equals("")) ? "0" : param[1];
		course = (param[2] == null || param[2].equals("")) ? "0" : param[2];
		car_state = (param[3] == null  || param[3].equals("")) ? "0" : param[3];
		pos_time = (param[4] == null) ? "2015-01-01 00:00" : param[4]; // TODO
		int pos_index = (param[5] == null || param[5].equals("")) ? 0 : Integer.parseInt(param[5]);
		pos_type=postype[pos_index];
		lat = (int) (Double.parseDouble(param[6]) * 1E6);
		lng = (int) (Double.parseDouble(param[7]) * 1E6);
		real_lat = param[8];
		real_lng = param[9];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trace other = (Trace) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
