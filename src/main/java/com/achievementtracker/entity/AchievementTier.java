package com.achievementtracker.entity;

import java.awt.*;

public enum AchievementTier {
    COMMON(100, 50, 1, new Color(168,168,168)),
    UNCOMMON(50, 25, 2, new Color(12,147,33)),
    RARE(25, 10, 5, new Color(0,145,200)),
    EPIC(10, 5, 10, new Color(178,51,195)),
    LEGENDARY(5, 1, 50, new Color(218,184,66)),
    MYTHIC(1, 0, 100, new Color(255,50,70));

    private final double maxPercentage;
    private final double minPercentage;
    private final int weight;
    private final Color color;

    AchievementTier(double maxPercentage, double minPercentage, int weight, Color color) {
        this.maxPercentage = maxPercentage;
        this.minPercentage = minPercentage;
        this.weight = weight;
        this.color = color;
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

    public String getColor() {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    public static AchievementTier fromPercentage(double percentage) {
        for (AchievementTier tier : AchievementTier.values()) {
            if (percentage >= tier.minPercentage && percentage < tier.maxPercentage)
                return tier;
        }
        return AchievementTier.COMMON;
    }
}
