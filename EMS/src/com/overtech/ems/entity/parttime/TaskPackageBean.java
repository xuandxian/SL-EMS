package com.overtech.ems.entity.parttime;

import java.util.List;
/**
 * 
 * @author Will
 *	@解析抢单列表数据
 */
public class TaskPackageBean {
	private List<TaskPackage> model;
	private String success;
	public List<TaskPackage> getModel() {
		return model;
	}
	public void setModel(List<TaskPackage> model) {
		this.model = model;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
}
