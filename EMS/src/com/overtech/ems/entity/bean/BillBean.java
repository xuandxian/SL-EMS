package com.overtech.ems.entity.bean;

import java.util.List;

import com.overtech.ems.entity.parttime.Bill;

public class BillBean {
	private List<Bill> model;
	private boolean success;
	public List<Bill> getModel() {
		return model;
	}
	public void setModel(List<Bill> model) {
		this.model = model;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
