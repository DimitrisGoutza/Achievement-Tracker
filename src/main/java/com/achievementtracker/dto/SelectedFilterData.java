package com.achievementtracker.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectedFilterData {
    private String searchTerm;
    private List<Long> categoryIds = new ArrayList<>();
    private boolean achievements;
    private boolean hiddenAchievements;
    private final Integer defaultMinReviews;
    private final Integer defaultMaxReviews;
    private Integer minReviews;
    private Integer maxReviews;

    public SelectedFilterData(String searchTerm, List<Long> categoryIds, boolean achievements,
                              boolean hiddenAchievements, Integer defaultMinReviews, Integer defaultMaxReviews,
                              Integer minReviews, Integer maxReviews) {
        this.searchTerm = searchTerm;
        this.categoryIds = categoryIds;
        this.achievements = achievements;
        this.hiddenAchievements = hiddenAchievements;
        this.defaultMinReviews = defaultMinReviews;
        this.defaultMaxReviews = defaultMaxReviews;
        this.minReviews = minReviews;
        this.maxReviews = maxReviews;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public boolean isAchievements() {
        return achievements;
    }

    public void setAchievements(boolean achievements) {
        this.achievements = achievements;
    }

    public boolean isHiddenAchievements() {
        return hiddenAchievements;
    }

    public void setHiddenAchievements(boolean hiddenAchievements) {
        this.hiddenAchievements = hiddenAchievements;
    }

    public Integer getDefaultMinReviews() {
        return defaultMinReviews;
    }

    public Integer getDefaultMaxReviews() {
        return defaultMaxReviews;
    }

    public Integer getMinReviews() {
        return minReviews;
    }

    public void setMinReviews(Integer minReviews) {
        this.minReviews = minReviews;
    }

    public Integer getMaxReviews() {
        return maxReviews;
    }

    public void setMaxReviews(Integer maxReviews) {
        this.maxReviews = maxReviews;
    }
}
