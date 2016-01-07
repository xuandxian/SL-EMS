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
	 * 0.RSA
	 */
	public static final String URL_RSA = "/employee/security.action";
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
	public static final String URL_GRABTASK = "/task/grabTaskList.action";
	/**
	 * 4.关键字搜索
	 */
	public static final String URL_DO_SEARCH = "/doSearch.action";
	/**
	 * 5.筛选
	 */
    public static final String URL_DO_FILTER = "/task/filtrateGrabtask.action";
    /**
	 * 6.关键字自动补全
	 */
    public static final String URL_KEY_WORD_AUTO = "/task/grabTaskListForAuto.action";
	/**
	 * 7.抢单
	 */
    public static final String URL_DO_GRABTASK = "/task/grabTask.action";
	/**
	 * 8.附近
	 */
	public static final String URL_NEARBY = "/task/nearby.action";
	/**
	 * 9.小区任务包
	 */
	public static final String URL_COMMUNITY_PACKAGE_LIST = "/communityPackageList.action";
	/**
	 * 10.忘记密码（验证手机号／短信验证码）
	 */
	public static final String URL_LOST_PASSWORD = "/employee/lostPassword.action";
	/**
	 * 11.忘记密码（更新密码）
	 */
	public static final String URL_UPDATE_PASSWORD = "/employee/updatePassword.action";
	/**
	 * 12.任务单(未完成)
	 */
	public static final String URL_TASK_LIST_NONE = "/task/taskListNone.action";
	/**
	 * 13.任务包详情
	 */
	public static final String URL_TASK_PACKAGE_DETAIL ="/task/taskPackageDetail.action";
	/**
	 * 14.电梯详情
	 */
	public static final String URL_ELEVATOR_DETAIL = "/elevatorDetail.action";
	/**
	 * 15.任务单（退单）
	 */
	public static final String URL_CHARGE_BACK_TASK = "/chargeBackTask.action";
	/**
	 * 16.任务单（已完成）
	 */
	public static final String URL_TASK_LIST_DONE = "/task/taskListDone.action";
	/**
	 * 17.任务单（开始）--->指向最后一个界面
	 */
	public static final String URL_TASK_START = "/taskStart.action";
	/**
	 * 17.任务单（开始）--->指向最后一个界面
	 */
	public static final String URL_WORK_TYPE = "/workType.action";
	/**
	 * 18.维保清单完成
	 */
	public static final String URL_MAINTENCE_LIST_COMPLETE = "/maintenceListComplete.action";
	/**
	 * 19.问题反馈
	 */
	public static final String URL_PROBLEMS_FEEDBACK = "/feedBack.action";
	/**
	 * 20.互相评价
	 */
	public static final String URL_EVALUATION_EACH_OTHER = "/evaluationEachOther.action";
	/**
	 * 21.我的
	 */
	public static final String URL_PERSONAL_AVATOR="/employee/personalAvator.action";
	/**
	 * 22.账户信息
	 */
	public static final String URL_PERSONAL_ACCOUNT = "/employee/personalAccount.action";
	/**
	 * 23.更换手机号（验证密码）
	 */
	public static final String URL_CHANGE_PHONENO_PASSWORD = "/changePhoneNoPassword.action";
	/**
	 * 24.更换手机号（手机号码／短信验证码）
	 */
	public static final String URL_CHANGE_PHONENO_UPDATE = "/changePhoneNoUpdate.action";
	/**
	 * 25.我的账单（已结算／未结算）
	 */
    public static final String URL_PERSONAL_BILL = "/employee/personalBill.action";
	/**
	 * 26.奖励记录
	 */
	public static final String URL_PERSONAL_BONUS_LIST = "/employee/personalBonusList.action";
	/**
	 * 27.退单纪录
	 */
	public static final String URL_PERSONAL_CHARGEBACK_LIST = "/employee/personalChargeBackList.action";

}
