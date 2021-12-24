package com.codenjoy.dojo.services.event;


/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2021 Codenjoy
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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ScoresMap<V> {

    public static Object PROCESS_ALL_KEYS = null;

    private Map<Object, Function<V, Integer>> map = new HashMap<>();
    private SettingsReader settings;

    public ScoresMap(SettingsReader settings) {
        this.settings = settings;
    }

    boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    Function<V, Integer> get(Object key) {
        return map.get(key);
    }
    
    protected void put(Object key, Function<V, Integer> value) {
        map.put(key, value);
    }

    public SettingsReader settings() {
        return settings;
    }
}