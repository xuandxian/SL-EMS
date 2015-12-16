package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/16.
 */
public class TaskPackageDetail {
    private String elevatorName;       //电梯名称
    private String elevatorBrand;      //电梯品牌
    private String elevatorNo;         //电梯编号
    private String elevatorFloor;      //电梯层站
    private String partnerPhone;       //搭档电弧（主要用于我的任务单）

    public TaskPackageDetail(String elevatorName, String elevatorBrand, String elevatorNo, String elevatorFloor, String partnerPhone) {
        this.elevatorName = elevatorName;
        this.elevatorBrand = elevatorBrand;
        this.elevatorNo = elevatorNo;
        this.elevatorFloor = elevatorFloor;
        this.partnerPhone = partnerPhone;
    }

    public String getElevatorName() {
        return elevatorName;
    }

    public void setElevatorName(String elevatorName) {
        this.elevatorName = elevatorName;
    }

    public String getElevatorBrand() {
        return elevatorBrand;
    }

    public void setElevatorBrand(String elevatorBrand) {
        this.elevatorBrand = elevatorBrand;
    }

    public String getElevatorNo() {
        return elevatorNo;
    }

    public void setElevatorNo(String elevatorNo) {
        this.elevatorNo = elevatorNo;
    }

    public String getElevatorFloor() {
        return elevatorFloor;
    }

    public void setElevatorFloor(String elevatorFloor) {
        this.elevatorFloor = elevatorFloor;
    }

    public String getPartnerPhone() {
        return partnerPhone;
    }

    public void setPartnerPhone(String partnerPhone) {
        this.partnerPhone = partnerPhone;
    }
}
