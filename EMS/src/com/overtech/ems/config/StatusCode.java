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
	//附近
	public static final int GET_DATA_BY_MYLOCATION_SUCCESS = 0x11;
	public static final int GET_DATA_BY_MYLOCATION_FAILED = 0x12;
	public static final int GET_DATA_BY_LOCATION_SUCCESS = 0x13;
	public static final int GET_DATA_BY_LOCATION_FAILED = 0x14;
	//注册
	public static final int REGISTER_SUCCESS=0x15;
	public static final int REGISTER_FAILED=0x16;
	


	// 忘记密码
	public static final int GET_PHONENO_SUCCESS_EXIST = 0x17;
	public static final int GET_SERVER_SUCCESS = 0x18;
	// 忘记密码
	public static final int RESET_PASSWORD_SUCCESS = 0x19;
	public static final int RESET_PASSWORD_FAILED = 0x20;

	/**
	 * 任务单未完成加载成功
	 */
	public static final int TASKLIST_NONE_SUCCESS=0x21;
	/**
	 * 任务单未完成加载失败
	 */
	public static final int TASKLIST_NONE_FAILED=0x22;
	/**
	 * 任务单已完成加载成功
	 */
	public static final int TASKLIST_DONET_SUCCESS=0x23;
	/**
	 * 任务单已完成加载失败
	 */
	public static final int TASKLIST_DONET_FAILED=0x24;


	/**
	 * 任务包详情成功
	 */
	public static final int PACKAGE_DETAILS_SUCCESS = 0x25;
	/**
	 * 任务包详情失败
	 */
	public static final int PACKAGE_DETAILS_FAILED=0x26;
	/**
	 * 退单验证维保时间与服务器当前时间差
	 */
	public static final int VALIDATE_TIME_SUCCESS=0x33;
	/**
	 * 退单与服务器交互成功
	 */
	public static final int CHARGEBACK_SUCCESS=0x34;
	/**
	 * 获取工作类型内容成功
	 */
	public static final int WORK_DETAILS_SUCCESS = 0x27;
	/**
	 * 获取工作类型内容失败
	 */
	public static final int WORK_DETAILS_FAILED=0x28;

	/**
	 * 电梯详情
	 */
	public static final int GET_ELEVATOR_DETAILS_SUCCESS=0x29;
	public static final int GET_ELEVATOR_DETAILS_FAILED=0x30;
	/**
	 * 获取电梯完成状态成功
	 */
	public static final int MAINTENANCE_COMPLETE_SUCCESS=0x31;
	/**
	 * 获取电梯完成状态失败
	 */
	public static final int MAINTENENCE_COMPLETE_FAILED=0x32;
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
	 * 个人信息概览返回码--成功
	 */
	public static final int PERSONAL_ZONE_SUCCESS=0x33;
	/**
	 * 个人账单加载成功
	 */
	public static final int PERSONAL_BOUNDS_SUCCESS=0x34;
	/**
	 * 个人信息退单成功
	 */
	public static final int PERSONAL_CHARGEBACK_SUCCESS=0x35;
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
	/**
	 * 公告加载成功
	 */
	public static final int ANNOUNCEMENT_SUCCESS=0x52;

}
