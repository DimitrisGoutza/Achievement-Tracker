package com.achievementtracker.proxy;

import com.achievementtracker.dto.GameTagResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steam-spy-api", url = "${steam.spy.api.url}")
public interface SteamSpyProxy {

    @GetMapping("/?request=appdetails&appid={appId}")
    GameTagResponse fetchTagsByGameId(@RequestParam("appid") int appId);
}
