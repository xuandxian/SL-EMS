package com.overtech.ems.entity.parttime;

/**
 * Created by Tony1213 on 15/12/14.
 */
public class MaintenanceSite {
    private String id;
    private String maintenanceSiteName;
    private String maintenancePhone;

    public MaintenanceSite(String id, String maintenanceSiteName, String maintenancePhone) {
        this.id = id;
        this.maintenanceSiteName = maintenanceSiteName;
        this.maintenancePhone = maintenancePhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaintenanceSiteName() {
        return maintenanceSiteName;
    }

    public void setMaintenanceSiteName(String maintenanceSiteName) {
        this.maintenanceSiteName = maintenanceSiteName;
    }

    public String getMaintenancePhone() {
        return maintenancePhone;
    }

    public void setMaintenancePhone(String maintenancePhone) {
        this.maintenancePhone = maintenancePhone;
    }
}
