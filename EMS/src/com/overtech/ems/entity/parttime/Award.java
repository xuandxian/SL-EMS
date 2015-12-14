package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/14.
 */
public class Award {
    private String id;
    private String empId;
    private String empName;
    private String awardSum;
    private String awardDate;
    private String awardRemark;
    private String closeingState;
    private String remark;

    public Award(String id, String empId, String empName, String awardSum, String awardDate, String awardRemark, String closeingState, String remark) {
        this.id = id;
        this.empId = empId;
        this.empName = empName;
        this.awardSum = awardSum;
        this.awardDate = awardDate;
        this.awardRemark = awardRemark;
        this.closeingState = closeingState;
        this.remark = remark;
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

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getAwardSum() {
        return awardSum;
    }

    public void setAwardSum(String awardSum) {
        this.awardSum = awardSum;
    }

    public String getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(String awardDate) {
        this.awardDate = awardDate;
    }

    public String getAwardRemark() {
        return awardRemark;
    }

    public void setAwardRemark(String awardRemark) {
        this.awardRemark = awardRemark;
    }

    public String getCloseingState() {
        return closeingState;
    }

    public void setCloseingState(String closeingState) {
        this.closeingState = closeingState;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
