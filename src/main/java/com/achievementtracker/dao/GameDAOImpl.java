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
    public List<Game> findAll(boolean achievementsOnly ,Page page) {
        // Count query
        TypedQuery<Long> queryForCount = em.createQuery("SELECT COUNT(DISTINCT g.storeId) FROM Game g " +
                (achievementsOnly ? "JOIN Achievement a ON a.game.storeId = g.storeId " : ""), Long.class);
        Long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        // Main query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);
        cq.select(gameRoot).distinct(true);
        if (achievementsOnly)
            gameRoot.join(Game_.achievements, JoinType.INNER);

        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findAllByCategoryId(List<Long> categoryIds, boolean achievementsOnly, Page page) {
        // Count query
        TypedQuery<Long> queryForCount = em.createQuery("SELECT COUNT(g.storeId) FROM Game g WHERE g.storeId IN ( " +
                "SELECT DISTINCT g.storeId FROM Game g " +
                (achievementsOnly ? "JOIN Achievement a ON a.game.storeId = g.storeId " : "") +
                "JOIN CategorizedGame cg ON cg.id.gameId = g.storeId " +
                "WHERE cg.category.id IN :categoryIds " +
                "GROUP BY g.storeId " +
                "HAVING COUNT(DISTINCT cg.category.id)= :categoryCount)", Long.class);
        queryForCount.setParameter("categoryIds", categoryIds)
                .setParameter("categoryCount", categoryIds.size());
        Long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        // Main query for Ids
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForIds = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> idGameRoot = cqForIds.from(Game.class);
        // SELECT distinct g.storeId
        cqForIds.select(idGameRoot.get(Game_.storeId)).distinct(true);
        // (JOIN Achievement)
        if (achievementsOnly)
            idGameRoot.join(Game_.achievements, JoinType.INNER);
        // JOIN CategorizedGame
        Join<Game, CategorizedGame> categorizedGameJoin = idGameRoot.join(Game_.categorizedGames, JoinType.INNER);
        // WHERE CategorizedGame.id.categoryId IN :categoryIds
        cqForIds.where(categorizedGameJoin.get(CategorizedGame_.id).get("categoryId").in(categoryIds))
                // GROUP BY g.storeId
                .groupBy(idGameRoot.get(Game_.storeId))
                // HAVING COUNT(DISTINCT cg.id.categoryId) = categoryIds.size()
                .having(cb.equal(cb.countDistinct(categorizedGameJoin.get(CategorizedGame_.id).get("categoryId")), categoryIds.size()));

        List<Long> gameIds = em.createQuery(cqForIds).getResultList();

        // Main Query for Games
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);
        cq.where(gameRoot.get(Game_.storeId).in(gameIds));

        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }
}
