package com.codenjoy.dojo.services.event;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import com.codenjoy.dojo.client.TestGameSettings;
import com.codenjoy.dojo.services.CustomMessage;
import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.settings.SettingsReader;
import lombok.ToString;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AbstractScoresTest {

    private SettingsReader settings;

    @Before
    public void setup() {
        settings = new TestGameSettings();
        ScoresImpl.setup(settings, ScoresImpl.CUMULATIVELY);
    }

    @Test
    public void shouldProcess_customMessage() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<CustomMessage>(settings){{
            put("message1",
                    value -> {
                        assertEquals("[message1]", value.toString());
                        return 1;
                    });

            put("message2",
                    value -> {
                        assertEquals("[message2]", value.toString());
                        return 2;
                    });
        }});

        // when
        scores.event(new CustomMessage("message1"));

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(new CustomMessage("message2"));

        // then
        assertEquals(103, scores.getScore());
    }

    @Test
    public void shouldProcess_jsonObject() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<JSONObject>(settings){{
            put("type1",
                    value -> {
                        assertEquals("{\"type\":\"type1\",\"value\":\"value1\"}",
                                value.toString());
                        return 1;
                    });

            put("type2",
                    value -> {
                        assertEquals("{\"type\":\"type2\",\"value\":\"value2\"}",
                                value.toString());
                        return 2;
                    });
        }});

        // when
        scores.event(new JSONObject("{'type':'type1','value':'value1'}"));

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(new JSONObject("{'type':'type2','value':'value2'}"));

        // then
        assertEquals(103, scores.getScore());
    }

    @ToString
    static class SomeEventObject implements EventObject<SomeEventObject.Type, Integer> {

        private Type type;
        private Integer value;

        SomeEventObject(Type type, Integer value) {
            this.type = type;
            this.value = value;
        }

        enum Type {
            TYPE1, TYPE2;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public Integer value() {
            return value;
        }
    }

    @Test
    public void shouldProcess_eventObject() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<Integer>(settings){{
            put(SomeEventObject.Type.TYPE1,
                    value -> {
                        assertEquals("11", value.toString());
                        return 1;
                    });

            put(SomeEventObject.Type.TYPE2,
                    value -> {
                        assertEquals("22", value.toString());
                        return 2;
                    });
        }});

        // when
        scores.event(new SomeEventObject(SomeEventObject.Type.TYPE1, 11));

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(new SomeEventObject(SomeEventObject.Type.TYPE2, 22));

        // then
        assertEquals(103, scores.getScore());
    }

    @ToString
    enum SomeValuedEvent implements EventObject<SomeValuedEvent, Integer> {

        TYPE1(11),
        TYPE2(22);

        private final int value;

        SomeValuedEvent(int value) {
            this.value = value;
        }

        @Override
        public SomeValuedEvent type() {
            return this;
        }

        @Override
        public Integer value() {
            return value;
        }
    }

    @Test
    public void shouldProcess_valuedEventEnum() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<Integer>(settings){{
            put(SomeValuedEvent.TYPE1,
                    value -> {
                        assertEquals("11", value.toString());
                        return 1;
                    });

            put(SomeValuedEvent.TYPE2,
                    value -> {
                        assertEquals("22", value.toString());
                        return 2;
                    });
        }});

        // when
        scores.event(SomeValuedEvent.TYPE1);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(SomeValuedEvent.TYPE2);

        // then
        assertEquals(103, scores.getScore());
    }

    enum SomeEvent {

        TYPE1,
        TYPE2,
        TYPE3;
    }

    @Test
    public void shouldProcess_eventEnum() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<SomeEvent>(settings){{
            put(SomeEvent.TYPE1,
                    value -> {
                        assertEquals(null, value);
                        return 1;
                    });

            put(SomeEvent.TYPE2,
                    value -> {
                        assertEquals(null, value);
                        return 2;
                    });
        }});

        // when
        scores.event(SomeEvent.TYPE1);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(SomeEvent.TYPE2);

        // then
        assertEquals(103, scores.getScore());
    }

    @Test
    public void shouldProcess_object() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<>(settings){{
            put("string1",
                    value -> {
                        assertEquals("string1", value);
                        return 1;
                    });

            put(2,
                    value -> {
                        assertEquals(2, value);
                        return 2;
                    });

            put(true,
                    value -> {
                        assertEquals(true, value);
                        return 3;
                    });
        }});

        // when
        scores.event("string1");

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(2);

        // then
        assertEquals(103, scores.getScore());

        // when
        scores.event(true);

        // then
        assertEquals(106, scores.getScore());
    }

    @Test
    public void shouldProcess_object_caseAllInOne() {
        // given
        List<String> actual = new LinkedList<>();

        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<>(settings){{
            put(PROCESS_ALL_KEYS,
                    value -> {
                        actual.add(value.toString());
                        return 1;
                    });
        }});

        // when
        scores.event("string1");

        // then
        assertEquals(101, scores.getScore());
        assertEquals("[string1]", actual.toString());

        // when
        scores.event(2);

        // then
        assertEquals(102, scores.getScore());
        assertEquals("[string1, 2]", actual.toString());

        // when
        scores.event(true);

        // then
        assertEquals(103, scores.getScore());
        assertEquals("[string1, 2, true]", actual.toString());
    }

    @Test
    public void shouldProcess_whenNoKey() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<>(settings){{
            put(SomeEvent.TYPE1,
                    value -> {
                        assertEquals(null, value);
                        return 1;
                    });
        }});

        // when
        scores.event(SomeEvent.TYPE1);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(SomeEvent.TYPE2); // no key

        // then
        assertEquals(101, scores.getScore());
    }

    @Test
    public void shouldProcess_whenNullKey() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new ScoresMap<>(settings){{
            put(SomeEvent.TYPE1,
                    value -> {
                        assertEquals(null, value);
                        return 1;
                    });

            put(PROCESS_ALL_KEYS,  // default processor
                    value -> {
                        assertEquals(null, value);
                        return 2;
                    });
        }});

        // when
        scores.event(SomeEvent.TYPE1);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(SomeEvent.TYPE2); // no key, but null

        // then
        assertEquals(103, scores.getScore());

        // when
        scores.event(SomeEvent.TYPE3); // no key, but null

        // then
        assertEquals(105, scores.getScore());
    }

    @Test
    public void shouldProcess_maxScore() {
        // given
        ScoresImpl.setup(settings, ScoresImpl.MAX_VALUE);

        PlayerScores scores = new ScoresImpl<>(2, new ScoresMap<>(settings){{
            put(PROCESS_ALL_KEYS, value -> (int)value);
        }});

        // when
        scores.event(1);

        // then
        assertEquals(2, scores.getScore());

        // when
        scores.event(2);

        // then
        assertEquals(2, scores.getScore());

        // when
        scores.event(3);

        // then
        assertEquals(3, scores.getScore());

        // when
        scores.event(101);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(2);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(102);

        // then
        assertEquals(102, scores.getScore());
    }
}