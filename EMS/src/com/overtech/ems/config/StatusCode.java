package com.overtech.ems.config;

public class StatusCode {
	
	//网络异常
	public static final int RESPONSE_NET_FAILED = 0x01;
	//服务端异常
    public static final int RESPONSE_SERVER_EXCEPTION = 0x99;
	
	//登录
	public static final int LOGIN_FAILED = 0x02;
	public static final int LOGIN_SUCCESS = 0x03;
	public static final int LOGIN_NOT_EXIST=0x04;
	
	// 抢单
	public static final int GRAB_GET_DATA_FAILED = 0x05;
	public static final int GRAB_GET_DATA_SUCCESS = 0x06;
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


	/**
	 * 任务包详情成功
	 */
	public static final int PACKAGE_DETAILS_SUCCESS = 0x21;
	/**
	 * 任务包详情失败
	 */
	public static final int PACKAGE_DETAILS_FAILED=0x22;
	/**
	 * 获取工作类型内容成功
	 */
	public static final int WORK_DETAILS_SUCCESS = 0x23;
	/**
	 * 获取工作类型内容失败
	 */
	public static final int WORK_DETAILS_FAILED=0x24;

	/**
	 * 获取电梯详情成功
	 */
	public static final int ELEVATOR_SUCCESS=0x25;
	/**
	 * 获取电梯详情失败
	 */
	public static final int ELEVATOR_FAILED=0x26;
	/**
	 * 获取电梯完成状态成功
	 */
	public static final int MAINTENANCE_COMPLETE_SUCCESS=0x30;
	/**
	 * 获取电梯完成状态失败
	 */
	public static final int MAINTENENCE_COMPLETE_FAILED=0x31;
	/**
	 * 更换手机号
	 */
	public static final int RESPONSE_VALICATE_PASSWORD_SUCCESS=0x40;
	public static final int RESPONSE_VALICATE_PASSWORD_FAILURE=0x41;
	public static final int GET_PHONENO_NOT_EXIST=0x42;
	public static final int GET_PHONENO_EXIST=0x43;
	public static final int UPDATE_PHONENO_SUCCESS=0x44;
	public static final int UPDATE_PHONENO_FAILURE=0x45;
	/**
	 * 任务包详情-->维保清单
	 */
	public static final int RESULT_TASKLIST_PACKAGEDETAIL=0x46;
	
	/**
	 * 个人详细信息返回码--成功
	 */
	public static final int PERSONAL_DETAIL_SUCCESS=0x47;
	/**
	 * 个人详细信息返回码--失败
	 */
	public static final int PERSONAL_DETAIL_FAILED=0x48;
	
	/**
	 * 个人账单加载成功
	 */
	public static final int ACCOUNT_LIST_SUCCESS=0x49;
	
	//OnActivityFroResult StatusCode

	//抢单
	public static final int RESULT_GRAB_DO_FILTER = 0x50;
	public static final int RESULT_GRAB_DO_SEARCH = 0x51;

}
