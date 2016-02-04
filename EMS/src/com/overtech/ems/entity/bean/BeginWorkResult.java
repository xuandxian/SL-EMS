package com.overtech.ems.entity.bean;
/**
 * 二维码扫描的时候开始
 * @author Will
 *
 */
public class BeginWorkResult {
 private String taskNo;
 private String workType;
 private String zonePhone;
 private String isStart;
 private String isFinish;
 private String longitude;
 private String latitude;
public String getLongitude() {
	return longitude;
}
public void setLongitude(String longitude) {
	this.longitude = longitude;
}
public String getLatitude() {
	return latitude;
}
public void setLatitude(String latitude) {
	this.latitude = latitude;
}
public String getIsFinish() {
	return isFinish;
}
public void setIsFinish(String isFinish) {
	this.isFinish = isFinish;
}
public String getTaskNo() {
	return taskNo;
}
public void setTaskNo(String taskNo) {
	this.taskNo = taskNo;
}
public String getWorkType() {
	return workType;
}
public void setWorkType(String workType) {
	this.workType = workType;
}
public String getZonePhone() {
	return zonePhone;
}
public void setZonePhone(String zonePhone) {
	this.zonePhone = zonePhone;
}
public String getIsStart() {
	return isStart;
}
public void setIsStart(String isStart) {
	this.isStart = isStart;
}
 
}
