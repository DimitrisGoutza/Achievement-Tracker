package com.achievementtracker.dao;

import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Game;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

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
}
