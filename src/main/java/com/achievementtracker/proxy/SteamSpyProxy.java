package com.achievementtracker.proxy;

import com.achievementtracker.dto.GameCategoriesAndReviewsDTO;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "steam-spy-api", url = "${steam.spy.api.url}")
@Retryable(retryFor = {RetryableException.class, FeignException.BadGateway.class}, maxAttemptsExpression = "${retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${retry.backoff-delay}", multiplierExpression = "${retry.backoff-multiplier}"))
public interface SteamSpyProxy {

    @GetMapping("/?request=appdetails&appid={appId}")
    GameCategoriesAndReviewsDTO fetchCategoriesAndReviewsByGameId(@RequestParam("appid") Long appId);
}
