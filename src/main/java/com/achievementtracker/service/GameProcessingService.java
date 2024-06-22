package com.achievementtracker.service;

import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.games_endpoint.GameDTO;
import com.achievementtracker.dto.games_endpoint.GameRequestParams;
import com.achievementtracker.dto.games_endpoint.UsefulFilterData;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Category;
import com.achievementtracker.entity.Game;

import java.util.List;
import java.util.Map;

public interface GameProcessingService {
    List<GameDTO> getFilteredGames(GameRequestParams params, Page page);
    Map<Long, List<Achievement>> getTopXAchievementsForGames(int topAmount, List<GameDTO> games);
    Game findGameByIdWithAchievements(Long gameId);
    UsefulFilterData getUsefulFilterData();
    List<Category> getCategoriesForGame(Long gameId);
    int getChallengeRatingPercentileRounded(Long gameId);
}