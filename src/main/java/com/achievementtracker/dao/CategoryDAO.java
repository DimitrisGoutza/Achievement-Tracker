package com.achievementtracker.dao;

import com.achievementtracker.entity.Category;

public interface CategoryDAO extends GenericDAO<Category, Long> {
    Category findByName(String name);
}
