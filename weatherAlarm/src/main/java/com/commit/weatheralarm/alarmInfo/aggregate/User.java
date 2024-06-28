package com.commit.weatheralarm.alarmInfo.aggregate;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Table(name = "user")
@Getter
@RequiredArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(name = "location")
    private String location;

    @Column(name = "weather")
    private String weather;

    @Column(name = "alarm_time")
    private String alarmTime;

    @Builder
    public User(int id, String email, String password, String location, String weather, String alarmTime) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.location = location;
        this.weather = weather;
        this.alarmTime = alarmTime;
    }
}
