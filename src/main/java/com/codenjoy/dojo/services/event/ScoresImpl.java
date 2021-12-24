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

import java.util.function.Function;

public class ScoresImpl<V> implements PlayerScores {

    // TODO выделить полноценный блок настроек подсчета очков в settings как Rounds/Semifinal/Inactivity
    public static final String SCORE_COUNTING_TYPE =
            "[Score] Count score cumulatively or take into account the maximum value";

    public static final boolean MAX_VALUE = false;
    public static final boolean CUMULATIVELY = !MAX_VALUE;

    protected boolean countingType;
    protected volatile int score;
    protected ScoresMap<V> map;

    public static void setup(SettingsReader settings, boolean mode) {
        settings.bool(() -> SCORE_COUNTING_TYPE, mode);
    }

    public ScoresImpl(int score, ScoresMap<V> map) {
        this.score = score;
        this.map = map;
        init(map.settings());
    }

    public static boolean mode(SettingsReader settings) {
        return settings.bool(() -> SCORE_COUNTING_TYPE);
    }

    private void init(SettingsReader settings) {
        countingType = settings.hasParameter(SCORE_COUNTING_TYPE)
                ? mode(settings)
                : CUMULATIVELY;
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
        int amount = scoreFor(map, event);
        if (countingType) {
            score += amount;
        } else {
            score = Math.max(score, amount);
        }
        score = Math.max(0, score);
    }

    public static <V> int scoreFor(ScoresMap<V> map, Object input) {
        Pair pair = parseEvent(input);

        Function<V, Integer> function = getValue(map, pair);
        if (function == null) {
            return 0;
        }

        return function.apply((V) pair.value());
    }

    private static <V> Function<V, Integer> getValue(ScoresMap<V> map, Pair pair) {
        if (map.containsKey(pair.key().getClass())) {
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
    }
}