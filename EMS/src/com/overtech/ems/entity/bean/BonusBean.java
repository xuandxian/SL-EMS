package com.overtech.ems.entity.bean;

import java.util.List;

import com.overtech.ems.entity.parttime.Bonus;

public class BonusBean {
	private List<Bonus> model;
	private boolean success;
	public List<Bonus> getModel() {
		return model;
	}
	public void setModel(List<Bonus> model) {
		this.model = model;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
