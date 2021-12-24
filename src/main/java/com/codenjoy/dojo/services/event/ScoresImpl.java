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
import com.codenjoy.dojo.services.settings.SelectBox;
import com.codenjoy.dojo.services.settings.SettingsReader;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class ScoresImpl<V> implements PlayerScores {

    // TODO выделить полноценный блок настроек подсчета очков в settings как Rounds/Semifinal/Inactivity
    public static final SettingsReader.Key SCORE_COUNTING_TYPE = () -> "[Score] Counting score mode";

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

    protected Mode counting;
    protected volatile int score;
    protected volatile int series;
    protected ScoresMap<V> map;

    // метод для инициализации настроек select'ом с заданным default Mode
    // либо если это уже произошло, то обновление значения настройки
    public static void setup(SettingsReader settings, Mode mode) {
        if (settings.hasParameter(SCORE_COUNTING_TYPE.key())) {
            mode(settings).select(mode.value());
        } else {
            settings.options(SCORE_COUNTING_TYPE,
                    Mode.keys(),
                    mode.key());
        }
    }

    // метод для получения enum Mode из настроек
    public static Mode modeValue(SettingsReader<SettingsReader> settings) {
        if (!settings.hasParameter(SCORE_COUNTING_TYPE.key())) {
            return Mode.CUMULATIVELY;
        }
        return Mode.values()[mode(settings).index()];
    }

    // метод для получения parameter Mode из настроек
    private static SelectBox mode(SettingsReader<SettingsReader> settings) {
        return settings.parameter(SCORE_COUNTING_TYPE, SelectBox.class);
    }

    public ScoresImpl(int score, ScoresMap<V> map) {
        this.score = score;
        this.series = score;
        this.map = map;
        this.counting = modeValue(map.settings());
    }

    @Override
    public int clear() {
        series = 0;
        return score = 0;
    }

    @Override
    public Integer getScore() {
        return score;
    }

    public Integer getSeries() {
        return series;
    }

    @Override
    public void event(Object event) {
        Integer amount = scoreFor(map, event);
        if (counting == Mode.CUMULATIVELY) {
            if (amount == null) amount = 0;
            score += amount;
        } else if (counting == Mode.MAX_VALUE) {
            if (amount == null) amount = 0;
            score = Math.max(score, amount);
        } else if (counting == Mode.SERIES_MAX_VALUE) {
            if (amount == null) {
                series = 0;
            } else {
                series += amount;
                series = Math.max(0, series);
            }
            score = Math.max(score, series);
        }
        score = Math.max(0, score);
        if (counting != Mode.SERIES_MAX_VALUE) {
            series = score;
        }
    }

    public static <V> Integer scoreFor(ScoresMap<V> map, Object input) {
        Pair pair = parseEvent(input);

        Function<V, Integer> function = getValue(map, pair);
        if (function == null) {
            return 0;
        }

        return function.apply((V) pair.value());
    }

    private static <V> Function<V, Integer> getValue(ScoresMap<V> map, Pair pair) {
        if (pair.key() != null && map.containsKey(pair.key().getClass())) {
            return map.get(pair.key().getClass());
        }

        if (map.containsKey(pair.key())) {
            return map.get(pair.key());
        }

        if (map.containsKey(null)) {
            return map.get(null);
        }

        return null;
    }

    private static Pair parseEvent(Object input) {
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

    @Override
    public void update(Object score) {
        this.score = Integer.parseInt(score.toString());
        this.series = this.score;
    }
}