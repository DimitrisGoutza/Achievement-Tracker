package com.achievementtracker.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectedFilterData {
    List<Long> categoryIds = new ArrayList<>();
    boolean achievementsOnly = false;

    public SelectedFilterData() {
    }

    public SelectedFilterData(List<Long> categoryIds, boolean achievementsOnly) {
        this.categoryIds = categoryIds;
        this.achievementsOnly = achievementsOnly;
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
