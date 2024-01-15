package com.achievementtracker.proxy;

import com.achievementtracker.dto.GameTagsDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SteamSpyProxyTest {

    @Autowired
    private SteamSpyProxy steamSpyProxy;

    @ParameterizedTest
    @MethodSource("getExpectedData")
    void verifyTagsForGame(String expectedData) {
        GameTagsDTO actualData = steamSpyProxy.fetchTagsByGameId(440);

        System.out.println(actualData.getTags());

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        // 2nd assert : check that the map the API returned contains these items
        assertTrue(actualData.getTags().containsKey(expectedData));
    }

    static Stream<String> getExpectedData() {
        return Stream.of(
                ("Free to Play"),
                ("Hero Shooter"),
                ("Multiplayer")
        );
    }
}