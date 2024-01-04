package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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

    public static class AchievementPercentages {
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

        public static class Achievement {
            @JsonProperty("name")
            private String name;
            @JsonProperty("percent")
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
                return "Achievement{" +
                        "name='" + name + '\'' +
                        ", percentage=" + percentage +
                        '}';
            }
        }
    }
}
