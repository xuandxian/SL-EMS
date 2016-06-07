package com.overtech.ems.entity.bean;
/**
 * login parse
 * @author Overtech
 *
 */
public class LoginBean {
	public int st;
	public String msg;
	public Body body;
	public class Body{
		public String uid;
		public String certificate;
		public String employeeType;
	}
}

