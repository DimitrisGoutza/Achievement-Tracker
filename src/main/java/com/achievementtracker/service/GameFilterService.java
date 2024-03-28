package com.achievementtracker.service;

import com.achievementtracker.dao.Page;
import com.achievementtracker.entity.Game;

import java.util.List;

public interface GameFilterService {
    List<Game> getFilteredGames(Page page);
}
