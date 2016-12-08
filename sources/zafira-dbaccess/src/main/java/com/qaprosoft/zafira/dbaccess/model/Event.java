package com.qaprosoft.zafira.dbaccess.model;

import java.util.Date;

public class Event extends AbstractEntity
{
	private static final long serialVersionUID = 2389981230350789124L;

	public enum Type
	{
		REQUEST_DEVICE_CONNECT, CONNECT_DEVICE, REQUEST_DEVICE_DISCONNECT, DISCONNECT_DEVICE, DEVICE_WAIT_TIMEOUT, DEVICE_NOT_FOUND, HEARTBEAT_TIMEOUT
	};

	private Type type;
	private String testRunId;
	private String testId;
	private String data;
	private Date received;
	
	public Event()
	{
	}
	
	public Event(Type type, String testRunId)
	{
		this.type = type;
		this.testRunId = testRunId;
	}

	public Event(Type type, String testRunId, String testId, String data)
	{
		this.type = type;
		this.testRunId = testRunId;
		this.testId = testId;
		this.data = data;
	}
	
	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public String getTestRunId()
	{
		return testRunId;
	}

	public void setTestRunId(String testRunId)
	{
		this.testRunId = testRunId;
	}

	public String getTestId()
	{
		return testId;
	}

	public void setTestId(String testId)
	{
		this.testId = testId;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public Date getReceived()
	{
		return received;
	}

	public void setReceived(Date received)
	{
		this.received = received;
	}
}