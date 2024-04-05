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
        /* Count query */
        TypedQuery<Long> queryForCount = em.createQuery("SELECT COUNT(DISTINCT g.storeId) FROM Game g " +
                (achievementsOnly ? "WHERE EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId) " : ""), Long.class);
        long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        /* Main query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);
        cq.distinct(true);

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
        /* Count query */
        TypedQuery<Long> queryForCount = em.createQuery("SELECT COUNT(DISTINCT g.storeId) FROM Game g " +
                "WHERE g.title LIKE :searchPattern " +
                (achievementsOnly ? "AND EXISTS(SELECT 1 FROM Achievement a WHERE a.game.storeId = g.storeId) " : ""), Long.class);
        queryForCount.setParameter("searchPattern", "%" + searchTerm + "%");
        long totalRecordCount = queryForCount.getSingleResult();
        page.setTotalRecords(totalRecordCount);

        /* Main query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        Root<Game> gameRoot = cq.from(Game.class);
        cq.distinct(true);
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
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> gameRootForCount = countQuery.from(Game.class);
        // SELECT COUNT(DISTINCT g.storeId)
        countQuery.select(cb.countDistinct(gameRootForCount.get(Game_.storeId)));
        // (JOIN Achievement)
        if (achievementsOnly)
            gameRootForCount.join(Game_.achievements, JoinType.INNER);
        // JOIN CategorizedGame
        Predicate[] categoryPredicatesForCount = buildCategorizedGameJoin(cb, gameRootForCount, categoryIds);
        // WHERE cg.category.id = categoryId (for each categoryId)
        countQuery.where(cb.and(categoryPredicatesForCount));

        long totalRecordCount = em.createQuery(countQuery).getSingleResult();
        page.setTotalRecords(totalRecordCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g
        cq.distinct(true);
        // (JOIN Achievement)
        if (achievementsOnly)
            gameRoot.join(Game_.achievements, JoinType.INNER);
        // JOIN CategorizedGame
        Predicate[] categoryPredicates = buildCategorizedGameJoin(cb, gameRoot, categoryIds);
        // WHERE cg.category.id = categoryId (for each categoryId)
        cq.where(cb.and(categoryPredicates));

        TypedQuery<Game> query = page.createQuery(em, cq, gameRoot);
        return query.getResultList();
    }

    @Override
    public List<Game> findAllByCategoryId(String searchTerm, List<Long> categoryIds, boolean achievementsOnly, Page page) {
        /* Count query */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        // FROM Game
        Root<Game> gameRootForCount = countQuery.from(Game.class);
        // SELECT COUNT(DISTINCT g.storeId)
        countQuery.select(cb.countDistinct(gameRootForCount.get(Game_.storeId)));
        // (JOIN Achievement)
        if (achievementsOnly)
            gameRootForCount.join(Game_.achievements, JoinType.INNER);
        // JOIN CategorizedGame
        Predicate[] categoryPredicatesForCount = buildCategorizedGameJoin(cb, gameRootForCount, categoryIds);
        // WHERE cg.category.id = categoryId (for each categoryId)
        countQuery.where(cb.and(cb.like(gameRootForCount.get(Game_.title), "%" + searchTerm + "%"),
                cb.and(categoryPredicatesForCount)));

        long totalRecordCount = em.createQuery(countQuery).getSingleResult();
        page.setTotalRecords(totalRecordCount);

        /* Main query */
        CriteriaQuery<Game> cq = cb.createQuery(Game.class);
        // FROM Game
        Root<Game> gameRoot = cq.from(Game.class);
        // SELECT DISTINCT g
        cq.distinct(true);
        // (JOIN Achievement)
        if (achievementsOnly)
            gameRoot.join(Game_.achievements, JoinType.INNER);
        // JOIN CategorizedGame
        Predicate[] categoryPredicates = buildCategorizedGameJoin(cb, gameRoot, categoryIds);
        // WHERE cg.category.id = categoryId (for each categoryId)
        cq.where(cb.and(cb.like(gameRoot.get(Game_.title), "%" + searchTerm + "%"),
                cb.and(categoryPredicates)));

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
