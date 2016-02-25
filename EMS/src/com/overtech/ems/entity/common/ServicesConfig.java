package com.overtech.ems.entity.common;

import com.overtech.ems.BuildConfig;
import com.overtech.ems.config.SystemConfig;

public class ServicesConfig {
	/**
	 * 0.RSA初始化
	 */
	public static final String RSA_INIT = SystemConfig.IP+ BusinessConfig.URL_RSA;
	/**
	 * 1.登录
	 */
	public static final String LOGIN = SystemConfig.IP+ BusinessConfig.URL_LOGIN;
	/**
	 * 2.注册
	 */
	public static final String REGISTER = SystemConfig.IP+ BusinessConfig.URL_REGISTER;
	/**
	 * 3.抢单列表
	 */
	public static final String GRABTASK = SystemConfig.IP+ BusinessConfig.URL_GRABTASK;
	/**
	 * 4.关键字搜索
	 */
	public static final String DO_SEARCH = SystemConfig.IP+ BusinessConfig.URL_DO_SEARCH;
	/**
	 * 5.筛选
	 */
	public static final String DO_FILTER = SystemConfig.IP+ BusinessConfig.URL_DO_FILTER;
	/**
	 * 6.关键字自动补全
	 */
	public static final String KEY_WORD_COMPLETE = SystemConfig.IP+ BusinessConfig.URL_KEY_WORD_AUTO;
	/**
	 * 7.抢单
	 */
	public static final String Do_GRABTASK = SystemConfig.IP+ BusinessConfig.URL_DO_GRABTASK;
	/**
	 * 8.附近
	 */
	public static final String NEARBY = SystemConfig.IP+ BusinessConfig.URL_NEARBY;
	/**
	 * 9.小区任务包
	 */
	public static final String COMMUNITY_PACKAGE_LIST = SystemConfig.IP+ BusinessConfig.URL_COMMUNITY_PACKAGE_LIST;
	/**
	 * 10.忘记密码（验证手机号／短信验证码）
	 */
	public static final String LOST_PASSWORD = SystemConfig.IP+ BusinessConfig.URL_LOST_PASSWORD;
	/**
	 * 11.忘记密码（更新密码）
	 */
	public static final String UPDATE_PASSWORD = SystemConfig.IP+ BusinessConfig.URL_UPDATE_PASSWORD;
	/**
	 * 12.任务单(未完成)
	 */
	public static final String TASK_LIST_NONE = SystemConfig.IP+ BusinessConfig.URL_TASK_LIST_NONE;
	/**
	 * 13.任务包详情
	 */
	public static final String TASK_PACKAGE_DETAIL = SystemConfig.IP+ BusinessConfig.URL_TASK_PACKAGE_DETAIL;
	/**
	 * 14.扫描二维码，查询任务包中的电梯
	 */
	public static final String QUERY_TASK_PACKAGE_ELEVATOR = SystemConfig.IP+ BusinessConfig.URL_QUERY_TASK_PACKAGE_ELEVATOR;
	/**
	 * 14.电梯详情
	 */
	public static final String ELEVATOR_DETAIL = SystemConfig.IP+ BusinessConfig.URL_ELEVATOR_DETAIL;
	/**
	 * 15.任务单退单（验证维保时间）
	 */
	public static final String CHARGE_BACK_TASK_VALIDATE_TIME=SystemConfig.IP+BusinessConfig.URL_CHARGE_BACK_TASK_VALIDATE_TIME;
	/**
	 * 16.任务单（退单）
	 */
	public static final String CHARGE_BACK_TASK = SystemConfig.IP+ BusinessConfig.URL_CHARGE_BACK_TASK;
	/**
	 * 17.任务单（已完成）
	 */
	public static final String TASK_LIST_DONE = SystemConfig.IP+ BusinessConfig.URL_TASK_LIST_DONE;
	/**
	 * 18.任务单（开始）--->指向最后一个界面
	 */
	public static final String TASK_START = SystemConfig.IP+ BusinessConfig.URL_TASK_START;
	/**
	 * 19.根据工作类型获取工作内容
	 */
	public static final String WORK_TYPE = SystemConfig.IP+ BusinessConfig.URL_WORK_TYPE;
	/**
	 * 20.维保清单完成
	 */
	public static final String MAINTENCE_LIST_COMPLETE = SystemConfig.IP+ BusinessConfig.URL_MAINTENCE_LIST_COMPLETE;
	/**
	 * 21.问题反馈
	 */
	public static final String PROBLEMS_FEEDBACK = SystemConfig.IP+ BusinessConfig.URL_PROBLEMS_FEEDBACK;
	/**
	 * 22.互相评价
	 */
	public static final String EVALUATION_EACH_OTHER = SystemConfig.IP+ BusinessConfig.URL_EVALUATION_EACH_OTHER;
	/**
	 * 23.我的
	 */
	public static final String PERSONAL_AVATOR = SystemConfig.IP+ BusinessConfig.URL_PERSONAL_AVATOR;
	/**
	 * 24.账户信息
	 */
	public static final String PERSONAL_ACCOUNT = SystemConfig.IP+ BusinessConfig.URL_PERSONAL_ACCOUNT;
	/**
	 * 25.更换手机号（验证密码）
	 */
	public static final String CHANGE_PHONENO_PASSWORD = SystemConfig.IP+ BusinessConfig.URL_CHANGE_PHONENO_PASSWORD;
	/**
	 * 26.更换手机号（验证新手机号是否存在）
	 */
	public static final String CHANGE_PHONENO_VALICATE = SystemConfig.IP+ BusinessConfig.URL_CHANGE_PHONENO_VALIDATE;
	/**
	 * 27.更换手机号（更新手机号）
	 */
	public static final String CHANGE_PHONENO_UPDATE = SystemConfig.IP+ BusinessConfig.URL_CHANGE_PHONENO_UPDATE;
	/**
	 * 28.我的账单（已结算／未结算）
	 */
	public static final String PERSONAL_BILL = SystemConfig.IP+ BusinessConfig.URL_PERSONAL_BILL;
	/**
	 * 29.奖励记录
	 */
	public static final String PERSONAL_BONUS_LIST = SystemConfig.IP+ BusinessConfig.URL_PERSONAL_BONUS_LIST;
	/**
	 * 30.退单纪录
	 */
	public static final String PERSONAL_CHARGEBACK_LIST = SystemConfig.IP+ BusinessConfig.URL_PERSONAL_CHARGEBACK_LIST;
	/**
	 * 31.公告
	 */
	public static final String PERSONAL_ANNOUNCEMENT=SystemConfig.IP+BusinessConfig.URL_PERSONAL_ANNOUNCEMENT;
	/**
	 * 32.公告详情
	 */
	public static final String PERSONAL_ANNOUNCEMENT_DETAIL=SystemConfig.IP+BusinessConfig.URL_PERSONAL_ANNOUNCEMENT_DETAIL;
	/**
	 * 33.获取短信验证码
	 */
	public static final String COMMON_GET_SMS_CODE=SystemConfig.IP+BusinessConfig.URL_COMMON_GET_SMS_CODE;
	/**
	 * 34.验证短信验证码
	 */
	public static final String COMMON_VARLICATE_SMS_CODE=SystemConfig.IP+BusinessConfig.URL_COMMON_VARLICATE_SMS_CODE;
	
}
