package com.codenjoy.dojo.services.multiplayer;

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

import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class FieldServiceTest {

    @Test
    public void shouldLoadLastIdFromChat() {
        // given
        FieldService fields = new FieldService(5);

        // when
        GameField field = mock(GameField.class);
        fields.register(field);

        // then
        assertEquals(6, fields.id(field));

        // when
        GameField field2 = mock(GameField.class);
        fields.register(field2);

        // then
        assertEquals(6, fields.id(field));
        assertEquals(7, fields.id(field2));
    }

    @Test
    public void shouldNextId_increment() {
        // given
        FieldService fields = new FieldService(0);

        // when
        GameField field1 = mock(GameField.class);
        fields.register(field1);

        // then
        assertEquals(1, fields.id(field1));

        // when
        GameField field2 = mock(GameField.class);
        fields.register(field2);

        // then
        assertEquals(1, fields.id(field1));
        assertEquals(2, fields.id(field2));

        // when
        GameField field3 = mock(GameField.class);
        fields.register(field3);

        // then
        assertEquals(1, fields.id(field1));
        assertEquals(2, fields.id(field2));
        assertEquals(3, fields.id(field3));
    }

    @Test
    public void shouldRemove() {
        // given
        FieldService fields = new FieldService(0);

        GameField field1 = mock(GameField.class);
        fields.register(field1);

        GameField field2 = mock(GameField.class);
        fields.register(field2);

        GameField field3 = mock(GameField.class);
        fields.register(field3);

        assertEquals(1, fields.id(field1));
        assertEquals(2, fields.id(field2));
        assertEquals(3, fields.id(field3));

        // when
        fields.remove(field1);

        // then
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(field1));
        assertEquals(2, fields.id(field2));
        assertEquals(3, fields.id(field3));

        // when
        fields.remove(field2);

        // then
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(field1));
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(field2));
        assertEquals(3, fields.id(field3));

        // when
        fields.remove(field3);

        // then
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(field1));
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(field2));
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(field3));
    }

    @Test
    public void shouldNextId_afterRemove() {
        // given
        FieldService fields = new FieldService(0);

        GameField field1 = mock(GameField.class);
        fields.register(field1);
        fields.remove(field1);

        GameField field2 = mock(GameField.class);
        fields.register(field2);
        fields.remove(field2);

        // when
        GameField field3 = mock(GameField.class);
        fields.register(field3);

        // then
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(field1));
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(field2));
        assertEquals(3, fields.id(field3));
    }

    @Test
    public void shouldException_ifFieldNotRegistered() {
        // given
        FieldService fields = new FieldService(0);

        // when then
        TestUtils.assertException("IllegalStateException: Found unregistered field",
                () -> fields.id(mock(GameField.class)));
    }
}