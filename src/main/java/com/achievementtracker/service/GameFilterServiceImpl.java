package com.achievementtracker.service;

import com.achievementtracker.dao.CategoryDAO;
import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.SelectedFilterData;
import com.achievementtracker.entity.Category;
import com.achievementtracker.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameFilterServiceImpl implements GameFilterService {
    private final GameDAO gameDAO;
    private final CategoryDAO categoryDAO;

    @Autowired
    public GameFilterServiceImpl(GameDAO gameDAO, CategoryDAO categoryDAO) {
        this.gameDAO = gameDAO;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public List<Category> getAvailableCategories() {
        return categoryDAO.findAllSortedByPopularity();
    }

    @Override
    public List<Game> getFilteredGames(SelectedFilterData selectedFilterData, Page page) {
        String searchTerm = selectedFilterData.getSearchTerm();
        List<Long> categoryIds = selectedFilterData.getCategoryIds();
        boolean achievementsOnly = selectedFilterData.isAchievementsOnly();

        if (!categoryIds.isEmpty())
            return searchTerm.isEmpty() ? gameDAO.findAll(achievementsOnly, page)
                    : gameDAO.findAll(searchTerm, achievementsOnly, page);
        else
            return searchTerm.isEmpty() ? gameDAO.findAllByCategoryId(categoryIds, achievementsOnly, page)
                    : gameDAO.findAllByCategoryId(searchTerm, categoryIds, achievementsOnly, page);
    }
}
