package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class GameDAOImpl extends GenericDAOImpl<Game, Long> implements GameDAO {

    public GameDAOImpl() {
        super(Game.class);
    }

    @Override
    public Game findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Game findById(Long id, LockModeType lockModeType) {
        return super.findById(id, lockModeType);
    }

    @Override
    public Game findReferenceById(Long id) {
        return super.findReferenceById(id);
    }

    @Override
    public List<Game> findAll() {
        return super.findAll();
    }

    @Override
    public Long getCount() {
        return super.getCount();
    }
    @Transactional
    @Override
    public Game save(Game entity) {
        return super.save(entity);
    }
    @Transactional
    @Override
    public void remove(Game entity) {
        super.remove(entity);
    }
}
