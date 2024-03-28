package com.achievementtracker.service;

import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.dao.Page;
import com.achievementtracker.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameFilterServiceImpl implements GameFilterService {
    private final GameDAO gameDAO;

    @Autowired
    public GameFilterServiceImpl(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    @Override
    public List<Game> getFilteredGames(Page page) {
        // no filters currently
        return gameDAO.findAll(page);
    }
}
