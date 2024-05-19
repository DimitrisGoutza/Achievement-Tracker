package com.achievementtracker.controller;

import com.achievementtracker.dao.OffsetPage;
import com.achievementtracker.dto.games_endpoint.GameDTO;
import com.achievementtracker.dto.games_endpoint.GameRequestParams;
import com.achievementtracker.dto.games_endpoint.UsefulFilterData;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Game_;
import com.achievementtracker.service.GameFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

@Controller
public class GameController {
    private final GameFilterService gameFilterService;

    @Autowired
    public GameController(GameFilterService gameFilterService) {
        this.gameFilterService = gameFilterService;
    }

    @GetMapping("/games")
    public String getGames(@Validated @ModelAttribute GameRequestParams params, Model model) {
        OffsetPage page = new OffsetPage(params.getSizeAsInt(), params.getEntriesAsInt(),
                params.getSortAttribute(), params.getSortDirection(),
                Game_.storeId, Game_.title, Game_.releaseDate,
                Game_.challengeRating, Game_.difficultySpread, Game_.rating);
        page.setCurrent(params.getPageAsInt());

        List<GameDTO> games = gameFilterService.getFilteredGames(params, page);
        Map<Long, List<Achievement>> achievementsMap = gameFilterService.getTopXAchievementsForGames(3, games);
        UsefulFilterData usefulFilterData = gameFilterService.getUsefulFilterData();

        model.addAttribute("games", games);
        model.addAttribute("achievements", achievementsMap);
        model.addAttribute("usefulFilterData", usefulFilterData);
        model.addAttribute("prevRequestParams", params);
        model.addAttribute("page", page);

        return "games";
    }
}