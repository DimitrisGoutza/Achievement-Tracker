package com.achievementtracker.proxy;

import com.achievementtracker.dto.AchievementResponse;
import com.achievementtracker.dto.AppListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steam-api-global-stats", url = "${steam.api.url}")
public interface SteamGlobalStatsProxy {
    @GetMapping("${global.achievement.stats.endpoint}" + "/?gameid={gameId}&format=json")
    AchievementResponse fetchAchievementStatsByGameId(@RequestParam("gameid") int gameId);

    @GetMapping("${app.list.endpoint}" + "/")
    AppListResponse fetchAllApps();
}
