package com.overtech.ems.entity.bean;

import java.util.List;

public class TaskPackageDetailBean {
	public int st;
	public String msg;
	public Body body;

	public class Body {
		public List<TaskPackage> data;
		public String partnerPhone;
		public String partnerName;
		public String zone;//区域
		public String latitude;
		public String longitude;
		public String maintenanceDate;
		public String taskPackageName;
	}

	public class TaskPackage {
		public String elevatorName;
		public String elevatorBrand;
		public String workType;
		public String elevatorNo;
		public String maintainPrice;
		public String storeySite;
		public String partnerPhone;// 用于我的任务单
		public String isFinish;
	}
}
