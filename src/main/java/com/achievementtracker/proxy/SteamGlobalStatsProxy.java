package com.achievementtracker.proxy;

import com.achievementtracker.dto.AchievementResponse;
import com.achievementtracker.dto.AppListResponse;
import com.achievementtracker.dto.GameSchemaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steam-api-global-data", url = "${steam.api.url}")
public interface SteamGlobalStatsProxy {
    @GetMapping("${global.achievement.stats.endpoint}" + "/?gameid={gameId}&format=json")
    AchievementResponse fetchAchievementStatsByGameId(@RequestParam("gameid") int gameId);

    @GetMapping("${app.list.endpoint}" + "/")
    AppListResponse fetchAllApps();

    @GetMapping("${game.schema.endpoint}" + "/?key=${steam.api.key}&appid={appId}") // this endpoint requires an api key
    GameSchemaResponse fetchSchemaByAppId(@RequestParam("appid") int appId);   // appid is the same as gameid
}
