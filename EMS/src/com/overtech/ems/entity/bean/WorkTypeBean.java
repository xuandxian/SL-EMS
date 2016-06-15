package com.overtech.ems.entity.bean;

import java.util.List;
/**
 *更新维保清单的解析实体
 * @author Overtech Will
 *
 */
public class WorkTypeBean {
	public int st;
	public String msg;
	public Body body;

	public class Body {
		public List<String> data;
	}
}
