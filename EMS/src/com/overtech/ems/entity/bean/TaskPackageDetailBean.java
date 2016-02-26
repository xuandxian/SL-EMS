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
