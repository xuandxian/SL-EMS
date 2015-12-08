package com.overtech.ems.entity.common;

import com.overtech.ems.config.SystemConfig;

public class ServicesConfig {
	/**
	 * 登录
	 */
	public static final String URL_LOGIN = SystemConfig.IP
			+ BusinessConfig.LOGIN;
	/**
	 * 注册
	 */
	public static final String URL_REGISTER = SystemConfig.IP
			+ BusinessConfig.REGISTER;
	/**
	 * 重置密码
	 */
	public static final String URL_RESET_PASSWORD = SystemConfig.IP
			+ BusinessConfig.RESET_PASSWORD;

}
