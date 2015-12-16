package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/16.
 * 任务包
 */
public class TaskPackage {
    private String taskNo;             //维保单号
    private String projectName;        //项目名称
    private String elevatorAmounts;    //电梯数量
    private String maintenanceAddress; //维保地点
    private String longitude;          //经度
    private String latitude;           //纬度
    private String distances;          //距离
    private String maintenanceDate;    //维保日期

    public TaskPackage(String taskNo, String projectName, String elevatorAmounts, String maintenanceAddress, String longitude, String latitude, String distances, String maintenanceDate) {
        this.taskNo = taskNo;
        this.projectName = projectName;
        this.elevatorAmounts = elevatorAmounts;
        this.maintenanceAddress = maintenanceAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.distances = distances;
        this.maintenanceDate = maintenanceDate;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getElevatorAmounts() {
        return elevatorAmounts;
    }

    public void setElevatorAmounts(String elevatorAmounts) {
        this.elevatorAmounts = elevatorAmounts;
    }

    public String getMaintenanceAddress() {
        return maintenanceAddress;
    }

    public void setMaintenanceAddress(String maintenanceAddress) {
        this.maintenanceAddress = maintenanceAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDistances() {
        return distances;
    }

    public void setDistances(String distances) {
        this.distances = distances;
    }

    public String getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(String maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }
}
