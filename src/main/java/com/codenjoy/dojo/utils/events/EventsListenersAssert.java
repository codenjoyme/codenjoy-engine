package com.codenjoy.dojo.utils.events;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2020 Codenjoy
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

import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.utils.core.Testing;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.codenjoy.dojo.utils.core.MockitoJunitTesting.testing;
import static java.util.stream.Collectors.toList;

public class EventsListenersAssert {

    private Supplier<List<EventListener>> listeners;
    private Class eventsClass;

    public EventsListenersAssert(Supplier<List<EventListener>> listeners, Class eventsClass) {
        this.listeners = listeners;
        this.eventsClass = eventsClass;
    }

    private List<EventListener> listeners() {
        return listeners.get();
    }

    public String getEvents(Integer... indexes) {
        int size = listeners().size();
        return collectAll(size, indexes, index -> getEventsFormatted(size, index));
    }

    private String getEvents(EventListener events) {
        String result = tryCatch(
                () -> {
                    Testing.Captor captor = testing().captorForClass(eventsClass);
                    testing().verify(events, testing().atLeast(1)).event(captor.capture());
                    return captor.getAllValues().toString();
                },
                "WantedButNotInvoked", () -> "[]");
        testing().reset(events);
        return result;
    }

    private static boolean is(Class e, String exception) {
        if (e == null) {
            return false;
        }

        return e.getSimpleName().equals(exception)
                || is(e.getSuperclass(), exception);
    }

    private Integer[] range(int size, Integer[] indexes) {
        if (indexes.length == 0) {
            indexes = IntStream.range(0, size)
                    .boxed()
                    .collect(toList()).toArray(new Integer[0]);
        }
        return indexes;
    }

    public void assertAll(String expected, int size, Integer[] indexes,
                          Function<Integer, String> function)
    {
        String actual = collectAll(size, indexes, function);
        testing().assertEquals(expected, actual);
    }

    private String collectAll(int size, Integer[] indexes,
                             Function<Integer, String> function)
    {
        indexes = range(size, indexes);

        String result = "";
        for (Integer integer : indexes) {
            String line = function.apply(integer);
            if (line != null) {
                result += line;
            }
        }
        return result;
    }

    private static <A> A tryCatch(Supplier<A> tryCode,
                                  String exception, Supplier<A> failureCode) {
        try {
            return tryCode.get();
        } catch (Throwable e) {
            if (is(e.getClass(), exception)) {
                return failureCode.get();
            } else {
                throw e;
            }
        }
    }

    public void verifyEvents(EventListener events, String expected) {
        if (expected.equals("[]")) {
            tryCatch(
                    () -> {
                        testing().verify(events, testing().never()).event(testing().anyObject());
                        return null;
                    },
                    "NeverWantedButInvoked", () -> {
                        testing().assertEquals(expected, getEvents(events));
                        return null;
                    });
        } else {
            testing().assertEquals(expected, getEvents(events));
        }
        testing().reset(events);
    }

    public void verifyAllEvents(String expected, Integer... indexes) {
        int size = listeners().size();
        assertAll(expected, size, indexes, index -> getEventsFormatted(size, index));
    }

    private String getEventsFormatted(int size, Integer index) {
        String actual = getEvents(listeners().get(index));
        if ("[]".equals(actual)) {
            return null;
        }
        if (size == 1) {
            return actual;
        }

        return String.format("listener(%s) => %s\n", index, actual);
    }
}
