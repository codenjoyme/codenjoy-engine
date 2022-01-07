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
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.joystick.Act;
import com.codenjoy.dojo.services.joystick.RoundsDirectionActJoystick;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.Direction.*;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.junit.Assert.assertEquals;

public class RouteProcessorTest {

    private static final boolean SLIDING = true;
    private static final boolean TURN_FORWARD = true;
    private static final boolean SIDE_VIEW = true;

    private Hero hero;

    static class Hero extends PointImpl implements RouteProcessor, RoundsDirectionActJoystick {

        private boolean isTurnForwardMode;
        private boolean isSideViewMode;
        private boolean isSliding;

        private Route route;
        private Direction direction;

        public Hero(boolean isTurnForwardMode, boolean isSideViewMode, boolean isSliding) {
            super(pt(10, 10));
            direction = RIGHT;
            this.isTurnForwardMode = isTurnForwardMode;
            this.isSideViewMode = isSideViewMode;
            this.isSliding = isSliding;
        }

        @Override
        public void route(Route route) {
            this.route = route;
        }

        @Override
        public Route route() {
            return route;
        }

        @Override
        public void direction(Direction direction) {
            this.direction = direction;
        }

        @Override
        public Direction direction() {
            return direction;
        }

        @Override
        public boolean canMove(Point pt) {
            return true;
        }

        @Override
        public void beforeMove() {
            // do nothing
        }

        @Override
        public void doMove(Point pt) {
            move(pt);
        }

        @Override
        public boolean isSliding() {
            return isSliding;
        }

        @Override
        public boolean isSideViewMode() {
            return isSideViewMode;
        }

        @Override
        public boolean isTurnForwardMode() {
            return isTurnForwardMode;
        }

        @Override
        public void change(Direction direction) {
            RouteProcessor.super.change(direction);
        }

        @Override
        public void message(String command) {
            // do nothing
        }

        @Override
        public boolean isActiveAndAlive() {
            return true;
        }

        @Override
        public void act(Act act) {
            // do nothing
        }

        @Override
        public String toString() {
            return String.format("[%s, %s, %s]",
                    getX(),
                    getY(),
                    direction);
        }
    }

    private String move(Runnable method) {
        // when
        if (method != null) {
            method.run();
        }
        hero.processMove();

        // then
        return hero.toString();
    }

    private void assertHero(String expected) {
        assertEquals(expected, hero.toString());
    }

    private void assertMoves(String expected, Direction... directions) {
        List<String> actual = new LinkedList<>();
        for (Direction direction : directions) {
            actual.add(String.format("%s -> %s -> %s",
                    rightPad(hero.toString(), 15),
                    rightPad(direction == null ? "" : direction.toString(), 5),
                    rightPad(direction == null ? move(null) : move(hero(direction)), 15)));
        }
        assertEquals(expected, StringUtils.join(actual, "\n"));
    }

    private Runnable hero(Direction direction) {
        switch (direction) {
            case LEFT: return hero::left;
            case RIGHT: return hero::right;
            case UP: return hero::up;
            case DOWN: return hero::down;
            default: throw new IllegalArgumentException();
        }
    }

