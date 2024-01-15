package com.achievementtracker.dto;

import com.achievementtracker.deserializer.GameSchemaDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = GameSchemaDeserializer.class)
public class GameSchemaDTO {
    private String name;
    private List<AchievementDetailsDTO> achievements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AchievementDetailsDTO> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AchievementDetailsDTO> achievements) {
        this.achievements = achievements;
    }

    @Override
    public String toString() {
        return "GameSchemaDTO{" +
                "name='" + name + '\'' +
                ", achievements=" + achievements +
                '}';
    }

    public static class AchievementDetailsDTO {
        private String name;
        private String displayName;
        private boolean hidden;
        private String description;
        private String iconUrl;
        private String iconGrayUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getIconGrayUrl() {
            return iconGrayUrl;
        }

        public void setIconGrayUrl(String iconGrayUrl) {
            this.iconGrayUrl = iconGrayUrl;
        }

        @Override
        public String toString() {
            return "AchievementDetailsDTO{" +
                    "name='" + name + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", hidden=" + hidden +
                    ", description='" + description + '\'' +
                    ", iconUrl='" + iconUrl + '\'' +
                    ", iconGrayUrl='" + iconGrayUrl + '\'' +
                    '}';
        }
    }
}
