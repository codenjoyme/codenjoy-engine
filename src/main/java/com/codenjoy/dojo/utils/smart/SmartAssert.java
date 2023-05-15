package com.codenjoy.dojo.utils.smart;

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

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.runners.model.MultipleFailureException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private static List<AssertionError> failures = new CopyOnWriteArrayList<>();

    public static void assertEquals(String message, Object expected, Object actual) {
        try {
            Assert.assertEquals(message, expected, actual);
        } catch (AssertionError e) {
            failures.add(e);
        }
    }

    public static void assertNotEquals(Object expected, Object actual) {
        try {
            Assert.assertNotEquals(expected, actual);
        } catch (AssertionError e) {
            failures.add(e);
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        try {
            Assert.assertEquals(expected, actual);
        } catch (AssertionError e) {
            failures.add(e);
        }
    }

    public static void assertSame(Object object1, Object object2) {
        try {
            Assert.assertSame(object1, object2);
        } catch (AssertionError e) {
            failures.add(e);
        }
    }

    private static void checkResult(List<AssertionError> list) throws Exception {
        if (list.isEmpty()) return;

        List<Throwable> errors = new LinkedList<>(list);
        list.clear();
        throw new MultipleFailureException(errors);
    }

    @SneakyThrows
    public static void checkResult() {
        checkResult(failures);
    }
}
