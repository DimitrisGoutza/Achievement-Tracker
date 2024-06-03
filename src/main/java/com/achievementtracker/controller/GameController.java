package com.achievementtracker.controller;

import com.achievementtracker.dao.OffsetPage;
import com.achievementtracker.dto.games_endpoint.GameDTO;
import com.achievementtracker.dto.games_endpoint.GameRequestParams;
import com.achievementtracker.dto.games_endpoint.UsefulFilterData;
import com.achievementtracker.entity.*;
import com.achievementtracker.service.AchievementAnalyticsService;
import com.achievementtracker.service.GameProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
public class GameController {
    private final GameProcessingService gameProcessingService;
    private final AchievementAnalyticsService achievementAnalyticsService;

    @Autowired
    public GameController(GameProcessingService gameProcessingService, AchievementAnalyticsService achievementAnalyticsService) {
        this.gameProcessingService = gameProcessingService;
        this.achievementAnalyticsService = achievementAnalyticsService;
    }

    @GetMapping("/games")
    public String getGames(@Validated @ModelAttribute GameRequestParams params, Model model) {
        OffsetPage page = new OffsetPage(params.getSizeAsInt(), params.getEntriesAsInt(),
                params.getSortAttribute(), params.getSortDirection(),
                Game_.storeId, Game_.title, Game_.releaseDate,
                Game_.challengeRating, Game_.difficultySpread, Game_.rating);
        page.setCurrent(params.getPageAsInt());

        List<GameDTO> games = gameProcessingService.getFilteredGames(params, page);
        Map<Long, List<Achievement>> achievementsMap = gameProcessingService.getTopXAchievementsForGames(3, games);
        UsefulFilterData usefulFilterData = gameProcessingService.getUsefulFilterData();

        model.addAttribute("games", games);
        model.addAttribute("achievements", achievementsMap);
        model.addAttribute("usefulFilterData", usefulFilterData);
        model.addAttribute("prevRequestParams", params);
        model.addAttribute("page", page);

        return "games";
    }

    @GetMapping("/games/{gameId}")
    public String getGameDetails(@PathVariable("gameId") Long gameId, Model model) {

        Game game = gameProcessingService.findGameByIdWithAchievements(gameId);
        Map<AchievementTier, Integer> tierCountMap = achievementAnalyticsService.getAchievementCountPerTier(game.getAchievementsAsList());
        List<Category> categories = gameProcessingService.getCategoriesForGame(game.getStoreId());

        model.addAttribute("game", game);
        model.addAttribute("tiers", AchievementTier.values());
        model.addAttribute("tierCountMap", tierCountMap);
        model.addAttribute("categories", categories);

        return "gameDetails";
    }
}