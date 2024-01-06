package com.achievementtracker.model.test;

public class SchemaForGame {
    private int gameId;
    private String name;

    public SchemaForGame(int gameId, String name) {
        this.gameId = gameId;
        this.name = name;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
