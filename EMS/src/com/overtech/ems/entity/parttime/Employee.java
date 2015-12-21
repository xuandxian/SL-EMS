package com.overtech.ems.entity.parttime;

import java.io.Serializable;

public class Employee implements Serializable{
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	private int gender;
	/**
	 * 电话号码
	 */
	private String phoneNo;
	/**
	 * 用户id
	 */
	private String loginName;
	/**
	 * 用户密码
	 */
	private String password;
	/**
	 * 身份证号码
	 */
	private String idcardNo;
	/**
	 * 上岗证编号
	 */
	private String workNo;
	
	/**
	 * 出生年月
	 */
	private String birthDate;
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 区域
	 */
	private String zone;
	/**
	 * 学历
	 */
	private String eduLevel;
	/**
	 * 目前单位
	 */
	private String workUnit;
	/**
	 * 入行时间
	 */
	private String entryTime;
	/**
	 * 工作年限
	 */
	private String workYears;
	/**
	 * 电梯品牌
	 */
	private String elevatorBrand;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginId) {
		this.loginName = loginId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIdcardNo() {
		return idcardNo;
	}
	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}
	public String getWorkNo() {
		return workNo;
	}
	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getEduLevel() {
		return eduLevel;
	}
	public void setEduLevel(String eduLevel) {
		this.eduLevel = eduLevel;
	}
	public String getWorkUnit() {
		return workUnit;
	}
	public void setWorkUnit(String workUnit) {
		this.workUnit = workUnit;
	}
	public String getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}
	public String getWorkYears() {
		return workYears;
	}
	public void setWorkYears(String workYears) {
		this.workYears = workYears;
	}
	public String getElevatorBrand() {
		return elevatorBrand;
	}
	public void setElevatorBrand(String elevatorBrand) {
		this.elevatorBrand = elevatorBrand;
	}
	
}
