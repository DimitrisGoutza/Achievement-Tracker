package com.achievementtracker.dto;

import com.achievementtracker.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class FilterData {
    private List<Category> categories = new ArrayList<>();
    private int maxReviews;

    public FilterData() {
    }

    public FilterData(List<Category> categories, int maxReviews) {
        this.categories = categories;
        this.maxReviews = maxReviews;
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
}
