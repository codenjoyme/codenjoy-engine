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

import java.util.ArrayList;
import java.util.List;

/**
 * Оптимизированная версия List<Direction>.
 * Как часть Points помогает понять куда в этой клеточке
 * мы можем еще попробовать двигаться.
 */
public class Status {

    private byte goes = 0b0000;

    public void add(Direction direction) {
        set(direction, true);
    }

    private int mask(Direction direction) {
        return 0b1 << direction.value();
    }

    public boolean done(Direction direction) {
        boolean result = is(direction);
        set(direction, false);
        return result;
    }

    public void set(Direction direction, boolean check) {
        if (check) {
            goes |= mask(direction);
        } else {
            goes &= ~mask(direction);
        }
    }

    public boolean is(Direction direction) {
        return (goes & mask(direction)) > 0;
    }

    public boolean empty() {
        return goes == 0;
    }

    public List<Direction> directions() {
        List<Direction> result = new ArrayList<>(4);

        for (Direction direction : Direction.values()) {
            if (is(direction)) {
                result.add(direction);
            }
        }
        return result;
    }
}
