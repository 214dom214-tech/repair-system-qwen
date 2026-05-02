package com.example.repairsystem.model;

public enum RequestStatus {
    NEW,         // создана
    IN_PROGRESS, // принята в работу
    CLOSED,      // закрыта после ремонта (ожидает подтверждения)
    CONFIRMED,   // ремонт подтверждён
    CANCELLED    // отменена
}
