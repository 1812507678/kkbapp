package com.ttxgps.bean;

import java.io.Serializable;

import android.R.integer;

public class HomeGridviewBean implements Serializable{

	public static final String NAME_LOCATION = "��λ";
	public static final String NAME_TALK_BACK= "�����Խ�";
	public static final String NAME_HEALTH= "�Ʋ���";
	public static final String NAME_SOUND= "¼��";
	public static final String NAME_MESSAGE= "��Ϣ����";
	public static final String NAME_GUARDER= "�໤�˹���";
	public static final String NAME_MONITOR= "����";
	public static final String NAME_SETTING= "�ն�����";
	public static final String NAME_INJECT= "ע�������ѯ";
	public static final String NAME_MANMACHINE= "�˻�����";
	public static final String NAME_AMUSEMENT= "����";
	public static final String NAME_TEACH= "����";
	public static final String NAME_SWITCH= "���ܿ���";
	public static final String NAME_TEMPERATURE= "�¶�����";
	public static final String NAME_TIME= "ʱ���趨";
	public static final String NAME_PREVENTION= "��������";



	private int normaldrawableId;
	private int onclickdrawableId;
	private String drawableName;
	private int Background;

	/**
	 * ��ȡ normaldrawableId
	 * @return ���� normaldrawableId
	 */
	public int getNormaldrawableId() {
		return normaldrawableId;
	}
	/**
	 * ���� normaldrawableId
	 * @param ��normaldrawableId���и�ֵ
	 */
	public void setNormaldrawableId(int normaldrawableId) {
		this.normaldrawableId = normaldrawableId;
	}
	/**
	 * ��ȡ onclickdrawableId
	 * @return ���� onclickdrawableId
	 */
	public int getOnclickdrawableId() {
		return onclickdrawableId;
	}
	/**
	 * ���� onclickdrawableId
	 * @param ��onclickdrawableId���и�ֵ
	 */
	public void setOnclickdrawableId(int onclickdrawableId) {
		this.onclickdrawableId = onclickdrawableId;
	}
	/**
	 * ��ȡ drawableName
	 * @return ���� drawableName
	 */
	public String getDrawableName() {
		return drawableName;
	}
	/**
	 * ���� drawableName
	 * @param ��drawableName���и�ֵ
	 */
	public void setDrawableName(String drawableName) {
		this.drawableName = drawableName;
	}

	/**
	 * ��ȡ Background
	 * @return ���� Background
	 */
	public int getBackground() {
		return Background;
	}
	/**
	 * ���� Background
	 * @param ��Background���и�ֵ
	 */
	public void setBackground(int Background) {
		this.Background = Background;
	}
	/** (�� Javadoc)
	 * @������: toString
	 * @����:
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
