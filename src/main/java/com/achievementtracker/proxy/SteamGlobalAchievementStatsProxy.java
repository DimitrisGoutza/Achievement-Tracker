package com.achievementtracker.proxy;

import com.achievementtracker.dto.AchievementResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "global-achievement-stats", url = "${steam.api.global.achievement.stats.url}")
public interface SteamGlobalAchievementStatsProxy {

    @GetMapping("/?gameid={gameId}&format=json")
    AchievementResponse fetchAchievementDataByGameId(@RequestParam("gameid") int gameId);
}
