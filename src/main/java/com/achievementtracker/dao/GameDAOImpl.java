package com.achievementtracker.dao;

import com.achievementtracker.entity.Game;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public Page<Game> findAllWithCollections(Pageable pageable) {
        TypedQuery<Long> queryForIDs = em.createQuery("SELECT storeId FROM Game", Long.class);

        int offset = pageable.getPageNumber() * pageable.getPageSize();
        queryForIDs.setFirstResult(offset);
        queryForIDs.setMaxResults(pageable.getPageSize());
        List<Long> gameIds = queryForIDs.getResultList();

        TypedQuery<Game> query = em.createQuery("FROM Game g LEFT JOIN FETCH " +
                "g.achievements LEFT JOIN FETCH " +
                "g.categorizedGames cg LEFT JOIN FETCH " +
                "cg.category " +
                "WHERE g.storeId IN :gameIds", Game.class);
        query.setParameter("gameIds", gameIds);
        List<Game> games = query.getResultList();

        return new PageImpl<>(games, pageable, getCount());
    }
}
