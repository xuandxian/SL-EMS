package com.overtech.ems.entity.parttime;

import java.util.ArrayList;

public class ScanResultBean {
	private ArrayList<String> model;
	private boolean success;
	
	public ScanResultBean(ArrayList<String> model, boolean success) {
		super();
		this.model = model;
		this.success = success;
	}
	public ArrayList<String> getModel() {
		return model;
	}
	public void setModel(ArrayList<String> model) {
		this.model = model;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
