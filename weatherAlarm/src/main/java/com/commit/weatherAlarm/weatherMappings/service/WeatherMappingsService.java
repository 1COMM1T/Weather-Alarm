package com.commit.weatherAlarm.weatherMappings.service;

import com.commit.weatherAlarm.weatherMappings.view.KeyView;

import java.io.IOException;

public interface WeatherMappingsService {
    KeyView getKeyByEmail(String email) throws IOException;
}
