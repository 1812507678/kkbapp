package com.ttxgps.bean;

import java.io.Serializable;

import android.R.integer;

public class HomeGridviewBean implements Serializable{

	public static final String NAME_LOCATION = "定位";
	public static final String NAME_TALK_BACK= "语音对讲";
	public static final String NAME_HEALTH= "计步器";
	public static final String NAME_SOUND= "录音";
	public static final String NAME_MESSAGE= "信息中心";
	public static final String NAME_GUARDER= "监护人管理";
	public static final String NAME_MONITOR= "监听";
	public static final String NAME_SETTING= "终端设置";
	public static final String NAME_INJECT= "注射疫苗查询";
	public static final String NAME_MANMACHINE= "人机交互";
	public static final String NAME_AMUSEMENT= "娱乐";
	public static final String NAME_TEACH= "教育";
	public static final String NAME_SWITCH= "智能开关";
	public static final String NAME_TEMPERATURE= "温度设置";
	public static final String NAME_TIME= "时间设定";
	public static final String NAME_PREVENTION= "防盗报警";



	private int normaldrawableId;
	private int onclickdrawableId;
	private String drawableName;
	private int Background;

	/**
	 * 获取 normaldrawableId
	 * @return 返回 normaldrawableId
	 */
	public int getNormaldrawableId() {
		return normaldrawableId;
	}
	/**
	 * 设置 normaldrawableId
	 * @param 对normaldrawableId进行赋值
	 */
	public void setNormaldrawableId(int normaldrawableId) {
		this.normaldrawableId = normaldrawableId;
	}
	/**
	 * 获取 onclickdrawableId
	 * @return 返回 onclickdrawableId
	 */
	public int getOnclickdrawableId() {
		return onclickdrawableId;
	}
	/**
	 * 设置 onclickdrawableId
	 * @param 对onclickdrawableId进行赋值
	 */
	public void setOnclickdrawableId(int onclickdrawableId) {
		this.onclickdrawableId = onclickdrawableId;
	}
	/**
	 * 获取 drawableName
	 * @return 返回 drawableName
	 */
	public String getDrawableName() {
		return drawableName;
	}
	/**
	 * 设置 drawableName
	 * @param 对drawableName进行赋值
	 */
	public void setDrawableName(String drawableName) {
		this.drawableName = drawableName;
	}

	/**
	 * 获取 Background
	 * @return 返回 Background
	 */
	public int getBackground() {
		return Background;
	}
	/**
	 * 设置 Background
	 * @param 对Background进行赋值
	 */
	public void setBackground(int Background) {
		this.Background = Background;
	}
	/** (非 Javadoc)
	 * @方法名: toString
	 * @描述:
	 * @return
	 * @see java.lang.Object#toString()
	 */


	@Override
	public String toString() {
		return "HomeGridviewBean [normaldrawableId=" + normaldrawableId
				+ ", onclickdrawableId=" + onclickdrawableId + ", drawableName="
				+ drawableName + "]";
	}
}
