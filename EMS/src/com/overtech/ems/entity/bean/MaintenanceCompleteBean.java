package com.overtech.ems.entity.bean;
/**
 * 任务完成后解析返回json
 * @author Overtech Will
 *
 */
public class MaintenanceCompleteBean {
	public int st;
	public String msg;
	public Body body;
	public class Body{
		public String isAllCompleted;//1全部电梯已经完成 0尚有未完成的
		public String updateStatus;//电梯完成状态更新结果 0更新失败 1更新成功
		public String taskStatus;//任务包中的电梯完成状态 0还有未完成的  1任务包中的电梯全部完成
	}
}
