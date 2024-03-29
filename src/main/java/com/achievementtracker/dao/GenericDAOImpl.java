package com.achievementtracker.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {
    @PersistenceContext
    protected EntityManager em;
    protected final Class<T> entityClass;

    protected GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T findById(ID id) {
        return findById(id, LockModeType.NONE);
    }

    @Override
    public T findById(ID id, LockModeType lockModeType) {
        return em.find(entityClass, id, lockModeType);
    }

    @Override
    public T findReferenceById(ID id) {
        return em.getReference(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        CriteriaQuery<T> c = em.getCriteriaBuilder().createQuery(entityClass);
        c.select(c.from(entityClass));
        return em.createQuery(c).getResultList();
    }

    @Override
    public Long getCount() {
        CriteriaQuery<Long> c = em.getCriteriaBuilder().createQuery(Long.class);
        c.select(em.getCriteriaBuilder().count(c.from(entityClass)));
        return em.createQuery(c).getSingleResult();
    }

    @Transactional
    @Override
    public T save(T entity) {
        return em.merge(entity);
    }

    @Transactional
    @Override
    public void remove(T entity) {
        em.remove(entity);
    }

    @Transactional
    @Override
    public void removeAll() {
        CriteriaDelete<T> delete = em.getCriteriaBuilder().createCriteriaDelete(entityClass);
        delete.from(entityClass);
        em.createQuery(delete).executeUpdate();
    }
}
