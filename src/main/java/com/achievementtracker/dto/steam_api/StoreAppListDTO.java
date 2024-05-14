package com.achievementtracker.dto.steam_api;

import com.achievementtracker.deserializer.StoreAppListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = StoreAppListDeserializer.class)
public class StoreAppListDTO {
    private List<StoreAppDTO> storeApps;
    private boolean hasMoreResults;
    private Long lastAppId;

    public boolean hasMoreResults() {
        return hasMoreResults;
    }

    public void setHasMoreResults(boolean hasMoreResults) {
        this.hasMoreResults = hasMoreResults;
    }

    public Long getLastAppId() {
        return lastAppId;
    }

    public void setLastAppId(Long lastAppId) {
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
            return "StoreAppDTO{" +
                    "storeId=" + storeId +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
