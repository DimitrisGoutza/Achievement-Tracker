package com.achievementtracker.dao;

import com.achievementtracker.entity.Category;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CategoryDAOImpl extends GenericDAOImpl<Category, Long> implements CategoryDAO {

    public CategoryDAOImpl() {
        super(Category.class);
    }

    @Override
    public Category findByName(String name) {
        try {
            TypedQuery<Category> query = em.createQuery("FROM Category WHERE name = :name", Category.class)
                    .setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Category findById(Long aLong) {
        return super.findById(aLong);
    }

    @Override
    public Category findById(Long aLong, LockModeType lockModeType) {
        return super.findById(aLong, lockModeType);
    }

    @Override
    public Category findReferenceById(Long aLong) {
        return super.findReferenceById(aLong);
    }

    @Override
    public List<Category> findAll() {
        return super.findAll();
    }

    @Override
    public Long getCount() {
        return super.getCount();
    }
    @Transactional
    @Override
    public Category save(Category entity) {
        return super.save(entity);
    }
    @Transactional
    @Override
    public void remove(Category entity) {
        super.remove(entity);
    }
}
