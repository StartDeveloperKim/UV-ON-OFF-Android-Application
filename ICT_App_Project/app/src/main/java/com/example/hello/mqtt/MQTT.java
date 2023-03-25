package com.example.hello.mqtt;

import androidx.annotation.NonNull;

public enum MQTT {
    SUBSCRIBES("subscribes"), TOPICS("topics");

    private final String value;

    MQTT(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
