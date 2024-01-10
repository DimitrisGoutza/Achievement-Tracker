package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class GameTagResponse {
    @JsonProperty("tags")
    private Map<String, Integer> tags;

    public Map<String, Integer> getTags() {
        return tags;
    }

    public void setTags(Map<String, Integer> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "GameTagResponse{" +
                "tags=" + tags +
                '}';
    }
}
