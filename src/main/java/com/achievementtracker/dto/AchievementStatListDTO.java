package com.achievementtracker.dto;

import com.achievementtracker.deserializer.AchievementStatListDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = AchievementStatListDeserializer.class)
public class AchievementStatListDTO {
    private List<AchievementStatsDTO> achievements;

    public List<AchievementStatsDTO> getAchievementStats() {
        return achievements;
    }

    public void setAchievementStats(List<AchievementStatsDTO> achievementStatDTOS) {
        this.achievements = achievementStatDTOS;
    }

    @Override
    public String toString() {
        return "AchievementStatListDTO{" +
                "achievements=" + achievements +
                '}';
    }

    public static class AchievementStatsDTO {
        private String name;
        private double percentage;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        @Override
        public String toString() {
            return "AchievementStats{" +
                    "name='" + name + '\'' +
                    ", percentage=" + percentage +
                    '}';
        }
    }
}
