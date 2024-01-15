package com.achievementtracker.deserializer;

import com.achievementtracker.dto.AchievementStatListDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class AchievementStatListDeserializer extends JsonDeserializer<AchievementStatListDTO> {
    @Override
    public AchievementStatListDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);

        AchievementStatListDTO achievementStatListDTO = new AchievementStatListDTO();
        List<AchievementStatListDTO.AchievementStatsDTO> achievements = new LinkedList<>();

        JsonNode achievementsNode = parentNode.get("achievementpercentages").get("achievements");
        for (JsonNode achievement : achievementsNode) {
            AchievementStatListDTO.AchievementStatsDTO achievementStatsDTO = new AchievementStatListDTO.AchievementStatsDTO();

            achievementStatsDTO.setName(achievement.get("name").textValue());
            achievementStatsDTO.setPercentage(achievement.get("percent").doubleValue());

            achievements.add(achievementStatsDTO);
        }

        achievementStatListDTO.setAchievementStats(achievements);
        return achievementStatListDTO;
    }
}
