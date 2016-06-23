package com.ttxgps.bean;
import java.io.Serializable;

public class StealthTimeBean
implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String begintime;
	private String createtime;
	private String endtime;
	private String id;
	private String openflag;
	private String source;
	private String weeks;

	public String getBegintime()
	{
		return this.begintime;
	}

	public String getCreatetime()
	{
		return this.createtime;
	}

	public String getEndtime()
	{
		return this.endtime;
	}

	public String getId()
	{
		return this.id;
	}

	public String getOpenflag()
	{
		return this.openflag;
	}

	public String getSource()
	{
		return this.source;
	}

	public String getWeeks()
	{
		return this.weeks;
	}

	public void setBegintime(String paramString)
	{
		this.begintime = paramString;
	}

	public void setCreatetime(String paramString)
	{
		this.createtime = paramString;
	}

	public void setEndtime(String paramString)
	{
		this.endtime = paramString;
	}

	public void setId(String paramString)
	{
		this.id = paramString;
	}

	public void setOpenflag(String paramString)
	{
		this.openflag = paramString;
	}

	public void setSource(String paramString)
	{
		this.source = paramString;
	}

	public void setWeeks(String paramString)
	{
		this.weeks = paramString;
	}
}

/* Location:           E:\test-tools\dex2jar-2.0\HYWatch20150824-dex2jar.jar
 * Qualified Name:     com.hy.hywatch.entity.StealthTimeBean
 * JD-Core Version:    0.6.0
 */