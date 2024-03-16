package com.achievementtracker.service;

import com.achievementtracker.entity.Achievement;

import java.util.Collection;

public interface AchievementAnalyticsService {
    Double calculateAverageAchievementCompletion(Collection<Achievement> achievements);
    int calculateChallengeRating(Collection<Achievement> achievements);
    Double calculateDifficultySpread(Collection<Achievement> achievements);
}
