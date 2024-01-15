package com.achievementtracker.deserializer;

import com.achievementtracker.dto.StoreAppListDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class StoreAppListDeserializer extends JsonDeserializer<StoreAppListDTO> {
    @Override
    public StoreAppListDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode responseNode = parentNode.get("response");

        StoreAppListDTO storeAppListDTO = new StoreAppListDTO();
        List<StoreAppListDTO.StoreAppDTO> apps = new LinkedList<>();
        storeAppListDTO.setHasMoreResults(responseNode.get("have_more_results").booleanValue());
        storeAppListDTO.setLastAppId(responseNode.get("last_appid").intValue());

        JsonNode appListNode = responseNode.get("apps");
        for (JsonNode app : appListNode) {
            StoreAppListDTO.StoreAppDTO storeAppDTO = new StoreAppListDTO.StoreAppDTO();

            storeAppDTO.setId(app.get("appid").intValue());
            storeAppDTO.setName(app.get("name").textValue());

            apps.add(storeAppDTO);
        }

        storeAppListDTO.setStoreApps(apps);
        return storeAppListDTO;
    }
}
