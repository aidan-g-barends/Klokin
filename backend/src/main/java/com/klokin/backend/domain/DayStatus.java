package com.klokin.backend.domain;

// What one square on the heatmap shows
public enum DayStatus {
    WORKED,       // clocked in and out
    IN_PROGRESS,  // today, still clocked in
    INCOMPLETE,   // forgot to clock out: needs a correction request
    ABSENT,       // a working day that has passed with no clock-in
    OFF,          // not a working day (e.g. Sunday)
    UPCOMING      // hasn't happened yet
}
