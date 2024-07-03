package com.achievementtracker.dao;

import com.achievementtracker.dto.games_endpoint.GameDTO;
import com.achievementtracker.dto.home_endpoint.LeaderboardGameDTO1;
import com.achievementtracker.dto.home_endpoint.LeaderboardGameDTO2;
import com.achievementtracker.dto.search_endpoint.MinimalGameDTO;
import com.achievementtracker.entity.*;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
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
    public Double calculateChallengeRatingPercentile(Long gameId) {
        TypedQuery<Double> query = em.createQuery(
                "SELECT (CAST((SELECT COUNT(g2.storeId) FROM Game g2 WHERE g2.challengeRating <> 0 AND g2.challengeRating <= g.challengeRating) AS DOUBLE) / " +
                        "CAST((SELECT COUNT(g2.storeId) FROM Game g2 WHERE g2.challengeRating <> 0)  AS DOUBLE)) " +
                        "FROM Game g WHERE g.storeId = :gameId", Double.class);
        query.setParameter("gameId", gameId);
        return query.getSingleResult();
    }

    @Override
    public List<LeaderboardGameDTO1> findTopXGamesByChallengeRating(int amount) {
        TypedQuery<LeaderboardGameDTO1> query = em.createQuery("SELECT new " +
                "com.achievementtracker.dto.home_endpoint.LeaderboardGameDTO1(g.storeId, g.title, g.images.capsuleSmallImageURL, g.challengeRating) " +
                "FROM Game g ORDER BY g.challengeRating DESC", LeaderboardGameDTO1.class);
        query.setMaxResults(amount);
        return query.getResultList();
    }

    @Override
    public List<LeaderboardGameDTO2> findTopXGamesByAchievementCount(int amount) {
        TypedQuery<LeaderboardGameDTO2> query = em.createQuery("SELECT new " +
                "com.achievementtracker.dto.home_endpoint.LeaderboardGameDTO2(g.storeId, g.title, g.images.capsuleSmallImageURL, COUNT(a.id)) " +
                "FROM Game g JOIN Achievement a on a.game.storeId = g.storeId " +
                "GROUP BY g.storeId ORDER BY COUNT(a.id) DESC", LeaderboardGameDTO2.class);
        query.setMaxResults(amount);
        return query.getResultList();
    }

    @Override
    public List<GameDTO> findAllGames(Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        /* Count query */
        if (countQuery) {
            CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
            // FROM Game g
            Root<Game> gameRootForCount = cqForCount.from(Game.class);
            // SELECT COUNT(g.storeId)
            cqForCount.select(cb.count(gameRootForCount.get(Game_.storeId)));
            // WHERE .. (common predicates)
            Predicate finalPredicateForCount = cb.conjunction();
            finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, minReviews, maxReviews, minRelease, maxRelease);
            cqForCount.where(finalPredicateForCount);
            long resultCount = em.createQuery(cqForCount).getSingleResult();
            page.setTotalRecords(resultCount);
        }

        /* Main query */
        CriteriaQuery<GameDTO> cq = cb.createQuery(GameDTO.class);
        // FROM Game g
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT g.(only needed columns)
        cq.multiselect(gameRoot.get(Game_.storeId),
                gameRoot.get(Game_.title),
                gameRoot.get(Game_.releaseDate),
                gameRoot.get(Game_.rating),
                gameRoot.get(Game_.images).get(Image_.capsuleImageURL),
                gameRoot.get(Game_.challengeRating),
                gameRoot.get(Game_.difficultySpread));
        // WHERE .. (common predicates)
        Predicate finalPredicate = cb.conjunction();
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<GameDTO> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<GameDTO> findOnlyGamesWithAchievements(Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        /* Count query */
        if (countQuery) {
            CriteriaQuery<Long> cqForCount = cb.createQuery(Long.class);
            // FROM Game g
            Root<Game> gameRootForCount = cqForCount.from(Game.class);
            // SELECT COUNT(g.storeId)
            cqForCount.select(cb.count(gameRootForCount.get(Game_.storeId)));
            // WHERE g.CHALLENGE_RATING != 0 (means the Game has achievements)
            Predicate finalPredicateForCount = cb.notEqual(gameRootForCount.get(Game_.challengeRating), 0);
            // AND .. (common predicates)
            finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, minReviews, maxReviews, minRelease, maxRelease);
            cqForCount.where(finalPredicateForCount);
            long resultCount = em.createQuery(cqForCount).getSingleResult();
            page.setTotalRecords(resultCount);
        }

        /* Main query */
        CriteriaQuery<GameDTO> cq = cb.createQuery(GameDTO.class);
        // FROM Game g
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT g.(only needed columns)
        cq.multiselect(gameRoot.get(Game_.storeId),
                gameRoot.get(Game_.title),
                gameRoot.get(Game_.releaseDate),
                gameRoot.get(Game_.rating),
                gameRoot.get(Game_.images).get(Image_.capsuleImageURL),
                gameRoot.get(Game_.challengeRating),
                gameRoot.get(Game_.difficultySpread));
        // WHERE g.CHALLENGE_RATING != 0 (means the Game has achievements)
        Predicate finalPredicate = cb.notEqual(gameRoot.get(Game_.challengeRating), 0);
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<GameDTO> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<GameDTO> findAllGamesByCategoryId(List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        /* Count query */
        if (countQuery) {
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
            finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, minReviews, maxReviews, minRelease, maxRelease);
            cqForCount.where(finalPredicateForCount);
            long totalRecordCount = em.createQuery(cqForCount).getSingleResult();
            page.setTotalRecords(totalRecordCount);
        }

        /* Main query */
        CriteriaQuery<GameDTO> cq = cb.createQuery(GameDTO.class);
        // FROM Game
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g.(only needed columns)
        cq.multiselect(gameRoot.get(Game_.storeId),
                        gameRoot.get(Game_.title),
                        gameRoot.get(Game_.releaseDate),
                        gameRoot.get(Game_.rating),
                        gameRoot.get(Game_.images).get(Image_.capsuleImageURL),
                        gameRoot.get(Game_.challengeRating),
                        gameRoot.get(Game_.difficultySpread))
                .distinct(true);
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicates = buildCategorizedGameJoin(cb, gameRoot, categoryIds);
        // WHERE cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        Predicate finalPredicate = cb.and(categoryPredicates);
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<GameDTO> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<GameDTO> findOnlyGamesWithAchievementsByCategoryId(List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        /* Count query */
        if (countQuery) {
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
            finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, minReviews, maxReviews, minRelease, maxRelease);
            cqForCount.where(finalPredicateForCount);
            long totalRecordCount = em.createQuery(cqForCount).getSingleResult();
            page.setTotalRecords(totalRecordCount);
        }

        /* Main query */
        CriteriaQuery<GameDTO> cq = cb.createQuery(GameDTO.class);
        // FROM Game
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g.(only needed columns)
        cq.multiselect(gameRoot.get(Game_.storeId),
                        gameRoot.get(Game_.title),
                        gameRoot.get(Game_.releaseDate),
                        gameRoot.get(Game_.rating),
                        gameRoot.get(Game_.images).get(Image_.capsuleImageURL),
                        gameRoot.get(Game_.challengeRating),
                        gameRoot.get(Game_.difficultySpread))
                .distinct(true);
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicates = buildCategorizedGameJoin(cb, gameRoot, categoryIds);
        // WHERE cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        Predicate finalPredicate = cb.and(cb.and(categoryPredicates),
                // AND g.CHALLENGE_RATING != 0 (means the Game has achievements)
                cb.notEqual(gameRoot.get(Game_.challengeRating), 0));
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<GameDTO> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<GameDTO> findOnlyGamesWithHiddenAchievements(Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        /* Count query */
        if (countQuery) {
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
            finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, minReviews, maxReviews, minRelease, maxRelease);
            cqForCount.where(finalPredicateForCount);
            long resultCount = em.createQuery(cqForCount).getSingleResult();
            page.setTotalRecords(resultCount);
        }

        /* Main query */
        CriteriaQuery<GameDTO> cq = cb.createQuery(GameDTO.class);
        // FROM Game g
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g.(only needed columns)
        cq.multiselect(gameRoot.get(Game_.storeId),
                        gameRoot.get(Game_.title),
                        gameRoot.get(Game_.releaseDate),
                        gameRoot.get(Game_.rating),
                        gameRoot.get(Game_.images).get(Image_.capsuleImageURL),
                        gameRoot.get(Game_.challengeRating),
                        gameRoot.get(Game_.difficultySpread))
                .distinct(true);
        // JOIN Achievement a
        Join<Game, Achievement> achievementJoin = gameRoot.join(Game_.achievements, JoinType.INNER);
        // WHERE a.hidden = true
        Predicate finalPredicate = cb.isTrue(achievementJoin.get(Achievement_.hidden));
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<GameDTO> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<GameDTO> findOnlyGamesWithHiddenAchievementsByCategoryId(List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page, boolean countQuery) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        /* Count query */
        if (countQuery) {
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
            finalPredicateForCount = buildCommonPredicates(cb, gameRootForCount, finalPredicateForCount, minReviews, maxReviews, minRelease, maxRelease);
            cqForCount.where(finalPredicateForCount);
            long totalRecordCount = em.createQuery(cqForCount).getSingleResult();
            page.setTotalRecords(totalRecordCount);
        }

        /* Main query */
        CriteriaQuery<GameDTO> cq = cb.createQuery(GameDTO.class);
        // FROM Game
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g.(only needed columns)
        cq.multiselect(gameRoot.get(Game_.storeId),
                        gameRoot.get(Game_.title),
                        gameRoot.get(Game_.releaseDate),
                        gameRoot.get(Game_.rating),
                        gameRoot.get(Game_.images).get(Image_.capsuleImageURL),
                        gameRoot.get(Game_.challengeRating),
                        gameRoot.get(Game_.difficultySpread))
                .distinct(true);
        // JOIN CategorizedGame cg1, cg1 JOIN CategorizedGame cg2, cg2 ..
        Predicate[] categoryPredicates = buildCategorizedGameJoin(cb, gameRoot, categoryIds);
        // JOIN Achievement a
        Join<Game, Achievement> achievementJoin = gameRoot.join(Game_.achievements, JoinType.INNER);
        // WHERE a.hidden = true
        Predicate finalPredicate = cb.isTrue(achievementJoin.get(Achievement_.hidden));
        // AND cg1.category.id = categoryId1 AND cg2.category.id = categoryId2 AND cg3 ..
        finalPredicate = cb.and(finalPredicate, cb.and(categoryPredicates));
        // AND .. (common predicates)
        finalPredicate = buildCommonPredicates(cb, gameRoot, finalPredicate, minReviews, maxReviews, minRelease, maxRelease);
        cq.where(finalPredicate);
        TypedQuery<GameDTO> query = page.createQuery(em, cq, gameRoot);
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
                                            Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease) {
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
        return finalPredicate;
    }

    @Override
    public Game findByIdWithAchievements(Long gameId) {
        TypedQuery<Game> query = em.createQuery("SELECT g FROM Game g " +
                "LEFT JOIN FETCH g.achievements WHERE g.storeId = :gameId", Game.class);
        query.setParameter("gameId", gameId);
        return query.getSingleResult();
    }

    /* ------------------------------------- Native Full-Text search queries ------------------------------------- */

    @Override
    public List<MinimalGameDTO> searchAllGames(String searchTerm) {
        Query query = em.createNativeQuery("SELECT g.STORE_ID, g.TITLE, g.RELEASE_DATE, g.CAPSULE_SMALL_IMAGE " +
                "FROM GAME g WHERE MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE)");
        query.setParameter("searchTerm", searchTerm);

        List<Object[]> resultList = query.getResultList();
        List<MinimalGameDTO> games = new LinkedList<>();
        for (Object[] row : resultList) {
            Long storeId = (Long) row[0];
            String title = (String) row[1];
            String capsuleSmallImageURL = (String) row[2];

            MinimalGameDTO minimalGameDTO = new MinimalGameDTO(
                    storeId, title, capsuleSmallImageURL
            );
            games.add(minimalGameDTO);
        }
        return games;
    }

    @Override
    public List<MinimalGameDTO> searchAllGames(String searchTerm, int size) {
        Query query = em.createNativeQuery("SELECT g.STORE_ID, g.TITLE, g.CAPSULE_SMALL_IMAGE " +
                "FROM GAME g WHERE MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) LIMIT :size" );
        query.setParameter("searchTerm", searchTerm);
        query.setParameter("size", size);

        List<Object[]> resultList = query.getResultList();
        List<MinimalGameDTO> games = new LinkedList<>();
        for (Object[] row : resultList) {
            Long storeId = (Long) row[0];
            String title = (String) row[1];
            String capsuleSmallImageURL = (String) row[2];

            MinimalGameDTO minimalGameDTO = new MinimalGameDTO(
                    storeId, title, capsuleSmallImageURL
            );
            games.add(minimalGameDTO);
        }
        return games;
    }

    @Override
    public List<GameDTO> searchAllGames(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        String sqlForCount = "SELECT COUNT(g.STORE_ID) FROM GAME g WHERE MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sqlForCount = appendCommonPredicates(sqlForCount, minReviews, maxReviews, minRelease, maxRelease);

        Query queryForCount = em.createNativeQuery(sqlForCount);
        bindCommonPredicateValues(queryForCount, sqlForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        long resultCount = (long) queryForCount.getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        String sql = "SELECT g.STORE_ID, g.TITLE, g.RELEASE_DATE, g.RATING, g.CAPSULE_IMAGE, g.CHALLENGE_RATING, g.DIFFICULTY_SPREAD FROM GAME g " +
                "WHERE MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sql = appendCommonPredicates(sql, minReviews, maxReviews, minRelease, maxRelease);

        Query query = page.createNativeQuery(em, sql, "g", transformAttributeToNativeSQL(page.getSortAttribute()));
        bindCommonPredicateValues(query, sql, searchTerm, minReviews, maxReviews, minRelease, maxRelease);

        return castResultListToDTO(query.getResultList());
    }

    @Override
    public List<GameDTO> searchOnlyGamesWithAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        String sqlForCount = "SELECT COUNT(g.STORE_ID) FROM GAME g WHERE g.CHALLENGE_RATING <> 0 AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sqlForCount = appendCommonPredicates(sqlForCount, minReviews, maxReviews, minRelease, maxRelease);

        Query queryForCount = em.createNativeQuery(sqlForCount);
        bindCommonPredicateValues(queryForCount, sqlForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        long resultCount = (long) queryForCount.getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        String sql = "SELECT g.STORE_ID, g.TITLE, g.RELEASE_DATE, g.RATING, g.CAPSULE_IMAGE, g.CHALLENGE_RATING, g.DIFFICULTY_SPREAD FROM GAME g " +
                "WHERE g.CHALLENGE_RATING <> 0 AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sql = appendCommonPredicates(sql, minReviews, maxReviews, minRelease, maxRelease);

        Query query = page.createNativeQuery(em, sql, "g", transformAttributeToNativeSQL(page.getSortAttribute()));
        bindCommonPredicateValues(query, sql, searchTerm, minReviews, maxReviews, minRelease, maxRelease);

        return castResultListToDTO(query.getResultList());
    }

    @Override
    public List<GameDTO> searchAllGamesByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        String sqlForCount = "SELECT COUNT(DISTINCT g.STORE_ID) FROM GAME g " + buildCategorizedGameJoinNativeSQL(categoryIds) +
                "AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sqlForCount = appendCommonPredicates(sqlForCount, minReviews, maxReviews, minRelease, maxRelease);

        Query queryForCount = em.createNativeQuery(sqlForCount);
        bindCommonPredicateValues(queryForCount, sqlForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease, categoryIds);
        long resultCount = (long) queryForCount.getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        String sql = "SELECT DISTINCT g.STORE_ID, g.TITLE, g.RELEASE_DATE, g.RATING, g.CAPSULE_IMAGE, g.CHALLENGE_RATING, g.DIFFICULTY_SPREAD FROM GAME g " +
                buildCategorizedGameJoinNativeSQL(categoryIds) + "AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sql = appendCommonPredicates(sql, minReviews, maxReviews, minRelease, maxRelease);

        Query query = page.createNativeQuery(em, sql, "g", transformAttributeToNativeSQL(page.getSortAttribute()));
        bindCommonPredicateValues(query, sql, searchTerm, minReviews, maxReviews, minRelease, maxRelease, categoryIds);

        return castResultListToDTO(query.getResultList());
    }

    @Override
    public List<GameDTO> searchOnlyGamesWithAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        String sqlForCount = "SELECT COUNT(DISTINCT g.STORE_ID) FROM GAME g " + buildCategorizedGameJoinNativeSQL(categoryIds) +
                "AND g.CHALLENGE_RATING <> 0 AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sqlForCount = appendCommonPredicates(sqlForCount, minReviews, maxReviews, minRelease, maxRelease);

        Query queryForCount = em.createNativeQuery(sqlForCount);
        bindCommonPredicateValues(queryForCount, sqlForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease, categoryIds);
        long resultCount = (long) queryForCount.getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        String sql = "SELECT DISTINCT g.STORE_ID, g.TITLE, g.RELEASE_DATE, g.RATING, g.CAPSULE_IMAGE, g.CHALLENGE_RATING, g.DIFFICULTY_SPREAD FROM GAME g " +
                buildCategorizedGameJoinNativeSQL(categoryIds) + "AND g.CHALLENGE_RATING <> 0 AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sql = appendCommonPredicates(sql, minReviews, maxReviews, minRelease, maxRelease);

        Query query = page.createNativeQuery(em, sql, "g", transformAttributeToNativeSQL(page.getSortAttribute()));
        bindCommonPredicateValues(query, sql, searchTerm, minReviews, maxReviews, minRelease, maxRelease, categoryIds);

        return castResultListToDTO(query.getResultList());
    }

    @Override
    public List<GameDTO> searchOnlyGamesWithHiddenAchievements(String searchTerm, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        String sqlForCount = "SELECT COUNT(DISTINCT g.STORE_ID) FROM GAME g JOIN ACHIEVEMENT a ON g.STORE_ID = a.GAME_ID " +
                "WHERE a.HIDDEN = TRUE AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sqlForCount = appendCommonPredicates(sqlForCount, minReviews, maxReviews, minRelease, maxRelease);

        Query queryForCount = em.createNativeQuery(sqlForCount);
        bindCommonPredicateValues(queryForCount, sqlForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease);
        long resultCount = (long) queryForCount.getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        String sql = "SELECT DISTINCT g.STORE_ID, g.TITLE, g.RELEASE_DATE, g.RATING, g.CAPSULE_IMAGE, g.CHALLENGE_RATING, g.DIFFICULTY_SPREAD FROM GAME g " +
                "JOIN ACHIEVEMENT a ON g.STORE_ID = a.GAME_ID WHERE a.HIDDEN = TRUE AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sql = appendCommonPredicates(sql, minReviews, maxReviews, minRelease, maxRelease);

        Query query = page.createNativeQuery(em, sql, "g", transformAttributeToNativeSQL(page.getSortAttribute()));
        bindCommonPredicateValues(query, sql, searchTerm, minReviews, maxReviews, minRelease, maxRelease);

        return castResultListToDTO(query.getResultList());
    }

    @Override
    public List<GameDTO> searchOnlyGamesWithHiddenAchievementsByCategoryId(String searchTerm, List<Long> categoryIds, Integer minReviews, Integer maxReviews, LocalDate minRelease, LocalDate maxRelease, Page page) {
        /* Count query */
        String sqlForCount = "SELECT COUNT(DISTINCT g.STORE_ID) FROM GAME g JOIN ACHIEVEMENT a ON g.STORE_ID = a.GAME_ID " + buildCategorizedGameJoinNativeSQL(categoryIds) +
                "AND a.HIDDEN = TRUE AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sqlForCount = appendCommonPredicates(sqlForCount, minReviews, maxReviews, minRelease, maxRelease);

        Query queryForCount = em.createNativeQuery(sqlForCount);
        bindCommonPredicateValues(queryForCount, sqlForCount, searchTerm, minReviews, maxReviews, minRelease, maxRelease, categoryIds);
        long resultCount = (long) queryForCount.getSingleResult();
        page.setTotalRecords(resultCount);

        /* Main query */
        String sql = "SELECT DISTINCT g.STORE_ID, g.TITLE, g.RELEASE_DATE, g.RATING, g.CAPSULE_IMAGE, g.CHALLENGE_RATING, g.DIFFICULTY_SPREAD FROM GAME g " +
                "JOIN ACHIEVEMENT a ON g.STORE_ID = a.GAME_ID " + buildCategorizedGameJoinNativeSQL(categoryIds) +
                "AND a.HIDDEN = TRUE AND MATCH(g.TITLE) AGAINST(:searchTerm IN BOOLEAN MODE) ";
        sql = appendCommonPredicates(sql, minReviews, maxReviews, minRelease, maxRelease);

        Query query = page.createNativeQuery(em, sql, "g", transformAttributeToNativeSQL(page.getSortAttribute()));
        bindCommonPredicateValues(query, sql, searchTerm, minReviews, maxReviews, minRelease, maxRelease, categoryIds);

        return castResultListToDTO(query.getResultList());
    }

    private String buildCategorizedGameJoinNativeSQL(List<Long> categoryIds) {
        StringBuilder join = new StringBuilder();

        for (int index=1; index <= categoryIds.size(); index++)
            join.append("JOIN GAME_CATEGORY gc").append(index).append(" ON g.STORE_ID = gc").append(index).append(".GAME_ID ");

        join.append("WHERE gc1.CATEGORY_ID = :categoryId1 ");
        for (int index=2; index <= categoryIds.size(); index++)
            join.append("AND gc").append(index).append(".CATEGORY_ID = :categoryId").append(index).append(" ");

        return join.toString();
    }

    private String appendCommonPredicates(String sql, Integer minReviews, Integer maxReviews,
                                          LocalDate minRelease, LocalDate maxRelease) {
        if (minReviews != null && minReviews != 0) // also check for "0", since g.reviews >= 0 is redundant
            sql += "AND g.REVIEWS >= :minReviews ";
        if (maxReviews != null)
            sql += "AND g.REVIEWS <= :maxReviews ";
        if (minRelease != null)
            sql += "AND g.RELEASE_DATE >= :minRelease ";
        if (maxRelease != null)
            sql += "AND g.RELEASE_DATE <= :maxRelease ";

        return sql;
    }

    private void bindCommonPredicateValues(Query query, String sql, String searchTerm, Integer minReviews, Integer maxReviews,
                                           LocalDate minRelease, LocalDate maxRelease) {
        query.setParameter("searchTerm", "*" + searchTerm + "*");

        if (sql.contains(":minReviews"))
            query.setParameter("minReviews", minReviews);
        if (sql.contains(":maxReviews"))
            query.setParameter("maxReviews", maxReviews);
        if (sql.contains(":minRelease"))
            query.setParameter("minRelease", minRelease);
        if (sql.contains(":maxRelease"))
            query.setParameter("maxRelease", maxRelease);
    }

    private void bindCommonPredicateValues(Query query, String sql, String searchTerm, Integer minReviews, Integer maxReviews,
                                           LocalDate minRelease, LocalDate maxRelease, List<Long> categoryIds) {
        for (int index=0; index < categoryIds.size(); index++)
            query.setParameter("categoryId" + (index + 1), categoryIds.get(index));

        query.setParameter("searchTerm", "*" + searchTerm + "*");

        if (sql.contains(":minReviews"))
            query.setParameter("minReviews", minReviews);
        if (sql.contains(":maxReviews"))
            query.setParameter("maxReviews", maxReviews);
        if (sql.contains(":minRelease"))
            query.setParameter("minRelease", minRelease);
        if (sql.contains(":maxRelease"))
            query.setParameter("maxRelease", maxRelease);
    }

    private List<GameDTO> castResultListToDTO(List<Object[]> resultList) {
        List<GameDTO> games = new LinkedList<>();
        for (Object[] row : resultList) {
            Long storeId = (Long) row[0];
            String title = (String) row[1];
            java.sql.Date releaseDate = (Date) row[2];
            BigDecimal rating = (BigDecimal) row[3];
            String capsuleImageURL = (String) row[4];
            Integer challengeRating = (Integer) row[5];
            BigDecimal difficultySpread = (BigDecimal) row[6];

            GameDTO gameDTO = new GameDTO(storeId, title, releaseDate == null ? null : releaseDate.toLocalDate(),
                    rating == null ? null : rating.doubleValue(), capsuleImageURL, challengeRating,
                    difficultySpread == null ? null : difficultySpread.doubleValue());

            games.add(gameDTO);
        }
        return games;
    }

    private String transformAttributeToNativeSQL(SingularAttribute<?, ?> attribute) {
        return switch (attribute.getName()) {
            case "storeId" -> "STORE_ID";
            case "title" -> "TITLE";
            case "challengeRating" -> "CHALLENGE_RATING";
            case "difficultySpread" -> "DIFFICULTY_SPREAD";
            case "rating" -> "RATING";
            case "releaseDate" -> "RELEASE_DATE";
            default -> "CHALLENGE_RATING"; // will never get here, but is here as a safeguard
        };
    }

}