package com.overtech.ems.entity.fulltime;

import java.util.List;

/**
 * 维修单实体类
 * 
 * @author Overtech
 * 
 */
public class MaintenanceBean {
	public int st;
	public String msg;
	public Body body;

	public class Body {
		// 维修单明细部分
		public String repairAddress;
		public String elevatorNo;
		public String elevatorBrand;
		public String storeySite;
		public String latitude;
		public String longitude;
		public String partnerTel;
		public String remark;
		public String faultCause;
		public String faultFrom;
		public String faultComponent;

		public List<Workorder> data;

		public String workorderCode;// 维修单二维码部分
		public String siteTel;
	}

	public class Workorder {
		public String workorderCode;
		public String elevatorNo;
		public String faultType;
		public String address;
		public String latitude;
		public String longitude;
		public String publishDatetime;
		public String accomplishDate;
	}
}
