package com.achievementtracker.dto;

import com.achievementtracker.deserializer.StoreAppListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = StoreAppListDeserializer.class)
public class StoreAppListDTO {
    private List<StoreAppDTO> storeApps;
    private boolean hasMoreResults;
    private int lastAppId;

    public boolean hasMoreResults() {
        return hasMoreResults;
    }

    public void setHasMoreResults(boolean hasMoreResults) {
        this.hasMoreResults = hasMoreResults;
    }

    public int getLastAppId() {
        return lastAppId;
    }

    public void setLastAppId(int lastAppId) {
        this.lastAppId = lastAppId;
    }

    public List<StoreAppDTO> getStoreApps() {
        return storeApps;
    }

    public void setStoreApps(List<StoreAppDTO> storeApps) {
        this.storeApps = storeApps;
    }

    @Override
    public String toString() {
        return "StoreAppListDTO{" +
                "storeApps=" + storeApps +
                ", hasMoreResults=" + hasMoreResults +
                ", lastAppId=" + lastAppId +
                '}';
    }

    public static class StoreAppDTO {
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
            return "StoreAppDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
