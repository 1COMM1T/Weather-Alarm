package com.commit.weatherAlarm.weatherApi.view;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class WeatherInfoView {
    private String weather;
    private String cityName;
    private double temp;

}
