package com.ttxgps.msg;

import android.os.Parcel;
import android.os.Parcelable;

public class MsgInfo implements Parcelable{
	public static final Creator<MsgInfo> CREATOR;
	public String content;
	public String time;
	public int type;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(this.type);
		arg0.writeString(this.content);
		arg0.writeString(this.time);
	}

	static {
		CREATOR = new Creator<MsgInfo>() {
			@Override
			public MsgInfo createFromParcel(Parcel arg0) {
				MsgInfo info = new MsgInfo();
				info.type = arg0.readInt();
				info.content = arg0.readString();
				info.time = arg0.readString();
				return info;
			}

			@Override
			public MsgInfo[] newArray(int arg0) {
				return new MsgInfo[arg0];
			}
		};
	}

}
