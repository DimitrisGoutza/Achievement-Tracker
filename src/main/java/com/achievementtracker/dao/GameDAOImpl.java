package com.achievementtracker.dao;

import com.achievementtracker.entity.*;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    public LocalDate findMinimumReleaseDate() {
        TypedQuery<LocalDate> query = em.createQuery("SELECT MIN(g.releaseDate) FROM Game g", LocalDate.class);
        return query.getSingleResult();
    }

    @Override
    public List<Game> findAllGames(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game g
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(g.storeId)
        cqForCount.select(cb.count(gameRootForCount.get(Game_.storeId)));
        // WHERE .. (common predicates)
        Predicate finalPredicateForCount = cb.conjunction();
        finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        cqForCount.where(finalPredicateForCount);
        long resultCount = em.createQuery(cqForCount).getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game g
        Root<Game> gameRoot = cq.from(Game.class);
        // WHERE .. (common predicates)
        Predicate finalPredicate = cb.conjunction();
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findOnlyGamesWithAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game g
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(g.storeId)
        cqForCount.select(cb.count(gameRootForCount.get(Game_.storeId)));
        // WHERE g.CHALLENGE_RATING != 0 (means the Game has achievements)
        Predicate finalPredicateForCount = cb.notEqual(gameRootForCount.get(Game_.challengeRating), 0);
        // AND .. (common predicates)
        finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        cqForCount.where(finalPredicateForCount);
        long resultCount = em.createQuery(cqForCount).getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game g
        Root<Game> gameRoot = cq.from(Game.class);
        // WHERE g.CHALLENGE_RATING != 0 (means the Game has achievements)
        Predicate finalPredicate = cb.notEqual(gameRoot.get(Game_.challengeRating), 0);
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findAllGamesByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
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
        // AND .. (common predicates)
        finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
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
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findOnlyGamesWithAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
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
        Predicate finalPredicateForCount = cb.and(cb.and(categoryPredicatesForCount),
                // AND g.CHALLENGE_RATING != 0 (means the Game has achievements)
                cb.notEqual(gameRootForCount.get(Game_.challengeRating), 0));
        // AND .. (common predicates)
        finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
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
        Predicate finalPredicate = cb.and(cb.and(categoryPredicates),
                // AND g.CHALLENGE_RATING != 0 (means the Game has achievements)
                cb.notEqual(gameRoot.get(Game_.challengeRating), 0));
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findOnlyGamesWithHiddenAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
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
        // AND .. (common predicates)
        finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
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
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findOnlyGamesWithHiddenAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> gameRootForCount = cqForCount.from(Game.class);
        // SELECT COUNT(DISTINCT g.storeId)
        cqForCount.select(cb.countDistinct(gameRootForCount.get(Game_.storeId)));
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicatesForCount = buildCategorizedGameJoin(cb, gameRootForCount, categoryIds);
        // JOIN Achievement a
        Join<Game, Achievement> achievementJoinForCount = gameRootForCount.join(Game_.achievements, JoinType.INNER);
        // WHERE a.hidden = true
        Predicate finalPredicateForCount = cb.isTrue(achievementJoinForCount.get(Achievement_.hidden));
        // AND cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        finalPredicateForCount = cb.and(finalPredicateForCount, cb.and(categoryPredicatesForCount));
        // AND .. (common predicates)
        finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
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
        // JOIN Achievement a
        Join<Game, Achievement> achievementJoin = gameRoot.join(Game_.achievements, JoinType.INNER);
        // WHERE a.hidden = true
        Predicate finalPredicate = cb.isTrue(achievementJoin.get(Achievement_.hidden));
        // AND cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        finalPredicate = cb.and(finalPredicate, cb.and(categoryPredicates));
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
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

    private Predicate buildCommonPredicates(CriteriaBuilder cb, Root<Game> gameRoot, Predicate finalPredicate,
                                            String searchTerm, Integer minReviews, Integer maxReviews,
                                            LocalDate minRelease, LocalDate maxRelease) {
        if (minReviews != null && minReviews != 0) { // also check for "0", since g.reviews >= 0 is redundant
            finalPredicate = cb.and(finalPredicate,
                    // g.reviews >= minReviews
                    cb.greaterThanOrEqualTo(gameRoot.get(Game_.reviews), minReviews));
        }
        if (maxReviews != null) {
            finalPredicate = cb.and(finalPredicate,
                    // g.reviews <= maxReviews
                    cb.lessThanOrEqualTo(gameRoot.get(Game_.reviews), maxReviews));
        }
        if (minRelease != null) {
            finalPredicate = cb.and(finalPredicate,
                    // g.releaseDate >= minRelease
                    cb.greaterThanOrEqualTo(gameRoot.get(Game_.releaseDate), minRelease));
        }
        if (maxRelease != null) {
            finalPredicate = cb.and(finalPredicate,
                    // g.releaseDate <= maxRelease
                    cb.lessThanOrEqualTo(gameRoot.get(Game_.releaseDate), maxRelease));
        }
        if (searchTerm != null) {
            finalPredicate = cb.and(finalPredicate,
                    // g.title LIKE '%term%'
                    cb.like(gameRoot.get(Game_.title), "%" +searchTerm+ "%"));
        }
        return finalPredicate;
    }
}
