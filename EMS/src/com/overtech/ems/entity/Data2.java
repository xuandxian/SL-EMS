package com.overtech.ems.entity;

public class Data2 {
	public String elevtorName;
	public String elevtorProductor;
	public String elevtorNo;
	public String elevtorType;
	
	public Data2(String elevtorName, String elevtorProductor, String elevtorNo,
			String elevtorType) {
		super();
		this.elevtorName = elevtorName;
		this.elevtorProductor = elevtorProductor;
		this.elevtorNo = elevtorNo;
		this.elevtorType = elevtorType;
	}
	public String getElevtorName() {
		return elevtorName;
	}
	public void setElevtorName(String elevtorName) {
		this.elevtorName = elevtorName;
	}
	public String getElevtorProductor() {
		return elevtorProductor;
	}
	public void setElevtorProductor(String elevtorProductor) {
		this.elevtorProductor = elevtorProductor;
	}
	public String getElevtorNo() {
		return elevtorNo;
	}
	public void setElevtorNo(String elevtorNo) {
		this.elevtorNo = elevtorNo;
	}
	public String getElevtorType() {
		return elevtorType;
	}
	public void setElevtorType(String elevtorType) {
		this.elevtorType = elevtorType;
	}

}
