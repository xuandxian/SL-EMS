package com.overtech.ems.entity.parttime;

import java.io.Serializable;

/**
 * Created by Tony1213 on 15/12/16.
 * 任务包
 */
public class TaskPackage implements Serializable {
	private String taskNo;          //维保单号
	private String taskPackageName;        //项目名称
	private String elevatorAmounts;    //电梯数量
	private String isFinish;			//抢单人数
	private String latitude;           //纬度
	private String longitude;          //经度
	private String maintenanceAddress; //维保地点
	private long maintenanceDate;    //维保日期
    private String topState;			//是否置顶
    
    
	public TaskPackage(String taskNo, String taskPackageName,
			String elevatorAmounts, String isFinish, String latitude,
			String longitude, String maintenanceAddress, long maintenanceDate,
			String topState) {
		super();
		this.taskNo = taskNo;
		this.taskPackageName = taskPackageName;
		this.elevatorAmounts = elevatorAmounts;
		this.isFinish = isFinish;
		this.latitude = latitude;
		this.longitude = longitude;
		this.maintenanceAddress = maintenanceAddress;
		this.maintenanceDate = maintenanceDate;
		this.topState = topState;
	}
	public String getTaskNo() {
		return taskNo;
	}
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}
	public String getElevatorAmounts() {
		return elevatorAmounts;
	}
	public void setElevatorAmounts(String elevatorAmounts) {
		this.elevatorAmounts = elevatorAmounts;
	}
	public String getIsFinish() {
		return isFinish;
	}
	public void setIsFinish(String isFinish) {
		this.isFinish = isFinish;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getMaintenanceAddress() {
		return maintenanceAddress;
	}
	public void setMaintenanceAddress(String maintenanceAddress) {
		this.maintenanceAddress = maintenanceAddress;
	}
	
	public String getTaskPackageName() {
		return taskPackageName;
	}
	public void setTaskPackageName(String taskPackageName) {
		this.taskPackageName = taskPackageName;
	}
	public String getTopState() {
		return topState;
	}
	public void setTopState(String topState) {
		this.topState = topState;
	}
	public long getMaintenanceDate() {
		return maintenanceDate;
	}
	public void setMaintenanceDate(long maintenanceDate) {
		this.maintenanceDate = maintenanceDate;
	}
	 
}
