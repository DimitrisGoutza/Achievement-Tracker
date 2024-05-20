package com.achievementtracker.entity;

public enum AchievementRarity {
    COMMON(100, 50, 1, "#A8A8A8"),
    UNCOMMON(50, 25, 2, "#006616"),
    RARE(25, 10, 5, "#50A7D3"),
    EPIC(10, 5, 10, "#B233C3"),
    LEGENDARY(5, 1, 50, "#CEAE39"),
    MYTHIC(1, 0, 100, "#FF4F29");

    private final double maxPercentage;
    private final double minPercentage;
    private final int weight;
    private final String colorHex;

    AchievementRarity(double maxPercentage, double minPercentage, int weight, String colorHex) {
        this.maxPercentage = maxPercentage;
        this.minPercentage = minPercentage;
        this.weight = weight;
        this.colorHex = colorHex;
    }

    public int getWeight() {
        return weight;
    }

    public String getHexColor() {
        return colorHex;
    }

    public static AchievementRarity fromPercentage(double percentage) {
        for (AchievementRarity rarity : AchievementRarity.values()) {
            if (percentage >= rarity.minPercentage && percentage < rarity.maxPercentage)
                return rarity;
        }
        return AchievementRarity.COMMON;
    }
}
