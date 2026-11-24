package com.klokin.backend.domain;

// Everything that can happen in a work day. The phone records these as they happen;
// the server works out hours from them later.
public enum EventType {
    CLOCK_IN,
    CLOCK_OUT,
    LUNCH_START,
    LUNCH_END,
    SITE_ARRIVE,
    SITE_LEAVE
}
