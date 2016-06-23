package com.ttxgps.bean;
import java.io.Serializable;
import java.util.ArrayList;

import com.ttxgps.entity.Trace;

public class BabyInfoBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String adminPhoneNum;
	private String birthday;
	private String headIconPath;
	private String height;
	private String imei;
	private String nickName;
	private String phoneNum;
	private String relation;
	private String remark;
	private int role; //0管理员1监护人
	private int sex;
	private String sn;
	private String id;
	private String tdcodePath;
	private String weight;
	private String pid;//"groupID"
	private int status;//在线非0，离线0
	private String statusInf;//在线非0，离线0
	private String statusmosq = "0"; //驱蚊状态，0关1开
	private String deviceVersion;//设备版本号
	private String deviceTime; //手表到期时间


	private String address;
	private String lat;
	private String lng;
	//	public String lat_real;
	//	public String lng_real;
	private String childcount;
	private String onlinecount;
	private Boolean isAdmin=false;

	public Trace traces;//当前位置
	public ArrayList<FenceBaseInfo> fencelist = new ArrayList<FenceBaseInfo>();//电子围栏队列
	public String StealthTimeStr;


	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getAdminPhoneNum()
	{
		return this.adminPhoneNum;
	}

	public String getBirthday()
	{
		return this.birthday;
	}

	public String getHeadIconPath()
	{
		return this.headIconPath;
	}

	public String getHeight()
	{
		return this.height;
	}

	public String getImei()
	{
		return this.imei;
	}

	public String getNickName()
	{
		return this.nickName;
	}

	public String getPhoneNum()
	{
		return this.phoneNum;
	}

	public String getRelation()
	{
		return this.relation;
	}

	public String getRemark()
	{
		return this.remark;
	}

	public int getRole()
	{
		return this.role;
	}

	public int getSex()
	{
		return this.sex;
	}

	public String getSn()
	{
		return this.sn;
	}

	public String getId()
	{
		return this.id;
	}

	public String getTdcodePath()
	{
		return this.tdcodePath;
	}

	public String getWeight()
	{
		return this.weight;
	}

	public void setAdminPhoneNum(String paramString)
	{
		this.adminPhoneNum = paramString;
	}

	public void setBirthday(String paramString)
	{
		this.birthday = paramString;
	}

	public void setHeadIconPath(String paramString)
	{
		this.headIconPath = paramString;
	}

	public void setHeight(String paramString)
	{
		this.height = paramString;
	}

	public void setImei(String paramString)
	{
		this.imei = paramString;
	}

	public void setNickName(String paramString)
	{
		this.nickName = paramString;
	}

	public void setPhoneNum(String paramString)
	{
		this.phoneNum = paramString;
	}

	public void setRelation(String paramString)
	{
		this.relation = paramString;
	}

	public void setRemark(String paramString)
	{
		this.remark = paramString;
	}

	public void setRole(int paramInt)
	{
		this.role = paramInt;
	}

	public void setSex(int paramInt)
	{
		this.sex = paramInt;
	}

	public void setSn(String paramString)
	{
		this.sn = paramString;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setTdcodePath(String paramString)
	{
		this.tdcodePath = paramString;
	}

	public void setWeight(String paramString)
	{
		this.weight = paramString;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}


	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getChildcount() {
		return childcount;
	}

	public void setChildcount(String childcount) {
		this.childcount = childcount;
	}

	public String getOnlinecount() {
		return onlinecount;
	}

	public void setOnlinecount(String onlinecount) {
		this.onlinecount = onlinecount;
	}
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusInf() {
		return statusInf;
	}

	public void setStatusInf(String statusInf) {
		this.statusInf = statusInf;
	}

	public String getstatusmosq() {
		return statusmosq;
	}

	public void setstatusmosq(String statusmosq) {
		this.statusmosq = statusmosq;
	}

	public String getdeviceVersion() {
		return deviceVersion;
	}

	public void setdeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public String getdeviceTime() {
		return deviceTime;
	}

	public void setdeviceTime(String deviceTime) {
		this.deviceTime = deviceTime;
	}

}

/* Location:           E:\test-tools\dex2jar-2.0\HYWatch20150824-dex2jar.jar
 * Qualified Name:     com.hy.hywatch.entity.BabyInfoBean
 * JD-Core Version:    0.6.0
 */