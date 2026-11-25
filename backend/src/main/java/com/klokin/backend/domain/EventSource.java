package com.klokin.backend.domain;

// How an event was recorded, so the boss can tell automatic clock-ins from manual ones
public enum EventSource {
    AUTO_WIFI,  // phone saw the shop Wi-Fi
    AUTO_GPS,   // phone entered the shop's location circle
    MANUAL,     // employee tapped a button
    KIOSK,      // shared tablet in the shop (PIN + selfie)
    CORRECTION  // added when the boss approved a correction request
}