    @Test
    public void classicMode() {
        // given
        hero = new Hero(!TURN_FORWARD, !SIDE_VIEW, !SLIDING);
        assertHero("[10, 10, RIGHT]");

        // when then
        assertMoves("[10, 10, RIGHT] -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> LEFT  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> UP    -> [10, 11, UP]   \n" +
                    "[10, 11, UP]    -> UP    -> [10, 12, UP]   \n" +
                    "[10, 12, UP]    -> DOWN  -> [10, 11, DOWN] \n" +
                    "[10, 11, DOWN]  -> DOWN  -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> DOWN  -> [9, 9, DOWN]   \n" +
                    "[9, 9, DOWN]    -> LEFT  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> UP    -> [8, 10, UP]    \n" +
                    "[8, 10, UP]     -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> DOWN  -> [9, 9, DOWN]   \n" +
                    "[9, 9, DOWN]    -> RIGHT -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> UP    -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> DOWN  -> [10, 9, DOWN]  \n" +
                    "[10, 9, DOWN]   -> DOWN  -> [10, 8, DOWN]  \n" +
                    "[10, 8, DOWN]   -> LEFT  -> [9, 8, LEFT]   \n" +
                    "[9, 8, LEFT]    -> RIGHT -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> UP    -> [10, 9, UP]    \n" +
                    "[10, 9, UP]     -> UP    -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> DOWN  -> [10, 9, DOWN]  \n" +
                    "[10, 9, DOWN]   -> UP    -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> DOWN  -> [10, 9, DOWN]  \n" +
                    "[10, 9, DOWN]   -> DOWN  -> [10, 8, DOWN]  \n" +
                    "[10, 8, DOWN]   -> UP    -> [10, 9, UP]    \n" +
                    "[10, 9, UP]     -> LEFT  -> [9, 9, LEFT]   \n" +
                    "[9, 9, LEFT]    -> LEFT  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> RIGHT -> [9, 9, RIGHT]  \n" +
                    "[9, 9, RIGHT]   -> UP    -> [9, 10, UP]    \n" +
                    "[9, 10, UP]     -> UP    -> [9, 11, UP]    \n" +
                    "[9, 11, UP]     -> RIGHT -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> LEFT  -> [9, 11, LEFT]  \n" +
                    "[9, 11, LEFT]   -> LEFT  -> [8, 11, LEFT]  \n" +
                    "[8, 11, LEFT]   -> DOWN  -> [8, 10, DOWN]  \n" +
                    "[8, 10, DOWN]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> RIGHT -> [12, 10, RIGHT]\n" +
                    "[12, 10, RIGHT] -> LEFT  -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> LEFT  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> DOWN  -> [8, 9, DOWN]   \n" +
                    "[8, 9, DOWN]    -> DOWN  -> [8, 8, DOWN]   \n" +
                    "[8, 8, DOWN]    -> DOWN  -> [8, 7, DOWN]   \n" +
                    "[8, 7, DOWN]    -> DOWN  -> [8, 6, DOWN]   \n" +
                    "[8, 6, DOWN]    -> UP    -> [8, 7, UP]     \n" +
                    "[8, 7, UP]      -> UP    -> [8, 8, UP]     \n" +
                    "[8, 8, UP]      -> UP    -> [8, 9, UP]     \n" +
                    "[8, 9, UP]      -> UP    -> [8, 10, UP]    \n" +
                    "[8, 10, UP]     -> LEFT  -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   -> LEFT  -> [6, 10, LEFT]  \n" +
                    "[6, 10, LEFT]   -> RIGHT -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  ->       -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  ->       -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  -> RIGHT -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  -> UP    -> [8, 11, UP]    \n" +
                    "[8, 11, UP]     ->       -> [8, 11, UP]    \n" +
                    "[8, 11, UP]     ->       -> [8, 11, UP]    \n" +
                    "[8, 11, UP]     -> UP    -> [8, 12, UP]    \n" +
                    "[8, 12, UP]     -> DOWN  -> [8, 11, DOWN]  \n" +
                    "[8, 11, DOWN]   ->       -> [8, 11, DOWN]  \n" +
                    "[8, 11, DOWN]   ->       -> [8, 11, DOWN]  \n" +
                    "[8, 11, DOWN]   -> DOWN  -> [8, 10, DOWN]  \n" +
                    "[8, 10, DOWN]   -> LEFT  -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   -> RIGHT -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  ->       -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  -> UP    -> [8, 11, UP]    \n" +
                    "[8, 11, UP]     ->       -> [8, 11, UP]    \n" +
                    "[8, 11, UP]     -> DOWN  -> [8, 10, DOWN]  \n" +
                    "[8, 10, DOWN]   ->       -> [8, 10, DOWN]  ",
                directions());
    }

