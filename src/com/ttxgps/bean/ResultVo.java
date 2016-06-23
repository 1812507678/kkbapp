package com.ttxgps.bean;

/**
 * TODO: 服务器返回数据结果集
 * 
 * @author li.xy
 * @date 2014-9-15 上午10:52:57
 * @version 0.1.0 
 * @copyright Wonhigh Information Technology (Shenzhen) Co.,Ltd.
 */
public class ResultVo<T> {
	
	private int errorCode = 0;

	private String errorMessage = "";
	
	private T data;

	public int getErrorCode() {
		return errorCode;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
