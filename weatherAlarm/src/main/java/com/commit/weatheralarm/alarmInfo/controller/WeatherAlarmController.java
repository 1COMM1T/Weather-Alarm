package com.commit.weatheralarm.alarmInfo.controller;

import com.commit.weatheralarm.alarmInfo.dto.AlarmInfoDTO;
import com.commit.weatheralarm.alarmInfo.dto.WeatherInfoDTO;
import com.commit.weatheralarm.alarmInfo.service.WeatherAlarmService;
import com.commit.weatheralarm.alarmInfo.vo.RegistTimeAndLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WeatherAlarmController {

    private WeatherAlarmService weatherAlarmService;

    @Autowired
    public WeatherAlarmController(WeatherAlarmService weatherAlarmService) {
        this.weatherAlarmService = weatherAlarmService;
    }

    @GetMapping("/health_check")
    public String health_check() {
        return "test!!!!";
    }

    @GetMapping("/v1/weather-mappings/{id}")
    public ResponseEntity<WeatherInfoDTO> userAlarmInfo(@PathVariable int id) {
        WeatherInfoDTO weatherInfo = weatherAlarmService.userAlarmInfo(id);
        return ResponseEntity.status(HttpStatus.OK).body(weatherInfo);
    }

    @GetMapping("/v1/weather-mappings")
    public ResponseEntity<List<WeatherInfoDTO>> allAlarmInfo() {
        List<WeatherInfoDTO> weatherInfos = weatherAlarmService.allAlarmInfo();
        return ResponseEntity.status(HttpStatus.OK).body(weatherInfos);
    }

    @PutMapping("/v1/weather-mappings/{id}")
    public ResponseEntity<AlarmInfoDTO> registAlarmInfo(@RequestBody RegistTimeAndLocation timeAndLocation, @PathVariable int id) {
        AlarmInfoDTO alarmInfo = weatherAlarmService.registAlarmInfo(timeAndLocation, id);
        return ResponseEntity.status(HttpStatus.OK).body(alarmInfo);
    }
}
