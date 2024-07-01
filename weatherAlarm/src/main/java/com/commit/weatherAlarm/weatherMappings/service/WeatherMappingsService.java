package com.commit.weatherAlarm.weatherMappings.service;

import com.commit.weatherAlarm.weatherMappings.view.KeyView;

import java.io.IOException;
import java.util.Map;

public interface WeatherMappingsService {
    KeyView getKeyByEmail(String email) throws IOException;

    void setUserInfo(String key, Map<String, Object> jsonData) throws IOException;

    void setAlarmInfo(String key, Map<String, Object> updates) throws IOException;

    void deleteUserInfo(String key) throws IOException;
}
