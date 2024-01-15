package com.achievementtracker.proxy;

import com.achievementtracker.dto.AchievementStatListDTO;
import com.achievementtracker.dto.AppListDTO;
import com.achievementtracker.dto.GameSchemaDTO;
import com.achievementtracker.dto.StoreAppListDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SteamGlobalStatsProxyTest {

    @Autowired
    private SteamGlobalStatsProxy steamGlobalStatsProxy;

    @ParameterizedTest
    @MethodSource("getExpectedAchievements")
    void verifyAchievementDataPerGame(String expectedData) {
        AchievementStatListDTO actualData = steamGlobalStatsProxy.fetchAchievementStatsByGameId(440);

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        List<String> actualAchievementData = actualData.getAchievementStats()
                // make List comparable
                .stream().map(AchievementStatListDTO.AchievementStatsDTO::getName).toList();

        // 2nd assert : check that the API returned the correct achievements
        assertTrue(actualAchievementData.contains(expectedData));
    }

    @ParameterizedTest
    @MethodSource("getExpectedAchievements")
    void verifySchemaForGame(String expectedData) {
        GameSchemaDTO actualData = steamGlobalStatsProxy.fetchSchemaByAppId(440);

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        List<String> actualName = actualData.getAchievements()
                // make List comparable
                .stream().map(GameSchemaDTO.AchievementDetailsDTO::getName).toList();

        // 2nd assert : check that the API returned the correct data
        assertTrue(actualName.contains(expectedData));
    }

    @ParameterizedTest
    @MethodSource("getExpectedApps")
    void verifyAppList(String expectedData) {
        AppListDTO actualData = steamGlobalStatsProxy.fetchAllApps();

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        List<String> actualApps = actualData.getApps()
                // make List comparable
                .stream().map(AppListDTO.AppDetailsDTO::getName).toList();

        // 2nd assert : check that the List the API returned has these items
        assertTrue(actualApps.contains(expectedData));
    }

    @ParameterizedTest
    @MethodSource("getExpectedStoreApps")
    void verifyStoreAppList(String expectedData) {
        StoreAppListDTO actualData = steamGlobalStatsProxy.fetchAllStoreApps(true, false, 0, 1000);

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        List<String> actualApps = actualData.getStoreApps()
                // make List comparable by keeping only String values
                .stream().map(StoreAppListDTO.StoreAppDTO::getName).toList();

        // 2nd assert : check that the List the API returned has these items
        assertTrue(actualApps.contains(expectedData));
    }

    static Stream<String> getExpectedApps() {
        return Stream.of(
                ("DINNERDINNNER"),
                ("Super Space Planet Fighter Demo"),
                ("Runaway Farm Demo")
        );
    }

    static Stream<String> getExpectedStoreApps() {
        return Stream.of(
                ("Day of Defeat"),
                ("Counter-Strike: Source"),
                ("Left 4 Dead")
        );
    }

    static Stream<String> getExpectedAchievements() {
        return Stream.of(
                ("TF_SCOUT_LONG_DISTANCE_RUNNER"),
                ("TF_HEAVY_DAMAGE_TAKEN"),
                ("TF_GET_CONSECUTIVEKILLS_NODEATHS")
        );
    }
}