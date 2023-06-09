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
import java.util.LinkedList;
import java.util.List;

public abstract class AlgorithmLevelImpl extends QuestionAnswerLevelImpl implements Algorithm {

    public static final int MAX_QUESTION_FOR_ONE_INT_ARGUMENT = 25;

    private int complexity;

    public AlgorithmLevelImpl(String... input) {
        if (input.length == 0) {
            questions = getQuestions();
            if (questions.isEmpty()) {
                questions = prepareQuestions(MAX_QUESTION_FOR_ONE_INT_ARGUMENT);
            }
        } else {
            questions = Arrays.asList(input);
        }
        prepareAnswers();
    }

    @Override
    public int complexity() {
        return complexity;
    }

    @Override
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    protected List<String> prepareQuestions(int max) {
        List<String> result = new LinkedList<>();
        for (int index = 1; index <= max; index++) {
            result.add(String.valueOf(index));
        }
        return result;
    }

    private void prepareAnswers() {
        answers = new LinkedList<>();
        for (String question : questions) {
            answers.add(get(question));
        }
    }

    @Override
    public List<String> getQuestions() {
        return questions;
    }

    public String get(String input) {
        try {
            if (input.contains(", ")) {
                String[] inputs = input.split(", ");
                int[] ints = new int[inputs.length];
                try {
                    for (int index = 0; index < inputs.length; index++) {
                        ints[index] = Integer.parseInt(inputs[index]);
                    }
                    return get(ints);
                } catch (NumberFormatException | IllegalStateException e) {
                    return get(inputs);
                }
            }

            try {
                int number = Integer.parseInt(input);
                return get(number);
            } catch (NumberFormatException e) {
                throw notImplemented();
            }
        } catch (IllegalStateException e) {
            // we cant throw exception here, because it will break whole system
            return null;
        }
    }

    public String get(int input) {
        throw notImplemented();
    }

    private RuntimeException notImplemented() {
        return new IllegalStateException("You should override one of 'get' methods");
    }

    public String get(int... input) {
        throw notImplemented();
    }

    public String get(String... input) {
        throw notImplemented();
    }

}
