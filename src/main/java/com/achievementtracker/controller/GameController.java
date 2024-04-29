package com.achievementtracker.controller;

import com.achievementtracker.dao.OffsetPage;
import com.achievementtracker.dto.GameRequestParams;
import com.achievementtracker.dto.UsefulFilterData;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Game;
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
        OffsetPage page = setPaginationAndSorting(params);

        List<Game> games = gameFilterService.getFilteredGames(params, page);
        Map<Long, List<Achievement>> achievementsMap = gameFilterService.getTopXAchievementsForGames(3, games);
        UsefulFilterData usefulFilterData = gameFilterService.getUsefulFilterData();

        model.addAttribute("games", games);
        model.addAttribute("achievements", achievementsMap);
        model.addAttribute("usefulFilterData", usefulFilterData);
        model.addAttribute("selectedFilters", params);
        model.addAttribute("page", page);

        return "gameTable";
    }

    private OffsetPage setPaginationAndSorting(GameRequestParams params) {
        OffsetPage page = new OffsetPage(params.getSizeAsInt(), gameFilterService.getGameEntryCount(),
                params.getSortAttribute(), params.getSortDirection(),
                Game_.storeId, Game_.title, Game_.releaseDate,
                Game_.challengeRating, Game_.difficultySpread, Game_.rating);
        page.setCurrent(params.getPageAsInt());

        return page;
    }
}