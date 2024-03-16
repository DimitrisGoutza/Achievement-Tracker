package com.achievementtracker.service;

import com.achievementtracker.entity.Achievement;
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
            score += calculateWeight(achievement.getPercentage());
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

    private int calculateWeight(Double percentage) {
        if (percentage <= 100 && percentage >= 50)
            return 1;
        else if (percentage <= 50 && percentage >= 25)
            return 2;
        else if (percentage <=25 && percentage >= 10)
            return 5;
        else if (percentage <=10 && percentage >= 5)
            return 10;
        else if (percentage <= 5 && percentage >= 1)
            return 50;
        else
            return 100;
    }
}
