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


import com.codenjoy.dojo.services.CustomMessage;
import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.settings.SettingsReader;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractScores<V> implements PlayerScores {

    protected volatile int score;
    protected SettingsReader settings;

    public AbstractScores(int score, SettingsReader settings) {
        this.score = score;
        this.settings = settings;
    }

    @Override
    public int clear() {
        return score = 0;
    }

    @Override
    public Integer getScore() {
        return score;
    }

    @Override
    public void event(Object event) {
        score += scoreFor(eventToScore(), event);
        score = Math.max(0, score);
    }

    protected abstract Map<Object, Function<V, Integer>> eventToScore();

    public static <V> int scoreFor(Map<Object, Function<V, Integer>> map, Object input) {
        Map.Entry<Object, Object> entry = parseEvent(input);

        if (!map.containsKey(entry.getKey())) {
            return 0;
        }

        return map.get(entry.getKey()).apply((V) entry.getValue());
    }

    private static Map.Entry<Object, Object> parseEvent(Object input) {
        if (input instanceof Enum) {
            Enum event = (Enum) input;
            return new AbstractMap.SimpleEntry<>(
                    event, null);
        }

        if (input instanceof EventObject) {
            EventObject event = (EventObject) input;
            return new AbstractMap.SimpleEntry<>(
                    event.type(), event.value());
        }

        if (input instanceof JSONObject) {
            JSONObject event = (JSONObject) input;
            return new AbstractMap.SimpleEntry<>(
                    event.getString("type"), event);
        }

        if (input instanceof CustomMessage) {
            CustomMessage event = (CustomMessage) input;
            return new AbstractMap.SimpleEntry<>(
                    event, event.getMessage());
        }

        return new AbstractMap.SimpleEntry<>(
                null, null);
    }

    @Override
    public void update(Object score) {
        this.score = Integer.parseInt(score.toString());
    }
}