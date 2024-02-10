package com.achievementtracker.proxy;

import com.achievementtracker.dto.GameCategoriesDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SteamSpyProxyTest {

    @Autowired
    private SteamSpyProxy steamSpyProxy;

    @ParameterizedTest
    @MethodSource("getExpectedData")
    void verifyCategoriesForGame(String expectedData) {
        GameCategoriesDTO actualData = steamSpyProxy.fetchCategoriesByGameId(440L);

        System.out.println(actualData.getCategories());

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        List<String> actualCategories = actualData.getCategories().stream()
                // make List comparable
                .map(GameCategoriesDTO.CategoryDetailsDTO::getName).toList();

        // 2nd assert : check that the map the API returned contains these items
        assertTrue(actualCategories.contains(expectedData));
    }

    static Stream<String> getExpectedData() {
        return Stream.of(
                ("Free to Play"),
                ("Hero Shooter"),
                ("Multiplayer")
        );
    }
}