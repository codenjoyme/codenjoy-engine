package com.codenjoy.dojo.utils.smart;

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

import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.codenjoy.dojo.utils.core.MockitoJunitTesting.testing;

/**
 * Где стоит использовать этот способ проверки?
 *
 * В простых юнит тестах, которые ранаются быстро, особенно
 * если там используется approvals подход с
 * assertEquals("expected data", actual.toString()) нет
 * надобности в SmartAssert. А вот если тест интеграционный
 * спринговый, рест например, когда время его выполнения десятки
 * секунд, когда в тесте несколько assert проверок таких, что их
 * нельзя объединить (approvals подходом в одну) - то лучше
 * использовать SmartAssert.
 *
 * SmartAssert в каждом своем assertEquals накапливает
 * возражения, а потом в tearDown теста методом
 * SmartAssert.checkResult() делается проверка и слетают
 * все "expected but was actual" сообщения.
 */
public class SmartAssert {

    private static Map<String, List<AssertionError>> failures = new ConcurrentHashMap<>();

    private static StackTraceElement[] stackTrace() {
        Exception exception = new Exception();
        return exception.getStackTrace();
    }

    private static StackTraceElement getCaller() {
        StackTraceElement[] elements = stackTrace();
        for (int i = 0; i < elements.length; i++) {
            StackTraceElement element = elements[i];
            String className = element.getClassName();

            if (className.contains(".Abstract")
                || className.equals(SmartAssert.class.getName())
                || className.contains(SmartAssert.class.getSimpleName() + "$")
                || className.contains("dojo.services.helper"))
            {
                continue;
            }
            return element;
        }
        throw new RuntimeException();
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        try {
            testing().assertEquals(message, expected, actual);
        } catch (AssertionError e) {
            failures().add(e);
        }
    }

    public static void assertNotEquals(Object expected, Object actual) {
        try {
            testing().assertNotEquals(expected, actual);
        } catch (AssertionError e) {
            failures().add(e);
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        try {
            testing().assertEquals(expected, actual);
        } catch (AssertionError e) {
            failures().add(e);
        }
    }

    public static void assertSame(Object object1, Object object2) {
        try {
            testing().assertSame(object1, object2);
        } catch (AssertionError e) {
            failures().add(e);
        }
    }

    private static void checkResult(List<AssertionError> list) throws Exception {
        if (list.isEmpty()) return;

        List<Throwable> errors = new LinkedList<>(list);
        list.clear();
        throw testing().multipleFailureException(errors);
    }

    @SneakyThrows
    public static void checkResult() {
        checkResult(failures());
    }

    @SneakyThrows
    public static void checkResult(Class<?> caller) {
        checkResult(failures(caller.getName()));
    }

    private static List<AssertionError> failures() {
        return failures(getCaller().getClassName().split("[$]")[0]);
    }
    
    private static List<AssertionError> failures(String caller) {
        if (!failures.containsKey(caller)) {
            failures.put(caller, new LinkedList<>());
        }
        return failures.get(caller);
    }
}
