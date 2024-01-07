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
        private List<AppDetails> appDetails;

        public List<AppDetails> getAppDetails() {
            return appDetails;
        }

        public void setAppDetails(List<AppDetails> appDetails) {
            this.appDetails = appDetails;
        }

        @Override
        public String toString() {
            return "AppList{" +
                    "appDetails=" + appDetails +
                    '}';
        }

        public static class AppDetails {
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
                return "AppDetails{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}
