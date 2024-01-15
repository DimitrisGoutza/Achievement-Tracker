package com.achievementtracker.dto;

import com.achievementtracker.deserializer.GameTagDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonDeserialize(using = GameTagDeserializer.class)
public class GameTagsDTO {
    private Map<String, Integer> tags;

    public Map<String, Integer> getTags() {
        return tags;
    }

    public void setTags(Map<String, Integer> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "GameTagsDTO{" +
                "tags=" + tags +
                '}';
    }
}
