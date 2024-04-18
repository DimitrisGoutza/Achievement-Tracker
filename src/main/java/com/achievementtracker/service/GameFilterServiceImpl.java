package com.achievementtracker.service;

import com.achievementtracker.dao.AchievementDAO;
import com.achievementtracker.dao.CategoryDAO;
import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.FilterData;
import com.achievementtracker.dto.SelectedFilterData;
import com.achievementtracker.entity.Achievement;
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
    public List<Game> getFilteredGames(SelectedFilterData selectedFilterData, Page page) {
        String searchTerm = selectedFilterData.getSearchTerm();
        List<Long> categoryIds = selectedFilterData.getCategoryIds();
        boolean achievements = selectedFilterData.isAchievements();
        boolean hiddenAchievements = selectedFilterData.isHiddenAchievements();
        Integer minReviews = selectedFilterData.getMinReviews();
        Integer maxReviews = selectedFilterData.getMaxReviews();

        if (categoryIds.isEmpty()) { // No categories
            if (achievements) {
                if (hiddenAchievements)
                    return gameDAO.findOnlyGamesWithHiddenAchievements(searchTerm, minReviews, maxReviews, page);
                else
                    return gameDAO.findOnlyGamesWithAchievements(searchTerm, minReviews, maxReviews, page);
            } else {
                return gameDAO.findAllGames(searchTerm, minReviews, maxReviews, page);
            }
        } else { // Categorized
            if (achievements) {
                if (hiddenAchievements)
                    return gameDAO.findOnlyGamesWithHiddenAchievementsByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, page);
                else
                    return gameDAO.findOnlyGamesWithAchievementsByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, page);
            } else {
                return gameDAO.findAllGamesByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, page);
            }
        }
    }

    @Override
    public Map<Long, List<Achievement>> getTopXAchievementsForGames(int topAmount, List<Game> games) {
        List<Long> gameIds = games.stream().map(Game::getStoreId).toList();
        return achievementDAO.getTopXAchievementsForGames(topAmount, gameIds);
    }

    @Override
    public Long getGameEntryCount() {
        return gameDAO.getCount();
    }

    @Override
    public FilterData getFilterData() {
        return new FilterData(
                categoryDAO.findAllSortedByPopularity(),
                gameDAO.findMaxReviews()
        );
    }
}
