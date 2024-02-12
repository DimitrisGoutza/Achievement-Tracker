package com.achievementtracker.dao;

import com.achievementtracker.entity.CategorizedGame;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CategorizedGameDAOImpl extends GenericDAOImpl<CategorizedGame, Long> implements CategorizedGameDAO {

    public CategorizedGameDAOImpl() {
        super(CategorizedGame.class);
    }

    @Override
    public CategorizedGame findById(Long aLong) {
        return super.findById(aLong);
    }

    @Override
    public CategorizedGame findById(Long aLong, LockModeType lockModeType) {
        return super.findById(aLong, lockModeType);
    }

    @Override
    public CategorizedGame findReferenceById(Long aLong) {
        return super.findReferenceById(aLong);
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
