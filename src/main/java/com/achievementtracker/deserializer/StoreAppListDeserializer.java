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

    /* 1) Response that has more results to show:
    https://api.steampowered.com/IStoreService/GetAppList/v1/?key=36778BC8EA3A4E0D03F55092558DF5F5&include_dlc=false&last_appid=0

    *  2) Response that has no remaining results to show ➡ (hasMoreResults = false, lastAppId = null):
    https://api.steampowered.com/IStoreService/GetAppList/v1/?key=36778BC8EA3A4E0D03F55092558DF5F5&include_dlc=false&last_appid=2793700 */

    @Override
    public StoreAppListDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);
        JsonNode responseNode = parentNode.get("response");

        StoreAppListDTO storeAppListDTO = new StoreAppListDTO();
        List<StoreAppListDTO.StoreAppDTO> apps = new LinkedList<>();

        // Case #2
        storeAppListDTO.setHasMoreResults(responseNode.has("have_more_results"));
        storeAppListDTO.setLastAppId(
                responseNode.has("last_appid") ?
                        responseNode.get("last_appid").longValue() : null);


        JsonNode appListNode = responseNode.get("apps");
        for (JsonNode app : appListNode) {
            StoreAppListDTO.StoreAppDTO storeAppDTO = new StoreAppListDTO.StoreAppDTO();

            storeAppDTO.setStoreId(app.get("appid").longValue());
            storeAppDTO.setName(app.get("name").textValue());

            apps.add(storeAppDTO);
        }

        storeAppListDTO.setStoreApps(apps);
        return storeAppListDTO;
    }
}
