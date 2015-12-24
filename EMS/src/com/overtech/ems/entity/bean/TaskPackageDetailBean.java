package com.overtech.ems.entity.bean;

import java.util.List;
import com.overtech.ems.entity.parttime.TaskPackageDetail;

public class TaskPackageDetailBean {
	private List<TaskPackageDetail> model;
	private boolean success;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<TaskPackageDetail> getModel() {
		return model;
	}
	public void setModel(List<TaskPackageDetail> model) {
		this.model = model;
	}

}
