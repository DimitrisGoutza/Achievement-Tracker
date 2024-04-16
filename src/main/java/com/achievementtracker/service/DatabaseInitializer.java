package com.achievementtracker.service;

import com.achievementtracker.dao.CategorizedGameDAO;
import com.achievementtracker.dao.CategoryDAO;
import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.dto.*;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.CategorizedGame;
import com.achievementtracker.entity.Category;
import com.achievementtracker.entity.Game;
import com.achievementtracker.proxy.SteamGlobalStatsProxy;
import com.achievementtracker.proxy.SteamSpyProxy;
import com.achievementtracker.proxy.SteamStorefrontProxy;
import com.achievementtracker.util.TimeUtility;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@Component
class DatabaseInitializer {
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private final SteamGlobalStatsProxy steamGlobalStatsProxy;
    private final SteamSpyProxy steamSpyProxy;
    private final SteamStorefrontProxy steamStorefrontProxy;
    private final GameDAO gameDAO;
    private final CategoryDAO categoryDAO;
    private final CategorizedGameDAO categorizedGameDAO;
    private final AchievementAnalyticsService achievementAnalyticsService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    DatabaseInitializer(SteamGlobalStatsProxy steamGlobalStatsProxy,
                        SteamSpyProxy steamSpyProxy, SteamStorefrontProxy steamStorefrontProxy, GameDAO gameDAO,
                        CategoryDAO categoryDAO, CategorizedGameDAO categorizedGameDAO, AchievementAnalyticsService achievementAnalyticsService,
                        TransactionTemplate transactionTemplate) {
        this.steamGlobalStatsProxy = steamGlobalStatsProxy;
        this.steamSpyProxy = steamSpyProxy;
        this.steamStorefrontProxy = steamStorefrontProxy;
        this.gameDAO = gameDAO;
        this.categoryDAO = categoryDAO;
        this.categorizedGameDAO = categorizedGameDAO;
        this.achievementAnalyticsService = achievementAnalyticsService;
        this.transactionTemplate = transactionTemplate;
    }

    void initialize(long requestedTotal) {
        long currentTotal = gameDAO.getCount();

        int MAX_RESULTS = 1000;
        long lastProcessedAppId = getLatestEntryIdFromDatabase();
        StoreAppListDTO storeAppListDTO;

        do {
            storeAppListDTO = steamGlobalStatsProxy.fetchAllStoreApps(true, false, lastProcessedAppId, MAX_RESULTS);
            List<StoreAppListDTO.StoreAppDTO> storeApps = storeAppListDTO.getStoreApps();

            for (StoreAppListDTO.StoreAppDTO storeApp : storeApps) {
                transactionTemplate.executeWithoutResult( status -> {
                    persistGameWithAchievements(storeApp.getStoreId());
                    persistCategoriesFromGame(storeApp.getStoreId());
                    associateCategoriesWithGame(storeApp.getStoreId());
                });

                currentTotal = gameDAO.getCount();
                if (currentTotal >= requestedTotal)
                    break;

                // polling rate = 1 request to all APIs per 2 seconds
                TimeUtility.waitSeconds(2);
            }
            lastProcessedAppId = storeAppListDTO.getLastAppId();
        } while (storeAppListDTO.hasMoreResults() && currentTotal < requestedTotal);
    }

    private long getLatestEntryIdFromDatabase() {
        Game latestGameEntry = gameDAO.retrieveLatestEntry();
        if (latestGameEntry != null)
            return latestGameEntry.getStoreId();
        return 0;
    }

    private void persistCategoriesFromGame(Long storeAppId) {
        GameCategoriesAndReviewsDTO gameCategoriesAndReviewsDTO = steamSpyProxy.fetchCategoriesAndReviewsByGameId(storeAppId);
        List<GameCategoriesAndReviewsDTO.CategoryDetailsDTO> categories = gameCategoriesAndReviewsDTO.getCategories();

        for (GameCategoriesAndReviewsDTO.CategoryDetailsDTO categoryDTO : categories) {
            // category is unique, first check if it already exists
            Category category = categoryDAO.findByName(categoryDTO.getName());
            if (category == null)
                category = new Category(categoryDTO.getName());
            else
                category.increasePopularity();
            categoryDAO.save(category);
        }
    }

