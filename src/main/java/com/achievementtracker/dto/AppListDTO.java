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
        private int id;
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
            return "AppDetailsDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
