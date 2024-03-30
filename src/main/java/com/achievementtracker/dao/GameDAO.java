package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;

import java.util.List;

public interface GameDAO extends GenericDAO<Game, Long> {
    Game retrieveLatestEntry();

    List<Game> findAllWithCollections(Page page);

    List<Game> findAll(boolean achievementsOnly ,Page page);

    List<Game> findAll(String searchTerm, boolean achievementsOnly, Page page);

    List<Game> findAllByCategoryId(List<Long> categoryIds, boolean achievementsOnly, Page page);

    List<Game> findAllByCategoryId(String searchTerm, List<Long> categoryIds, boolean achievementsOnly, Page page);
}
