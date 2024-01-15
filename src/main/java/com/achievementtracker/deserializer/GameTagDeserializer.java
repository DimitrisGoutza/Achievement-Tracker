package com.achievementtracker.deserializer;

import com.achievementtracker.dto.GameTagsDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameTagDeserializer extends JsonDeserializer<GameTagsDTO> {
    @Override
    public GameTagsDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);

        GameTagsDTO gameTagsDTO = new GameTagsDTO();
        Map<String, Integer> tags = new LinkedHashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fields = parentNode.get("tags").fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            tags.put(entry.getKey(), entry.getValue().intValue());
        }

        gameTagsDTO.setTags(tags);
        return gameTagsDTO;
    }
}
