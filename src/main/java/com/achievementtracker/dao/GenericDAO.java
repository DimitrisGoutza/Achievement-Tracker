package com.achievementtracker.dao;

import jakarta.persistence.LockModeType;

import java.util.List;

public interface GenericDAO<T, ID> {
    T findById(ID id);
    T findById(ID id, LockModeType lock);
    T findReferenceById(ID id);
    List<T> findAll();
    Long getCount();
    T save(T entity);
    void remove(T entity);
}
