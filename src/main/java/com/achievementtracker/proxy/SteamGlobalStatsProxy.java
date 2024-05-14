package com.achievementtracker.proxy;

import com.achievementtracker.dto.steam_api.AchievementStatsDTO;
import com.achievementtracker.dto.steam_api.AppListDTO;
import com.achievementtracker.dto.steam_api.GameSchemaDTO;
import com.achievementtracker.dto.steam_api.StoreAppListDTO;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steam-api-global-data", url = "${steam.api.url}")
@Retryable(retryFor = {RetryableException.class, FeignException.BadGateway.class}, maxAttemptsExpression = "${retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${retry.backoff-delay}", multiplierExpression = "${retry.backoff-multiplier}"))
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
            @RequestParam("lastAppId") Long lastAppId,
            @RequestParam("max_results") int maxResults);

    @GetMapping("${game.schema.endpoint}" + "/?key=${steam.api.key}&appid={appId}")
    GameSchemaDTO fetchSchemaByAppId(@RequestParam("appid") Long appId);

    @GetMapping("${global.achievement.stats.endpoint}" + "/?gameid={gameId}")
    AchievementStatsDTO fetchAchievementStatsByGameId(@RequestParam("gameid") Long gameId); // gameid = appid
}
