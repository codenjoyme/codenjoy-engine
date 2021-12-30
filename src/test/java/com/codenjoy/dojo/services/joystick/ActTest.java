package com.codenjoy.dojo.services.joystick;

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

import static org.junit.Assert.assertEquals;

public class ActTest {

    @Test
    public void testWithoutArguments() {
        assertEquals(true, new Act(null).is());
        assertEquals(true, new Act().is());
    }

    @Test
    public void testWithArguments() {
        assertEquals(false, new Act(1).is());
        assertEquals(true, new Act(1).is(1));
        assertEquals(false, new Act(1).is(2));
    }
}
