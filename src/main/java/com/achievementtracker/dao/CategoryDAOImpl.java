package com.achievementtracker.dao;

import com.achievementtracker.entity.Category;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

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
    public List<Category> findAllSortedByPopularity() {
        TypedQuery<Category> query = em.createQuery("FROM Category ORDER BY popularity DESC", Category.class);
        return query.getResultList();
    }

    @Override
    public List<Long> findAvailableBasedOnFilteredGames(List<Long> gameIds) {
        TypedQuery<Long> query = em.createQuery("SELECT DISTINCT c.id FROM Category c " +
                "JOIN CategorizedGame cg ON cg.category.id = c.id " +
                "WHERE cg.game.storeId IN :gameIds", Long.class);
        query.setParameter("gameIds", gameIds);
        return query.getResultList();
    }
}
