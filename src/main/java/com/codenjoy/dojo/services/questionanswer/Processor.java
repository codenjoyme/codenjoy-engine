package com.codenjoy.dojo.services.questionanswer;

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

import org.json.JSONArray;

import java.util.LinkedList;
import java.util.List;

public class Processor {

    private List<QuestionAnswers> history;

    public Processor() {
        this.history = new LinkedList<>();
    }

    public void clear() {
        history.clear();
    }

    public List<QuestionAnswer> getLastHistory() {
        if (history.isEmpty()) {
            return null;
        }
        return history.get(history.size() - 1).getQuestionAnswers();
    }

    public List<QuestionAnswers> getHistory() {
        List<QuestionAnswers> result = new LinkedList<>();
        result.addAll(history);
        return result;
    }

    public List<String> nextAnswer(String inputAnswers) {
        JSONArray array = new JSONArray(inputAnswers);
        List<String> answers = new LinkedList<>();
        for (Object object : array) {
            answers.add(object.toString());
        }

        logNextAttempt();

        return answers;
    }

    private void logSuccess(String question, String answer) {
        log(question, answer, true);
    }

    private void logFailure(String question, String answer) {
        log(question, answer, false);
    }

    public void logNextAttempt() {
        history.add(new QuestionAnswers());
    }

    private void log(String question, String answer, boolean valid) {
        QuestionAnswer qa = new QuestionAnswer(question, answer);
        qa.setValid(valid);
        history.get(history.size() - 1).add(qa);
    }

    public boolean checkAnswers(List<String> questions,
                                List<String> expectedAnswers,
                                List<String> actualAnswers)
    {
        boolean isWin = true;
        for (int index = 0; index < questions.size(); index++) {
            String question = questions.get(index);
            String expectedAnswer = expectedAnswers.get(index);
            String actualAnswer = "???";
            if (index < actualAnswers.size()) {
                actualAnswer = actualAnswers.get(index);
            }

            if (expectedAnswer.equals(actualAnswer)) {
                logSuccess(question, actualAnswer);
            } else {
                logFailure(question, actualAnswer);
                isWin = false;
            }
        }
        return isWin;
    }
}
