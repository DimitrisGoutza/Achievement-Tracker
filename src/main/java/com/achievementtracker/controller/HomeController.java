package com.achievementtracker.controller;

import com.achievementtracker.dto.home_endpoint.LeaderboardGameDTO1;
import com.achievementtracker.dto.home_endpoint.LeaderboardGameDTO2;
import com.achievementtracker.dto.search_endpoint.MinimalGameDTO;
import com.achievementtracker.service.GameProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private final GameProcessingService gameProcessingService;

    @Autowired
    public HomeController(GameProcessingService gameProcessingService) {
        this.gameProcessingService = gameProcessingService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        final int TOP_AMOUNT = 10;

        List<LeaderboardGameDTO1> topChallengingGames = gameProcessingService.getTopChallengingGames(TOP_AMOUNT);
        List<LeaderboardGameDTO2> topGamesByCount = gameProcessingService.getTopGamesByAchievementCount(TOP_AMOUNT);

        model.addAttribute("challengingGames", topChallengingGames);
        model.addAttribute("gamesWithMostAchievements", topGamesByCount);

        return "/pages/home";
    }

    @GetMapping("/search")
    public String getSearchResults(@RequestParam(name = "term") String searchTerm,
                                   @RequestParam(name = "size", required = false, defaultValue = "20") String size,
                                   Model model) {
        int sizeAsInt = Integer.parseInt(size);
        List<MinimalGameDTO> games = gameProcessingService.searchAllGames(searchTerm, sizeAsInt);

        model.addAttribute("games", games);
        model.addAttribute("searchTerm", searchTerm);

        return "search-result-list";
    }
}
