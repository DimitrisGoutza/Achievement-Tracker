package com.achievementtracker.model.test;

import java.util.List;

public class AchievementsPerGame {
    private final int gameId;
    private final List<String> achievements;

    public AchievementsPerGame(int gameId, List<String> achievements) {
        this.gameId = gameId;
        this.achievements = achievements;
    }

    public int getGameId() {
        return gameId;
    }

    public List<String> getAchievements() {
        return achievements;
    }
}
