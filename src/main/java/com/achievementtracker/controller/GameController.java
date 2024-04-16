package com.achievementtracker.controller;

import com.achievementtracker.dao.OffsetPage;
import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.SelectedFilterData;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Category;
import com.achievementtracker.entity.Game;
import com.achievementtracker.entity.Game_;
import com.achievementtracker.service.GameFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class GameController {
    private final GameFilterService gameFilterService;
    private final OffsetPage page;

    @Autowired
    public GameController(GameFilterService gameFilterService) {
        this.gameFilterService = gameFilterService;

        this.page = new OffsetPage(50, gameFilterService.getGameEntryCount(),
                Game_.challengeRating, Page.SortDirection.DESC,
                Game_.storeId, Game_.steamAppId, Game_.title, Game_.releaseDate,
                Game_.challengeRating, Game_.averageCompletion, Game_.difficultySpread);
    }

    @GetMapping("/games")    // TODO : Add validation for params
    public String getGames(@RequestParam(name = "page") Optional<Integer> pageOptional,
                           @RequestParam(name = "size") Optional<Integer> sizeOptional,
                           @RequestParam(name = "sort") Optional<String> sortOptional,
                           @RequestParam(name = "search") Optional<String> searchOptional,
                           @RequestParam(name = "categoryid") Optional<String> categoryOptional,
                           @RequestParam(name = "achievements") Optional<String> achievementsOptional,
                           @RequestParam(name = "hidden") Optional<String> hiddenOptional,
                           Model model) {
        String sortParamValue = sortOptional.orElseGet(() -> "challenge-rating_desc");
        String sortColumn = sortParamValue.split("_")[0].toLowerCase();
        String sortDirection = sortParamValue.split("_")[1].toLowerCase();
        // Filters
        String categoriesParam = categoryOptional.orElseGet(() -> "");
        SelectedFilterData selectedFilterData = new SelectedFilterData(
                searchOptional.orElseGet(() -> ""),
                extractCategoryIds(categoriesParam),
                achievementsOptional.isPresent(),
                hiddenOptional.isPresent()
        );

        // Pagination
        page.setCurrent(pageOptional.orElseGet(() -> 1));
        page.setSize(sizeOptional.orElseGet(() -> 50));
        page.setSortAttribute(
                switch (sortColumn) {
                    case "id" -> Game_.storeId;
                    case "name" -> Game_.title;
                    case "release" -> Game_.releaseDate;
                    case "challenge-rating" -> Game_.challengeRating;
                    case "difficulty-spread" -> Game_.difficultySpread;
                    default -> Game_.challengeRating;
                }
        );
        page.setSortDirection(sortDirection.equalsIgnoreCase(Page.SortDirection.ASC.name()) ?
                Page.SortDirection.ASC : Page.SortDirection.DESC);

        List<Game> games = gameFilterService.getFilteredGames(selectedFilterData, page);
        Map<Long, List<Achievement>> achievementsMap = gameFilterService.getTopXAchievementsForGames(3, games);
        List<Category> categories = gameFilterService.getAvailableCategories(selectedFilterData, games);

        model.addAttribute("games", games);
        model.addAttribute("achievements", achievementsMap);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedFilters", selectedFilterData);
        model.addAttribute("page", page);

        return "gameTable";
    }

    private List<Long> extractCategoryIds(String categoryIds) {
        if (categoryIds.isEmpty())
            return List.of();
        if (categoryIds.contains(","))
            return Arrays.stream(categoryIds.split(","))
                    .map(Long::valueOf).toList();
        else
            return List.of(Long.valueOf(categoryIds));
    }
}