package com.overtech.ems.config;

public class UrlConfig {
	/**
	 * 登录
	 */
	public static final String URL_LOGIN = IpConfig.IP+"appUserAction!appLogin.sat";
	/**
	 * 注册
	 */
	public static final String URL_REGISTER = IpConfig.IP+"appUserAction!appRegistUser.sat";
	/**
	 * 重置密码
	 */
	public static final String URL_RESET_PASSWORD = IpConfig.IP+"appUserAction!updateAppPasswordByLoginNameOrPhoneNo.sat";

}
