package com.achievementtracker.deserializer;

import com.achievementtracker.dto.GameCategoriesDTO;
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

public class GameCategoriesDeserializer extends JsonDeserializer<GameCategoriesDTO> {

    /* 1) Response with a valid app:
    https://steamspy.com/api.php?request=appdetails&appid=440

    *  2) Response with an invalid app:
    https://steamspy.com/api.php?request=appdetails&appid=13333 */

    @Override
    public GameCategoriesDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);

        GameCategoriesDTO gameCategoriesDTO = new GameCategoriesDTO();
        List<GameCategoriesDTO.CategoryDetailsDTO> categories = new LinkedList<>();

        Iterator<Map.Entry<String, JsonNode>> fields = parentNode.get("tags").fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();

            GameCategoriesDTO.CategoryDetailsDTO categoryDetailsDTO = new GameCategoriesDTO.CategoryDetailsDTO();
            categoryDetailsDTO.setName(entry.getKey());
            categoryDetailsDTO.setVotes(entry.getValue().intValue());

            categories.add(categoryDetailsDTO);
        }

        gameCategoriesDTO.setCategories(categories);
        return gameCategoriesDTO;
    }
}
