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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class GameDetailDeserializer extends JsonDeserializer<GameDetailDTO> {
    private final DateTimeFormatter DATE_FORMAT_COMMON = DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.ENGLISH);
    private final DateTimeFormatter DATE_FORMAT_RARE = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    /* 1) Response with a valid app that has achievements:
    https://store.steampowered.com/api/appdetails?appids=440&l=en

    *  2) Response with a valid app that has an empty ("") release date ➡ (releaseDate = null):
    https://store.steampowered.com/api/appdetails?appids=11180&l=en

    *  3) Response with a valid app that hasn't released yet but has "Coming soon",
        instead of a set release date ➡ (comingSoon = true, releaseDate = null):
    https://store.steampowered.com/api/appdetails?appids=1032980&l=en

    *  4) Response with an invalid app ➡ (GameDetailDTO = null):
    https://store.steampowered.com/api/appdetails?appids=13333&l=en */

    @Override
    public GameDetailDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);

        String appId = parentNode.fieldNames().next();
        JsonNode appIdNode = parentNode.get(appId);
        JsonNode successNode = appIdNode.get("success");

        GameDetailDTO gameDetailDTO = new GameDetailDTO();

        // Case #4
        boolean gameFound = successNode.booleanValue();
        if (gameFound) {
            JsonNode dataNode = appIdNode.get("data");

            gameDetailDTO.setTitle(dataNode.get("name").textValue());
            gameDetailDTO.setSteamAppId(dataNode.get("steam_appid").longValue());

            // about_the_game node contains HTML content, need to sanitize first
            String htmlContent = dataNode.get("about_the_game").textValue();
            gameDetailDTO.setLongDescription(Jsoup.clean(htmlContent, Safelist.relaxed()));

            gameDetailDTO.setShortDescription(dataNode.get("short_description").textValue());
            gameDetailDTO.setHeaderImageUrl(dataNode.get("header_image").textValue());
            gameDetailDTO.setCapsuleImageUrl(dataNode.get("capsule_image").textValue());
            gameDetailDTO.setCapsuleSmallImageUrl(dataNode.get("capsule_imagev5").textValue());

            boolean comingSoon = dataNode.get("release_date").get("coming_soon").booleanValue();
            gameDetailDTO.setComingSoon(comingSoon);

            // Case #2
            boolean dateIsPresent = !dataNode.get("release_date").get("date").textValue().isEmpty();
            if (dateIsPresent) {
                String releaseDate = dataNode.get("release_date").get("date").textValue();
                gameDetailDTO.setReleaseDate(
                        // Case #3
                        releaseDate.equals("Coming soon") ?
                            null : parseDate(releaseDate)
                );
            }

            gameDetailDTO.setBackgroundImageUrl(dataNode.get("background").textValue());
            gameDetailDTO.setBackgroundRawImageUrl(dataNode.get("background_raw").textValue());
        }

        return gameDetailDTO;
    }

    private LocalDate parseDate(String releaseDate) {
        try {
            return LocalDate.parse(releaseDate, DATE_FORMAT_COMMON);
        } catch (DateTimeParseException e1) {
            try {
                // parse() requires the full date, including days
                releaseDate = "1 " + releaseDate;
                return LocalDate.parse(releaseDate, DATE_FORMAT_RARE);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}
