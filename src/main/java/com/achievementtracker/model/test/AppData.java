package com.achievementtracker.model.test;

public class AppData {
    private int appId;
    private String name;

    public AppData(int appId, String name) {
        this.appId = appId;
        this.name = name;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
