package com.achievementtracker.dao;

import com.achievementtracker.entity.CategorizedGame;
import com.achievementtracker.entity.CategorizedGame_;
import com.achievementtracker.entity.Game;
import com.achievementtracker.entity.Game_;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
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
        CriteriaQuery<Long> criteriaQueryForIds = cb.createQuery(Long.class);

        Root<Game> idRoot = criteriaQueryForIds.from(Game.class);
        criteriaQueryForIds.select(idRoot.get(Game_.storeId));

        TypedQuery<Long> queryForIds = page.createQuery(em, criteriaQueryForIds, idRoot);
        List<Long> gameIds = queryForIds.getResultList();

        // Then we the retrieve Games (along with eagerly loaded collections) that correspond to those IDs
        CriteriaQuery<Game> criteriaQueryForGames = cb.createQuery(Game.class);
        // FROM Game
        Root<Game> gameRoot = criteriaQueryForGames.from(Game.class);
        // JOIN FETCH associated collections
        gameRoot.fetch(Game_.achievements, JoinType.LEFT);
        Fetch<Game, CategorizedGame> categorizedGameFetch = gameRoot.fetch(Game_.categorizedGames, JoinType.LEFT);
        categorizedGameFetch.fetch(CategorizedGame_.category, JoinType.LEFT);
        // WHERE Game.storeId IN gameIds
        criteriaQueryForGames.where(gameRoot.get(Game_.storeId).in(gameIds));
        // PROJECTION
        criteriaQueryForGames.select(gameRoot).distinct(true);

        TypedQuery<Game> query = em.createQuery(criteriaQueryForGames);
        return query.getResultList();
    }

    @Override
    public List<Game> findAll(Page page) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Game> criteriaQuery = cb.createQuery(Game.class);
        // FROM Game
        Root<Game> gameRoot = criteriaQuery.from(Game.class);
        // PROJECTION
        criteriaQuery.select(gameRoot);

        TypedQuery<Game> query = page.createQuery(em, criteriaQuery, gameRoot);
        return query.getResultList();
    }
}
