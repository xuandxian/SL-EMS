package com.overtech.ems.entity.bean;

import java.util.List;

/**
 * 
 * @author Will
 * @解析抢单列表数据
 */
public class TaskPackageBean {
	public int st;
	public String msg;
	public Body body;

	public class Body {
		public String status;
		public String taskNo;
		public List<TaskPackage> data;
	}

	public class TaskPackage {	
		public String taskNo; // 维保单号
		public String taskPackageName; // 项目名称
		public String elevatorAmounts; // 电梯数量
		public String grabNums; // 抢单人数
		public String latitude; // 纬度
		public String longitude; // 经度
		public String maintenanceAddress; // 维保地点
		public String maintenanceDate; // 维保日期
		public String topState; // 是否置顶
	}
}
