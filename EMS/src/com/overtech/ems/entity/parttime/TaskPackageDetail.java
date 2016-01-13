package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/16.
 */
public class TaskPackageDetail {
    private String elevatorName;       //电梯名称
    private String elevatorBrand;      //电梯品牌
    private String workType;           //维保类型    
    private String elevatorNo;         //电梯编号
    private String maintainPrice;      //维保价格
    private String elevatorFloor;      //电梯层站
    private String partnerPhone;       //搭档电话（主要用于我的任务单）
    private String isFinish;			   //电梯的完成状态（用于提交电梯完成）
    
	

	public TaskPackageDetail(String elevatorName, String elevatorBrand,
			String workType, String elevatorNo, String maintainPrice,
			String elevatorFloor, String partnerPhone) {
		super();
		this.elevatorName = elevatorName;
		this.elevatorBrand = elevatorBrand;
		this.workType = workType;
		this.elevatorNo = elevatorNo;
		this.maintainPrice = maintainPrice;
		this.elevatorFloor = elevatorFloor;
		this.partnerPhone = partnerPhone;
	}

	public String getMaintainPrice() {
		return maintainPrice;
	}

	public void setMaintainPrice(String maintainPrice) {
		this.maintainPrice = maintainPrice;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
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

	public String getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(String isFinish) {
		this.isFinish = isFinish;
	}
    
}
