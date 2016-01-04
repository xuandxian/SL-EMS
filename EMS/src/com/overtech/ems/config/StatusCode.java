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
	
	//注册
	public static final int REGISTER_SUCCESS=0x11;
	public static final int REGISTER_FAILED=0x12;
	


	// 忘记密码
	public static final int GET_PHONENO_SUCCESS_EXIST = 0x13;
	public static final int GET_PHONENO_SUCCESS_NONE = 0x14;
	// 忘记密码
	public static final int RESET_PASSWORD_SUCCESS = 0x15;
	public static final int RESET_PASSWORD_FAILED = 0x16;

	/**
	 * 任务单未完成加载成功
	 */
	public static final int TASKLIST_NONE_SUCCESS=0x17;
	/**
	 * 任务单未完成加载失败
	 */
	public static final int TASKLIST_NONE_FAILED=0x18;
	/**
	 * 任务单已完成加载成功
	 */
	public static final int TASKLIST_DONET_SUCCESS=0x19;
	/**
	 * 任务单已完成加载失败
	 */
	public static final int TASKLIST_DONET_FAILED=0x20;


	// 任务包详情
	public static final int PACKAGE_DETAILS_SUCCESS = 0x21;


	
	//OnActivityFroResult StatusCode

	//抢单
	public static final int RESULT_GRAB_DO_FILTER = 0x50;
	public static final int RESULT_GRAB_DO_SEARCH = 0x51;

	
}
