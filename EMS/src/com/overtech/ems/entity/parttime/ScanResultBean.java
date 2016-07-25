package com.overtech.ems.entity.parttime;

import java.util.List;

/**
 * 维保开始时候 二维码扫描开始查询任务单 解析实体类
 * @author Overtech Will
 *
 */
public class ScanResultBean {
	public int st;
	public String msg;
	public Body body;
	public class Body{
		public List<BeginWorkResult> data;
		public String isMeetRequire;
		public String feedbacked;//是否已经完成了维修的电梯
	}
	public class BeginWorkResult{
		public String elevatorNo;
		public String taskNo;
		public String workType;
		public String zonePhone;
		public String isStart;
		public String isFinish;
		public String longitude;
		public String latitude;
	}
}
