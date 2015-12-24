package com.overtech.ems.entity.bean;

import java.util.List;

import com.overtech.ems.entity.parttime.TaskPackage;
/**
 * 
 * @author Will
 *	@解析抢单列表数据
 */
public class TaskPackageBean {
	private List<TaskPackage> model;
	private boolean success;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<TaskPackage> getModel() {
		return model;
	}
	public void setModel(List<TaskPackage> model) {
		this.model = model;
	}
	
}
