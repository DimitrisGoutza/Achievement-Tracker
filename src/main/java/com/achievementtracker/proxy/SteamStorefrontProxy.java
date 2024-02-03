package com.achievementtracker.proxy;

import com.achievementtracker.dto.GameDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steam-store-api", url = "${steam.store.api.url}")
public interface SteamStorefrontProxy {

    @GetMapping("${game.details.endpoint}" + "/?l=en&appids={appId}")
    GameDetailDTO fetchDetailsByGameId(@RequestParam("appids") Long appId);

}
