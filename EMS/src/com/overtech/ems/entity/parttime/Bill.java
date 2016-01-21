package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/14.
 * 我的账单
 */
public class Bill {
    private String maintenanceDate;
    private String taskPackageName;
    private String taskNo;
    private String totalPrice;
    private String closingDate;
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
	public String getMaintenanceDate() {
		return maintenanceDate;
	}
	public void setMaintenanceDate(String maintenanceDate) {
		this.maintenanceDate = maintenanceDate;
	}
	public String getTaskPackageName() {
		return taskPackageName;
	}
	public void setTaskPackageName(String taskPackageName) {
		this.taskPackageName = taskPackageName;
	}
	public String getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(String closingDate) {
		this.closingDate = closingDate;
	}
	
    
}
