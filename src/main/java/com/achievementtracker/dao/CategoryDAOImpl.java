package com.achievementtracker.dao;

import com.achievementtracker.entity.Category;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

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
}
