package com.achievementtracker.service;

import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.SelectedFilterData;
import com.achievementtracker.entity.Category;
import com.achievementtracker.entity.Game;

import java.util.List;

public interface GameFilterService {
    List<Category> getAvailableCategories();
    List<Game> getFilteredGames(SelectedFilterData selectedFilterData, Page page);
}
