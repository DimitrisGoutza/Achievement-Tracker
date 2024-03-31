package com.achievementtracker.dao;

import com.achievementtracker.entity.*;
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
                (achievementsOnly ? "WHERE EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId) " : ""), Long.class);
        long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        // Main query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);
        cq.select(gameRoot).distinct(true);

        if (achievementsOnly) {
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<Achievement> achievementRoot = subquery.from(Achievement.class);
            subquery.where(cb.equal(achievementRoot.get(Achievement_.game).get(Game_.storeId), gameRoot.get(Game_.storeId)));

            // WHERE EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId)
            cq.where(cb.exists(subquery));
        }

        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findAll(String searchTerm, boolean achievementsOnly, Page page) {
        // Count query
        TypedQuery<Long> queryForCount = em.createQuery("SELECT COUNT(DISTINCT g.storeId) FROM Game g " +
                "WHERE g.title LIKE :searchPattern " +
                (achievementsOnly ? "AND EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId) " : ""), Long.class);
        queryForCount.setParameter("searchPattern", "%" + searchTerm + "%");
        long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        // Main query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);
        cq.select(gameRoot).distinct(true);
        // WHERE g.title LIKE :searchPattern
        Predicate searchPredicate = cb.like(gameRoot.get(Game_.title), "%" + searchTerm + "%");

        if (achievementsOnly) {
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<Achievement> achievementRoot = subquery.from(Achievement.class);
            subquery.where(cb.equal(achievementRoot.get(Achievement_.game).get(Game_.storeId), gameRoot.get(Game_.storeId)));

            // AND EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId)
            Predicate gameHasAchievements = cb.exists(subquery);
            searchPredicate = cb.and(searchPredicate, gameHasAchievements);
        }
        cq.where(searchPredicate);

        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findAllByCategoryId(List<Long> categoryIds, boolean achievementsOnly, Page page) {
        // Count query
        TypedQuery<Long> queryForCount = em.createQuery("SELECT COUNT(g.storeId) FROM Game g WHERE g.storeId IN ( " +
                "SELECT DISTINCT g.storeId FROM Game g " +
                "JOIN CategorizedGame cg ON cg.id.gameId = g.storeId " +
                "WHERE cg.category.id IN :categoryIds " +
                (achievementsOnly ? "AND EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId) " : "") +
                "GROUP BY g.storeId " +
                "HAVING COUNT(DISTINCT cg.category.id)= :categoryCount)", Long.class);
        queryForCount.setParameter("categoryIds", categoryIds)
                .setParameter("categoryCount", categoryIds.size());
        long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        // Main query (for IDs)
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForIds = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> idGameRoot = cqForIds.from(Game.class);
        // SELECT distinct g.storeId
        cqForIds.select(idGameRoot.get(Game_.storeId)).distinct(true);
        // JOIN CategorizedGame
        Join<Game, CategorizedGame> categorizedGameJoin = idGameRoot.join(Game_.categorizedGames, JoinType.INNER);
        // WHERE cg.category.id IN :categoryIds
        Predicate categoryPredicate = categorizedGameJoin.get(CategorizedGame_.category).get(Category_.id).in(categoryIds);

        if (achievementsOnly) {
            Subquery<Long> subquery = cqForIds.subquery(Long.class);
            Root<Achievement> achievementRoot = subquery.from(Achievement.class);
            subquery.select(cb.literal(1L));
            subquery.where(cb.equal(achievementRoot.get(Achievement_.game).get(Game_.storeId), idGameRoot.get(Game_.storeId)));

            // AND EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId)
            Predicate gameHasAchievements = cb.exists(subquery);
            categoryPredicate = cb.and(categoryPredicate, gameHasAchievements);
        }
        cqForIds.where(categoryPredicate)
                // GROUP BY g.storeId
                .groupBy(idGameRoot.get(Game_.storeId))
                // HAVING COUNT(DISTINCT cg.id.categoryId) = categoryIds.size()
                .having(cb.equal(cb.countDistinct(categorizedGameJoin.get(CategorizedGame_.category).get(Category_.id)), categoryIds.size()));
        List<Long> gameIds = em.createQuery(cqForIds).getResultList();

        // Main Query for Games
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);
        cq.where(gameRoot.get(Game_.storeId).in(gameIds));

        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findAllByCategoryId(String searchTerm, List<Long> categoryIds, boolean achievementsOnly, Page page) {
        // Count query
        TypedQuery<Long> queryForCount = em.createQuery("SELECT COUNT(g.storeId) FROM Game g WHERE g.storeId IN ( " +
                "SELECT DISTINCT g.storeId FROM Game g " +
                "JOIN CategorizedGame cg ON cg.id.gameId = g.storeId " +
                "WHERE cg.category.id IN :categoryIds AND g.title LIKE :searchPattern " +
                (achievementsOnly ? "AND EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId) " : "") +
                "GROUP BY g.storeId " +
                "HAVING COUNT(DISTINCT cg.category.id)= :categoryCount)", Long.class);
        queryForCount.setParameter("categoryIds", categoryIds)
                .setParameter("searchPattern", "%" + searchTerm + "%")
                .setParameter("categoryCount", categoryIds.size());
        long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        // Main query for Ids
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForIds = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> idGameRoot = cqForIds.from(Game.class);
        // SELECT distinct g.storeId
        cqForIds.select(idGameRoot.get(Game_.storeId)).distinct(true);
        // JOIN CategorizedGame
        Join<Game, CategorizedGame> categorizedGameJoin = idGameRoot.join(Game_.categorizedGames, JoinType.INNER);
        // WHERE cg.category.id IN :categoryIds
        Predicate finalPredicate = cb.and(categorizedGameJoin.get(CategorizedGame_.category).get(Category_.id).in(categoryIds),
                // AND g.title LIKE :searchPattern
                cb.like(idGameRoot.get(Game_.title), "%" + searchTerm + "%"));

        if (achievementsOnly) {
            Subquery<Long> subquery = cqForIds.subquery(Long.class);
            Root<Achievement> achievementRoot = subquery.from(Achievement.class);
            subquery.select(cb.literal(1L));
            subquery.where(cb.equal(achievementRoot.get(Achievement_.game).get(Game_.storeId), idGameRoot.get(Game_.storeId)));

            // AND EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId)
            Predicate gameHasAchievements = cb.exists(subquery);
            finalPredicate = cb.and(finalPredicate, gameHasAchievements);
        }
        cqForIds.where(finalPredicate)
                // GROUP BY g.storeId
                .groupBy(idGameRoot.get(Game_.storeId))
                // HAVING COUNT(DISTINCT cg.id.categoryId) = categoryIds.size()
                .having(cb.equal(cb.countDistinct(categorizedGameJoin.get(CategorizedGame_.category).get(Category_.id)), categoryIds.size()));
        List<Long> gameIds = em.createQuery(cqForIds).getResultList();

        // Main Query for Games
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);
        cq.where(gameRoot.get(Game_.storeId).in(gameIds));

        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }
}