    @Test
    public void classicMode_sliding() {
        // given
        hero = new Hero(!TURN_FORWARD, !SIDE_VIEW, SLIDING);
        assertHero("[10, 10, RIGHT]");

        // when then
        assertMoves("[10, 10, RIGHT] -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> LEFT  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> UP    -> [10, 11, UP]   \n" +
                    "[10, 11, UP]    -> UP    -> [10, 12, UP]   \n" +
                    "[10, 12, UP]    -> DOWN  -> [10, 11, DOWN] \n" +
                    "[10, 11, DOWN]  -> DOWN  -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> DOWN  -> [9, 9, DOWN]   \n" +
                    "[9, 9, DOWN]    -> LEFT  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> UP    -> [8, 10, UP]    \n" +
                    "[8, 10, UP]     -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> DOWN  -> [9, 9, DOWN]   \n" +
                    "[9, 9, DOWN]    -> RIGHT -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> UP    -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> DOWN  -> [10, 9, DOWN]  \n" +
                    "[10, 9, DOWN]   -> DOWN  -> [10, 8, DOWN]  \n" +
                    "[10, 8, DOWN]   -> LEFT  -> [9, 8, LEFT]   \n" +
                    "[9, 8, LEFT]    -> RIGHT -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> UP    -> [10, 9, UP]    \n" +
                    "[10, 9, UP]     -> UP    -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> DOWN  -> [10, 9, DOWN]  \n" +
                    "[10, 9, DOWN]   -> UP    -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> DOWN  -> [10, 9, DOWN]  \n" +
                    "[10, 9, DOWN]   -> DOWN  -> [10, 8, DOWN]  \n" +
                    "[10, 8, DOWN]   -> UP    -> [10, 9, UP]    \n" +
                    "[10, 9, UP]     -> LEFT  -> [9, 9, LEFT]   \n" +
                    "[9, 9, LEFT]    -> LEFT  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> RIGHT -> [9, 9, RIGHT]  \n" +
                    "[9, 9, RIGHT]   -> UP    -> [9, 10, UP]    \n" +
                    "[9, 10, UP]     -> UP    -> [9, 11, UP]    \n" +
                    "[9, 11, UP]     -> RIGHT -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> LEFT  -> [9, 11, LEFT]  \n" +
                    "[9, 11, LEFT]   -> LEFT  -> [8, 11, LEFT]  \n" +
                    "[8, 11, LEFT]   -> DOWN  -> [8, 10, DOWN]  \n" +
                    "[8, 10, DOWN]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> RIGHT -> [12, 10, RIGHT]\n" +
                    "[12, 10, RIGHT] -> LEFT  -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> LEFT  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> DOWN  -> [8, 9, DOWN]   \n" +
                    "[8, 9, DOWN]    -> DOWN  -> [8, 8, DOWN]   \n" +
                    "[8, 8, DOWN]    -> DOWN  -> [8, 7, DOWN]   \n" +
                    "[8, 7, DOWN]    -> DOWN  -> [8, 6, DOWN]   \n" +
                    "[8, 6, DOWN]    -> UP    -> [8, 7, UP]     \n" +
                    "[8, 7, UP]      -> UP    -> [8, 8, UP]     \n" +
                    "[8, 8, UP]      -> UP    -> [8, 9, UP]     \n" +
                    "[8, 9, UP]      -> UP    -> [8, 10, UP]    \n" +
                    "[8, 10, UP]     -> LEFT  -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [6, 10, LEFT]  \n" +
                    "[6, 10, LEFT]   ->       -> [5, 10, LEFT]  \n" +
                    "[5, 10, LEFT]   -> LEFT  -> [4, 10, LEFT]  \n" +
                    "[4, 10, LEFT]   -> RIGHT -> [5, 10, RIGHT] \n" +
                    "[5, 10, RIGHT]  ->       -> [6, 10, RIGHT] \n" +
                    "[6, 10, RIGHT]  ->       -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  -> RIGHT -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  -> UP    -> [8, 11, UP]    \n" +
                    "[8, 11, UP]     ->       -> [8, 12, UP]    \n" +
                    "[8, 12, UP]     ->       -> [8, 13, UP]    \n" +
                    "[8, 13, UP]     -> UP    -> [8, 14, UP]    \n" +
                    "[8, 14, UP]     -> DOWN  -> [8, 13, DOWN]  \n" +
                    "[8, 13, DOWN]   ->       -> [8, 12, DOWN]  \n" +
                    "[8, 12, DOWN]   ->       -> [8, 11, DOWN]  \n" +
                    "[8, 11, DOWN]   -> DOWN  -> [8, 10, DOWN]  \n" +
                    "[8, 10, DOWN]   -> LEFT  -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [6, 10, LEFT]  \n" +
                    "[6, 10, LEFT]   -> RIGHT -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  ->       -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  -> UP    -> [8, 11, UP]    \n" +
                    "[8, 11, UP]     ->       -> [8, 12, UP]    \n" +
                    "[8, 12, UP]     -> DOWN  -> [8, 11, DOWN]  \n" +
                    "[8, 11, DOWN]   ->       -> [8, 10, DOWN]  ",
                directions());
    }

