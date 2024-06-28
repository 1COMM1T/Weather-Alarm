package com.commit.weatherAlarm.weatherMappings.controller;

import com.commit.weatherAlarm.weatherMappings.service.WeatherMappingsService;
import com.commit.weatherAlarm.weatherMappings.view.KeyView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/weather-mappings")
public class WeatherMappingsController {

    private WeatherMappingsService weatherMappingsService;

    @Autowired
    public WeatherMappingsController(WeatherMappingsService weatherMappingsService) {
        this.weatherMappingsService = weatherMappingsService;
    }

    @GetMapping("")
    public ResponseEntity<KeyView> getKeyByEmail(@RequestParam(value = "email", required = true) String email) throws IOException {
        KeyView result = weatherMappingsService.getKeyByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
