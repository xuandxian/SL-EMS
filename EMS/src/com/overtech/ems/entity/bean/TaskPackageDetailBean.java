package com.overtech.ems.entity.bean;

import java.util.List;
import com.overtech.ems.entity.parttime.TaskPackageDetail;

public class TaskPackageDetailBean {
	private List<TaskPackageDetail> model;
	private boolean success;
	private String partnerPhone;
	private String partnerName;
	private String zonePhone;
	private String zone;
	private String taskPackageName;    //任务包名称
	private String latitude;           //纬度
	private String longitude;          //经度
	private String maintenanceDate;    //维保日期
	

	public TaskPackageDetailBean(List<TaskPackageDetail> model,
			boolean success, String partnerPhone, String partnerName,
			String zonePhone, String zone, String taskPackageName,
			String latitude, String longitude, String maintenanceDate) {
		super();
		this.model = model;
		this.success = success;
		this.partnerPhone = partnerPhone;
		this.partnerName = partnerName;
		this.zonePhone = zonePhone;
		this.zone = zone;
		this.taskPackageName = taskPackageName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.maintenanceDate = maintenanceDate;
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

	public String getZonePhone() {
		return zonePhone;
	}
	public void setZonePhone(String zonePhone) {
		this.zonePhone = zonePhone;
	}
	public String getPartnerPhone() {
		return partnerPhone;
	}
	public void setPartnerPhone(String partnerPhone) {
		this.partnerPhone = partnerPhone;
	}
	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
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
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}

}
