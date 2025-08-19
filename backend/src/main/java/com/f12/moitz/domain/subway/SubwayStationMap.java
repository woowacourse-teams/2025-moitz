package com.f12.moitz.domain.subway;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Document(collection = "subway_station_map")
public class SubwayStationMap {

    @Id
    private String id = "subway_map";

    private Map<String, SubwayStation> stationMap = new HashMap<>();

    public SubwayStationMap(final Map<String, SubwayStation> stationMap) {
        this.stationMap = stationMap;
    }
}
