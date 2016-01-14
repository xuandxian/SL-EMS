package com.overtech.ems.entity.bean;

import com.overtech.ems.entity.parttime.ElevatorInfo;

public class ElevatorInfoBean {
	
	private ElevatorInfo model;
	private boolean success;
	public ElevatorInfoBean(ElevatorInfo model, boolean success) {
		super();
		this.model = model;
		this.success = success;
	}
	public ElevatorInfo getModel() {
		return model;
	}
	public void setModel(ElevatorInfo model) {
		this.model = model;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
