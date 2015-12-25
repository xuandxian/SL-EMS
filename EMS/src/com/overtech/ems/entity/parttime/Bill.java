package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/14.
 * 我的账单
 */
public class Bill {
    private long maintenanceDate;
    private String projectName;
    private String taskNo;
    private String totalPrice;
    private long closingDate;
	public long getMaintenanceDate() {
		return maintenanceDate;
	}
	public void setMaintenanceDate(long maintenanceDate) {
		this.maintenanceDate = maintenanceDate;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getTaskNo() {
		return taskNo;
	}
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public long getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(long closingDate) {
		this.closingDate = closingDate;
	}
	
    
}
