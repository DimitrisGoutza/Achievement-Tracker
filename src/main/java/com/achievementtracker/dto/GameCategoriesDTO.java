package com.achievementtracker.dto;

import com.achievementtracker.deserializer.GameCategoriesDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = GameCategoriesDeserializer.class)
public class GameCategoriesDTO {
    private List<CategoryDetailsDTO> categories;

    public List<CategoryDetailsDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDetailsDTO> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "GameCategoriesDTO{" +
                "categories=" + categories +
                '}';
    }

    public static class CategoryDetailsDTO {
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
            return "CategoryDetailsDTO{" +
                    "name='" + name + '\'' +
                    ", votes=" + votes +
                    '}';
        }
    }
}
