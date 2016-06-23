package com.ttxgps.msg;

import java.util.ArrayList;
import java.util.List;

public class WearnSetsInfo {
	public List<ItemInfo> data;
	public String message;
	public int retCode;
	public String sn;

	public static class ItemInfo {
		public String categoryName;
		public boolean isChange;
		public String notifyType;
		public int status;
		public String typeDesc;
		public long typeId;
		public String typeName;
		public String value;

		@Override
		public String toString() {
			return "ItemInfo [typeId=" + this.typeId + ", categoryName=" + this.categoryName + ", typeName=" + this.typeName + ", typeDesc=" + this.typeDesc + ", notifyType=" + this.notifyType + ", status=" + this.status + ", value=" + this.value + ", isChange=" + this.isChange + "]";
		}
	}

	public WearnSetsInfo() {
		this.data = new ArrayList();
	}

}
