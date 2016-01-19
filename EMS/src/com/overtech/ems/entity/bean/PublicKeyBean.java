package com.overtech.ems.entity.bean;

public class PublicKeyBean {
	private String model;
	private boolean success;
	
	public PublicKeyBean(String model, boolean success) {
		super();
		this.model = model;
		this.success = success;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
