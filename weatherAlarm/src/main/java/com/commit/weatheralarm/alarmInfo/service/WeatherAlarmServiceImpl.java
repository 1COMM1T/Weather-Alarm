package com.commit.weatheralarm.alarmInfo.service;

import com.commit.weatheralarm.alarmInfo.aggregate.User;
import com.commit.weatheralarm.alarmInfo.dto.AlarmInfoDTO;
import com.commit.weatheralarm.alarmInfo.dto.WeatherInfoDTO;
import com.commit.weatheralarm.alarmInfo.repository.WeatherAlarmRepository;
import com.commit.weatheralarm.alarmInfo.vo.RegistTimeAndLocation;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WeatherAlarmServiceImpl implements com.commit.weatheralarm.alarmInfo.service.WeatherAlarmService {

    private WeatherAlarmRepository weatherAlarmRepository;
    private ModelMapper modelMapper;

    @Autowired
    public WeatherAlarmServiceImpl(WeatherAlarmRepository weatherAlarmRepository, ModelMapper modelMapper) {
        this.weatherAlarmRepository = weatherAlarmRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WeatherInfoDTO userAlarmInfo(int userId) {
        User weatherInfo = weatherAlarmRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        WeatherInfoDTO weatherInfoDTO = modelMapper.map(weatherInfo, WeatherInfoDTO.class);
        return weatherInfoDTO;
    }

    @Override
    public List<WeatherInfoDTO> allAlarmInfo() {
        List<User> weatherInfos = weatherAlarmRepository.findAll();
        List<WeatherInfoDTO> weatherInfoDTO = weatherInfos.stream().map(weatherInfo -> modelMapper.map(weatherInfo, WeatherInfoDTO.class)).collect(Collectors.toList());
        return weatherInfoDTO;
    }

    @Override
    @Transactional
    public AlarmInfoDTO registAlarmInfo(RegistTimeAndLocation timeAndLocation, int id) {
        User alarmInfo = weatherAlarmRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        User newAlarmInfo = User.builder()
                .id(alarmInfo.getId())
                .email(alarmInfo.getEmail())
                .password(alarmInfo.getPassword())
                .location(timeAndLocation.getLocation())
                .weather(alarmInfo.getWeather())
                .alarmTime(timeAndLocation.getAlarmTime())
                .build();

        weatherAlarmRepository.save(newAlarmInfo);

        AlarmInfoDTO updateAlarmInfo = modelMapper.map(alarmInfo, AlarmInfoDTO.class);

        return updateAlarmInfo;
    }
}
