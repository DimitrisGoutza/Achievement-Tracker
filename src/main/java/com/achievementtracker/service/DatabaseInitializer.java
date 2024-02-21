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
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
class DatabaseInitializer {
    private final SteamGlobalStatsProxy steamGlobalStatsProxy;
    private final SteamSpyProxy steamSpyProxy;
    private final SteamStorefrontProxy steamStorefrontProxy;
    private final GameDAO gameDAO;
    private final CategoryDAO categoryDAO;
    private final CategorizedGameDAO categorizedGameDAO;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    DatabaseInitializer(SteamGlobalStatsProxy steamGlobalStatsProxy,
                        SteamSpyProxy steamSpyProxy, SteamStorefrontProxy steamStorefrontProxy, GameDAO gameDAO,
                        CategoryDAO categoryDAO, CategorizedGameDAO categorizedGameDAO, TransactionTemplate transactionTemplate) {
        this.steamGlobalStatsProxy = steamGlobalStatsProxy;
        this.steamSpyProxy = steamSpyProxy;
        this.steamStorefrontProxy = steamStorefrontProxy;
        this.gameDAO = gameDAO;
        this.categoryDAO = categoryDAO;
        this.categorizedGameDAO = categorizedGameDAO;
        this.transactionTemplate = transactionTemplate;
    }

    void initialize(long requestedTotal) {
        long currentTotal = gameDAO.getCount();

        int MAX_RESULTS = 1000;
        long startFromAppId = getLatestEntryIdFromDatabase();
        StoreAppListDTO storeAppListDTO = steamGlobalStatsProxy.fetchAllStoreApps(true, false, startFromAppId, MAX_RESULTS);

        while (storeAppListDTO.hasMoreResults() && currentTotal < requestedTotal) {
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
                wait(2);
            }
            startFromAppId = storeAppListDTO.getLastAppId();
            storeAppListDTO = steamGlobalStatsProxy.fetchAllStoreApps(true, false, startFromAppId, MAX_RESULTS);
        }
    }

    private long getLatestEntryIdFromDatabase() {
        Game latestGameEntry = gameDAO.retrieveLatestEntry();
        if (latestGameEntry != null)
            return latestGameEntry.getSteamAppId();
        return 0;
    }

    private void persistCategoriesFromGame(Long storeAppId) {
        GameCategoriesDTO gameCategoriesDTO = steamSpyProxy.fetchCategoriesByGameId(storeAppId);
        List<GameCategoriesDTO.CategoryDetailsDTO> categories = gameCategoriesDTO.getCategories();

        for (GameCategoriesDTO.CategoryDetailsDTO categoryDTO : categories) {
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
            Game game = new Game(storeAppId, gameDetailDTO);

            GameSchemaDTO gameSchemaDTO = fetchSchemaForGame(game.getSteamAppId());
            AchievementStatsDTO achievementStatsDTO = fetchAchievementStatsForGame(game.getSteamAppId());

            boolean hasAchievements = (achievementStatsDTO != null) && (gameSchemaDTO != null);
            if (hasAchievements) {
                List<GameSchemaDTO.AchievementDetailsDTO> achievements = gameSchemaDTO.getAchievements();
                for (GameSchemaDTO.AchievementDetailsDTO achievementDTO : achievements) {
                    Achievement achievement = new Achievement(achievementDTO, achievementStatsDTO, game);
                    game.addAchievement(achievement);
                }
            }
            gameDAO.save(game);
        }
    }

    private AchievementStatsDTO fetchAchievementStatsForGame(Long gameId) {
        try {
            return steamGlobalStatsProxy.fetchAchievementStatsByGameId(gameId);
        } catch (FeignException.Forbidden e) {
            return null;
        }
    }

    private GameSchemaDTO fetchSchemaForGame(Long gameId) {
        try {
            return steamGlobalStatsProxy.fetchSchemaByAppId(gameId);
        } catch (FeignException.BadRequest | FeignException.Forbidden e) {
            // BadRequest = invalid app, Forbidden = app hasn't released yet
            return null;
        }
    }

    private void associateCategoriesWithGame(Long storeAppId) {
        GameCategoriesDTO gameCategoriesDTO = steamSpyProxy.fetchCategoriesByGameId(storeAppId);
        List<GameCategoriesDTO.CategoryDetailsDTO> categories = gameCategoriesDTO.getCategories();
        for (GameCategoriesDTO.CategoryDetailsDTO categoryDTO : categories) {

            Category category = categoryDAO.findByName(categoryDTO.getName());
            Game game = gameDAO.findById(storeAppId);
            // Both must exist before persisting their association in the Join Table
            if (category != null && game != null) {
                CategorizedGame categorizedGame = new CategorizedGame(categoryDTO.getVotes(), category, game);
                categorizedGameDAO.save(categorizedGame);
            }
        }
    }

    private void wait(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}