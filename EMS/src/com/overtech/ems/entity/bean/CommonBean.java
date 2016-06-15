package com.overtech.ems.entity.bean;

/**
 * 通用解析类 当返回数据量少 简单时使用
 * 
 * @author Overtech Will
 * 
 */
public class CommonBean {
	public int st;
	public String msg;
	public Body body;

	public class Body {
		public String result;

		public String avator;
		public String id;
		public String name;
		public String phone;
		public String workNo;
		public String registerTime;
		public String employeeRate;
		
		public String success;
	}
}
