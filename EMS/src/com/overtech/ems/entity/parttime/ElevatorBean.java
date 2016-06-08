package com.overtech.ems.entity.parttime;

public class ElevatorBean {
	public int st;
	public String msg;
	public Body body;

	public class Body {
		/**
		 * 项目名称
		 */
		public String projectName;
		/**
		 * 电梯品牌
		 */
		public String elevatorBrand;
		/**
		 * 电梯型号
		 */
		public String elevatorModel;
		/**
		 * 电梯编号
		 */
		public String elevatorNo;
		/**
		 * 梯号
		 */
		public String elevatorAliase;
		/**
		 * 物业公司
		 */
		public String tenementCompany;
		/**
		 * 物业联系人
		 */
		public String tenementPerson;
		/**
		 * 物业联系电话
		 */
		public String tenementTel;
		/**
		 * 维保公司
		 */
		public String maintenanceCompany;
		/**
		 * 额定载重
		 */
		public String loadCapacity;
		/**
		 * 额定速度
		 */
		public String nominalSpeed;
		/**
		 * 层/站/门
		 */
		public String storeyPlatformDoor;
		/**
		 * 提升高度
		 */
		public String elevatorHigher;
		/**
		 * 保养类型
		 */
		public String maintenanceType;
		/**
		 * 设备地址
		 */
		public String deviceAddress;
		/**
		 * 年检日期
		 */
		public String annualInspectionDate;
		/**
		 * 上次保养日期
		 */
		public String lastMaintenanceDate;
	}

}
