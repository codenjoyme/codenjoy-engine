package com.codenjoy.dojo.services.questionanswer.levels;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
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


import java.util.Arrays;
import java.util.List;

public class NullLevel implements Level {

    @Override
    public List<String> getQuestions() {
        return Arrays.asList();
    }

    @Override
    public List<String> getAnswers() {
        return Arrays.asList();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int complexity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setComplexity(int value) {
        // should not be reset
    }

    @Override
    public List<String> description() {
        return Arrays.asList("No more Levels. You win!");
    }
}
