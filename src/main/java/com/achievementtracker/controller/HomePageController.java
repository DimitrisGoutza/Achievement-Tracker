package com.achievementtracker.controller;

import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.dao.OffsetPage;
import com.achievementtracker.dao.Page;
import com.achievementtracker.entity.Game;
import com.achievementtracker.entity.Game_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomePageController {
    private final GameDAO gameDAO;
    private final OffsetPage page;

    @Autowired
    public HomePageController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;

        this.page = new OffsetPage(50, gameDAO.getCount(),
                Game_.challengeRating, Page.SortDirection.DESC,
                Game_.storeId, Game_.steamAppId, Game_.title, Game_.releaseDate,
                Game_.challengeRating, Game_.averageCompletion, Game_.difficultySpread);
    }

    @GetMapping("/home")
    public String getHomePage(@RequestParam(name = "page") Optional<Integer> pageOptional,
                              @RequestParam(name = "size") Optional<Integer> sizeOptional,
                              @RequestParam(name = "sort") Optional<String> sortOptional,
                              Model model) {
        String sortParamValue = sortOptional.orElse("challenge-rating_desc");
        String sortColumn = sortParamValue.split("_")[0].toLowerCase();
        String sortDirection = sortParamValue.split("_")[1].toLowerCase();

        page.setCurrent(pageOptional.orElse(1));
        page.setSize(sizeOptional.orElse(50));
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

        List<Game> games = gameDAO.findAll(page);

        model.addAttribute("games", games);
        model.addAttribute("page", page);

        return "home";
    }
}