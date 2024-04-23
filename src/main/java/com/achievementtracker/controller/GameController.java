package com.achievementtracker.controller;

import com.achievementtracker.dao.OffsetPage;
import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.GameReqParamsDTO;
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
    public String getGames(@Validated @ModelAttribute GameReqParamsDTO paramsDTO, Model model) {
        OffsetPage page = setPaginationAndSorting(paramsDTO);

        List<Game> games = gameFilterService.getFilteredGames(paramsDTO, page);
        Map<Long, List<Achievement>> achievementsMap = gameFilterService.getTopXAchievementsForGames(3, games);
        UsefulFilterData usefulFilterData = gameFilterService.getUsefulFilterData();

        model.addAttribute("games", games);
        model.addAttribute("achievements", achievementsMap);
        model.addAttribute("usefulFilterData", usefulFilterData);
        model.addAttribute("selectedFilters", paramsDTO);
        model.addAttribute("page", page);

        return "gameTable";
    }

    private OffsetPage setPaginationAndSorting(GameReqParamsDTO paramsDTO) {
        OffsetPage page = new OffsetPage(paramsDTO.getSizeAsInt(), gameFilterService.getGameEntryCount(),
                Game_.storeId, Page.SortDirection.ASC,
                Game_.storeId, Game_.steamAppId, Game_.title, Game_.releaseDate,
                Game_.challengeRating, Game_.averageCompletion, Game_.difficultySpread,
                Game_.rating);

        // Pagination
        page.setCurrent(paramsDTO.getPageAsInt());
        page.setSize(paramsDTO.getSizeAsInt());

        // Sorting
        String sortColumn = paramsDTO.getSort().split("_")[0].toLowerCase();
        String sortDirection = paramsDTO.getSort().split("_")[1].toLowerCase();
        page.setSortAttribute(
                switch (sortColumn) {
                    case "id" -> Game_.storeId;
                    case "name" -> Game_.title;
                    case "release" -> Game_.releaseDate;
                    case "challenge" -> Game_.challengeRating;
                    case "difficulty" -> Game_.difficultySpread;
                    case "rating" -> Game_.rating;
                    default -> Game_.challengeRating;
                }
        );
        page.setSortDirection(sortDirection.equalsIgnoreCase(Page.SortDirection.ASC.name()) ?
                Page.SortDirection.ASC : Page.SortDirection.DESC);

        return page;
    }
}