package com.achievementtracker.service;

import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.SelectedFilterData;
import com.achievementtracker.dto.UsefulFilterData;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Game;

import java.util.List;
import java.util.Map;

public interface GameFilterService {
    List<Game> getFilteredGames(SelectedFilterData selectedFilterData, Page page);
    Map<Long, List<Achievement>> getTopXAchievementsForGames(int topAmount, List<Game> games);
    Long getGameEntryCount();
    UsefulFilterData getUsefulFilterData();
}
