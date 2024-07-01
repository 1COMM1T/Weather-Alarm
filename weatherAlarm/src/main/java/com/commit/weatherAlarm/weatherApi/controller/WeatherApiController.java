package com.commit.weatherAlarm.weatherApi.controller;

import com.commit.weatherAlarm.weatherApi.service.WeatherApiService;
import com.commit.weatherAlarm.weatherApi.view.WeatherInfoView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WeatherApiController {

    private final WeatherApiService weatherApiService;

    @Autowired
    public WeatherApiController(WeatherApiService weatherApiService) {
        this.weatherApiService = weatherApiService;
    }

    @GetMapping("/v1/weather-info/{cityCode}")
    public Mono<WeatherInfoView> getWeather(@PathVariable String cityCode) {
        Mono<WeatherInfoView> weatherInfoView = weatherApiService.getWeather(cityCode);
        return ResponseEntity.status(HttpStatus.OK).body(weatherInfoView).getBody();
    }
}
