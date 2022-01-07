package com.codenjoy.dojo.services.route;

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

public enum Route {

    UP,
    DOWN,
    LEFT,
    RIGHT,
    TURN_LEFT,
    TURN_RIGHT,
    FORWARD,
    BACKWARD;

    public static Route getInSideMode(Direction direction) {
        switch (direction) {
            case UP : return UP;
            case DOWN : return DOWN;
            case LEFT: return LEFT;
            case RIGHT: return RIGHT;
            default: return null;
        }
    }

    public static Route getInTurnMode(Direction direction) {
        switch (direction) {
            case UP : return FORWARD;
            case DOWN : return BACKWARD;
            case LEFT: return TURN_LEFT;
            case RIGHT: return TURN_RIGHT;
            default: return null;
        }
    }
}