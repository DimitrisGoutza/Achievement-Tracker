package com.achievementtracker.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectedFilterData {
    List<Long> categoryIds = new ArrayList<>();

    public SelectedFilterData() {
    }

    public SelectedFilterData(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

}