    private void persistGameWithAchievements(Long storeAppId) {
        GameDetailDTO gameDetailDTO = steamStorefrontProxy.fetchDetailsByGameId(storeAppId);
        boolean appIsValid = (gameDetailDTO.getSteamAppId() != null);

        if (appIsValid) {
            GameCategoriesAndReviewsDTO gameCategoriesAndReviewsDTO = steamSpyProxy.fetchCategoriesAndReviewsByGameId(storeAppId);

            Game game = new Game(storeAppId, gameDetailDTO, gameCategoriesAndReviewsDTO);

            GameSchemaDTO gameSchemaDTO = fetchSchemaForGame(game.getSteamAppId());
            AchievementStatsDTO achievementStatsDTO = fetchAchievementStatsForGame(game.getSteamAppId());

            boolean hasAchievements = (achievementStatsDTO != null) && (gameSchemaDTO != null);
            if (hasAchievements) {
                List<GameSchemaDTO.AchievementDetailsDTO> achievements = gameSchemaDTO.getAchievements();
                for (GameSchemaDTO.AchievementDetailsDTO achievementDTO : achievements) {
                    Achievement achievement = new Achievement(achievementDTO, achievementStatsDTO, game);
                    game.addAchievement(achievement);
                }
                List<Achievement> orderedAchievements = game.getAchievements().stream().sorted(Comparator.comparingDouble(Achievement::getPercentage)).toList();
                orderedAchievements.forEach(achievement -> achievement.setPosition(orderedAchievements.indexOf(achievement) + 1));
            }
            game.setChallengeRating(achievementAnalyticsService.calculateChallengeRating(game.getAchievements()));
            game.setAverageCompletion(achievementAnalyticsService.calculateAverageAchievementCompletion(game.getAchievements()));
            game.setDifficultySpread(achievementAnalyticsService.calculateDifficultySpread(game.getAchievements()));

            gameDAO.save(game);
        }
    }

    private AchievementStatsDTO fetchAchievementStatsForGame(Long gameId) {
        try {
            return steamGlobalStatsProxy.fetchAchievementStatsByGameId(gameId);
        } catch (FeignException.Forbidden e) {
            logger.info(e.getClass().getName() + " <" + e.getMessage() + "> \nwas caught while fetching Achievement Stats, " +
                    "because Game (steam-appid: " + gameId + ") has no achievements OR hasn't released yet.");
            return null;
        }
    }

    private GameSchemaDTO fetchSchemaForGame(Long gameId) {
        try {
            return steamGlobalStatsProxy.fetchSchemaByAppId(gameId);
        } catch (FeignException.Forbidden e) {
            logger.info(e.getClass().getName() + " <" + e.getMessage() + "> \nwas caught while fetching Game Schema, " +
                    "because Game (steam-appid: " + gameId + ") hasn't released yet.");
            return null;
        } catch (FeignException.BadRequest e) {
            logger.warning(e.getClass().getName() + " <" + e.getMessage() + "> \nwas caught while fetching Game Schema, " +
                    "because Game (steam-appid: " + gameId + ") is not a valid app OR is not available for purchase anymore.");
            return null;
        }
    }

    private void associateCategoriesWithGame(Long storeAppId) {
        GameCategoriesAndReviewsDTO gameCategoriesAndReviewsDTO = steamSpyProxy.fetchCategoriesAndReviewsByGameId(storeAppId);
        List<GameCategoriesAndReviewsDTO.CategoryDetailsDTO> categories = gameCategoriesAndReviewsDTO.getCategories();
        for (GameCategoriesAndReviewsDTO.CategoryDetailsDTO categoryDTO : categories) {

            Category category = categoryDAO.findByName(categoryDTO.getName());
            Game game = gameDAO.findById(storeAppId);
            // Both must exist before persisting their association in the Join Table
            if (category != null && game != null) {
                CategorizedGame categorizedGame = new CategorizedGame(categoryDTO.getVotes(), category, game);
                categorizedGameDAO.save(categorizedGame);
            }
        }
    }
}