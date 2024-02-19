package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;

public interface GameDAO extends GenericDAO<Game, Long> {
    Game retrieveLatestEntry();
}
