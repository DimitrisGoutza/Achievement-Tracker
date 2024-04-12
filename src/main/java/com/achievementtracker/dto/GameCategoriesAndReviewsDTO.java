package com.achievementtracker.dto;

import com.achievementtracker.deserializer.GameCategoriesDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = GameCategoriesDeserializer.class)
public class GameCategoriesAndReviewsDTO {
    private List<CategoryDetailsDTO> categories;
    private int positiveReviews;
    private int negativeReviews;

    public List<CategoryDetailsDTO> getCategories() {
        return categories;
    }

    public int getPositiveReviews() {
        return positiveReviews;
    }

    public int getNegativeReviews() {
        return negativeReviews;
    }

    public void setCategories(List<CategoryDetailsDTO> categories) {
        this.categories = categories;
    }

    public void setPositiveReviews(int positiveReviews) {
        this.positiveReviews = positiveReviews;
    }

    public void setNegativeReviews(int negativeReviews) {
        this.negativeReviews = negativeReviews;
    }

    @Override
    public String toString() {
        return "GameCategoriesAndReviewsDTO{" +
                "categories=" + categories +
                ", positiveReviews=" + positiveReviews +
                ", negativeReviews=" + negativeReviews +
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
