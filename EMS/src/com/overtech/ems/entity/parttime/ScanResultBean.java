package com.overtech.ems.entity.parttime;

import java.util.List;

import com.overtech.ems.entity.bean.BeginWorkResult;

public class ScanResultBean {
	private List<BeginWorkResult> model;
	private boolean success;
	public List<BeginWorkResult> getModel() {
		return model;
	}
	public void setModel(List<BeginWorkResult> model) {
		this.model = model;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
