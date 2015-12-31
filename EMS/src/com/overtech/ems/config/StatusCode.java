package com.overtech.ems.config;

public class StatusCode {
	
	//网络异常
	public static final int RESPONSE_NET_FAILED = 0x07;
	
	//登录
	public static final int LOGIN_FAILED = 0x01;
	public static final int LOGIN_SUCCESS = 0x02;
	public static final int LOGIN_NOT_EXIST=0x03;
	
	// 抢单
	public static final int GRAB_FAILED = 0x04;
	public static final int GRAB_SUCCESS = 0x05;
	public static final int GRAG_RESPONSE_SUCCESS = 0x06;
	public static final int GRAG_RESPONSE_OTHER_FAILED = 0x08;
	
	// 任务包详情
	public static final int PACKAGE_DETAILS_SUCCESS = 0x09;
	
	/**
	 * 注册上传成功
	 */
	public static final int REGISTER_SUCCESS=0x10;
	/**
	 * 注册失败
	 */
	public static final int REGISTER_FAILED=0x11;
	
	
}
