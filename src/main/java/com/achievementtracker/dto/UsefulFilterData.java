package com.achievementtracker.dto;

import com.achievementtracker.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class UsefulFilterData {
    private List<Category> categories = new ArrayList<>();
    private int maxReviews;
    private String minReleaseDate;

    public UsefulFilterData() {
    }

    public UsefulFilterData(List<Category> categories, int maxReviews, String minReleaseDate) {
        this.categories = categories;
        this.maxReviews = maxReviews;
        this.minReleaseDate = minReleaseDate;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getMaxReviews() {
        return maxReviews;
    }

    public void setMaxReviews(int maxReviews) {
        this.maxReviews = maxReviews;
    }

    public String getMinReleaseDate() {
        return minReleaseDate;
    }

    public void setMinReleaseDate(String minReleaseDate) {
        this.minReleaseDate = minReleaseDate;
    }
}
