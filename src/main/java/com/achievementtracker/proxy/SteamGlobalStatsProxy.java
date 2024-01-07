package com.achievementtracker.proxy;

import com.achievementtracker.dto.AchievementResponse;
import com.achievementtracker.dto.AppListResponse;
import com.achievementtracker.dto.GameSchemaResponse;
import com.achievementtracker.dto.StoreAppListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steam-api-global-data", url = "${steam.api.url}")
public interface SteamGlobalStatsProxy {
    /* ---------------- All Apps ---------------- */
    @GetMapping("${app.list.endpoint}" + "/")
    AppListResponse fetchAllApps();

    /* ---------------- All Store Apps ---------------- */
    @GetMapping("${store.app.list.endpoint}" + "?key=${steam.api.key}" +
            "&include_games={games}&include_dlc={dlc}" +
            "&last_appid={lastAppId}")
    StoreAppListResponse fetchAllStoreApps(
            @RequestParam("games") boolean games,
            @RequestParam("dlc") boolean dlc,
            @RequestParam("lastAppId") int lastAppId);

    @GetMapping("${store.app.list.endpoint}" + "?key=${steam.api.key}" +
            "&include_games={games}&include_dlc={dlc}" +
            "&last_appid={lastAppId}&max_results={maxResults}")
    StoreAppListResponse fetchAllStoreApps(
            @RequestParam("games") boolean games,
            @RequestParam("dlc") boolean dlc,
            @RequestParam("lastAppId") int lastAppId,
            @RequestParam("max_results") int maxResults);

    /* ---------------- Game Schema ---------------- */
    @GetMapping("${game.schema.endpoint}" + "/?key=${steam.api.key}&appid={appId}")
    GameSchemaResponse fetchSchemaByAppId(@RequestParam("appid") int appId);

    /* ---------------- Game Achievement Stats ---------------- */
    @GetMapping("${global.achievement.stats.endpoint}" + "/?gameid={gameId}")
    AchievementResponse fetchAchievementStatsByGameId(@RequestParam("gameid") int gameId); // gameid = appid

}
