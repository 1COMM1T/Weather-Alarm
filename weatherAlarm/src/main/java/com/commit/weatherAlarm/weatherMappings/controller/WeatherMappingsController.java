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
        weatherMappingsService.setUserInfo(key, jsonData);
        return ResponseEntity.ok("유저정보 생성완료!");
    }

    @PutMapping("/{key}")
    public ResponseEntity<String> setAlarmInfo(@PathVariable String key, @RequestBody Map<String, Object> updates) throws IOException {
        weatherMappingsService.setAlarmInfo(key, updates);
        return ResponseEntity.ok("알람정보 설정완료!");
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteUserInfo(@PathVariable String key) throws IOException {
        weatherMappingsService.deleteUserInfo(key);
        return ResponseEntity.ok("유저정보 삭제완료!");
    }


}
