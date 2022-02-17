package com.codenjoy.dojo.services.algs;

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

import com.codenjoy.dojo.services.Direction;
import org.junit.Test;

public class StatusTest {

    @Test
    public void performance() {
        // about 0.7 sec

        // given
        int ticks = 10_000_000;

        // when then
        for (int i = 0; i < ticks; i++) {
            Status status = new Status();
            status.directions();
            for (Direction direction : Direction.values()) {
                status.set(direction, true);
                status.set(direction, false);
                status.is(direction);
                status.done(direction);
                status.empty();
                status.add(direction);
            }
        }
    }
}