    private Direction[] directions() {
        // тут нет особой закономерности, просто перебрал
        return new Direction[]{
                LEFT, LEFT, RIGHT, RIGHT,
                UP, UP, DOWN, DOWN,
                LEFT, DOWN, LEFT, UP,
                RIGHT, DOWN, RIGHT, UP,
                DOWN, DOWN, LEFT, RIGHT,
                UP, UP, RIGHT, LEFT,
                DOWN, UP, LEFT, RIGHT,
                DOWN, DOWN, UP,
                LEFT, LEFT, RIGHT,
                UP, UP, RIGHT,
                LEFT, LEFT, DOWN,
                RIGHT, RIGHT, RIGHT, RIGHT,
                LEFT, LEFT, LEFT, LEFT,
                DOWN, DOWN, DOWN, DOWN,
                UP, UP, UP, UP,
                LEFT, null, null, LEFT,
                RIGHT, null, null, RIGHT,
                UP, null, null, UP,
                DOWN, null, null, DOWN,
                LEFT, null, RIGHT, null,
                UP, null, DOWN, null};
    }

    @Test
    public void turnForwardMode() {
        // given
        hero = new Hero(TURN_FORWARD, !SIDE_VIEW, !SLIDING);
        assertHero("[10, 10, RIGHT]");

        // when then
        assertMoves("[10, 10, RIGHT] -> LEFT  -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> RIGHT -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> UP    -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> UP    -> [12, 10, RIGHT]\n" +
                    "[12, 10, RIGHT] -> DOWN  -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> DOWN  -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> LEFT  -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> DOWN  -> [10, 9, UP]    \n" +
                    "[10, 9, UP]     -> LEFT  -> [10, 9, LEFT]  \n" +
                    "[10, 9, LEFT]   -> UP    -> [9, 9, LEFT]   \n" +
                    "[9, 9, LEFT]    -> RIGHT -> [9, 9, UP]     \n" +
                    "[9, 9, UP]      -> DOWN  -> [9, 8, UP]     \n" +
                    "[9, 8, UP]      -> RIGHT -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> UP    -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> DOWN  -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> DOWN  -> [8, 8, RIGHT]  \n" +
                    "[8, 8, RIGHT]   -> LEFT  -> [8, 8, UP]     \n" +
                    "[8, 8, UP]      -> RIGHT -> [8, 8, RIGHT]  \n" +
                    "[8, 8, RIGHT]   -> UP    -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> UP    -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> RIGHT -> [10, 8, DOWN]  \n" +
                    "[10, 8, DOWN]   -> LEFT  -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> DOWN  -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> UP    -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> LEFT  -> [10, 8, UP]    \n" +
                    "[10, 8, UP]     -> RIGHT -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> DOWN  -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> DOWN  -> [8, 8, RIGHT]  \n" +
                    "[8, 8, RIGHT]   -> UP    -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> LEFT  -> [9, 8, UP]     \n" +
                    "[9, 8, UP]      -> LEFT  -> [9, 8, LEFT]   \n" +
                    "[9, 8, LEFT]    -> RIGHT -> [9, 8, UP]     \n" +
                    "[9, 8, UP]      -> UP    -> [9, 9, UP]     \n" +
                    "[9, 9, UP]      -> UP    -> [9, 10, UP]    \n" +
                    "[9, 10, UP]     -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> LEFT  -> [9, 10, UP]    \n" +
                    "[9, 10, UP]     -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> DOWN  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> RIGHT -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> RIGHT -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> LEFT  -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> LEFT  -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> DOWN  -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  -> DOWN  -> [12, 10, LEFT] \n" +
                    "[12, 10, LEFT]  -> DOWN  -> [13, 10, LEFT] \n" +
                    "[13, 10, LEFT]  -> DOWN  -> [14, 10, LEFT] \n" +
                    "[14, 10, LEFT]  -> UP    -> [13, 10, LEFT] \n" +
                    "[13, 10, LEFT]  -> UP    -> [12, 10, LEFT] \n" +
                    "[12, 10, LEFT]  -> UP    -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  -> UP    -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  ->       -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  ->       -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> LEFT  -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  ->       -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  ->       -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> RIGHT -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> UP    -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   ->       -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   ->       -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> UP    -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> DOWN  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   ->       -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   ->       -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> DOWN  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  ->       -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> RIGHT -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  ->       -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> UP    -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   ->       -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> DOWN  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  ->       -> [10, 10, LEFT] ",
                directions());
    }

