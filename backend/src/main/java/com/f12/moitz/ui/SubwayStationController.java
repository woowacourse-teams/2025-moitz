package com.f12.moitz.ui;

import com.f12.moitz.application.subway.SubwayStationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subway-stations")
public class SubwayStationController {

    private final SubwayStationService subwayStationService;

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllStationNames() {
        return ResponseEntity.ok(subwayStationService.findAllStationNames());
    }

}
