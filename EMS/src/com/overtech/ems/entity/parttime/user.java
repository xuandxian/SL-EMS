package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/9.
 */
public class User {
    private String name;
    private String password;
    
	public User(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
