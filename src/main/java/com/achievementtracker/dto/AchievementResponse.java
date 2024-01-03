package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AchievementResponse {
    @JsonProperty("achievementpercentages")
    private AchievementPercentages achievementPercentages;

    public AchievementPercentages getAchievementPercentages() {
        return achievementPercentages;
    }

    public void setAchievementPercentages(AchievementPercentages achievementPercentages) {
        this.achievementPercentages = achievementPercentages;
    }

    @Override
    public String toString() {
        return "AchievementResponse{" +
                "achievementPercentages=" + achievementPercentages +
                '}';
    }
}
