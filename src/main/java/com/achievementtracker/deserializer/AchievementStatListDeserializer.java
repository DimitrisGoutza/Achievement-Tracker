package com.achievementtracker.deserializer;

import com.achievementtracker.dto.steam_api.AchievementStatsDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AchievementStatListDeserializer extends JsonDeserializer<AchievementStatsDTO> {

    /*  1) Response with a valid appid that has achievements:
    https://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/?gameid=440

    *  2) Response with a valid appid that doesn't have achievements or hasn't released yet ➡ (throws FeignException.Forbidden 403):
    https://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/?gameid=10
    https://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/?gameid=230210 */

    @Override
    public AchievementStatsDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);

        AchievementStatsDTO achievementStatsDTO = new AchievementStatsDTO();
        Map<String, Double> achievements = new LinkedHashMap<>();

        JsonNode achievementsNode = parentNode.get("achievementpercentages").get("achievements");
        for (JsonNode achievement : achievementsNode) {
            String name = achievement.get("name").textValue();
            Double percentage = achievement.get("percent").doubleValue();

            achievements.put(name, percentage);
        }

        achievementStatsDTO.setAchievements(achievements);
        return achievementStatsDTO;
    }
}
