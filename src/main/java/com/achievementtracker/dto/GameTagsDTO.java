package com.achievementtracker.dto;

import com.achievementtracker.deserializer.GameTagDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = GameTagDeserializer.class)
public class GameTagsDTO {
    private List<TagDetailsDTO> tags;

    public List<TagDetailsDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDetailsDTO> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "GameTagsDTO{" +
                "tags=" + tags +
                '}';
    }

    public static class TagDetailsDTO {
        private String name;
        private Integer votes;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getVotes() {
            return votes;
        }

        public void setVotes(Integer votes) {
            this.votes = votes;
        }

        @Override
        public String toString() {
            return "TagDetailsDTO{" +
                    "name='" + name + '\'' +
                    ", votes=" + votes +
                    '}';
        }
    }
}
