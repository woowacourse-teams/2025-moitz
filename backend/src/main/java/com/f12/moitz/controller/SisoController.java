package com.f12.moitz.controller;

import com.f12.moitz.domain.Gumball;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/siso")
public class SisoController {

    @GetMapping
    public ResponseEntity<Gumball> get() {
        return ResponseEntity.ok(new Gumball("Hailey", 19));
    }

}
