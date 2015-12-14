package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/14.
 * 我的账单
 */
public class MyBill {
    private String id;
    private String uid;
    private String maintenanceNo;
    private String taskPackageName;
    private String maintenanceTime;
    private String maintenanceMoney;
    private String accountTime;
    private String ifAccount;

    public MyBill(String id, String uid, String maintenanceNo, String taskPackageName, String maintenanceTime, String maintenanceMoney, String accountTime, String ifAccount) {
        this.id = id;
        this.uid = uid;
        this.maintenanceNo = maintenanceNo;
        this.taskPackageName = taskPackageName;
        this.maintenanceTime = maintenanceTime;
        this.maintenanceMoney = maintenanceMoney;
        this.accountTime = accountTime;
        this.ifAccount = ifAccount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMaintenanceNo() {
        return maintenanceNo;
    }

    public void setMaintenanceNo(String maintenanceNo) {
        this.maintenanceNo = maintenanceNo;
    }

    public String getTaskPackageName() {
        return taskPackageName;
    }

    public void setTaskPackageName(String taskPackageName) {
        this.taskPackageName = taskPackageName;
    }

    public String getMaintenanceTime() {
        return maintenanceTime;
    }

    public void setMaintenanceTime(String maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }

    public String getMaintenanceMoney() {
        return maintenanceMoney;
    }

    public void setMaintenanceMoney(String maintenanceMoney) {
        this.maintenanceMoney = maintenanceMoney;
    }

    public String getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(String accountTime) {
        this.accountTime = accountTime;
    }

    public String getIfAccount() {
        return ifAccount;
    }

    public void setIfAccount(String ifAccount) {
        this.ifAccount = ifAccount;
    }
}
