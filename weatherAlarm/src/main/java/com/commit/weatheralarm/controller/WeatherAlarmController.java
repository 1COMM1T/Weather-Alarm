package com.commit.weatheralarm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherAlarmController {

    @GetMapping("/health_check")
    public String health_check() {
        return "test!!!!";
    }
}
