package com.commit.weatherAlarm.weatherMappings.controller;

import com.commit.weatherAlarm.weatherMappings.service.WeatherMappingsService;
import com.commit.weatherAlarm.weatherMappings.view.KeyView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/v1/weather-mappings")
public class WeatherMappingsController {

    private WeatherMappingsService weatherMappingsService;

    @Autowired
    public WeatherMappingsController(WeatherMappingsService weatherMappingsService) {
        this.weatherMappingsService = weatherMappingsService;
    }

    @GetMapping("/key")
    public ResponseEntity<KeyView> getKeyByEmail(@RequestParam(value = "email", required = true) String email) throws IOException {
        KeyView result = weatherMappingsService.getKeyByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("")
    public ResponseEntity<String> registUserInfo(@RequestBody Map<String, Object> jsonData) throws IOException {
        String key =  System.currentTimeMillis() + ".json";
        weatherMappingsService.registUserInfo(key, jsonData);
        return ResponseEntity.ok("유저정보 생성완료!");
    }


}
