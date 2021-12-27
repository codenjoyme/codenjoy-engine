package com.codenjoy.dojo.services.multiplayer;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public enum Mode implements SettingsReader.Key {

    MULTIPLE(0, "[MULTIPLE] One level chosen at random. Multi player (all together or in the rooms)."),
    SINGLE(1, "[SINGLE] One level chosen at random. Single player."),
    TOURNAMENT(2, "[TOURNAMENT] One level chosen at random. 2 players in room."),
    TRIPLE(3, "[TRIPLE] One level chosen at random. 3 players in room."),
    QUADRO(4, "[QUADRO] One level chosen at random. 4 players in room."),
    TRAINING(5, "[TRAINING] Consecutive levels. Each level is single player. Final is all together."),
    ALL_SINGLE(6, "[ALL_SINGLE] Consecutive levels. Each level is single player."),
    ALL_IN_ROOMS(7, "[ALL_IN_ROOMS] Consecutive levels. Each level is in the rooms."),
    TRAINING_FINAL_IN_ROOMS(8, "[TRAINING_FINAL_IN_ROOMS] Consecutive levels. Each level is single player. Final is in the rooms."),
    // TODO добавить LEVELS
    // TODO добавить TEAM
    ;

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

    public static Mode get(String key) {
        return Arrays.stream(values())
                .filter(mode -> mode.key().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Key not found: " + key));
    }

    public int value() {
        return value;
    }

    @Override
    public String key() {
        return key;
    }
}
