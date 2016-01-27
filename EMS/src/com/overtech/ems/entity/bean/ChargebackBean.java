package com.overtech.ems.entity.bean;

public class ChargebackBean {
	private long chargeBackTime;
	private long grabTime;
	private String taskNo;
	private String taskPackageName;
	public long getChargeBackTime() {
		return chargeBackTime;
	}
	public void setChargeBackTime(long chargeBackTime) {
		this.chargeBackTime = chargeBackTime;
	}
	public long getGrabTime() {
		return grabTime;
	}
	public void setGrabTime(long grabTime) {
		this.grabTime = grabTime;
	}
	public String getTaskNo() {
		return taskNo;
	}
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}
	public String getTaskPackageName() {
		return taskPackageName;
	}
	public void setTaskPackageName(String taskPackageName) {
		this.taskPackageName = taskPackageName;
	}
	
}
