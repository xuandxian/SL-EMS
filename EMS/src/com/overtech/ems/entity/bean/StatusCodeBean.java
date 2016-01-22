package com.overtech.ems.entity.bean;

public class StatusCodeBean {
	private String model;
	private String status;
	private String taskNo;
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTaskNo() {
		return taskNo;
	}
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}
	public StatusCodeBean(String model, String status) {
		super();
		this.model = model;
		this.status = status;
	}
}
