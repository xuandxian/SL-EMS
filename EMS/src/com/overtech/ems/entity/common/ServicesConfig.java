package com.overtech.ems.entity.common;

import com.overtech.ems.config.SystemConfig;

public class ServicesConfig {
	/**
	 * 1.登录
	 */
	public static final String LOGIN = SystemConfig.IP
			+ BusinessConfig.URL_LOGIN;
	/**
	 * 2.注册
	 */
	public static final String REGISTER = SystemConfig.IP
			+ BusinessConfig.URL_REGISTER;
	/**
	 * 3.抢单列表
	 */
	public static final String GRABTASK = SystemConfig.IP
			+ BusinessConfig.URL_GRABTASK;
	/**
	 * 4.关键字搜索
	 */
	public static final String DO_SEARCH = SystemConfig.IP
			+ BusinessConfig.URL_DO_SEARCH;
	/**
	 * 5.筛选
	 */
	public static final String DO_FILTER = SystemConfig.IP
			+ BusinessConfig.URL_DO_FILTER;
	/**
	 * 6.抢单
	 */
	public static final String Do_GRABTASK = SystemConfig.IP
			+ BusinessConfig.URL_DO_GRABTASK;
	/**
	 * 7.附近
	 */
	public static final String NEARBY = SystemConfig.IP
			+ BusinessConfig.URL_NEARBY;
	/**
	 * 8.小区任务包
	 */
	public static final String COMMUNITY_PACKAGE_LIST = SystemConfig.IP
			+ BusinessConfig.URL_COMMUNITY_PACKAGE_LIST;
	/**
	 * 9.忘记密码（验证手机号／短信验证码）
	 */
	public static final String LOST_PASSWORD = SystemConfig.IP
			+ BusinessConfig.URL_LOST_PASSWORD;
	/**
	 * 10.忘记密码（更新密码）
	 */
	public static final String UPDATE_PASSWORD = SystemConfig.IP
			+ BusinessConfig.URL_UPDATE_PASSWORD;
	/**
	 * 11.任务单(未完成)
	 */
	public static final String TASK_LIST_NONE = SystemConfig.IP
			+ BusinessConfig.URL_TASK_LIST_NONE;
	/**
	 * 12.任务包详情
	 */
	public static final String TASK_PACKAGE_DETAIL = SystemConfig.IP
			+ BusinessConfig.URL_TASK_PACKAGE_DETAIL;
	/**
	 * 13.电梯详情
	 */
	public static final String ELEVATOR_DETAIL = SystemConfig.IP
			+ BusinessConfig.URL_ELEVATOR_DETAIL;
	/**
	 * 14.任务单（退单）
	 */
	public static final String CHARGE_BACK_TASK = SystemConfig.IP
			+ BusinessConfig.URL_CHARGE_BACK_TASK;
	/**
	 * 15.任务单（已完成）
	 */
	public static final String TASK_LIST_DONE = SystemConfig.IP
			+ BusinessConfig.URL_TASK_LIST_DONE;
	/**
	 * 16.任务单（开始）--->指向最后一个界面
	 */
	public static final String TASK_START = SystemConfig.IP
			+ BusinessConfig.URL_TASK_START;
	/**
	 * 17.维保清单完成
	 */
	public static final String MAINTENCE_LIST_COMPLETE = SystemConfig.IP
			+ BusinessConfig.URL_MAINTENCE_LIST_COMPLETE;
	/**
	 * 18.问题反馈
	 */
	public static final String PROBLEMS_FEEDBACK = SystemConfig.IP
			+ BusinessConfig.URL_PROBLEMS_FEEDBACK;
	/**
	 * 19.互相评价
	 */
	public static final String EVALUATION_EACH_OTHER = SystemConfig.IP
			+ BusinessConfig.URL_EVALUATION_EACH_OTHER;
	/**
	 * 20.我的
	 */
	public static final String PERSONAL_AVATOR = SystemConfig.IP
			+ BusinessConfig.URL_PERSONAL_AVATOR;
	/**
	 * 21.账户信息
	 */
	public static final String PERSONAL_ACCOUNT = SystemConfig.IP
			+ BusinessConfig.URL_PERSONAL_ACCOUNT;
	/**
	 * 22.更换手机号（验证密码）
	 */
	public static final String CHANGE_PHONENO_PASSWORD = SystemConfig.IP
			+ BusinessConfig.URL_CHANGE_PHONENO_PASSWORD;
	/**
	 * 23.更换手机号（手机号码／短信验证码）
	 */
	public static final String CHANGE_PHONENO_UPDATE = SystemConfig.IP
			+ BusinessConfig.URL_CHANGE_PHONENO_UPDATE;
	/**
	 * 24.我的账单（已结算／未结算）
	 */
	public static final String PERSONAL_BILL = SystemConfig.IP
			+ BusinessConfig.URL_PERSONAL_BILL;
	/**
	 * 25.奖励记录
	 */
	public static final String PERSONAL_BONUS_LIST = SystemConfig.IP
			+ BusinessConfig.URL_PERSONAL_BONUS_LIST;
	/**
	 * 26.退单纪录
	 */
	public static final String PERSONAL_CHARGEBACK_LIST = SystemConfig.IP
			+ BusinessConfig.URL_PERSONAL_CHARGEBACK_LIST;

}
