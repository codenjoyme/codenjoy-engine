package com.codenjoy.dojo.services.event;

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

import com.codenjoy.dojo.client.TestGameSettings;
import com.codenjoy.dojo.services.CustomMessage;
import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.event.cases.*;
import com.codenjoy.dojo.services.settings.SettingsReader;
import com.codenjoy.dojo.utils.smart.SmartAssert;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static com.codenjoy.dojo.services.event.Mode.*;
import static com.codenjoy.dojo.utils.smart.SmartAssert.assertEquals;

public class ScoresImplTest {

    private SettingsReader settings;

    @Before
    public void setup() {
        settings = new TestGameSettings(){
            @Override
            public List<Key> allKeys() {
                return new LinkedList<>(super.allKeys()) {{
                    // to pass validation
                    add(ScoresImpl.SCORE_COUNTING_TYPE);
                }};
            }
        };
        settings.initScore(CUMULATIVELY);
    }

    @After
    public void after() {
        SmartAssert.checkResult();
    }

    @Test
    public void shouldProcess_customMessage() {
        // given
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<CustomMessage>(settings){{
                    put("message1",
                            value -> {
                                assertEquals(CustomMessage.class, value.getClass());
                                assertEquals("[message1]", value.toString());
                                return 1;
                            });

                    put("message2",
                            value -> {
                                assertEquals(CustomMessage.class, value.getClass());
                                assertEquals("[message2]", value.toString());
                                return 2;
                            });
                }}));

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
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<JSONObject>(settings){{
                    put("type1",
                            value -> {
                                    assertEquals(JSONObject.class, value.getClass());
                                    assertEquals("{\"type\":\"type1\",\"value\":\"value1\"}",
                                        value.toString());
                                return 1;
                            });

                    put("type2",
                            value -> {
                                assertEquals(JSONObject.class, value.getClass());
                                assertEquals("{\"type\":\"type2\",\"value\":\"value2\"}",
                                        value.toString());
                                return 2;
                            });
                }}));

        // when
        scores.event(new JSONObject("{'type':'type1','value':'value1'}"));

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(new JSONObject("{'type':'type2','value':'value2'}"));

        // then
        assertEquals(103, scores.getScore());
    }

    @Test
    public void shouldProcess_valueEvent() {
        // given
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<Integer>(settings){{
                    put(ValueEvent.Type.TYPE1,
                            value -> {
                                assertEquals(Integer.class, value.getClass());
                                assertEquals("11", value.toString());
                                return 1;
                            });

                    put(ValueEvent.Type.TYPE2,
                            value -> {
                                assertEquals(Integer.class, value.getClass());
                                assertEquals("22", value.toString());
                                return 2;
                            });
                }}));

        // when
        scores.event(new ValueEvent(ValueEvent.Type.TYPE1, 11));

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(new ValueEvent(ValueEvent.Type.TYPE2, 22));

        // then
        assertEquals(103, scores.getScore());
    }

    @Test
    public void shouldProcess_multiValueEvent() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new Calculator<>(new ScoresMap<MultiValuesEvent>(settings){{
            put(MultiValuesEvent.Type.TYPE1,
                    event -> {
                        assertEquals(MultiValuesEvent.class, event.getClass());
                        assertEquals("MultiValuesEvent" +
                                "(type=TYPE1, value1=11, value2=12)",
                                event.toString());
                        return 1;
                    });

            put(MultiValuesEvent.Type.TYPE2,
                    event -> {
                        assertEquals(MultiValuesEvent.class, event.getClass());
                        assertEquals("MultiValuesEvent" +
                                "(type=TYPE2, value1=22, value2=23)",
                                event.toString());
                        return 2;
                    });
        }}));

        // when
        scores.event(new MultiValuesEvent(MultiValuesEvent.Type.TYPE1, 11, 12));

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(new MultiValuesEvent(MultiValuesEvent.Type.TYPE2, 22, 23));

        // then
        assertEquals(103, scores.getScore());
    }

    @Test
    public void shouldProcess_objectEvent_byClass() {
        // given
        PlayerScores scores = new ScoresImpl<>(100, new Calculator<>(new ScoresMap<>(settings){{
            put(ObjectEvent1.class,
                    event -> {
                        assertEquals(ObjectEvent1.class, event.getClass());
                        assertEquals("ObjectEvent1(value=11)",
                                ((ObjectEvent1)event).toString());
                        return 1;
                    });

            put(ObjectEvent2.class,
                    event -> {
                        assertEquals(ObjectEvent2.class, event.getClass());
                        assertEquals("ObjectEvent2(value1=22, value2=true)",
                                ((ObjectEvent2)event).toString());
                        return 2;
                    });
        }}));

        // when
        scores.event(new ObjectEvent1(11));

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(new ObjectEvent2("22", true));

        // then
        assertEquals(103, scores.getScore());
    }

    @Test
    public void shouldProcess_enumValueEvent() {
        // given
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<Integer>(settings){{
                    put(EnumValueEvent.TYPE1,
                            value -> {
                                assertEquals(Integer.class, value.getClass());
                                assertEquals("11", value.toString());
                                return 1;
                            });

                    put(EnumValueEvent.TYPE2,
                            value -> {
                                assertEquals(Integer.class, value.getClass());
                                assertEquals("22", value.toString());
                                return 2;
                            });
                }}));

        // when
        scores.event(EnumValueEvent.TYPE1);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(EnumValueEvent.TYPE2);

        // then
        assertEquals(103, scores.getScore());
    }

    @Test
    public void shouldProcess_enumEvent() {
        // given
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<Void>(settings){{
                    put(EnumEvent.TYPE1,
                            value -> {
                                assertEquals(null, value);
                                return 1;
                            });

                    put(EnumEvent.TYPE2,
                            value -> {
                                assertEquals(null, value);
                                return 2;
                            });
                }}));

        // when
        scores.event(EnumEvent.TYPE1);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(EnumEvent.TYPE2);

        // then
        assertEquals(103, scores.getScore());
    }

    @Test
    public void shouldProcess_primitiveObject_byValue() {
        // given
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<>(settings){{
                    put("string1",
                            value -> {
                                assertEquals(String.class, value.getClass());
                                assertEquals("string1", value);
                                return 1;
                            });

                    put(2,
                            value -> {
                                assertEquals(Integer.class, value.getClass());
                                assertEquals(2, value);
                                return 2;
                            });

                    put(true,
                            value -> {
                                assertEquals(Boolean.class, value.getClass());
                                assertEquals(true, value);
                                return 3;
                            });
                }}));

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
    public void shouldProcess_primitiveObject_byClass() {
        // given
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<>(settings){{
                    put(String.class,
                            value -> {
                                assertEquals(String.class, value.getClass());
                                assertEquals("string1", value);
                                return 1;
                            });

                    put(Integer.class,
                            value -> {
                                assertEquals(Integer.class, value.getClass());
                                assertEquals(2, value);
                                return 2;
                            });

                    put(Boolean.class,
                            value -> {
                                assertEquals(Boolean.class, value.getClass());
                                assertEquals(true, value);
                                return 3;
                            });
                }}));

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
    public void shouldProcess_object_defaultProcessor() {
        // given
        AtomicReference<String> actual = new AtomicReference<>("");

        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<>(settings){{
                    put(PROCESS_ALL_KEYS,
                            value -> {
                                actual.set(String.format("[%s] %s",
                                        (value != null) ? value.getClass().getSimpleName() : "",
                                        value));

                                return 1;
                            });
                }}));

        // when
        scores.event("string1");

        // then
        assertEquals(101, scores.getScore());
        assertEquals("[String] string1", actual.toString());

        // when
        scores.event(2);

        // then
        assertEquals(102, scores.getScore());
        assertEquals("[Integer] 2", actual.toString());

        // when
        scores.event(true);

        // then
        assertEquals(103, scores.getScore());
        assertEquals("[Boolean] true", actual.toString());

        // when
        scores.event(new ObjectEvent1(11));

        // then
        assertEquals(104, scores.getScore());
        assertEquals("[ObjectEvent1] ObjectEvent1(value=11)", actual.toString());

        // when
        scores.event(new ObjectEvent2("11", false));

        // then
        assertEquals(105, scores.getScore());
        assertEquals("[ObjectEvent2] ObjectEvent2(value1=11, value2=false)", actual.toString());

        // when
        scores.event(EnumEvent.TYPE1);
        // then
        assertEquals(106, scores.getScore());
        assertEquals("[] null", actual.toString());

        // when
        scores.event(EnumValueEvent.TYPE2);

        // then
        assertEquals(107, scores.getScore());
        assertEquals("[Integer] 22", actual.toString());

        // when
        scores.event(new CustomMessage("message2"));

        // then
        assertEquals(108, scores.getScore());
        assertEquals("[CustomMessage] [message2]", actual.toString());

        // when
        scores.event(new JSONObject("{'type':'type2','value':'value2'}"));

        // then
        assertEquals(109, scores.getScore());
        assertEquals("[JSONObject] {\"type\":\"type2\",\"value\":\"value2\"}", actual.toString());

        // when
        scores.event(new ValueEvent(ValueEvent.Type.TYPE2, 22));

        // then
        assertEquals(110, scores.getScore());
        assertEquals("[Integer] 22", actual.toString());

        // when
        scores.event(new MultiValuesEvent(MultiValuesEvent.Type.TYPE2, 22, 23));

        // then
        assertEquals(111, scores.getScore());
        assertEquals("[MultiValuesEvent] MultiValuesEvent(type=TYPE2, value1=22, value2=23)", actual.toString());

        // when
        scores.event(null);

        // then
        assertEquals(112, scores.getScore());
        assertEquals("[] null", actual.toString());
    }

    @Test
    public void shouldProcess_object_manualProcessing_defaultProcessor() {
        // given
        AtomicReference<String> actual = new AtomicReference<>("");

        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<Answer>(settings){{
                    put(new Manual(null), function("null", actual));
                }}));

        // when
        scores.event("string1");

        // then
        assertEquals(101, scores.getScore());
        assertEquals("null => string1", actual.toString());

        // when
        scores.event(2);

        // then
        assertEquals(102, scores.getScore());
        assertEquals("null => 2", actual.toString());

        // when
        scores.event(true);

        // then
        assertEquals(103, scores.getScore());
        assertEquals("null => true", actual.toString());

        // when
        scores.event(new ObjectEvent1(11));

        // then
        assertEquals(104, scores.getScore());
        assertEquals("null => ObjectEvent1(value=11)", actual.toString());

        // when
        scores.event(new ObjectEvent2("11", false));

        // then
        assertEquals(105, scores.getScore());
        assertEquals("null => ObjectEvent2(value1=11, value2=false)", actual.toString());

        // when
        scores.event(EnumEvent.TYPE1);
        // then
        assertEquals(106, scores.getScore());
        assertEquals("null => null", actual.toString());

        // when
        scores.event(EnumValueEvent.TYPE2);

        // then
        assertEquals(107, scores.getScore());
        assertEquals("null => 22", actual.toString());

        // when
        scores.event(new CustomMessage("message2"));

        // then
        assertEquals(108, scores.getScore());
        assertEquals("null => [message2]", actual.toString());

        // when
        scores.event(new JSONObject("{'type':'type2','value':'value2'}"));

        // then
        assertEquals(109, scores.getScore());
        assertEquals("null => {\"type\":\"type2\",\"value\":\"value2\"}", actual.toString());

        // when
        scores.event(new ValueEvent(ValueEvent.Type.TYPE2, 22));

        // then
        assertEquals(110, scores.getScore());
        assertEquals("null => 22", actual.toString());

        // when
        scores.event(new MultiValuesEvent(MultiValuesEvent.Type.TYPE2, 22, 23));

        // then
        assertEquals(111, scores.getScore());
        assertEquals("null => MultiValuesEvent(type=TYPE2, value1=22, value2=23)", actual.toString());

        // when
        scores.event(null);

        // then
        assertEquals(112, scores.getScore());
        assertEquals("null => null", actual.toString());
    }

    @Test
    public void shouldProcess_object_manualProcessing_byProcessors() {
        // given
        AtomicReference<String> actual = new AtomicReference<>("");

        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<Answer>(settings){{
                    put(new Manual(String.class), function("String.class", actual));
                    put(new Manual(Integer.class), function("Integer.class", actual));
                    put(new Manual(Boolean.class), function("Boolean.class", actual));
                    put(new Manual(ObjectEvent1.class), function("ObjectEvent1.class", actual));
                    put(new Manual(ObjectEvent2.class), function("ObjectEvent2.class", actual));
                    put(new Manual(EnumEvent.TYPE1), function("EnumEvent.TYPE1", actual));
                    put(new Manual(EnumValueEvent.TYPE2), function("EnumValueEvent.TYPE2", actual));
                    put(new Manual("message2"), function("CustomMessage(message2)", actual));
                    put(new Manual("type2"), function("JSON(type2)", actual));
                    put(new Manual(ValueEvent.Type.TYPE2), function("ValueEvent.Type.TYPE2", actual));
                    put(new Manual(MultiValuesEvent.Type.TYPE2), function("MultiValuesEvent.Type.TYPE2", actual));
                    put(new Manual(null), function("null", actual));
                }}));

        // when
        scores.event("string1");

        // then
        assertEquals(101, scores.getScore());
        assertEquals("String.class => string1", actual.toString());

        // when
        scores.event(2);

        // then
        assertEquals(102, scores.getScore());
        assertEquals("Integer.class => 2", actual.toString());

        // when
        scores.event(true);

        // then
        assertEquals(103, scores.getScore());
        assertEquals("Boolean.class => true", actual.toString());

        // when
        scores.event(new ObjectEvent1(11));

        // then
        assertEquals(104, scores.getScore());
        assertEquals("ObjectEvent1.class => ObjectEvent1(value=11)", actual.toString());

        // when
        scores.event(new ObjectEvent2("11", false));

        // then
        assertEquals(105, scores.getScore());
        assertEquals("ObjectEvent2.class => ObjectEvent2(value1=11, value2=false)", actual.toString());

        // when
        scores.event(EnumEvent.TYPE1);
        // then
        assertEquals(106, scores.getScore());
        assertEquals("EnumEvent.TYPE1 => null", actual.toString());

        // when
        scores.event(EnumValueEvent.TYPE2);

        // then
        assertEquals(107, scores.getScore());
        assertEquals("EnumValueEvent.TYPE2 => 22", actual.toString());

        // when
        scores.event(new CustomMessage("message2"));

        // then
        assertEquals(108, scores.getScore());
        assertEquals("String.class => [message2]", actual.toString());

        // when
        scores.event(new JSONObject("{'type':'type2','value':'value2'}"));

        // then
        assertEquals(109, scores.getScore());
        assertEquals("String.class => {\"type\":\"type2\",\"value\":\"value2\"}", actual.toString());

        // when
        scores.event(new ValueEvent(ValueEvent.Type.TYPE2, 22));

        // then
        assertEquals(110, scores.getScore());
        assertEquals("ValueEvent.Type.TYPE2 => 22", actual.toString());

        // when
        scores.event(new MultiValuesEvent(MultiValuesEvent.Type.TYPE2, 22, 23));

        // then
        assertEquals(111, scores.getScore());
        assertEquals("MultiValuesEvent.Type.TYPE2 => MultiValuesEvent(type=TYPE2, value1=22, value2=23)", actual.toString());

        // when
        scores.event(null);

        // then
        assertEquals(112, scores.getScore());
        assertEquals("null => null", actual.toString());
    }

    private Function<Answer, Integer> function(String name, AtomicReference<String> actual) {
        return answer -> {
            Object value = answer.value(Object.class);
            actual.set(String.format("%s => %s", name, value));
            answer.score(answer.score() + 1);
            return null;
        };
    }

    @Test
    public void shouldProcess_whenNoKey() {
        // given
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<>(settings){{
                    put(EnumEvent.TYPE1,
                            value -> {
                                assertEquals(null, value);
                                return 1;
                            });
                }}));

        // when
        scores.event(EnumEvent.TYPE1);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(EnumEvent.TYPE2); // no key, no processing

        // then
        assertEquals(101, scores.getScore());
    }

    @Test
    public void shouldProcess_whenNullKey() {
        // given
        PlayerScores scores = new ScoresImpl<>(100,
                new Calculator<>(new ScoresMap<>(settings){{
                    put(EnumEvent.TYPE1,
                            value -> {
                                assertEquals(null, value);
                                return 1;
                            });

                    put(PROCESS_ALL_KEYS,  // default processor
                            value -> {
                                assertEquals(null, value);
                                return 2;
                            });
                }}));

        // when
        scores.event(EnumEvent.TYPE1);

        // then
        assertEquals(101, scores.getScore());

        // when
        scores.event(EnumEvent.TYPE2); // no key, but PROCESS_ALL_KEYS will process it

        // then
        assertEquals(103, scores.getScore());

        // when
        scores.event(EnumEvent.TYPE3); // no key, but PROCESS_ALL_KEYS will process it

        // then
        assertEquals(105, scores.getScore());
    }

    @Test
    public void shouldCounting_maxScoreMode() {
        // given
        settings.initScore(MAX_VALUE);

        ScoresImpl scores = new ScoresImpl<>(2,
                new Calculator<>(new ScoresMap<>(settings){{
                    put(PROCESS_ALL_KEYS, value -> (int)value);
                }}));

        // when
        scores.event(1);

        // then
        assertEquals(2, scores.getScore());
        assertEquals(2, scores.getSeries());

        // when
        scores.event(2);

        // then
        assertEquals(2, scores.getScore());
        assertEquals(2, scores.getSeries());

        // when
        scores.event(3);

        // then
        assertEquals(3, scores.getScore());
        assertEquals(3, scores.getSeries());

        // when
        scores.event(101);

        // then
        assertEquals(101, scores.getScore());
        assertEquals(101, scores.getSeries());

        // when
        scores.event(2);

        // then
        assertEquals(101, scores.getScore());
        assertEquals(101, scores.getSeries());

        // when
        scores.event(102);

        // then
        assertEquals(102, scores.getScore());
        assertEquals(102, scores.getSeries());
    }

    @Test
    public void shouldCounting_cumulativelyMode() {
        // given
        settings.initScore(CUMULATIVELY);

        ScoresImpl scores = new ScoresImpl<>(2,
                new Calculator<>(new ScoresMap<>(settings){{
                    put(PROCESS_ALL_KEYS, value -> (int)value);
                }}));

        // when
        scores.event(1);

        // then
        assertEquals(3, scores.getScore());
        assertEquals(3, scores.getSeries());

        // when
        scores.event(2);

        // then
        assertEquals(5, scores.getScore());
        assertEquals(5, scores.getSeries());

        // when
        scores.event(3);

        // then
        assertEquals(8, scores.getScore());
        assertEquals(8, scores.getSeries());

        // when
        scores.event(101);

        // then
        assertEquals(109, scores.getScore());
        assertEquals(109, scores.getSeries());

        // when
        scores.event(2);

        // then
        assertEquals(111, scores.getScore());
        assertEquals(111, scores.getSeries());

        // when
        scores.event(102);

        // then
        assertEquals(213, scores.getScore());
        assertEquals(213, scores.getSeries());
    }

    @Test
    public void shouldCounting_seriesMaxValueMode() {
        // given
        settings.initScore(SERIES_MAX_VALUE);
        String clearSeries = "CLEAN";

        ScoresImpl scores = new ScoresImpl<>(2,
                new Calculator<>(new ScoresMap<>(settings){{
                    put(clearSeries, value -> null); // return null to clean series

                    put(PROCESS_ALL_KEYS, value -> (int)value);
                }}));

        // when
        scores.event(2);

        // then
        // при загрузке очков, серия все равно == 0, потому тут 2, а не 4
        assertEquals(2, scores.getScore());
        assertEquals(2, scores.getSeries());

        // when
        scores.event(2);

        // then
        assertEquals(4, scores.getScore());
        assertEquals(4, scores.getSeries());

        // when
        scores.event(2);

        // then
        assertEquals(6, scores.getScore());
        assertEquals(6, scores.getSeries());

        // when
        scores.event(clearSeries); // clean only series

        // then
        assertEquals(6, scores.getScore());
        assertEquals(0, scores.getSeries());

        // when
        scores.event(2);

        // then
        assertEquals(6, scores.getScore());
        assertEquals(2, scores.getSeries());

        // when
        scores.event(4);

        // then
        assertEquals(6, scores.getScore());
        assertEquals(6, scores.getSeries());

        // when
        scores.event(4);

        // then
        assertEquals(10, scores.getScore());
        assertEquals(10, scores.getSeries());

        // when
        assertEquals(10, scores.clear()); // clean all scores and series

        // then
        assertEquals(0, scores.getScore());
        assertEquals(0, scores.getSeries());

        // when
        scores.event(2);

        // then
        assertEquals(2, scores.getScore());
        assertEquals(2, scores.getSeries());

        // when
        scores.event(4);

        // then
        assertEquals(6, scores.getScore());
        assertEquals(6, scores.getSeries());
    }
}