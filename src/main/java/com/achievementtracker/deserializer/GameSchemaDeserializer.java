package com.achievementtracker.deserializer;

import com.achievementtracker.dto.GameSchemaDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GameSchemaDeserializer extends JsonDeserializer<GameSchemaDTO> {
    @Override
    public GameSchemaDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode gameNode = parentNode.get("game");

        GameSchemaDTO gameSchemaDTO = new GameSchemaDTO();
        List<GameSchemaDTO.AchievementDetailsDTO> achievements = new LinkedList<>();

        if (gameNode.has("gameName") && gameNode.has("availableGameStats")) {
            gameSchemaDTO.setName(gameNode.get("gameName").textValue());
            JsonNode achievementsNode = gameNode.get("availableGameStats").get("achievements");

            for (JsonNode achievement : achievementsNode) {
                GameSchemaDTO.AchievementDetailsDTO achievementDetailsDTO = new GameSchemaDTO.AchievementDetailsDTO();

                achievementDetailsDTO.setName(achievement.get("name").textValue());
                achievementDetailsDTO.setDisplayName(achievement.get("displayName").textValue());
                achievementDetailsDTO.setHidden(achievement.get("hidden").booleanValue());
                achievementDetailsDTO.setDescription(achievement.get("description").textValue());
                achievementDetailsDTO.setIconUrl(achievement.get("icon").textValue());
                achievementDetailsDTO.setIconGrayUrl(achievement.get("icongray").textValue());

                achievements.add(achievementDetailsDTO);
            }
        }
        gameSchemaDTO.setAchievements(achievements);
        return gameSchemaDTO;
    }
}
