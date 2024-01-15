package com.achievementtracker.deserializer;

import com.achievementtracker.dto.GameDetailDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;

public class GameDetailDeserializer extends JsonDeserializer<GameDetailDTO> {
    @Override
    public GameDetailDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);

        String appId = parentNode.fieldNames().next();
        JsonNode appIdNode = parentNode.get(appId);
        JsonNode successNode = appIdNode.get("success");

        GameDetailDTO gameDetailDTO = new GameDetailDTO();

        boolean gameFound = successNode.booleanValue();
        if (gameFound) {
            JsonNode dataNode = appIdNode.get("data");

            gameDetailDTO.setName(dataNode.get("name").textValue());
            gameDetailDTO.setId(dataNode.get("steam_appid").textValue());

            // about_the_game node contains HTML content, need to sanitize first
            String htmlContent = dataNode.get("about_the_game").textValue();
            gameDetailDTO.setLongDescription(Jsoup.clean(htmlContent, Safelist.relaxed()));

            gameDetailDTO.setShortDescription(dataNode.get("short_description").textValue());
            gameDetailDTO.setHeaderImageUrl(dataNode.get("header_image").textValue());
            gameDetailDTO.setCapsuleImageUrl(dataNode.get("capsule_image").textValue());
            gameDetailDTO.setCapsuleSmallImageUrl(dataNode.get("capsule_imagev5").textValue());
            gameDetailDTO.setTotalAchievements(dataNode.has("achievements") ? dataNode.get("achievements").get("total").intValue() : 0);
            gameDetailDTO.setComingSoon(dataNode.get("release_date").get("coming_soon").booleanValue());
            gameDetailDTO.setReleaseDate(dataNode.get("release_date").get("date").textValue());
            gameDetailDTO.setBackgroundImageUrl(dataNode.get("background").textValue());
            gameDetailDTO.setBackgroundRawImageUrl(dataNode.get("background_raw").textValue());
        }

        return gameDetailDTO;
    }
}
