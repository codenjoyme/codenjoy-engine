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


import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AlgorithmLevelImplTest {

    @Test
    public void testAlgorithmAnswersPrepared() {
        // given when
        Level level = getLevel();

        // then
        assertEquals(3, level.size());
        assertEquals("[answer1, answer2, answer3]", level.getAnswers().toString());
    }

    private Level getLevel() {
        return new AlgorithmLevelImpl("question1", "question2", "question3") {
            @Override
            public int complexity() {
                return 0;
            }

            @Override
            public String get(String input) {
                return "answer" + input.substring("question".length());
            }

            @Override
            public List<String> description() {
                return Arrays.asList("BlaBlaDescription");
            }

            @Override
            public String author() {
                return null;
            }
        };
    }

    @Test
    public void testIntFormat() {
        // given
        AlgorithmLevelImpl level = mock(AlgorithmLevelImpl.class);

        when(level.get(anyString())).thenCallRealMethod();

        // when
        level.get("1");

        // then
        verify(level).get(1);
    }

    @Test
    public void testIntsFormat() {
        // given
        AlgorithmLevelImpl level = mock(AlgorithmLevelImpl.class);

        when(level.get(anyString())).thenCallRealMethod();

        // when
        level.get("1, 2, 3");

        // then
        verify(level).get(1, 2, 3);
    }

    @Test
    public void testStringsFormat() {
        // given
        AlgorithmLevelImpl level = mock(AlgorithmLevelImpl.class);

        when(level.get(anyString())).thenCallRealMethod();

        // when
        level.get("qwe, asd, zxc");

        // then
        verify(level).get("qwe", "asd", "zxc");
    }

    @Test
    public void testStringFormat() {
        // given
        AlgorithmLevelImpl level = mock(AlgorithmLevelImpl.class);

        when(level.get(anyString())).thenCallRealMethod();

        // when
        level.get("qwe");

        // then
        verify(level).get("qwe");
    }

    static class EmptyAlgorithm extends AlgorithmLevelImpl {

        public EmptyAlgorithm(String... input) {
            super(input);
        }

        @Override
        public List<String> description() {
            return null;
        }

        @Override
        public String author() {
            return null;
        }

        @Override
        public int complexity() {
            return 0;
        }

    }
    static class TestAlgorithm extends EmptyAlgorithm {

        public TestAlgorithm(String... input) {
            super(input);
        }

        @Override
        public String get(int input) {
            return "" + input;
        }

    }

    @Test
    public void shouldConstruct_getIsNotImplemented() {
        // given when
        Level level = new EmptyAlgorithm();

        // then
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, " +
                        "11, 12, 13, 14, 15, 16, 17, 18, 19, " +
                        "20, 21, 22, 23, 24, 25]",
                level.getQuestions().toString());
    }

    @Test
    public void shouldConstruct_inputIsEmpty_noOverride() {
        // given when
        Level level = new TestAlgorithm();

        // then
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, " +
                "11, 12, 13, 14, 15, 16, 17, 18, 19, " +
                "20, 21, 22, 23, 24, 25]",
                level.getQuestions().toString());
    }

    @Test
    public void shouldGetQuestionAnswers() {
        // given when
        Level level = getLevel();

        // then
        assertEquals("[question1=answer1, question2=answer2, question3=answer3]",
                level.getQuestionAnswers().toString());
    }

    @Test
    public void shouldConstruct_inputIsEmpty_withOverride() {
        // given when
        Level level = new TestAlgorithm() {
            @Override
            public List<String> getQuestions() {
                return Arrays.asList("1", "2");
            }
        };

        // then
        assertEquals("[1, 2]",
                level.getQuestions().toString());
    }

    @Test
    public void shouldConstruct_inputIsNotEmpty_withOverride() {
        // given when
        Level level = new TestAlgorithm("1", "2", "3", "4") {
            @Override
            public List<String> getQuestions() {
                return Arrays.asList("5", "6");
            }
        };

        // then
        assertEquals("[5, 6]",
                level.getQuestions().toString());
    }

    @Test
    public void shouldConstruct_inputIsNotEmpty_noOverride() {
        // given when
        Level level = new TestAlgorithm("1", "2", "3", "4");

        // then
        assertEquals("[1, 2, 3, 4]",
                level.getQuestions().toString());
    }
}