package com.overtech.ems.config;

public class StatusCode {
	
	//网络异常
	public static final int RESPONSE_NET_FAILED = 0x01;
	
	//登录
	public static final int LOGIN_FAILED = 0x02;
	public static final int LOGIN_SUCCESS = 0x03;
	public static final int LOGIN_NOT_EXIST=0x04;
	
	// 抢单
	public static final int GRAB_FAILED = 0x05;
	public static final int GRAB_SUCCESS = 0x06;
	public static final int GRAG_RESPONSE_SUCCESS = 0x07;
	public static final int GRAG_RESPONSE_OTHER_FAILED = 0x08;
	public static final int KEYWORDS_SUCCESS = 0x09;
	public static final int KEYWORDS_FAILED = 0x10;
	
	// 任务包详情
	public static final int PACKAGE_DETAILS_SUCCESS = 0x16;
	
	// 忘记密码，更新密码
	public static final int GET_PHONENO_SUCCESS_EXIST = 0x20;
	public static final int GET_PHONENO_SUCCESS_NONE = 0x21;
	public static final int RESET_PASSWORD_SUCCESS = 0x22;
	public static final int RESET_PASSWORD_FAILED = 0x23;







	//OnActivityFroResult StatusCode

	//抢单
	public static final int RESULT_GRAB_DO_FILTER = 0x50;
	public static final int RESULT_GRAB_DO_SEARCH = 0x51;
	
}
