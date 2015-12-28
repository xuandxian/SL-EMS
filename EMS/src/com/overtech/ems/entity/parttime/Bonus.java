package com.overtech.ems.entity.parttime;
/**
 * 奖励信息实体
 * @author Will
 *
 */
public class Bonus {
	private String awardRemark;
	private String awardSum;
	private long awardTime;
	private long maintenanceDate;
	private String projectName;
	private String taskNo;
	public String getAwardRemark() {
		return awardRemark;
	}
	public void setAwardRemark(String awardRemark) {
		this.awardRemark = awardRemark;
	}
	public String getAwardSum() {
		return awardSum;
	}
	public void setAwardSum(String awardSum) {
		this.awardSum = awardSum;
	}
	public long getAwardTime() {
		return awardTime;
	}
	public void setAwardTime(long awardTime) {
		this.awardTime = awardTime;
	}
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
	
}
