package com.overtech.ems.entity.common;

import com.overtech.ems.config.SystemConfig;

public class ServicesConfig {
	/**
	 * 登录
	 */
	public static final String LOGIN = SystemConfig.IP + BusinessConfig.URL_LOGIN;
	/**
	 * 注册
	 */
	public static final String REGISTER = SystemConfig.IP + BusinessConfig.URL_REGISTER;
	/**
	 * 重置密码
	 */
	public static final String RESET_PASSWORD = SystemConfig.IP + BusinessConfig.URL_RESET_PASSWORD;

}
