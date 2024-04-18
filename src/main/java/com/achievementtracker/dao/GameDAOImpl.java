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
    public Integer findMaxReviews() {
        TypedQuery<Integer> query = em.createQuery("SELECT MAX(DISTINCT g.reviews) FROM Game g", Integer.class);
        return query.getSingleResult();
    }

    @Override
    public List<Game> findAllGames(String searchTerm, Integer minReviews, Integer maxReviews, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game g
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(g.storeId)
        cqForCount.select(cb.count(gameRootForCount.get(Game_.storeId)));
        // WHERE ..
        Predicate finalPredicateForCount;
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            finalPredicateForCount = cb.greaterThanOrEqualTo(gameRootForCount.get(Game_.reviews), minReviews);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            finalPredicateForCount = cb.between(gameRootForCount.get(Game_.reviews), minReviews, maxReviews);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRootForCount.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicateForCount = cb.and(finalPredicateForCount, searchPredicate);
        }
        cqForCount.where(finalPredicateForCount);
        long resultCount = em.createQuery(cqForCount).getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game g
        Root<Game> gameRoot = cq.from(Game.class);
        // WHERE ..
        Predicate finalPredicate;
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            finalPredicate = cb.greaterThanOrEqualTo(gameRoot.get(Game_.reviews), minReviews);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            finalPredicate = cb.between(gameRoot.get(Game_.reviews), minReviews, maxReviews);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRoot.get(Game_.title), "%" + searchTerm + "%");
            finalPredicate = cb.and(finalPredicate, searchPredicate);
        }
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findOnlyGamesWithAchievements(String searchTerm, Integer minReviews, Integer maxReviews, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game g
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(g.storeId)
        cqForCount.select(cb.count(gameRootForCount.get(Game_.storeId)));
        // WHERE EXISTS (SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId)
        Subquery<Long> subQueryForCount = cqForCount.subquery(Long.class);
        Root<Achievement> achievementRootForCount = subQueryForCount.from(Achievement.class);
        subQueryForCount.where(cb.equal(achievementRootForCount.get(Achievement_.game).get(Game_.storeId), gameRootForCount.get(Game_.storeId)));
        Predicate finalPredicateForCount = cb.exists(subQueryForCount);
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRootForCount.get(Game_.reviews), minReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRootForCount.get(Game_.reviews), minReviews, maxReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRootForCount.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicateForCount = cb.and(finalPredicateForCount, searchPredicate);
        }
        cqForCount.where(finalPredicateForCount);
        long resultCount = em.createQuery(cqForCount).getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game g
        Root<Game> gameRoot = cq.from(Game.class);
        // WHERE EXISTS (SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId)
        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Achievement> achievementRoot = subquery.from(Achievement.class);
        subquery.where(cb.equal(achievementRoot.get(Achievement_.game).get(Game_.storeId), gameRoot.get(Game_.storeId)));
        Predicate finalPredicate = cb.exists(subquery);
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRoot.get(Game_.reviews), minReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRoot.get(Game_.reviews), minReviews, maxReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRoot.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicate = cb.and(finalPredicate, searchPredicate);
        }
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findAllGamesByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(DISTINCT g.storeId)
        cqForCount.select(cb.countDistinct(gameRootForCount.get(Game_.storeId)));
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicatesForCount = buildCategorizedGameJoin(cb, gameRootForCount, categoryIds);
        // WHERE cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        Predicate finalPredicateForCount = cb.and(categoryPredicatesForCount);
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRootForCount.get(Game_.reviews), minReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRootForCount.get(Game_.reviews), minReviews, maxReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRootForCount.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicateForCount = cb.and(finalPredicateForCount, searchPredicate);
        }
        cqForCount.where(finalPredicateForCount);
        long totalRecordCount = em.createQuery(cqForCount).getSingleResult();
        page.setTotalRecords(totalRecordCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g
        cq.distinct(true);
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicates = buildCategorizedGameJoin(cb, gameRoot, categoryIds);
        // WHERE cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        Predicate finalPredicate = cb.and(categoryPredicates);
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRoot.get(Game_.reviews), minReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRoot.get(Game_.reviews), minReviews, maxReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRoot.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicate = cb.and(finalPredicate, searchPredicate);
        }
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findOnlyGamesWithAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(DISTINCT g.storeId)
        cqForCount.select(cb.countDistinct(gameRootForCount.get(Game_.storeId)));
        // JOIN Achievement a
        gameRootForCount.join(Game_.achievements, JoinType.INNER);
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicatesForCount = buildCategorizedGameJoin(cb, gameRootForCount, categoryIds);
        // WHERE cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        Predicate finalPredicateForCount = cb.and(categoryPredicatesForCount);
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRootForCount.get(Game_.reviews), minReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRootForCount.get(Game_.reviews), minReviews, maxReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRootForCount.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicateForCount = cb.and(finalPredicateForCount, searchPredicate);
        }
        cqForCount.where(finalPredicateForCount);
        long totalRecordCount = em.createQuery(cqForCount).getSingleResult();
        page.setTotalRecords(totalRecordCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g
        cq.distinct(true);
        // JOIN Achievement a
        gameRoot.join(Game_.achievements, JoinType.INNER);
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicates = buildCategorizedGameJoin(cb, gameRoot, categoryIds);
        // WHERE cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        Predicate finalPredicate = cb.and(categoryPredicates);
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRoot.get(Game_.reviews), minReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRoot.get(Game_.reviews), minReviews, maxReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRoot.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicate = cb.and(finalPredicate, searchPredicate);
        }
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findOnlyGamesWithHiddenAchievements(String searchTerm, Integer minReviews, Integer maxReviews, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game g
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(g.storeId)
        cqForCount.select(cb.countDistinct(gameRootForCount.get(Game_.storeId)));
        // JOIN Achievement a
        Join<Game, Achievement> achievementJoinForCount = gameRootForCount.join(Game_.achievements, JoinType.INNER);
        // WHERE a.hidden = true
        Predicate finalPredicateForCount = cb.isTrue(achievementJoinForCount.get(Achievement_.hidden));
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRootForCount.get(Game_.reviews), minReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRootForCount.get(Game_.reviews), minReviews, maxReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRootForCount.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicateForCount = cb.and(finalPredicateForCount, searchPredicate);
        }
        cqForCount.where(finalPredicateForCount);
        long resultCount = em.createQuery(cqForCount).getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game g
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g
        cq.distinct(true);
        // JOIN Achievement a
        Join<Game, Achievement> achievementJoin = gameRoot.join(Game_.achievements, JoinType.INNER);
        // WHERE a.hidden = true
        Predicate finalPredicate = cb.isTrue(achievementJoin.get(Achievement_.hidden));
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRoot.get(Game_.reviews), minReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRoot.get(Game_.reviews), minReviews, maxReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRoot.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicate = cb.and(finalPredicate, searchPredicate);
        }
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findOnlyGamesWithHiddenAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(DISTINCT g.storeId)
        cqForCount.select(cb.countDistinct(gameRootForCount.get(Game_.storeId)));
        // JOIN Achievement a
        Join<Game, Achievement> achievementJoinForCount = gameRootForCount.join(Game_.achievements, JoinType.INNER);
        // WHERE a.hidden = true
        Predicate finalPredicateForCount = cb.isTrue(achievementJoinForCount.get(Achievement_.hidden));
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicatesForCount = buildCategorizedGameJoin(cb, gameRootForCount, categoryIds);
        // WHERE cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        finalPredicateForCount = cb.and(finalPredicateForCount, cb.and(categoryPredicatesForCount));
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRootForCount.get(Game_.reviews), minReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRootForCount.get(Game_.reviews), minReviews, maxReviews);
            finalPredicateForCount = cb.and(finalPredicateForCount, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRootForCount.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicateForCount = cb.and(finalPredicateForCount, searchPredicate);
        }
        cqForCount.where(finalPredicateForCount);
        long totalRecordCount = em.createQuery(cqForCount).getSingleResult();
        page.setTotalRecords(totalRecordCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g
        cq.distinct(true);
        // JOIN Achievement a
        Join<Game, Achievement> achievementJoin = gameRoot.join(Game_.achievements, JoinType.INNER);
        // WHERE a.hidden = true
        Predicate finalPredicate = cb.isTrue(achievementJoin.get(Achievement_.hidden));
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicates = buildCategorizedGameJoin(cb, gameRoot, categoryIds);
        // WHERE cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        finalPredicate = cb.and(finalPredicate, cb.and(categoryPredicates));
        if (maxReviews == null) {
            // g.REVIEWS >= minReviews
            Predicate reviewsPredicate = cb.greaterThanOrEqualTo(gameRoot.get(Game_.reviews), minReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        } else {
            // g.REVIEWS BETWEEN minReviews AND maxReviews
            Predicate reviewsPredicate = cb.between(gameRoot.get(Game_.reviews), minReviews, maxReviews);
            finalPredicate = cb.and(finalPredicate, reviewsPredicate);
        }
        if (!searchTerm.isEmpty()) {
            // g.title LIKE '%term%'
            Predicate searchPredicate = cb.like(gameRoot.get(Game_.title), "%" +searchTerm+ "%");
            finalPredicate = cb.and(finalPredicate, searchPredicate);
        }
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    private Predicate[] buildCategorizedGameJoin(CriteriaBuilder cb, Root<Game> gameRoot, List<Long> categoryIds) {
        Predicate[] categoryPredicates = new Predicate[categoryIds.size()];

        int index = 0;
        for (Long categoryId : categoryIds) {
            Join<Game, CategorizedGame> categorizedGameJoin = gameRoot.join(Game_.categorizedGames, JoinType.INNER);
            Predicate predicate = cb.equal(categorizedGameJoin.get(CategorizedGame_.category).get(Category_.id), categoryId);
            categoryPredicates[index++] = predicate;
        }

        return categoryPredicates;
    }
}
