package com.achievementtracker.dao;

import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Game;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AchievementDAOImpl extends GenericDAOImpl<Achievement, Long> implements AchievementDAO {

    public AchievementDAOImpl() {
        super(Achievement.class);
    }

    @Override
    public Long getCountForGame(Game game) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(a) FROM Achievement a WHERE a.game = :game", Long.class)
                .setParameter("game", game);
        return query.getSingleResult();
    }

    @Override
    public Map<Long, List<Achievement>> getTopXAchievementsForGames(int topAmount, List<Long> gameIds) {
        Map<Long, List<Achievement>> achievements = new HashMap<>();

        /* Batch Processing, Chunk = 15 */
        int batchSize = 15;
        for (int i=0; i < gameIds.size(); i+=batchSize) {
            List<Long> batchIds = gameIds.subList(i, Math.min(i+batchSize, gameIds.size()));

            /* Actual Method */
            Map<Long, List<Achievement>> achievementsBatch = new HashMap<>();

            TypedQuery<Object[]> query = em.createQuery("SELECT a.game.storeId, a FROM Achievement a " +
                    "WHERE a.game.storeId IN :gameIds AND (a.position BETWEEN 1 AND :topAmount) " +
                    "ORDER BY a.game.storeId, a.percentage", Object[].class);
            query.setParameter("gameIds", batchIds).setParameter("topAmount", topAmount);
            List<Object[]> results = query.getResultList();

            for (Object[] result : results) {
                Long storeId = (Long) result[0];
                Achievement achievement = (Achievement) result[1];

                if (!achievementsBatch.containsKey(storeId))
                    achievementsBatch.put(storeId, new ArrayList<>());
                achievementsBatch.get(storeId).add(achievement);
            }
            achievements.putAll(achievementsBatch);
        }
        return achievements;
    }
}