    @Test
    public void turnForwardMode_sliding() {
        // given
        hero = new Hero(TURN_FORWARD, !SIDE_VIEW, SLIDING);
        assertHero("[10, 10, RIGHT]");

        // when then
        assertMoves("[10, 10, RIGHT] -> LEFT  -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> RIGHT -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> UP    -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> UP    -> [12, 10, RIGHT]\n" +
                    "[12, 10, RIGHT] -> DOWN  -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> DOWN  -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> LEFT  -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> DOWN  -> [10, 9, UP]    \n" +
                    "[10, 9, UP]     -> LEFT  -> [10, 9, LEFT]  \n" +
                    "[10, 9, LEFT]   -> UP    -> [9, 9, LEFT]   \n" +
                    "[9, 9, LEFT]    -> RIGHT -> [9, 9, UP]     \n" +
                    "[9, 9, UP]      -> DOWN  -> [9, 8, UP]     \n" +
                    "[9, 8, UP]      -> RIGHT -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> UP    -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> DOWN  -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> DOWN  -> [8, 8, RIGHT]  \n" +
                    "[8, 8, RIGHT]   -> LEFT  -> [8, 8, UP]     \n" +
                    "[8, 8, UP]      -> RIGHT -> [8, 8, RIGHT]  \n" +
                    "[8, 8, RIGHT]   -> UP    -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> UP    -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> RIGHT -> [10, 8, DOWN]  \n" +
                    "[10, 8, DOWN]   -> LEFT  -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> DOWN  -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> UP    -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> LEFT  -> [10, 8, UP]    \n" +
                    "[10, 8, UP]     -> RIGHT -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> DOWN  -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> DOWN  -> [8, 8, RIGHT]  \n" +
                    "[8, 8, RIGHT]   -> UP    -> [9, 8, RIGHT]  \n" +
                    "[9, 8, RIGHT]   -> LEFT  -> [9, 8, UP]     \n" +
                    "[9, 8, UP]      -> LEFT  -> [9, 8, LEFT]   \n" +
                    "[9, 8, LEFT]    -> RIGHT -> [9, 8, UP]     \n" +
                    "[9, 8, UP]      -> UP    -> [9, 9, UP]     \n" +
                    "[9, 9, UP]      -> UP    -> [9, 10, UP]    \n" +
                    "[9, 10, UP]     -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> LEFT  -> [9, 10, UP]    \n" +
                    "[9, 10, UP]     -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> DOWN  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> RIGHT -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> RIGHT -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  -> LEFT  -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> LEFT  -> [10, 10, UP]   \n" +
                    "[10, 10, UP]    -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> DOWN  -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  -> DOWN  -> [12, 10, LEFT] \n" +
                    "[12, 10, LEFT]  -> DOWN  -> [13, 10, LEFT] \n" +
                    "[13, 10, LEFT]  -> DOWN  -> [14, 10, LEFT] \n" +
                    "[14, 10, LEFT]  -> UP    -> [13, 10, LEFT] \n" +
                    "[13, 10, LEFT]  -> UP    -> [12, 10, LEFT] \n" +
                    "[12, 10, LEFT]  -> UP    -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  -> UP    -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [10, 10, DOWN] \n" +
                    "[10, 10, DOWN]  ->       -> [10, 9, DOWN]  \n" +
                    "[10, 9, DOWN]   ->       -> [10, 8, DOWN]  \n" +
                    "[10, 8, DOWN]   -> LEFT  -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> RIGHT -> [10, 8, DOWN]  \n" +
                    "[10, 8, DOWN]   ->       -> [10, 7, DOWN]  \n" +
                    "[10, 7, DOWN]   ->       -> [10, 6, DOWN]  \n" +
                    "[10, 6, DOWN]   -> RIGHT -> [10, 6, LEFT]  \n" +
                    "[10, 6, LEFT]   -> UP    -> [9, 6, LEFT]   \n" +
                    "[9, 6, LEFT]    ->       -> [8, 6, LEFT]   \n" +
                    "[8, 6, LEFT]    ->       -> [7, 6, LEFT]   \n" +
                    "[7, 6, LEFT]    -> UP    -> [6, 6, LEFT]   \n" +
                    "[6, 6, LEFT]    -> DOWN  -> [7, 6, LEFT]   \n" +
                    "[7, 6, LEFT]    ->       -> [6, 6, LEFT]   \n" +
                    "[6, 6, LEFT]    ->       -> [5, 6, LEFT]   \n" +
                    "[5, 6, LEFT]    -> DOWN  -> [6, 6, LEFT]   \n" +
                    "[6, 6, LEFT]    -> LEFT  -> [6, 6, DOWN]   \n" +
                    "[6, 6, DOWN]    ->       -> [6, 5, DOWN]   \n" +
                    "[6, 5, DOWN]    -> RIGHT -> [6, 5, LEFT]   \n" +
                    "[6, 5, LEFT]    ->       -> [5, 5, LEFT]   \n" +
                    "[5, 5, LEFT]    -> UP    -> [4, 5, LEFT]   \n" +
                    "[4, 5, LEFT]    ->       -> [3, 5, LEFT]   \n" +
                    "[3, 5, LEFT]    -> DOWN  -> [4, 5, LEFT]   \n" +
                    "[4, 5, LEFT]    ->       -> [3, 5, LEFT]   ",
                directions());
    }

