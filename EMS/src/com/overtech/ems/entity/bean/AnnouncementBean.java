package com.overtech.ems.entity.bean;

import java.util.List;

import com.overtech.ems.entity.parttime.Announcement;

public class AnnouncementBean {
	private List<Announcement> model;
	private boolean success;
	public List<Announcement> getModel() {
		return model;
	}
	public void setModel(List<Announcement> model) {
		this.model = model;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
