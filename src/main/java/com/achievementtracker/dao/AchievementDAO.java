package com.achievementtracker.dao;

import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Game;

public interface AchievementDAO extends GenericDAO<Achievement, Long> {
    Long getCountForGame(Game game);
}
