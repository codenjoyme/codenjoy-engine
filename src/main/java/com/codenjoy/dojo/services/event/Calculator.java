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
import com.codenjoy.dojo.services.settings.SettingsReader;
import org.json.JSONObject;

import java.util.Map;
import java.util.function.Function;

public class Calculator<V> {

    protected ScoresMap<V> map;
    private ScoresImpl scores;

    public Calculator(ScoresMap<V> map) {
        this.map = map;
    }

    // TODO это конечно не очень хорошо, надо распутать под настроение
    public void init(ScoresImpl scores) {
        this.scores = scores;
    }

    public Integer score(Object input) {
        Pair pair = parseEvent(input);

        Map.Entry<Object, Function<V, Integer>> entry = map.find(pair.key());
        if (entry == null) {
            return 0;
        }

        V value = (V) value(entry.getKey(), pair.value());
        return entry.getValue().apply(value);
    }

    private Object value(Object key, Object value) {
        if (key instanceof Manual && scores != null) {
            return new Answer(scores, value);
        }
        return value;
    }

    public Pair parseEvent(Object input) {
        if (input instanceof EventObject) {
            EventObject event = (EventObject) input;
            return new Pair(event.type(), event.value());
        }

        if (input instanceof Enum) {
            Enum event = (Enum) input;
            return new Pair(event, null);
        }

        if (input instanceof JSONObject) {
            JSONObject event = (JSONObject) input;
            return new Pair(event.getString("type"), event);
        }

        if (input instanceof CustomMessage) {
            CustomMessage event = (CustomMessage) input;
            return new Pair(event.getMessage(), event.value());
        }

        return new Pair(input, input);
    }

    public SettingsReader<SettingsReader> settings() {
        return map.settings();
    }
}