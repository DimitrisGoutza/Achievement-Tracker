package com.achievementtracker.dto;

import com.achievementtracker.deserializer.AppListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = AppListDeserializer.class)
public class AppListDTO {
    private List<AppDetailsDTO> apps;

    public List<AppDetailsDTO> getApps() {
        return apps;
    }

    public void setApps(List<AppDetailsDTO> apps) {
        this.apps = apps;
    }

    @Override
    public String toString() {
        return "AppListDTO{" +
                "apps=" + apps +
                '}';
    }

    public static class AppDetailsDTO {
        private Long storeId;
        private String name;

        public Long getStoreId() {
            return storeId;
        }

        public void setStoreId(Long storeId) {
            this.storeId = storeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "AppDetailsDTO{" +
                    "storeId=" + storeId +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
