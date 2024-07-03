package com.commit.weatherAlarm.weatherApi.service;

import com.commit.weatherAlarm.weatherApi.view.WeatherInfoView;
import reactor.core.publisher.Mono;

public interface WeatherApiService {
    Mono<WeatherInfoView> getWeather(String cityCode);
}
