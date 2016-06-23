package com.ttxgps.bean;

import java.io.Serializable;
import java.util.List;


/**
 * TODO: 同步分页数据
 * 
 * @author li.xy
 * @date 2014-9-15 上午10:52:07
 * @version 0.1.0
 * @copyright Wonhigh Information Technology (Shenzhen) Co.,Ltd.
 */
public class DataSyncDto<T> implements Serializable{

	/** */
	private static final long serialVersionUID = 1L;

	private List<T> result;

	private boolean hasNext;

	//private Long syncTime;

	private String lastTimeSeq;

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public String getLastSyncId() {
		return lastTimeSeq;
	}

	public void setLastSyncId(String id) {
		this.lastTimeSeq = id;
	}



}
