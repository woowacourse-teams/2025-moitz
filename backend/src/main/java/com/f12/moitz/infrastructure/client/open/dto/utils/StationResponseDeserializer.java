package com.f12.moitz.infrastructure.client.open.dto.utils;

import com.f12.moitz.infrastructure.client.open.dto.StationResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Map;

public class StationResponseDeserializer extends JsonDeserializer<StationResponse> {

    private static final Map<String, String> LINE_NAME_EXCEPTIONS = Map.of(
            "경의선", "경의중앙선",
            "경의선 지선", "경의중앙선",
            "우이신설경전철", "우이신설선",
            "김포도시철도", "김포골드라인",
            "용인경전철", "용인에버라인",
            "인천선", "인천1호선"
    );

    private static final Map<String, String> STATION_NAME_EXCEPTIONS = Map.of(
            "이수", "총신대입구(이수)역",
            "총신대입구", "총신대입구(이수)역",
            "서울역", "서울역"
    );

    @Override
    public StationResponse deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String rawLineName = node.get("lineNm").asText();
        final String rawStationName = node.get("stnNm").asText();

        final String convertedLineName = convertLineName(rawLineName);
        final String convertedStationName = convertStationName(rawStationName, convertedLineName);

        return StationResponse.of(convertedStationName, convertedLineName);
    }

    private String convertLineName(final String lineName) {
        return LINE_NAME_EXCEPTIONS.getOrDefault(lineName, lineName);
    }

    private String convertStationName(final String stationName, final String convertedLineName) {
        if ("양평".equals(stationName)) {
            return String.format("%s역(%s)", stationName, convertedLineName);
        }

        if (STATION_NAME_EXCEPTIONS.containsKey(stationName)) {
            return STATION_NAME_EXCEPTIONS.get(stationName);
        }

        return stationName + "역";
    }

}
