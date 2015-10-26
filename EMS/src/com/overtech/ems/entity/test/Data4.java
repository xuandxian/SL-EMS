package com.overtech.ems.entity.test;

public class Data4 {
	private String address;
	private String elevtorNum;
	private String addressName;
	private String distance;
	private String date;
	public Data4(String address, String elevtorNum, String addressName,
			String distance, String date) {
		super();
		this.address = address;
		this.elevtorNum = elevtorNum;
		this.addressName = addressName;
		this.distance = distance;
		this.date = date;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getElevtorNum() {
		return elevtorNum;
	}
	public void setElevtorNum(String elevtorNum) {
		this.elevtorNum = elevtorNum;
	}
	public String getAddressName() {
		return addressName;
	}
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
