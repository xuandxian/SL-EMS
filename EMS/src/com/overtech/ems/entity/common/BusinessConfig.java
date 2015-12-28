package com.overtech.ems.entity.common;

/**
 * Constant of common project's name.
 * 
 * @author Tony
 *
 * @date 2015-12-09
 */
public class BusinessConfig {
	
	/**
	 * 1.登录
	 */
	public static final String URL_LOGIN = "/employee/login.action";
	/**
	 * 2.注册
	 */
	public static final String URL_REGISTER = "/employee/register.action";
	/**
	 * 3.抢单列表
	 */
	public static final String URL_GRABTASK = "/task/grabtask.action";
	/**
	 * 4.关键字搜索
	 */
	public static final String URL_DO_SEARCH = "/doSearch.action";
	/**
	 * 5.筛选
	 */
    public static final String URL_DO_FILTER = "/task/filtrateGrabtask.action";
	/**
	 * 6.抢单
	 */
    public static final String URL_DO_GRABTASK = "/doGrabTask.action";
	/**
	 * 7.附近
	 */
	public static final String URL_NEARBY = "/task/nearby.action";
	/**
	 * 8.小区任务包
	 */
	public static final String URL_COMMUNITY_PACKAGE_LIST = "/communityPackageList.action";
	/**
	 * 9.忘记密码（验证手机号／短信验证码）
	 */
	public static final String URL_LOST_PASSWORD = "/lostPassword.action";
	/**
	 * 10.忘记密码（更新密码）
	 */
	public static final String URL_UPDATE_PASSWORD = "/employee/updatePassword.action";
	/**
	 * 11.任务单(未完成)
	 */
	public static final String URL_TASK_LIST_NONE = "/taskListNone.action";
	/**
	 * 12.任务包详情
	 */
	public static final String URL_TASK_PACKAGE_DETAIL ="/task/taskPackageDetail.action";
	/**
	 * 13.电梯详情
	 */
	public static final String URL_ELEVATOR_DETAIL = "/elevatorDetail.action";
	/**
	 * 14.任务单（退单）
	 */
	public static final String URL_CHARGE_BACK_TASK = "/chargeBackTask.action";
	/**
	 * 15.任务单（已完成）
	 */
	public static final String URL_TASK_LIST_DONE = "/taskListDone.action";
	/**
	 * 16.任务单（开始）--->指向最后一个界面
	 */
	public static final String URL_TASK_START = "/taskStart.action";
	/**
	 * 17.维保清单完成
	 */
	public static final String URL_MAINTENCE_LIST_COMPLETE = "/maintenceListComplete.action";
	/**
	 * 18.问题反馈
	 */
	public static final String URL_PROBLEMS_FEEDBACK = "/feedBack.action";
	/**
	 * 19.互相评价
	 */
	public static final String URL_EVALUATION_EACH_OTHER = "/evaluationEachOther.action";
	/**
	 * 20.我的
	 */
	public static final String URL_PERSONAL_AVATOR="/employee/personalAvator.action";
	/**
	 * 21.账户信息
	 */
	public static final String URL_PERSONAL_ACCOUNT = "/employee/personalAccount.action";
	/**
	 * 22.更换手机号（验证密码）
	 */
	public static final String URL_CHANGE_PHONENO_PASSWORD = "/changePhoneNoPassword.action";
	/**
	 * 23.更换手机号（手机号码／短信验证码）
	 */
	public static final String URL_CHANGE_PHONENO_UPDATE = "/changePhoneNoUpdate.action";
	/**
	 * 24.我的账单（已结算／未结算）
	 */
    public static final String URL_PERSONAL_BILL = "/employee/personalBill.action";
	/**
	 * 25.奖励记录
	 */
	public static final String URL_PERSONAL_BONUS_LIST = "/employee/personalBonusList.action";
	/**
	 * 26.退单纪录
	 */
	public static final String URL_PERSONAL_CHARGEBACK_LIST = "/personalChargeBackList.action";

}
