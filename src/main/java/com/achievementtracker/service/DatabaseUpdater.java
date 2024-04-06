package com.achievementtracker.service;

import com.achievementtracker.dao.*;
import com.achievementtracker.dto.AchievementStatsDTO;
import com.achievementtracker.dto.GameCategoriesDTO;
import com.achievementtracker.dto.GameDetailDTO;
import com.achievementtracker.dto.GameSchemaDTO;
import com.achievementtracker.entity.*;
import com.achievementtracker.proxy.SteamGlobalStatsProxy;
import com.achievementtracker.proxy.SteamSpyProxy;
import com.achievementtracker.proxy.SteamStorefrontProxy;
import com.achievementtracker.util.TimeUtility;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
class DatabaseUpdater {
    private final SteamGlobalStatsProxy steamGlobalStatsProxy;
    private final SteamSpyProxy steamSpyProxy;
    private final SteamStorefrontProxy steamStorefrontProxy;
    private final GameDAO gameDAO;
    private final CategoryDAO categoryDAO;
    private final CategorizedGameDAO categorizedGameDAO;
    private final AchievementAnalyticsService achievementAnalyticsService;
    private final DatabaseInitializer databaseInitializer;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    DatabaseUpdater(SteamGlobalStatsProxy steamGlobalStatsProxy, SteamSpyProxy steamSpyProxy, SteamStorefrontProxy steamStorefrontProxy,
                    GameDAO gameDAO, CategoryDAO categoryDAO, CategorizedGameDAO categorizedGameDAO, DatabaseInitializer databaseInitializer,
                    AchievementAnalyticsService achievementAnalyticsService, TransactionTemplate transactionTemplate) {
        this.steamGlobalStatsProxy = steamGlobalStatsProxy;
        this.steamSpyProxy = steamSpyProxy;
        this.steamStorefrontProxy = steamStorefrontProxy;
        this.gameDAO = gameDAO;
        this.categoryDAO = categoryDAO;
        this.categorizedGameDAO = categorizedGameDAO;
        this.achievementAnalyticsService = achievementAnalyticsService;
        this.databaseInitializer = databaseInitializer;
        this.transactionTemplate = transactionTemplate;
    }


    @Scheduled(initialDelay = 24 * 60 * 60 * 1000, fixedRate = 24 * 60 * 60 * 1000) // every 24h
    void update() {
        OffsetPage page = new OffsetPage(5, gameDAO.getCount(),
                                Game_.storeId, Page.SortDirection.ASC,
                                Game_.storeId);
        int lastPage = page.getPageNumbers().get(page.getPageNumbers().size() - 1);

        do {
            List<Game> existingGames = gameDAO.findAllWithCollections(page);
            for (Game gameInstance : existingGames) {
                transactionTemplate.executeWithoutResult(status -> {
                    /*
                    Synchronize current entry with the persistence context (as it is currently in detached state),
                    any changes made to it (game) will be batch executed when the transaction finally commits
                    */
                    Game game = gameDAO.save(gameInstance);

                    updateGame(game);

                    updateAchievementsForGame(game);

                    updateCategoriesForGame(game);
                });
                // polling rate = 1 request to all APIs per 2 seconds
                TimeUtility.waitSeconds(2);
            }
            page.setCurrent(page.getNext());
        } while (page.getCurrent() <= lastPage);

        // after updating existing entries, move on to persisting new ones
        databaseInitializer.initialize(Long.MAX_VALUE);
    }

    private void updateCategoriesForGame(Game game) {
        GameCategoriesDTO gameCategoriesDTO = steamSpyProxy.fetchCategoriesByGameId(game.getStoreId());

        List<GameCategoriesDTO.CategoryDetailsDTO> updatedCategories = gameCategoriesDTO.getCategories();
        Set<CategorizedGame> associatedCategories = game.getCategorizedGames();

        for (GameCategoriesDTO.CategoryDetailsDTO categoryDTO : updatedCategories) {
            // check if it is already associated with game
            Optional<CategorizedGame> result = associatedCategories.stream().filter(
                    cg -> cg.getCategory().getName().equals(categoryDTO.getName())
                    ).findFirst();

            if (result.isPresent()) {
                CategorizedGame categorizedGame = result.get();
                // update its votes corresponding to this game
                categorizedGame.setVotes(categoryDTO.getVotes());
                categorizedGameDAO.save(categorizedGame);
            } else {
                // if not associated check if it at least exists
                Category category = categoryDAO.findByName(categoryDTO.getName());
                if (category == null)
                    category = new Category(categoryDTO.getName());
                else
                    category.increasePopularity();
                categoryDAO.save(category);
                // then associate it with the game
                CategorizedGame categorizedGame = new CategorizedGame(categoryDTO.getVotes(), category, game);
                categorizedGameDAO.save(categorizedGame);
            }
        }
    }

    private void updateAchievementsForGame(Game game) {
        GameSchemaDTO gameSchemaDTO = fetchSchemaForGame(game.getSteamAppId());
        AchievementStatsDTO achievementStatsDTO = fetchAchievementStatsForGame(game.getSteamAppId());

        boolean hasAchievements = (gameSchemaDTO != null) && (achievementStatsDTO != null);
        if (hasAchievements) {
            Set<Achievement> existingAchievements = game.getAchievements();

            List<GameSchemaDTO.AchievementDetailsDTO> updatedAchievements = gameSchemaDTO.getAchievements();
            for (GameSchemaDTO.AchievementDetailsDTO achievementDTO : updatedAchievements) {
                Optional<Achievement> matchingAchievement = existingAchievements.stream().filter(
                        existingAchievement -> existingAchievement.getName().equals(achievementDTO.getName()))
                        .findFirst();
                if (matchingAchievement.isPresent()) {
                    Achievement achievement = matchingAchievement.get();
                    achievement.setPercentage(achievementStatsDTO.getAchievements().get(achievementDTO.getName()));
                } else {
                    Achievement achievement = new Achievement(achievementDTO, achievementStatsDTO, game);
                    game.addAchievement(achievement);
                }
            }
            List<Achievement> orderedAchievements = game.getAchievements().stream().sorted(Comparator.comparingDouble(Achievement::getPercentage)).toList();
            orderedAchievements.forEach(achievement -> {
                achievement.setPosition(orderedAchievements.indexOf(achievement) + 1);
            });
        }
        game.setChallengeRating(achievementAnalyticsService.calculateChallengeRating(game.getAchievements()));
        game.setAverageCompletion(achievementAnalyticsService.calculateAverageAchievementCompletion(game.getAchievements()));
        game.setDifficultySpread(achievementAnalyticsService.calculateDifficultySpread(game.getAchievements()));
    }

    private void updateGame(Game game) {
        GameDetailDTO gameDetailDTO = steamStorefrontProxy.fetchDetailsByGameId(game.getStoreId());

        boolean gameHasNowReleased = (game.isComingSoon()) && (!gameDetailDTO.isComingSoon());
        if (gameHasNowReleased) {
            game.setComingSoon(gameDetailDTO.isComingSoon());
            game.setReleaseDate(gameDetailDTO.getReleaseDate());
        }
        // these could have been updated as well
        game.setShortDescription(gameDetailDTO.getShortDescription());
        game.setLongDescription(gameDetailDTO.getLongDescription());
        game.setImages(
                new Image(
                        gameDetailDTO.getHeaderImageUrl(),
                        gameDetailDTO.getCapsuleImageUrl(),
                        gameDetailDTO.getCapsuleSmallImageUrl(),
                        gameDetailDTO.getBackgroundImageUrl(),
                        gameDetailDTO.getBackgroundRawImageUrl()
                )
        );
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
}