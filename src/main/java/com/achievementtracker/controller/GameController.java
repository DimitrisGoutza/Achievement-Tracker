package com.achievementtracker.controller;

import com.achievementtracker.dao.OffsetPage;
import com.achievementtracker.dao.Page;
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
    private final OffsetPage page;

    @Autowired
    public GameController(GameFilterService gameFilterService) {
        this.gameFilterService = gameFilterService;

        // Most of the values set here don't matter as they will be overridden later ..
        this.page = new OffsetPage(100, gameFilterService.getGameEntryCount(),
                Game_.storeId, Page.SortDirection.ASC,
                Game_.storeId, Game_.title, Game_.releaseDate,
                Game_.challengeRating, Game_.difficultySpread, Game_.rating);
    }

    @GetMapping("/games")
    public String getGames(@Validated @ModelAttribute GameRequestParams params, Model model) {
        /* Pagination */
        page.setCurrent(params.getPageAsInt());
        page.setSize(params.getSizeAsInt());
        /* Sorting */
        page.setSortAttribute(params.getSortAttribute());
        page.setSortDirection(params.getSortDirection());

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
}