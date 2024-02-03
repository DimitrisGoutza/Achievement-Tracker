package com.achievementtracker.proxy;

import com.achievementtracker.dto.GameDetailDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SteamStorefrontProxyTest {

    @Autowired
    private SteamStorefrontProxy steamStorefrontProxy;

    @Test
    void verifyDetailsForGame() {
        GameDetailDTO actualData = steamStorefrontProxy.fetchDetailsByGameId(440L);

        // 1st assert : check that we got a response
        assertNotNull(actualData);

        // 2nd assert : check that we got the correct Data
        assertEquals(actualData.getShortDescription(),
                "Nine distinct classes provide a broad range of tactical abilities and personalities. Constantly updated with new game modes, maps, equipment and, most importantly, hats!");
    }
}