package com.commit.weatheralarm.alarmInfo.repository;

import com.commit.weatheralarm.alarmInfo.aggregate.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherAlarmRepository extends JpaRepository<User, Integer> {
}
