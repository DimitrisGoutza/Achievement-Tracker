package com.achievementtracker.deserializer;

import com.achievementtracker.dto.steam_api.GameCategoriesAndReviewsDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameCategoriesDeserializer extends JsonDeserializer<GameCategoriesAndReviewsDTO> {

    /* 1) Response with a valid app:
    https://steamspy.com/api.php?request=appdetails&appid=440

    *  2) Response with a valid app that doesn't have tags ➡ (List<categories> = null):
    https://steamspy.com/api.php?request=appdetails&appid=440

    *  3) Response with an invalid app ➡ (List<categories> = null):
    https://steamspy.com/api.php?request=appdetails&appid=13333 */

    @Override
    public GameCategoriesAndReviewsDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);

        GameCategoriesAndReviewsDTO gameCategoriesAndReviewsDTO = new GameCategoriesAndReviewsDTO();
        gameCategoriesAndReviewsDTO.setPositiveReviews(parentNode.get("positive").intValue());
        gameCategoriesAndReviewsDTO.setNegativeReviews(parentNode.get("negative").intValue());

        List<GameCategoriesAndReviewsDTO.CategoryDetailsDTO> categories = new LinkedList<>();

        Iterator<Map.Entry<String, JsonNode>> fields = parentNode.get("tags").fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();

            GameCategoriesAndReviewsDTO.CategoryDetailsDTO categoryDetailsDTO = new GameCategoriesAndReviewsDTO.CategoryDetailsDTO();
            categoryDetailsDTO.setName(entry.getKey());
            categoryDetailsDTO.setVotes(entry.getValue().intValue());

            categories.add(categoryDetailsDTO);
        }

        gameCategoriesAndReviewsDTO.setCategories(categories);
        return gameCategoriesAndReviewsDTO;
    }
}
