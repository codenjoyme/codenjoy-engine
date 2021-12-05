package com.codenjoy.dojo.services.log;

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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class DebugServiceTest {

    private DebugService service;

    @Test
    public void shouldCheckDefaultLoggers_debugDisabled_oneLogger() {
        // given when
        service = new DebugService(false,
                Arrays.asList("com.codenjoy"));

        // then
        assertLoggers("com.codenjoy:INFO");
    }

    @Test
    public void shouldCheckDefaultLoggers_debugDisabled_severalLoggers() {
        // given when
        service = new DebugService(false,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // then
        assertLoggers(
                "com.codenjoy:INFO\n" +
                "java.util:INFO\n" +
                "org.junit:INFO");
    }

    @Test
    public void shouldCheckDefaultLoggers_debugEnabled_oneLogger() {
        // given when
        service = new DebugService(true,
                Arrays.asList("com.codenjoy"));

        // then
        assertLoggers("com.codenjoy:DEBUG");
    }

    @Test
    public void shouldCheckDefaultLoggers_debugEnabled_severalLoggers() {
        // given when
        service = new DebugService(true,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // then
        assertLoggers(
                "com.codenjoy:DEBUG\n" +
                "java.util:DEBUG\n" +
                "org.junit:DEBUG");
    }

    @Test
    public void shouldSetDebugEnabled() {
        // given
        service = new DebugService(false,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // when
        service.setDebugEnable(true);

        // then
        assertLoggers(
                "com.codenjoy:DEBUG\n" +
                "java.util:DEBUG\n" +
                "org.junit:DEBUG");
    }

    @Test
    public void shouldIsWorking() {
        // given
        service = new DebugService(false,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // then
        assertEquals(false, service.isWorking());

        // when
        service.setDebugEnable(true);

        // then
        assertEquals(true, service.isWorking());
    }

    @Test
    public void shouldPauseResume() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy:DEBUG\n" +
                "java.util:DEBUG\n" +
                "org.junit:DEBUG");

        // when
        service.pause();

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy:INFO\n" +
                "java.util:INFO\n" +
                "org.junit:INFO");

        // when
        service.resume();

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy:DEBUG\n" +
                "java.util:DEBUG\n" +
                "org.junit:DEBUG");
    }

    @Test
    public void shouldSetLoggersLevels_canChangeAllLoggers_inParallelWillDisableDebugMode() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy:DEBUG\n" +
                "java.util:DEBUG\n" +
                "org.junit:DEBUG");

        // when
        service.setLoggersLevels(
                "com.codenjoy:INFO\n" +
                "java.util:INFO\n" +
                "org.junit:INFO");

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy:INFO\n" +
                "java.util:INFO\n" +
                "org.junit:INFO");
    }

    @Test
    public void shouldSetLoggersLevels_disableAllPreviousLoggers_andSetNew() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy:DEBUG\n" +
                "java.util:DEBUG\n" +
                "org.junit:DEBUG");

        // when
        service.setLoggersLevels(
                "com.codenjoy:ERROR\n" +  // old, will update
                "java.util:ERROR\n" +     // old, will update
                // "org.junit:DEBUG\n" +  // exclude, will set to default INFO
                "org.mockito:ERROR");     // new, will set

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy:ERROR\n" +
                "java.util:ERROR\n" +
                "org.mockito:ERROR");

        assertLevel("INFO", "org.junit");
    }

    private void assertLevel(String expectedLevel, String name) {
        assertEquals(expectedLevel,
                DebugService.getLevel(name).levelStr);
    }

    private void assertLoggers(String expected) {
        assertEquals(expected,
                String.join("\n", service.getLoggersLevels()));
    }
}