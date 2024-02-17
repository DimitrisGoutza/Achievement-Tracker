package com.achievementtracker.deserializer;

import com.achievementtracker.dto.AppListDTO;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class AppListDeserializer extends JsonDeserializer<AppListDTO> {

    /* Response:
    https://api.steampowered.com/ISteamApps/GetAppList/v2/
    */

    @Override
    public AppListDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode parentNode = jsonParser.getCodec().readTree(jsonParser);

        AppListDTO appListDTO = new AppListDTO();
        List<AppListDTO.AppDetailsDTO> apps = new LinkedList<>();

        JsonNode appsNode = parentNode.get("applist").get("apps");
        for (JsonNode app : appsNode) {
            AppListDTO.AppDetailsDTO appDetailsDTO = new AppListDTO.AppDetailsDTO();

            appDetailsDTO.setStoreId(app.get("appid").longValue());
            appDetailsDTO.setName(app.get("name").textValue());

            apps.add(appDetailsDTO);
        }

        appListDTO.setApps(apps);
        return appListDTO;
    }
}
