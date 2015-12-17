package com.overtech.ems.entity.parttime;

import java.util.List;

public class GrabTaskBean {
	public List<TaskPackage> model;
	public boolean success;
	public class TaskPackage{
		public int elevatorAmounts;
		public int isFinish;
		public String latitude;
		public String longitude;
		public String maintenanceAddress;
		public String maintenanceDate;
		public String projectName;
		public int topState;
	}
}