    @Test
    public void sideViewMode() {
        // given
        hero = new Hero(!TURN_FORWARD, SIDE_VIEW, !SLIDING);
        assertHero("[10, 10, RIGHT]");

        // when then
        assertMoves("[10, 10, RIGHT] -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> LEFT  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> UP    -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> UP    -> [10, 12, RIGHT]\n" +
                    "[10, 12, RIGHT] -> DOWN  -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> DOWN  -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> DOWN  -> [9, 9, LEFT]   \n" +
                    "[9, 9, LEFT]    -> LEFT  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> UP    -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> DOWN  -> [9, 9, RIGHT]  \n" +
                    "[9, 9, RIGHT]   -> RIGHT -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> UP    -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> DOWN  -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> DOWN  -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> LEFT  -> [9, 8, LEFT]   \n" +
                    "[9, 8, LEFT]    -> RIGHT -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> UP    -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> UP    -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> DOWN  -> [10, 9, LEFT]  \n" +
                    "[10, 9, LEFT]   -> UP    -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> DOWN  -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> DOWN  -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> UP    -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> LEFT  -> [9, 9, LEFT]   \n" +
                    "[9, 9, LEFT]    -> LEFT  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> RIGHT -> [9, 9, RIGHT]  \n" +
                    "[9, 9, RIGHT]   -> UP    -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> UP    -> [9, 11, RIGHT] \n" +
                    "[9, 11, RIGHT]  -> RIGHT -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> LEFT  -> [9, 11, LEFT]  \n" +
                    "[9, 11, LEFT]   -> LEFT  -> [8, 11, LEFT]  \n" +
                    "[8, 11, LEFT]   -> DOWN  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> RIGHT -> [12, 10, RIGHT]\n" +
                    "[12, 10, RIGHT] -> LEFT  -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> LEFT  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> DOWN  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> DOWN  -> [8, 8, LEFT]   \n" +
                    "[8, 8, LEFT]    -> DOWN  -> [8, 7, LEFT]   \n" +
                    "[8, 7, LEFT]    -> DOWN  -> [8, 6, LEFT]   \n" +
                    "[8, 6, LEFT]    -> UP    -> [8, 7, LEFT]   \n" +
                    "[8, 7, LEFT]    -> UP    -> [8, 8, LEFT]   \n" +
                    "[8, 8, LEFT]    -> UP    -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> UP    -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> LEFT  -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   -> LEFT  -> [6, 10, LEFT]  \n" +
                    "[6, 10, LEFT]   -> RIGHT -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  ->       -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  ->       -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  -> RIGHT -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  -> UP    -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  ->       -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  ->       -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  -> UP    -> [8, 12, RIGHT] \n" +
                    "[8, 12, RIGHT]  -> DOWN  -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  ->       -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  ->       -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  -> DOWN  -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  -> LEFT  -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   -> RIGHT -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  ->       -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  -> UP    -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  ->       -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  -> DOWN  -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  ->       -> [8, 10, RIGHT] ",
                directions());
    }

