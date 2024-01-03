package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AchievementPercentages {
    @JsonProperty("achievements")
    List<Achievement> achievements;

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @Override
    public String toString() {
        return "AchievementPercentages{" +
                "achievements=" + achievements +
                '}';
    }
}
