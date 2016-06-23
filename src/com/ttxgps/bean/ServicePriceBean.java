package com.ttxgps.bean;

import java.io.Serializable;

public class ServicePriceBean implements Serializable {

	private String ServiceCode;
	private String TypeName;
	private String Price;
	private String Remark;

	public String getServiceCode(){
		return ServiceCode;
	}
	public void setServiceCode(String ServiceCode){
		this.ServiceCode = ServiceCode;
	}
	public String getTypeName(){
		return TypeName;
	}
	public void setTypeName(String TypeName){
		this.TypeName = TypeName;
	}
	public String getPrice(){
		return Price;
	}
	public void setPrice(String Price){
		this.Price = Price;
	}
	public String getRemark(){
		return Remark;
	}
	public void setRemark(String Remark){
		this.Remark = Remark;
	}
}
