package com.commit.weatheralarm.alarmInfo.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class AlarmInfoDTO {

    private int id;
    private String email;
    private String location;
    private String alarmTime;

}
