package com.example.repairsystem.model;

public enum Role {
    ROLE_ADMIN,       // полный доступ + управление пользователями
    ROLE_CREATOR,     // создавать заявки
    ROLE_WORKER,      // принимать заявки в работу
    ROLE_CLOSER,      // закрывать заявки (после ремонта)
    ROLE_CONFIRMER,   // подтверждать ремонт
    ROLE_DELETER      // удалять заявки
}
