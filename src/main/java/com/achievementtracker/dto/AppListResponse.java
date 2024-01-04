package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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

    public static class AppList {
        @JsonProperty("apps")
        private List<App> apps;

        public List<App> getApps() {
            return apps;
        }

        public void setApps(List<App> apps) {
            this.apps = apps;
        }

        @Override
        public String toString() {
            return "AppList{" +
                    "apps=" + apps +
                    '}';
        }

        public static class App {
            @JsonProperty("appid")
            private int id;
            @JsonProperty("name")
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                return "App{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}
