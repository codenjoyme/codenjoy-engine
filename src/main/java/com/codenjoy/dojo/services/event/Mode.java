package com.codenjoy.dojo.services.event;

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
