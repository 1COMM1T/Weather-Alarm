package com.commit.weatheralarm.alarmInfo.service;

import com.commit.weatheralarm.alarmInfo.dto.AlarmInfoDTO;
import com.commit.weatheralarm.alarmInfo.dto.WeatherInfoDTO;
import com.commit.weatheralarm.alarmInfo.vo.RegistTimeAndLocation;

import java.util.List;

public interface WeatherAlarmService {
    WeatherInfoDTO userAlarmInfo(int userId);

    List<WeatherInfoDTO> allAlarmInfo();

    AlarmInfoDTO registAlarmInfo(RegistTimeAndLocation timeAndLocation, int id);
}
