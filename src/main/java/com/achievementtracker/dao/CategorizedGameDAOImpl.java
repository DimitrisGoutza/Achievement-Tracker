package com.achievementtracker.dao;

import com.achievementtracker.entity.CategorizedGame;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CategorizedGameDAOImpl extends GenericDAOImpl<CategorizedGame, CategorizedGame.Id> implements CategorizedGameDAO {

    public CategorizedGameDAOImpl() {
        super(CategorizedGame.class);
    }

    @Override
    public CategorizedGame findById(CategorizedGame.Id id) {
        return super.findById(id);
    }

    @Override
    public CategorizedGame findById(CategorizedGame.Id id, LockModeType lockModeType) {
        return super.findById(id, lockModeType);
    }

    @Override
    public CategorizedGame findReferenceById(CategorizedGame.Id id) {
        return super.findReferenceById(id);
    }

    @Override
    public List<CategorizedGame> findAll() {
        return super.findAll();
    }

    @Override
    public Long getCount() {
        return super.getCount();
    }
    @Transactional
    @Override
    public CategorizedGame save(CategorizedGame entity) {
        return super.save(entity);
    }
    @Transactional
    @Override
    public void remove(CategorizedGame entity) {
        super.remove(entity);
    }
}
