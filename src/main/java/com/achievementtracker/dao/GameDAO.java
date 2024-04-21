package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;

import java.time.LocalDate;
import java.util.List;

public interface GameDAO extends GenericDAO<Game, Long> {
    Game retrieveLatestEntry();
    List<Game> findAllWithCollections(Page page);
    Integer findMaxReviews();
    LocalDate findMinimumReleaseDate();
    List<Game> findAllGames(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<Game> findOnlyGamesWithAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<Game> findAllGamesByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<Game> findOnlyGamesWithAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<Game> findOnlyGamesWithHiddenAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<Game> findOnlyGamesWithHiddenAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
}
