package com.achievementtracker.service;

import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.AchievementRarity;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AchievementAnalyticsServiceImpl implements AchievementAnalyticsService {
    @Override
    public Double calculateAverageAchievementCompletion(Collection<Achievement> achievements) {
        if (achievements.isEmpty())
            return null;

        double percentageTotal = 0.0;
        for (Achievement achievement : achievements) {
            percentageTotal += achievement.getPercentage();
        }
        double average = percentageTotal / achievements.size();
        return Math.round(average * 100.0) / 100.0;
    }

    @Override
    public int calculateChallengeRating(Collection<Achievement> achievements) {
        if (achievements.isEmpty())
            return 0;

        int score = 0;
        for (Achievement achievement : achievements) {
            score += AchievementRarity.fromPercentage(achievement.getPercentage()).getWeight();
        }
        return score;
    }

    @Override
    public Double calculateDifficultySpread(Collection<Achievement> achievements) {
        if (achievements.isEmpty())
            return null;

        double highestPercentage = achievements.stream().mapToDouble(Achievement::getPercentage).max().orElse(0);
        double lowestPercentage = achievements.stream().mapToDouble(Achievement::getPercentage).min().orElse(0);
        double difficultySpread = highestPercentage - lowestPercentage;

        return Math.round(difficultySpread * 100.0) / 100.0;
    }
}