package com.achievementtracker.proxy;

import com.achievementtracker.dto.AchievementStatListDTO;
import com.achievementtracker.dto.AppListDTO;
import com.achievementtracker.dto.GameSchemaDTO;
import com.achievementtracker.dto.StoreAppListDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steam-api-global-data", url = "${steam.api.url}")
public interface SteamGlobalStatsProxy {
    @GetMapping("${app.list.endpoint}" + "/")
    AppListDTO fetchAllApps();

    @GetMapping("${store.app.list.endpoint}" + "?key=${steam.api.key}" +
            "&include_games={games}&include_dlc={dlc}" +
            "&last_appid={lastAppId}")
    StoreAppListDTO fetchAllStoreApps(
            @RequestParam("games") boolean games,
            @RequestParam("dlc") boolean dlc,
            @RequestParam("lastAppId") int lastAppId);

    @GetMapping("${store.app.list.endpoint}" + "?key=${steam.api.key}" +
            "&include_games={games}&include_dlc={dlc}" +
            "&last_appid={lastAppId}&max_results={maxResults}")
    StoreAppListDTO fetchAllStoreApps(
            @RequestParam("games") boolean games,
            @RequestParam("dlc") boolean dlc,
            @RequestParam("lastAppId") int lastAppId,
            @RequestParam("max_results") int maxResults);

    @GetMapping("${game.schema.endpoint}" + "/?key=${steam.api.key}&appid={appId}")
    GameSchemaDTO fetchSchemaByAppId(@RequestParam("appid") int appId);

    @GetMapping("${global.achievement.stats.endpoint}" + "/?gameid={gameId}")
    AchievementStatListDTO fetchAchievementStatsByGameId(@RequestParam("gameid") int gameId); // gameid = appid
}
