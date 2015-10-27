package com.overtech.ems.entity.test;


public class Data5 {
	private String imageStatusUrl;
	private String name;
	private String elevtorNum;
	private String hot;
	private String address;
	private String distance;
	private String date;
	

	public Data5(String imageStatusUrl, String name, String elevtorNum,
			String hot, String address, String distance, String date) {
		super();
		this.imageStatusUrl = imageStatusUrl;
		this.name = name;
		this.elevtorNum = elevtorNum;
		this.hot = hot;
		this.address = address;
		this.distance = distance;
		this.date = date;
	}
	public String getImageStatusUrl() {
		return imageStatusUrl;
	}
	public void setImageStatusUrl(String imageStatusUrl) {
		this.imageStatusUrl = imageStatusUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getElevtorNum() {
		return elevtorNum;
	}
	public void setElevtorNum(String elevtorNum) {
		this.elevtorNum = elevtorNum;
	}
	public String getHot() {
		return hot;
	}
	public void setHot(String hot) {
		this.hot = hot;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
