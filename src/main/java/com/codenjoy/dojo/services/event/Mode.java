package com.codenjoy.dojo.services.event;

import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public enum Mode implements SettingsReader.Key {

    CUMULATIVELY(0, "Accumulate points consistently"),
    MAX_VALUE(1, "Maximum points from the event"),
    SERIES_MAX_VALUE(2, "Maximum points from the series");

    private int value;
    private String key;

    Mode(int value, String key) {
        this.value = value;
        this.key = key;
    }

    public static List<String> keys() {
        return Arrays.stream(values())
                .map(Mode::key)
                .collect(toList());
    }

    public int value() {
        return value;
    }

    @Override
    public String key() {
        return key;
    }
}
