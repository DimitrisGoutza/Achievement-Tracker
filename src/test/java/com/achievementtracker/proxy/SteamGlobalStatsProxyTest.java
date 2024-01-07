package com.achievementtracker.proxy;

import com.achievementtracker.dto.AchievementResponse;
import com.achievementtracker.dto.AppListResponse;
import com.achievementtracker.dto.GameSchemaResponse;
import com.achievementtracker.dto.StoreAppListResponse;
import com.achievementtracker.model.test.AchievementsPerGame;
import com.achievementtracker.model.test.SchemaForGame;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SteamGlobalStatsProxyTest {

    @Autowired
    private SteamGlobalStatsProxy steamGlobalStatsProxy;

    @ParameterizedTest
    @MethodSource("getExpectedAchievementsPerGame")
    void verifyAchievementDataPerGame(AchievementsPerGame expectedData) {
        int gameId = expectedData.getGameId();
        AchievementResponse actualData = steamGlobalStatsProxy.fetchAchievementStatsByGameId(gameId);

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        List<String> actualAchievementData = actualData.getAchievementPercentages().getAchievementDetails()
                // make actual List comparable to expected List
                .stream().map(AchievementResponse.AchievementPercentages.AchievementDetails::getName).limit(3).toList();

        List<String> expectedAchievementData = expectedData.getAchievements();

        // 2nd assert : check that the API returned the correct achievements
        assertIterableEquals(actualAchievementData,expectedAchievementData);
    }

    @ParameterizedTest
    @MethodSource("getExpectedSchemaForGame")
    void verifySchemaForGame(SchemaForGame expectedData) {
        int gameId = expectedData.getGameId();
        GameSchemaResponse actualData = steamGlobalStatsProxy.fetchSchemaByAppId(gameId);

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        String actualName = actualData.getGameDetails().getName();

        String expectedName = expectedData.getName();

        // 2nd assert : check that the API returned the correct schema
        assertEquals(actualName, expectedName);
    }

    @ParameterizedTest
    @MethodSource("getExpectedApps")
    void verifyAppList(String expectedData) {
        AppListResponse actualData = steamGlobalStatsProxy.fetchAllApps();

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        List<String> actualApps = actualData.getAppList().getAppDetails()
                // make actual List comparable
                .stream().map(AppListResponse.AppList.AppDetails::getName).toList();

        // 2nd assert : check that the List the API returned has these items
        assertTrue(actualApps.contains(expectedData));
    }

    @ParameterizedTest
    @MethodSource("getExpectedApps")
    void verifyStoreAppList(String expectedData) {
        StoreAppListResponse actualData = steamGlobalStatsProxy.fetchAllStoreApps(true, false, 0, 100);

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        List<String> actualApps = actualData.getResponse().getApps()
                // make actual List comparable
                .stream().map(StoreAppListResponse.Response.AppDetails::getName).toList();

        // 2nd assert : check that the List the API returned has these items
        assertTrue(actualApps.contains(expectedData));
    }

    static Stream<String> getExpectedApps() {
        return Stream.of(
                ("Day of Defeat"),
                ("Counter-Strike: Source"),
                ("Left 4 Dead")
        );
    }

    static Stream<SchemaForGame> getExpectedSchemaForGame() {
        return Stream.of(
                new SchemaForGame(220, "Half-Life 2"),
                new SchemaForGame(400, "Portal"),
                new SchemaForGame(4000, "Garry's Mod")
        );
    }

    static Stream<AchievementsPerGame> getExpectedAchievementsPerGame() {
        return Stream.of(
                // Team Fortress 2 🔫
                new AchievementsPerGame(440,
                        List.of("TF_SCOUT_LONG_DISTANCE_RUNNER", "TF_HEAVY_DAMAGE_TAKEN", "TF_GET_CONSECUTIVEKILLS_NODEATHS")),
                // Left 4 Dead 2 🧟‍♀️
                new AchievementsPerGame(550,
                        List.of("ACH_KILL_SPITTER_FAST", "ACH_SAVE_PLAYER_FROM_JOCKEY_FAST", "GLOBAL_GNOME_ALONE")),
                // Portal 2 🧠
                new AchievementsPerGame(620,
                        List.of("ACH.SURVIVE_CONTAINER_RIDE", "ACH.WAKE_UP", "ACH.LASER"))
        );
    }
}