package com.overtech.ems.entity.parttime;

import com.overtech.ems.entity.bean.BeginWorkResult;

public class ScanResultBean {
	private BeginWorkResult model;
	private boolean success;
	public BeginWorkResult getModel() {
		return model;
	}
	public void setModel(BeginWorkResult model) {
		this.model = model;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
