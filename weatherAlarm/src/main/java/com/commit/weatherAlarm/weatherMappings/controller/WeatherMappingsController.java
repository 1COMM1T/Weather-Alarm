package com.commit.weatherAlarm.weatherMappings.controller;

import com.commit.weatherAlarm.weatherMappings.service.WeatherMappingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/weather-mappings")
public class WeatherMappingsController {

    private WeatherMappingsService weatherMappingsService;

    @Autowired
    public WeatherMappingsController(WeatherMappingsService weatherMappingsService) {
        this.weatherMappingsService = weatherMappingsService;
    }
}
