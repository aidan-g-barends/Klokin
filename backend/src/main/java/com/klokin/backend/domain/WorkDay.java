package com.klokin.backend.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// One calculated day. Not stored in the database: it's always worked out fresh from the events,
// so it can never disagree with them.
//
// workedMinutes = paid minutes (lunch already taken off)
// overtimeMinutes = the part of workedMinutes beyond the normal day
public record WorkDay(LocalDate date,
                      DayStatus status,
                      int workedMinutes,
                      int overtimeMinutes,
                      int lunchMinutes,
                      LocalTime firstIn,
                      LocalTime lastOut,
                      List<String> sites) {

    public static WorkDay withoutEvents(LocalDate date, DayStatus status) {
        return new WorkDay(date, status, 0, 0, 0, null, null, List.of());
    }
}
