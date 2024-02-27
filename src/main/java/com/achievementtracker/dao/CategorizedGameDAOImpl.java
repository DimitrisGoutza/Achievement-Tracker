package com.achievementtracker.dao;

import com.achievementtracker.entity.CategorizedGame;
import org.springframework.stereotype.Repository;

@Repository
public class CategorizedGameDAOImpl extends GenericDAOImpl<CategorizedGame, CategorizedGame.Id> implements CategorizedGameDAO {

    public CategorizedGameDAOImpl() {
        super(CategorizedGame.class);
    }
}
