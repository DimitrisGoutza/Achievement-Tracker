package com.achievementtracker.controller;

import com.achievementtracker.dao.OffsetPage;
import com.achievementtracker.dao.Page;
import com.achievementtracker.dto.FilterData;
import com.achievementtracker.dto.SelectedFilterData;
import com.achievementtracker.entity.Achievement;
import com.achievementtracker.entity.Game;
import com.achievementtracker.entity.Game_;
import com.achievementtracker.service.GameFilterService;
import jakarta.persistence.metamodel.SingularAttribute;
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

    /* Default Values */
    private final int DEFAULT_PAGE_SIZE = 100;
    private final int DEFAULT_PAGE_NUMBER = 1;
    private final SingularAttribute DEFAULT_SORT_COLUMN = Game_.challengeRating;
    private final Page.SortDirection DEFAULT_SORT_DIRECTION = Page.SortDirection.DESC;
    private final Integer DEFAULT_MIN_REVIEWS = 300;
    private final Integer DEFAULT_MAX_REVIEWS = null;

    @Autowired
    public GameController(GameFilterService gameFilterService) {
        this.gameFilterService = gameFilterService;

        this.page = new OffsetPage(DEFAULT_PAGE_SIZE, gameFilterService.getGameEntryCount(),
                DEFAULT_SORT_COLUMN, DEFAULT_SORT_DIRECTION,
                Game_.storeId, Game_.steamAppId, Game_.title, Game_.releaseDate,
                Game_.challengeRating, Game_.averageCompletion, Game_.difficultySpread,
                Game_.rating);
    }

    @GetMapping("/games")    // TODO : Add validation for params
    public String getGames(@RequestParam(name = "page") Optional<Integer> pageOptional,
                           @RequestParam(name = "size") Optional<Integer> sizeOptional,
                           @RequestParam(name = "sort") Optional<String> sortOptional,
                           @RequestParam(name = "search") Optional<String> searchOptional,
                           @RequestParam(name = "categories") Optional<String> categoryOptional,
                           @RequestParam(name = "achievements") Optional<Integer> achievementsOptional,
                           @RequestParam(name = "min_reviews") Optional<Integer> minReviewsOptional,
                           @RequestParam(name = "max_reviews") Optional<Integer> maxReviewsOptional,
                           Model model) {
        String sortParamValue = sortOptional.orElse(" _" + DEFAULT_SORT_DIRECTION.name());
        String sortColumn = sortParamValue.split("_")[0].toLowerCase();
        String sortDirection = sortParamValue.split("_")[1].toLowerCase();
        // Selected Filters
        String categoriesParam = categoryOptional.orElse("");
        SelectedFilterData selectedFilterData = new SelectedFilterData(
                searchOptional.orElse(""),
                extractCategoryIds(categoriesParam),
                achievementsOptional.isPresent(),
                achievementsOptional.isPresent() && achievementsOptional.get() == 2,
                DEFAULT_MIN_REVIEWS,
                DEFAULT_MAX_REVIEWS,
                minReviewsOptional.orElse(DEFAULT_MIN_REVIEWS),
                maxReviewsOptional.orElse(DEFAULT_MAX_REVIEWS)
        );

        // Pagination
        page.setCurrent(pageOptional.orElse(DEFAULT_PAGE_NUMBER));
        page.setSize(sizeOptional.orElse(DEFAULT_PAGE_SIZE));
        page.setSortAttribute(
                switch (sortColumn) {
                    case "id" -> Game_.storeId;
                    case "name" -> Game_.title;
                    case "release" -> Game_.releaseDate;
                    case "challenge" -> Game_.challengeRating;
                    case "difficulty" -> Game_.difficultySpread;
                    case "rating" -> Game_.rating;
                    default -> DEFAULT_SORT_COLUMN;
                }
        );
        page.setSortDirection(sortDirection.equalsIgnoreCase(Page.SortDirection.ASC.name()) ?
                Page.SortDirection.ASC : Page.SortDirection.DESC);

        List<Game> games = gameFilterService.getFilteredGames(selectedFilterData, page);
        Map<Long, List<Achievement>> achievementsMap = gameFilterService.getTopXAchievementsForGames(3, games);
        FilterData filterData = gameFilterService.getFilterData();

        model.addAttribute("games", games);
        model.addAttribute("achievements", achievementsMap);
        model.addAttribute("filterData", filterData);
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