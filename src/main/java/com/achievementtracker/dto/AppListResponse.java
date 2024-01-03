package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppListResponse {
    @JsonProperty("applist")
    private AppList appList;

    public AppList getAppList() {
        return appList;
    }

    public void setAppList(AppList appList) {
        this.appList = appList;
    }

    @Override
    public String toString() {
        return "AppListResponse{" +
                "appList=" + appList +
                '}';
    }
}
