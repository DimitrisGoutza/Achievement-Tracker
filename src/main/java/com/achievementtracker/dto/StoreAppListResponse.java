package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StoreAppListResponse {
    @JsonProperty("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "StoreAppListResponse{" +
                "response=" + response +
                '}';
    }

    public static class Response {
        @JsonProperty("apps")
        List<AppDetails> apps;

        public List<AppDetails> getApps() {
            return apps;
        }

        public void setApps(List<AppDetails> apps) {
            this.apps = apps;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "apps=" + apps +
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
