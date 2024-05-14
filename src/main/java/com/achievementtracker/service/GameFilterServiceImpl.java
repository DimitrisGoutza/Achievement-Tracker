package com.achievementtracker.service;

import com.achievementtracker.dao.AchievementDAO;
import com.achievementtracker.dao.CategoryDAO;
import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.games_endpoint.GameDTO;
import com.achievementtracker.dto.games_endpoint.GameRequestParams;
import com.achievementtracker.dto.games_endpoint.UsefulFilterData;
import com.achievementtracker.entity.Achievement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class GameFilterServiceImpl implements GameFilterService {
    private final GameDAO gameDAO;
    private final CategoryDAO categoryDAO;
    private final AchievementDAO achievementDAO;
    private final DateTimeFormatter yearMonthFormat = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    public GameFilterServiceImpl(GameDAO gameDAO, CategoryDAO categoryDAO, AchievementDAO achievementDAO) {
        this.gameDAO = gameDAO;
        this.categoryDAO = categoryDAO;
        this.achievementDAO = achievementDAO;
    }

    @Override
    public List<GameDTO> getFilteredGames(GameRequestParams params, Page page) {
        String searchTerm = params.getSearch();
        List<Long> categoryIds = params.getCategoriesAsList();
        boolean achievements = params.getAchievements() != null;
        boolean hiddenAchievements = achievements && (params.getAchievementsAsNullableInt() == 2);
        Integer minReviews = params.getMinReviewsAsNullableInt();
        Integer maxReviews = params.getMaxReviewsAsNullableInt();
        LocalDate minReleaseDate = params.getMinReleaseDateAsNullableDate();
        LocalDate maxReleaseDate = params.getMaxReleaseDateAsNullableDate();

        /*
        If the client sent us the totalEntries as a param, it means the User wants to perform an action that
        does not require updating the page#totalRecords attribute (sorting OR changing page size/number) with
        a count query.
        */
        boolean countQuery = (params.getEntries() == null);

        if (categoryIds.isEmpty()) { // No categories
            if (achievements) {
                if (hiddenAchievements)
                    return gameDAO.findOnlyGamesWithHiddenAchievements(searchTerm, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
                else
                    return gameDAO.findOnlyGamesWithAchievements(searchTerm, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
            } else {
                return gameDAO.findAllGames(searchTerm, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
            }
        } else { // Categorized
            if (achievements) {
                if (hiddenAchievements)
                    return gameDAO.findOnlyGamesWithHiddenAchievementsByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
                else
                    return gameDAO.findOnlyGamesWithAchievementsByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
            } else {
                return gameDAO.findAllGamesByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
            }
        }
    }

    @Override
    public Map<Long, List<Achievement>> getTopXAchievementsForGames(int topAmount, List<GameDTO> games) {
        List<Long> gameIds = games.stream().map(GameDTO::getStoreId).toList();
        return achievementDAO.getTopXAchievementsForGames(topAmount, gameIds);
    }

    @Override
    public UsefulFilterData getUsefulFilterData() {
        return new UsefulFilterData(
                categoryDAO.findAllSortedByPopularity(),
                gameDAO.findMaxReviews(),
                gameDAO.findMinimumReleaseDate().format(yearMonthFormat)
        );
    }
}
