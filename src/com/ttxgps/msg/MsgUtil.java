package com.ttxgps.msg;


import java.util.List;

import com.ttxgps.msg.WearnSetsInfo.ItemInfo;
import com.xtst.gps.R;

public class MsgUtil
{
	public static final String ELSEMSG = "other_msg";
	public static final int TYPE_ENTER_FENCE_WARN = 102004;
	public static final int TYPE_EXCISE_DEVICE_WARN = 101014;
	public static final int TYPE_LOW_VOLTAGE_WARN = 101016;
	public static final int TYPE_OUT_FENCE_WARN = 102005;
	public static final int TYPE_SOS_WARN = 101013;
	public static final int TYPE_TUMBLE_WARN = 101015;
	public static final int TYPE_COMMONLY_WARN = 101017;//“ª∞„–≈œ¢
	public static WearnSetsInfo categoryInfo;

	public static String getMsgCategoryName(String msgType, WearnSetsInfo categoryInfo) {
		if (categoryInfo == null) {
			categoryInfo = categoryInfo;
		}
		if (categoryInfo == null || msgType == null) {
			return ELSEMSG;
		}
		String categoryName = ELSEMSG;
		for (int i = 0; i < categoryInfo.data.size(); i++) {
			ItemInfo item = categoryInfo.data.get(i);
			if (msgType.equals(String.valueOf(item.typeId))) {
				categoryName = item.typeName;
			}
		}
		return categoryName;
	}

	public static int getMsgCategoryName(int msgType) {
		switch (msgType) {
		case TYPE_SOS_WARN /*101013*/:
			return R.string.msg_sos_warn;
		case TYPE_EXCISE_DEVICE_WARN /*101014*/:
			return R.string.msg_excise_device_warn;
		case TYPE_TUMBLE_WARN /*101015*/:
			return R.string.msg_tumble_warn;
		case TYPE_LOW_VOLTAGE_WARN /*101016*/:
			return R.string.msg_low_voltage_warn;
		case TYPE_ENTER_FENCE_WARN /*102004*/:
			return R.string.msg_enter_fence_warn;
		case TYPE_OUT_FENCE_WARN /*102005*/:
			return R.string.msg_out_fence_warn;
		default:
			return R.string.other_msg;
		}
	}

}