    @Test
    public void sideViewMode_sliding() {
        // given
        hero = new Hero(!TURN_FORWARD, SIDE_VIEW, SLIDING);
        assertHero("[10, 10, RIGHT]");

        // when then
        assertMoves("[10, 10, RIGHT] -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> LEFT  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> UP    -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> UP    -> [10, 12, RIGHT]\n" +
                    "[10, 12, RIGHT] -> DOWN  -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> DOWN  -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> DOWN  -> [9, 9, LEFT]   \n" +
                    "[9, 9, LEFT]    -> LEFT  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> UP    -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> DOWN  -> [9, 9, RIGHT]  \n" +
                    "[9, 9, RIGHT]   -> RIGHT -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> UP    -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> DOWN  -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> DOWN  -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> LEFT  -> [9, 8, LEFT]   \n" +
                    "[9, 8, LEFT]    -> RIGHT -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> UP    -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> UP    -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> DOWN  -> [10, 9, LEFT]  \n" +
                    "[10, 9, LEFT]   -> UP    -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> DOWN  -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> DOWN  -> [10, 8, RIGHT] \n" +
                    "[10, 8, RIGHT]  -> UP    -> [10, 9, RIGHT] \n" +
                    "[10, 9, RIGHT]  -> LEFT  -> [9, 9, LEFT]   \n" +
                    "[9, 9, LEFT]    -> LEFT  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> RIGHT -> [9, 9, RIGHT]  \n" +
                    "[9, 9, RIGHT]   -> UP    -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> UP    -> [9, 11, RIGHT] \n" +
                    "[9, 11, RIGHT]  -> RIGHT -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> LEFT  -> [9, 11, LEFT]  \n" +
                    "[9, 11, LEFT]   -> LEFT  -> [8, 11, LEFT]  \n" +
                    "[8, 11, LEFT]   -> DOWN  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> RIGHT -> [9, 10, RIGHT] \n" +
                    "[9, 10, RIGHT]  -> RIGHT -> [10, 10, RIGHT]\n" +
                    "[10, 10, RIGHT] -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] -> RIGHT -> [12, 10, RIGHT]\n" +
                    "[12, 10, RIGHT] -> LEFT  -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  -> LEFT  -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> LEFT  -> [9, 10, LEFT]  \n" +
                    "[9, 10, LEFT]   -> LEFT  -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> DOWN  -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> DOWN  -> [8, 8, LEFT]   \n" +
                    "[8, 8, LEFT]    -> DOWN  -> [8, 7, LEFT]   \n" +
                    "[8, 7, LEFT]    -> DOWN  -> [8, 6, LEFT]   \n" +
                    "[8, 6, LEFT]    -> UP    -> [8, 7, LEFT]   \n" +
                    "[8, 7, LEFT]    -> UP    -> [8, 8, LEFT]   \n" +
                    "[8, 8, LEFT]    -> UP    -> [8, 9, LEFT]   \n" +
                    "[8, 9, LEFT]    -> UP    -> [8, 10, LEFT]  \n" +
                    "[8, 10, LEFT]   -> LEFT  -> [7, 10, LEFT]  \n" +
                    "[7, 10, LEFT]   ->       -> [6, 10, LEFT]  \n" +
                    "[6, 10, LEFT]   ->       -> [5, 10, LEFT]  \n" +
                    "[5, 10, LEFT]   -> LEFT  -> [4, 10, LEFT]  \n" +
                    "[4, 10, LEFT]   -> RIGHT -> [5, 10, RIGHT] \n" +
                    "[5, 10, RIGHT]  ->       -> [6, 10, RIGHT] \n" +
                    "[6, 10, RIGHT]  ->       -> [7, 10, RIGHT] \n" +
                    "[7, 10, RIGHT]  -> RIGHT -> [8, 10, RIGHT] \n" +
                    "[8, 10, RIGHT]  -> UP    -> [8, 11, RIGHT] \n" +
                    "[8, 11, RIGHT]  ->       -> [9, 11, RIGHT] \n" +
                    "[9, 11, RIGHT]  ->       -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] -> UP    -> [10, 12, RIGHT]\n" +
                    "[10, 12, RIGHT] -> DOWN  -> [10, 11, RIGHT]\n" +
                    "[10, 11, RIGHT] ->       -> [11, 11, RIGHT]\n" +
                    "[11, 11, RIGHT] ->       -> [12, 11, RIGHT]\n" +
                    "[12, 11, RIGHT] -> DOWN  -> [12, 10, RIGHT]\n" +
                    "[12, 10, RIGHT] -> LEFT  -> [11, 10, LEFT] \n" +
                    "[11, 10, LEFT]  ->       -> [10, 10, LEFT] \n" +
                    "[10, 10, LEFT]  -> RIGHT -> [11, 10, RIGHT]\n" +
                    "[11, 10, RIGHT] ->       -> [12, 10, RIGHT]\n" +
                    "[12, 10, RIGHT] -> UP    -> [12, 11, RIGHT]\n" +
                    "[12, 11, RIGHT] ->       -> [13, 11, RIGHT]\n" +
                    "[13, 11, RIGHT] -> DOWN  -> [13, 10, RIGHT]\n" +
                    "[13, 10, RIGHT] ->       -> [14, 10, RIGHT]",
                directions());
    }
}