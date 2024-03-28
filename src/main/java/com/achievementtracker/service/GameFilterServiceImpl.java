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
        List<Long> categoryIds = selectedFilterData.getCategoryIds();

        if (!categoryIds.isEmpty())
            return gameDAO.findAllByCategoryId(categoryIds, page);
        else
            return gameDAO.findAll(page);
    }
}
