package com.achievementtracker.dto;

import com.achievementtracker.deserializer.AchievementStatListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonDeserialize(using = AchievementStatListDeserializer.class)
public class AchievementStatsDTO {
    private Map<String, Double> achievements;

    public Map<String, Double> getAchievements() {
        return achievements;
    }

    public void setAchievements(Map<String, Double> achievements) {
        this.achievements = achievements;
    }

    @Override
    public String toString() {
        return "AchievementStatsDTO{" +
                "achievements=" + achievements +
                '}';
    }
}
