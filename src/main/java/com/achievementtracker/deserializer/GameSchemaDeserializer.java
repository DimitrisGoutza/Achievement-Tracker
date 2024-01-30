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

    /* 1) Response with a valid app that has achievements:
    https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key=36778BC8EA3A4E0D03F55092558DF5F5&appid=440

    *  2) Response with a valid app that has achievements but not their descriptions ➡ (description = null):
    https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key=36778BC8EA3A4E0D03F55092558DF5F5&appid=8870

    *  3) Response with a valid app that doesn't have achievements ➡ (List of achievements = null):
    https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key=36778BC8EA3A4E0D03F55092558DF5F5&appid=10

    *  4) Response with an invalid app ➡ (will throw FeignException.BadRequest 400):
    https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key=36778BC8EA3A4E0D03F55092558DF5F5&appid=13333 */

    @Override
    public GameSchemaDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode gameNode = parentNode.get("game");

        GameSchemaDTO gameSchemaDTO = new GameSchemaDTO();
        List<GameSchemaDTO.AchievementDetailsDTO> achievements = new LinkedList<>();

        // Case #3
        if (gameNode.has("gameName") && gameNode.has("availableGameStats")) {
            gameSchemaDTO.setName(gameNode.get("gameName").textValue());
            if (gameNode.get("availableGameStats").has("achievements")) {
                JsonNode achievementsNode = gameNode.get("availableGameStats").get("achievements");

                for (JsonNode achievement : achievementsNode) {
                    GameSchemaDTO.AchievementDetailsDTO achievementDetailsDTO = new GameSchemaDTO.AchievementDetailsDTO();

                    achievementDetailsDTO.setName(achievement.get("name").textValue());
                    achievementDetailsDTO.setDisplayName(achievement.get("displayName").textValue());
                    achievementDetailsDTO.setHidden(achievement.get("hidden").booleanValue());

                    achievementDetailsDTO.setDescription(
                            // Case #2
                            achievement.has("description") ?
                                    achievement.get("description").textValue() : null
                    );

                    achievementDetailsDTO.setIconUrl(achievement.get("icon").textValue());
                    achievementDetailsDTO.setIconGrayUrl(achievement.get("icongray").textValue());

                    achievements.add(achievementDetailsDTO);
                }
            }
        }

        gameSchemaDTO.setAchievements(achievements);
        return gameSchemaDTO;
    }
}
