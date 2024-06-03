package com.achievementtracker.service;

import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.AchievementTier;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AchievementAnalyticsService {
    Double calculateAverageAchievementCompletion(Collection<Achievement> achievements);
    int calculateChallengeRating(Collection<Achievement> achievements);
    Double calculateDifficultySpread(Collection<Achievement> achievements);
    Map<AchievementTier, Integer> getAchievementCountPerTier(List<Achievement> achievements);
}