package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/14.
 * 退单纪录
 * 注：表名已修改
 */
public class ChargeBackTaskList {
    private String id;
    private String empId;
    private String maintenanceNo;
    private String taskPackageName;
    private String grabTime;
    private String chargeBackTime;

    public ChargeBackTaskList(String id, String empId, String maintenanceNo, String taskPackageName, String grabTime, String chargeBackTime) {
        this.id = id;
        this.empId = empId;
        this.maintenanceNo = maintenanceNo;
        this.taskPackageName = taskPackageName;
        this.grabTime = grabTime;
        this.chargeBackTime = chargeBackTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
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

    public String getGrabTime() {
        return grabTime;
    }

    public void setGrabTime(String grabTime) {
        this.grabTime = grabTime;
    }

    public String getChargeBackTime() {
        return chargeBackTime;
    }

    public void setChargeBackTime(String chargeBackTime) {
        this.chargeBackTime = chargeBackTime;
    }
}
