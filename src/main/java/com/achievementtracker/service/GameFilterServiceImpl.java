package com.achievementtracker.service;

import com.achievementtracker.dao.AchievementDAO;
import com.achievementtracker.dao.CategoryDAO;
import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.SelectedFilterData;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Category;
import com.achievementtracker.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// TODO : make this also prepare the String and Date attributes for View, or do it in JS
@Service
public class GameFilterServiceImpl implements GameFilterService {
    private final GameDAO gameDAO;
    private final CategoryDAO categoryDAO;

    private final AchievementDAO achievementDAO;

    @Autowired
    public GameFilterServiceImpl(GameDAO gameDAO, CategoryDAO categoryDAO, AchievementDAO achievementDAO) {
        this.gameDAO = gameDAO;
        this.categoryDAO = categoryDAO;
        this.achievementDAO = achievementDAO;
    }

    @Override
    public List<Category> getAvailableCategories(SelectedFilterData selectedFilterData, List<Game> games) {
        List<Long> selectedCategoryIds = selectedFilterData.getCategoryIds();
        List<Category> allCategories = categoryDAO.findAllSortedByPopularity();

        if (selectedCategoryIds.isEmpty()) {
            allCategories.forEach(category -> category.setAvailable(true));
        } else {
            List<Long> gameIds = games.stream().map(Game::getStoreId).toList();
            List<Long> availableCategoryIds = categoryDAO.findAvailableBasedOnFilteredGames(gameIds);
            for (Long categoryId : availableCategoryIds) {
                allCategories.stream().filter(category -> category.getId().equals(categoryId))
                        .findFirst().get().setAvailable(true);
            }
        }

        return allCategories;
    }

    @Override
    public List<Game> getFilteredGames(SelectedFilterData selectedFilterData, Page page) {
        String searchTerm = selectedFilterData.getSearchTerm();
        List<Long> categoryIds = selectedFilterData.getCategoryIds();
        boolean achievementsOnly = selectedFilterData.isAchievementsOnly();

        List<Game> games;
        if (categoryIds.isEmpty()) {
            games = searchTerm.isEmpty() ? gameDAO.findAll(achievementsOnly, page)
                    : gameDAO.findAll(searchTerm, achievementsOnly, page);
        }
        else {
            games = searchTerm.isEmpty() ? gameDAO.findAllByCategoryId(categoryIds, achievementsOnly, page)
                    : gameDAO.findAllByCategoryId(searchTerm, categoryIds, achievementsOnly, page);
        }
        return games;
    }

    @Override
    public Map<Long, List<Achievement>> getTopXAchievementsForGames(int topAmount, List<Game> games) {
        List<Long> gameIds = games.stream().map(Game::getStoreId).toList();
        return achievementDAO.getTopXAchievementsForGames(topAmount, gameIds);
    }
}
