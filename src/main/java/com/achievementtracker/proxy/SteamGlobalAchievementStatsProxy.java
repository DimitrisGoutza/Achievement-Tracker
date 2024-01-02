package com.achievementtracker.proxy;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "global-achievement-stats", url = "${steam.api.global.achievement.stats.url}")
public interface SteamGlobalAchievementStatsProxy {
    /* TODO */
}
