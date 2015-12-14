package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/14.
 */
public class MaintenanceType {
    private String id;
    private String maintenanceType;
    private String maintenanceContent;

    public MaintenanceType(String id, String maintenanceType, String maintenanceContent) {
        this.id = id;
        this.maintenanceType = maintenanceType;
        this.maintenanceContent = maintenanceContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getMaintenanceContent() {
        return maintenanceContent;
    }

    public void setMaintenanceContent(String maintenanceContent) {
        this.maintenanceContent = maintenanceContent;
    }
}
