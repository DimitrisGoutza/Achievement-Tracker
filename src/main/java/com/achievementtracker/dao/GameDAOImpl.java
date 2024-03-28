package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;
import com.achievementtracker.entity.Game_;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GameDAOImpl extends GenericDAOImpl<Game, Long> implements GameDAO {

    public GameDAOImpl() {
        super(Game.class);
    }

    @Override
    public Game retrieveLatestEntry() {
        try {
            TypedQuery<Game> query = em.createQuery("FROM Game ORDER BY storeId DESC LIMIT 1", Game.class);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Game> findAllWithCollections(Page page) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // First we retrieve the Game IDs paginated
        CriteriaQuery<Long> cqForIds = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> idRoot = cqForIds.from(Game.class);
        // SELECT g.storeId
        cqForIds.select(idRoot.get(Game_.storeId));

        TypedQuery<Long> queryForIds = page.createQuery(em, cqForIds, idRoot);
        List<Long> gameIds = queryForIds.getResultList();

        // Then we the retrieve Games (along with eagerly loaded collections) that correspond to those IDs
        TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g " +
                " LEFT JOIN FETCH g.achievements" +
                " LEFT JOIN FETCH g.categorizedGames cg" +
                " LEFT JOIN FETCH cg.category" +
                " WHERE g.storeId IN :gameIds", Game.class);
        query.setParameter("gameIds", gameIds);
        return query.getResultList();
    }

    @Override
    public List<Game> findAll(Page page) {
        // Count query
        TypedQuery<Long> queryForCount = em.createQuery("SELECT COUNT(g.storeId) FROM Game g", Long.class);
        Long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        // Main query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);

        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }
}
