package com.achievementtracker.service;

import com.achievementtracker.dao.AchievementDAO;
import com.achievementtracker.dao.CategoryDAO;
import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.games_endpoint.GameDTO;
import com.achievementtracker.dto.games_endpoint.GameRequestParams;
import com.achievementtracker.dto.games_endpoint.UsefulFilterData;
import com.achievementtracker.dto.home_endpoint.LeaderboardGameDTO1;
import com.achievementtracker.dto.home_endpoint.LeaderboardGameDTO2;
import com.achievementtracker.dto.search_endpoint.MinimalGameDTO;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Category;
import com.achievementtracker.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class GameProcessingServiceImpl implements GameProcessingService {
    private final GameDAO gameDAO;
    private final CategoryDAO categoryDAO;
    private final AchievementDAO achievementDAO;
    private final DateTimeFormatter yearMonthFormat = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    public GameProcessingServiceImpl(GameDAO gameDAO, CategoryDAO categoryDAO, AchievementDAO achievementDAO) {
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
        If the client sent us the totalEntries as a param, it means the User wants to perform an action
        (sorting OR changing page size/number) that does not require updating the page#totalRecords attribute
        with a count query.
        */
        boolean countQuery = (params.getEntries() == null);

        List<GameDTO> games;
        if (searchTerm == null || searchTerm.isEmpty()) {
            if (categoryIds.isEmpty()) {
                if (achievements) {
                    if (hiddenAchievements)
                        games = gameDAO.findOnlyGamesWithHiddenAchievements(minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
                    else
                        games = gameDAO.findOnlyGamesWithAchievements(minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
                } else {
                    games = gameDAO.findAllGames(minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
                }
            } else {
                if (achievements) {
                    if (hiddenAchievements)
                        games = gameDAO.findOnlyGamesWithHiddenAchievementsByCategoryId(categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
                    else
                        games = gameDAO.findOnlyGamesWithAchievementsByCategoryId(categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
                } else {
                    games = gameDAO.findAllGamesByCategoryId(categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page, countQuery);
                }
            }
        } else {
            if (categoryIds.isEmpty()) {
                if (achievements) {
                    if (hiddenAchievements)
                        games = gameDAO.searchOnlyGamesWithHiddenAchievements(searchTerm, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page);
                    else
                        games = gameDAO.searchOnlyGamesWithAchievements(searchTerm, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page);
                } else {
                    games = gameDAO.searchAllGames(searchTerm, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page);
                }
            } else {
                if (achievements) {
                    if (hiddenAchievements)
                        games = gameDAO.searchOnlyGamesWithHiddenAchievementsByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page);
                    else
                        games = gameDAO.searchOnlyGamesWithAchievementsByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page);
                } else {
                    games = gameDAO.searchAllGamesByCategoryId(searchTerm, categoryIds, minReviews, maxReviews, minReleaseDate, maxReleaseDate, page);
                }
            }
        }

        return games;
    }

    @Override
    public Map<Long, List<Achievement>> getTopXAchievementsForGames(int topAmount, List<GameDTO> games) {
        List<Long> gameIds = games.stream().map(GameDTO::getStoreId).toList();
        return achievementDAO.getTopXAchievementsForGames(topAmount, gameIds);
    }

    @Override
    public Game findGameByIdWithAchievements(Long gameId) {
        return gameDAO.findByIdWithAchievements(gameId);
    }

    @Override
    public UsefulFilterData getUsefulFilterData() {
        return new UsefulFilterData(
                categoryDAO.findAllSortedByPopularity(),
                gameDAO.findMaxReviews(),
                gameDAO.findMinimumReleaseDate().format(yearMonthFormat)
        );
    }

    @Override
    public List<Category> getCategoriesForGame(Long gameId) {
        return categoryDAO.findAllForGame(gameId);
    }

    @Override
    public int getChallengeRatingPercentileRounded(Long gameId) {
        double result = gameDAO.calculateChallengeRatingPercentile(gameId);
        return (int) Math.floor(result * 100.0);
    }

    @Override
    public List<MinimalGameDTO> searchAllGames(String searchTerm, int resultSize) {
        if (resultSize == 0)
            return gameDAO.searchAllGames(searchTerm);

        return gameDAO.searchAllGames(searchTerm, resultSize);
    }

    @Override
    public List<LeaderboardGameDTO1> getTopChallengingGames(int topAmount) {
        return gameDAO.findTopXGamesByChallengeRating(topAmount);
    }

    @Override
    public List<LeaderboardGameDTO2> getMostRecentGames(int amount) {
        return gameDAO.findRecentlyReleasedGames(amount);
    }
}