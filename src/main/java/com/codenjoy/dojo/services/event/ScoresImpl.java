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

import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.settings.SelectBox;
import com.codenjoy.dojo.services.settings.SettingsReader;

public class ScoresImpl<V> implements PlayerScores {

    // TODO выделить полноценный блок настроек подсчета очков в settings как Rounds/Semifinal/Inactivity
    public static final SettingsReader.Key SCORE_COUNTING_TYPE = () -> "[Score] Counting score mode";

    protected Mode counting;
    protected volatile int score;
    protected volatile int series;
    private Calculator<V> calculator;

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

    public ScoresImpl(int score, Calculator<V> calculator) {
        this.score = score;
        this.series = 0;
        this.calculator = calculator;
        this.calculator.init(this);
        this.counting = modeValue(calculator.settings());
    }

    @Override
    public int clear() {
        series = 0;
        int result = score;
        score = 0;
        return result;
    }

    @Override
    public Integer getScore() {
        return score;
    }

    public Integer getSeries() {
        return series;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    @Override
    public void event(Object event) {
        Integer amount = calculator.score(event);
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

    @Override
    public void update(Object score) {
        this.score = Integer.parseInt(score.toString());
        this.series = this.score;
    }
}