package com.overtech.ems.entity.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求实体
 * 
 * @author Overtech Will
 * 
 */
public class Requester {
	public String os = "android";
	public String ver = "v1.0";
	public String lg = "zh";
	public int cmd;
	public String uid;
	public String pwd;
	public String certificate;
	public Map<String, Object> body = new HashMap<String, Object>();

}
