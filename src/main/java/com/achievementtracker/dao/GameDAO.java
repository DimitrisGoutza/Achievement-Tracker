package com.achievementtracker.dao;

import com.achievementtracker.dto.games_endpoint.GameDTO;
import com.achievementtracker.entity.Game;

import java.time.LocalDate;
import java.util.List;

public interface GameDAO extends GenericDAO<Game, Long> {
    Game retrieveLatestEntry();
    List<Game> findAllWithCollections(Page page);
    Integer findMaxReviews();
    LocalDate findMinimumReleaseDate();
    List<GameDTO> findAllGames(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> findOnlyGamesWithAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> findAllGamesByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> findOnlyGamesWithAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> findOnlyGamesWithHiddenAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> findOnlyGamesWithHiddenAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
}
