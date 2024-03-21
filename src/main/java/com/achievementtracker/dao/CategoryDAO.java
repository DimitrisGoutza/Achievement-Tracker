package com.achievementtracker.dao;

import com.achievementtracker.entity.Category;

import java.util.List;

public interface CategoryDAO extends GenericDAO<Category, Long> {
    Category findByName(String name);

    List<Category> findAllSortedByPopularity();
}
