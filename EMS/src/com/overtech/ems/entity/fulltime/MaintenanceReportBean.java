package com.overtech.ems.entity.fulltime;

import java.io.Serializable;
import java.util.List;

/**
 * 维修报告单解析实体类
 * 
 * @author Overtech Will
 * 
 */
public class MaintenanceReportBean {
	public int st;
	public String msg;
	public Body body;

	public class Body {
		public List<Parent> datas;
	}

	public class Parent implements Serializable {
		public String checked;
		public String code;
		public String lv;
		public String name;
		public List<Children> children;
	}

	public class Children implements Serializable {
		public String checked;
		public String code;
		public String lv;
		public String name;
		public List<GrandSon> children;
	}

	public class GrandSon {
		public String checked;
		public String code;
		public String lv;
		public String name;
	}
}
