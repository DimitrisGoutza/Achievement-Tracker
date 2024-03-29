package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;

import java.util.List;

public interface GameDAO extends GenericDAO<Game, Long> {
    Game retrieveLatestEntry();

    List<Game> findAllWithCollections(Page page);

    List<Game> findAll(Page page);

    List<Game> findAllByCategoryId(List<Long> categoryIds, Page page);
}
