package com.commit.weatheralarm.alarmInfo.vo;

import lombok.*;

@Getter
@NoArgsConstructor
public class RegistTimeAndLocation {
    private String alarmTime;
    private String location;

    @Builder
    public RegistTimeAndLocation(String alarmTime, String location) {
        this.alarmTime = alarmTime;
        this.location = location;
    }
}
