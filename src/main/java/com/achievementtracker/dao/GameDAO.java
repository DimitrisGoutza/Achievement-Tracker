package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;

import java.util.List;

public interface GameDAO extends GenericDAO<Game, Long> {
    Game retrieveLatestEntry();
    List<Game> findAllWithCollections(Page page);
    Integer findMaxReviews();
    List<Game> findAllGames(String searchTerm, Integer minReviews, Integer maxReviews, Page page);
    List<Game> findOnlyGamesWithAchievements(String searchTerm, Integer minReviews, Integer maxReviews, Page page);
    List<Game> findAllGamesByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, Page page);
    List<Game> findOnlyGamesWithAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, Page page);
    List<Game> findOnlyGamesWithHiddenAchievements(String searchTerm, Integer minReviews, Integer maxReviews, Page page);
    List<Game> findOnlyGamesWithHiddenAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, Page page);
}
