package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameDAO extends GenericDAO<Game, Long> {
    Game retrieveLatestEntry();

    Page<Game> findAllWithCollections(Pageable pageable);
}
