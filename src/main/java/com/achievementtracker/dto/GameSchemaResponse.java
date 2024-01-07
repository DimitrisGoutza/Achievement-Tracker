package com.achievementtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GameSchemaResponse {
    @JsonProperty("game")
    private GameDetails gameDetails;

    public GameDetails getGameDetails() {
        return gameDetails;
    }

    public void setGameDetails(GameDetails gameDetails) {
        this.gameDetails = gameDetails;
    }

    @Override
    public String toString() {
        return "GameSchemaResponse{" +
                "gameDetails=" + gameDetails +
                '}';
    }

    public static class GameDetails {
        @JsonProperty("gameName")
        private String name;
        @JsonProperty("availableGameStats")
        private GameStats stats;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public GameStats getStats() {
            return stats;
        }

        public void setStats(GameStats stats) {
            this.stats = stats;
        }

        @Override
        public String toString() {
            return "GameDetails{" +
                    "name='" + name + '\'' +
                    ", stats=" + stats +
                    '}';
        }

        public static class GameStats {
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
                return "GameStats{" +
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
