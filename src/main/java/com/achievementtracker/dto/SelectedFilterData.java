package com.achievementtracker.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectedFilterData {
    private String searchTerm;
    private List<Long> categoryIds = new ArrayList<>();
    private boolean achievementsOnly;

    public SelectedFilterData() {
    }

    public SelectedFilterData(String searchTerm, List<Long> categoryIds, boolean achievementsOnly) {
        this.searchTerm = searchTerm;
        this.categoryIds = categoryIds;
        this.achievementsOnly = achievementsOnly;
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

    public boolean isAchievementsOnly() {
        return achievementsOnly;
    }

    public void setAchievementsOnly(boolean achievementsOnly) {
        this.achievementsOnly = achievementsOnly;
    }
}
