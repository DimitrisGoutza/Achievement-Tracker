package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;

import java.util.List;

public interface GameDAO extends GenericDAO<Game, Long> {
    Game retrieveLatestEntry();

    List<Game> findAllWithCollections(Page page);

    List<Game> findAllGames(String searchTerm, Page page);

    List<Game> findOnlyGamesWithAchievements(String searchTerm, Page page);

    List<Game> findAllGamesByCategoryId(String searchTerm, List<Long> categoryIds, Page page);

    List<Game> findOnlyGamesWithAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Page page);
}
