package com.achievementtracker.dao;

import com.achievementtracker.entity.Achievement;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AchievementDAOImpl extends GenericDAOImpl<Achievement, Long> implements AchievementDAO {

    public AchievementDAOImpl() {
        super(Achievement.class);
    }

    @Override
    public Achievement findById(Long aLong) {
        return super.findById(aLong);
    }

    @Override
    public Achievement findById(Long aLong, LockModeType lockModeType) {
        return super.findById(aLong, lockModeType);
    }

    @Override
    public Achievement findReferenceById(Long aLong) {
        return super.findReferenceById(aLong);
    }

    @Override
    public List<Achievement> findAll() {
        return super.findAll();
    }

    @Override
    public Long getCount() {
        return super.getCount();
    }

    @Transactional
    @Override
    public Achievement save(Achievement entity) {
        return super.save(entity);
    }

    @Transactional
    @Override
    public void remove(Achievement entity) {
        super.remove(entity);
    }
}
