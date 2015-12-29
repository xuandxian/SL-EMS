package com.overtech.ems.entity.parttime;
/**
 * 奖励信息实体
 * @author Will
 *
 */
public class Bonus {
	private String awardRemark;
	private String awardSum;
	private long awardTime;
	public String getAwardRemark() {
		return awardRemark;
	}
	public void setAwardRemark(String awardRemark) {
		this.awardRemark = awardRemark;
	}
	public String getAwardSum() {
		return awardSum;
	}
	public void setAwardSum(String awardSum) {
		this.awardSum = awardSum;
	}
	public long getAwardTime() {
		return awardTime;
	}
	public void setAwardTime(long awardTime) {
		this.awardTime = awardTime;
	}
	
}
