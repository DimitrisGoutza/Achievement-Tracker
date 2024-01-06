package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GameSchemaResponse {
    @JsonProperty("game")
    private Game game;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "GameSchemaResponse{" +
                "game=" + game +
                '}';
    }

    public static class Game {
        @JsonProperty("gameName")
        private String name;
        @JsonProperty("availableGameStats")
        private AvailableGameStats availableGameStats;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public AvailableGameStats getAvailableGameStats() {
            return availableGameStats;
        }

        public void setAvailableGameStats(AvailableGameStats availableGameStats) {
            this.availableGameStats = availableGameStats;
        }

        @Override
        public String toString() {
            return "Game{" +
                    "name='" + name + '\'' +
                    ", availableGameStats=" + availableGameStats +
                    '}';
        }

        public static class AvailableGameStats {
            @JsonProperty("achievements")
            private List<AchievementDetail> achievements;

            public List<AchievementDetail> getAchievements() {
                return achievements;
            }

            public void setAchievements(List<AchievementDetail> achievements) {
                this.achievements = achievements;
            }

            @Override
            public String toString() {
                return "AvailableGameStats{" +
                        "achievements=" + achievements +
                        '}';
            }

            public static class AchievementDetail {
                @JsonProperty("name")
                private String name;
                @JsonProperty("displayName")
                private String displayName;
                @JsonProperty("hidden")
                private int hidden;
                @JsonProperty("description")
                private String description;
                @JsonProperty("icon")
                private String iconUrl;
                @JsonProperty("icongray")
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

                public int getHidden() {
                    return hidden;
                }

                public void setHidden(int hidden) {
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
                    return "AchievementDetail{" +
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
    }
}
