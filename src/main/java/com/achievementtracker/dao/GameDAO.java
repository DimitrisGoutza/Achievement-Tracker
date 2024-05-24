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
    List<GameDTO> findAllGames(Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery);
    List<GameDTO> findOnlyGamesWithAchievements(Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery);
    List<GameDTO> findAllGamesByCategoryId(List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery);
    List<GameDTO> findOnlyGamesWithAchievementsByCategoryId(List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery);
    List<GameDTO> findOnlyGamesWithHiddenAchievements(Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery);
    List<GameDTO> findOnlyGamesWithHiddenAchievementsByCategoryId(List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery);
    Game findByIdWithAchievements(Long gameId);

    /* -------------------------- Native Full-Text search queries -------------------------- */
    List<GameDTO> searchAllGames(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> searchOnlyGamesWithAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> searchAllGamesByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> searchOnlyGamesWithAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> searchOnlyGamesWithHiddenAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
    List<GameDTO> searchOnlyGamesWithHiddenAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page);
}
