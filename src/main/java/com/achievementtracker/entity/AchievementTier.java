package com.achievementtracker.entity;

public enum AchievementTier {
    COMMON(100, 50, 1, "#A8A8A8"),
    UNCOMMON(50, 25, 2, "#0C9321"),
    RARE(25, 10, 5, "#0091FA"),
    EPIC(10, 5, 10, "#B233C3"),
    LEGENDARY(5, 1, 50, "#DAB842"),
    MYTHIC(1, 0, 100, "#FF3246");

    private final double maxPercentage;
    private final double minPercentage;
    private final int weight;
    private final String colorHex;

    AchievementTier(double maxPercentage, double minPercentage, int weight, String colorHex) {
        this.maxPercentage = maxPercentage;
        this.minPercentage = minPercentage;
        this.weight = weight;
        this.colorHex = colorHex;
    }

    public double getMaxPercentage() {
        return maxPercentage;
    }

    public double getMinPercentage() {
        return minPercentage;
    }

    public int getWeight() {
        return weight;
    }

    public String getColorHex() {
        return colorHex;
    }

    public static AchievementTier fromPercentage(double percentage) {
        for (AchievementTier tier : AchievementTier.values()) {
            if (percentage >= tier.minPercentage && percentage < tier.maxPercentage)
                return tier;
        }
        return AchievementTier.COMMON;
    }
}
