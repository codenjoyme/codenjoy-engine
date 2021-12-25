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

public class Answer {

    private ScoresImpl scores;
    private Object value;

    public Answer(ScoresImpl scores, Object value) {
        this.scores = scores;
        this.value = value;
    }

    public <T> T value(Class<T> type) {
        return (T)value;
    }

    public Integer score() {
        return scores.getScore();
    }

    public void score(Integer score) {
        scores.setScore(score);
    }

    public Integer series() {
        return scores.getSeries();
    }

    public void series(Integer series) {
        scores.setSeries(series);
    }

}
