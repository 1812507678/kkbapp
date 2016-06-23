package com.ttxgps.bean;

public class GuardianInfo {
	private String accType;
	private String headIconPath;
	private String mobileno;
	private String relation;
	private boolean IsAdmin;
	private String userId;

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMobileno() {
		return this.mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getHeadIconPath() {
		return this.headIconPath;
	}

	public void setHeadIconPath(String headIconPath) {
		this.headIconPath = headIconPath;
	}

	public String getRelation() {
		return this.relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public boolean getIsAdmin() {
		return this.IsAdmin;
	}

	public void setIsAdmin(boolean IsAdmin) {
		this.IsAdmin = IsAdmin;
	}

	public String getAccType() {
		return this.accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

}
