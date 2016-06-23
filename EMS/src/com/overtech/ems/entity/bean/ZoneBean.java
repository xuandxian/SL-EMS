package com.overtech.ems.entity.bean;

import java.util.List;

/**
 * 解析区域的实体类
 * 
 * @author Overtech Will
 * 
 */
public class ZoneBean {
	public int st;
	public String msg;
	public Body body;

	public class Body {
		public List<City> data;
	}

	public class City {
		public String parentCode;
		public String parentName;
		public List<Zone> list;
	}

	public class Zone {
		public String code;
		public String name;
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return name;
		}
	}
}
