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

import com.codenjoy.dojo.utils.smart.SmartAssert;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;

import static com.codenjoy.dojo.utils.smart.SmartAssert.assertEquals;

public class DebugServiceTest {

    private DebugService service;

    @After
    public void after() {
        SmartAssert.checkResult();
    }

    @Test
    public void shouldCheckDefaultLoggers_debugDisabled_oneLogger() {
        // given when
        service = new DebugService(false,
                Arrays.asList("com.codenjoy"));

        // then
        assertLoggers("com.codenjoy: INFO");
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
                "com.codenjoy: INFO\n" +
                "java.util: INFO\n" +
                "org.junit: INFO");
    }

    @Test
    public void shouldCheckDefaultLoggers_debugEnabled_oneLogger() {
        // given when
        service = new DebugService(true,
                Arrays.asList("com.codenjoy"));

        // then
        assertLoggers("com.codenjoy: DEBUG");
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
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG");
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
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG");
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
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG");

        // when
        service.pause();

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy: INFO\n" +
                "java.util: INFO\n" +
                "org.junit: INFO");

        // when
        service.resume();

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG");
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
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG");

        // when
        service.setLoggersLevels(
                "com.codenjoy: INFO\n" +
                "java.util: INFO\n" +
                "org.junit: INFO");

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy: INFO\n" +
                "java.util: INFO\n" +
                "org.junit: INFO");
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
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG");

        // when
        service.setLoggersLevels(
                "com.codenjoy: ERROR\n" +  // old, will update
                "java.util: ERROR\n" +     // old, will update
                // "org.junit: DEBUG\n" +  // skip, will set to default INFO
                "org.mockito: ERROR");     // new, will set

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy: ERROR\n" +
                "java.util: ERROR\n" +
                "org.junit: INFO\n" +
                "org.mockito: ERROR");

        assertLevel("org.junit", "INFO");
    }

    @Test
    public void shouldSetLoggersLevels_severalSpacesBeforeLevelAreAllowed() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG");

        // when
        service.setLoggersLevels(
                "com.codenjoy:DEBUG\n" +     // no spaces
                "java.util: DEBUG\n" +       // one space
                "org.junit:  DEBUG\n" +      // two spaces
                "org.mockito:       DEBUG"); // a lot of spaces

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG\n" +
                "org.mockito: DEBUG");
    }

    @Test
    public void shouldSetLoggersLevels_checkAllLevels() {
        // given
        service = new DebugService(false, Arrays.asList());

        // then
        assertEquals(false, service.isWorking());
        assertLoggers("");

        // when
        service.setLoggersLevels(
                "com.codenjoy: ALL\n" +
                "java.util: TRACE\n" +
                "org.junit: DEBUG\n" +
                "org.mockito: INFO\n" +
                "org.eclipse.jetty: WARN\n" +
                "org.springframework.mvc: ERROR\n" +
                "javax: OFF");

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: ALL\n" +
                "java.util: TRACE\n" +
                "org.junit: DEBUG\n" +
                "org.mockito: INFO\n" +
                "org.eclipse.jetty: WARN\n" +
                "org.springframework.mvc: ERROR\n" +
                "javax: OFF");
    }

    @Test
    public void shouldSetLoggersLevels_setForClass() {
        // given
        service = new DebugService(false, Arrays.asList());

        // then
        assertEquals(false, service.isWorking());
        assertLoggers("");

        // when
        service.setLoggersLevels(
                "com.codenjoy.dojo.services.Welcome: DEBUG");

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy.dojo.services.Welcome: DEBUG");
    }

    @Test
    public void shouldSetLoggersLevels_ignoredEmptyLines() {
        // given
        service = new DebugService(false, Arrays.asList());

        // then
        assertEquals(false, service.isWorking());
        assertLoggers("");

        // when
        service.setLoggersLevels(
                "\n" +
                "\n" +
                "\n" +
                "com.codenjoy: DEBUG\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n");

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: DEBUG");
    }

    @Test
    public void shouldSetLoggersLevels_disableAll() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy",
                        "java.util",
                        "org.junit"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: DEBUG\n" +
                "java.util: DEBUG\n" +
                "org.junit: DEBUG");

        // when
        service.setLoggersLevels("");

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy: INFO\n" + // set to default, because removed in request
                "java.util: INFO\n" +    // ...
                "org.junit: INFO");      // ...
    }

    @Test
    public void shouldSetLoggersLevels_validate_failIfNotAPackage() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: DEBUG");

        // when
        service.setLoggersLevels(
                "Not@A@Package: ERROR\n" + // not a package, ignored
                "org.mockito: ERROR");     // valid

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy: INFO\n" +   // set to default, because removed in request
                "org.mockito: ERROR");
    }

    @Test
    public void shouldSetLoggersLevels_validate_failIfTwoSeparators() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: DEBUG");

        // when
        service.setLoggersLevels(
                "com.codenjoy: : ERROR\n" + // two ':' separators, ignored
                "org.mockito: ERROR");      // valid

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy: INFO\n" +    // set to default, because removed in request
                "org.mockito: ERROR");
    }

    @Test
    public void shouldSetLoggersLevels_validate_failIfNoSeparator() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers("com.codenjoy: DEBUG");

        // when
        service.setLoggersLevels(
                "com.codenjoyERROR\n" +  // no ':' separator, ignored
                "org.mockito: ERROR");   // valid

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy: INFO\n" +   // set to default, because removed in request
                "org.mockito: ERROR");
    }

    @Test
    public void shouldSetLoggersLevels_validate_failIfBadLevel() {
        // given
        service = new DebugService(true,
                Arrays.asList("com.codenjoy"));

        // then
        assertEquals(true, service.isWorking());
        assertLoggers(
                "com.codenjoy: DEBUG");

        // when
        service.setLoggersLevels(
                "com.codenjoy: BAD\n" + // bad level, ignored
                "org.mockito: ERROR");  // valid

        // then
        assertEquals(false, service.isWorking());
        assertLoggers(
                "com.codenjoy: INFO\n" +  // set to default, because removed in request
                "org.mockito: ERROR");
    }

    private void assertLevel(String name, String expectedLevel) {
        assertEquals(expectedLevel,
                DebugService.getLevel(name).levelStr);
    }

    private void assertLoggers(String expected) {
        assertEquals(expected,
                String.join("\n", service.getLoggersLevels()));
    }
}