package com.achievementtracker.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectedFilterData {
    private String searchTerm;
    private List<Long> categoryIds = new ArrayList<>();
    private boolean achievements;
    private boolean hiddenAchievements;

    public SelectedFilterData() {
    }

    public SelectedFilterData(String searchTerm, List<Long> categoryIds, boolean achievements, boolean hiddenAchievements) {
        this.searchTerm = searchTerm;
        this.categoryIds = categoryIds;
        this.achievements = achievements;
        this.hiddenAchievements = hiddenAchievements;
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
}
