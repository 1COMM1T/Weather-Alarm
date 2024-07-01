package com.commit.weatherAlarm.weatherApi.service;

import reactor.core.publisher.Mono;

public interface WeatherApiService {
    Mono<String> getWeather(String cityCode);
}
