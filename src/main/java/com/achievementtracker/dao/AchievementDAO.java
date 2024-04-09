package com.achievementtracker.dao;

import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Game;

import java.util.List;
import java.util.Map;

public interface AchievementDAO extends GenericDAO<Achievement, Long> {
    Long getCountForGame(Game game);
    Map<Long, List<Achievement>> getTopXAchievementsForGames(int topAmount, List<Long> gameIds);
}
