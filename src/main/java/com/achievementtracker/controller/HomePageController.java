package com.achievementtracker.controller;

import com.achievementtracker.dao.GameDAO;
import com.achievementtracker.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomePageController {
    private final GameDAO gameDAO;

    @Autowired
    public HomePageController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        List<Game> games = gameDAO.findAll();

        model.addAttribute("games", games);

        return "home";
    }
}