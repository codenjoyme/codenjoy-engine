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

import static com.codenjoy.dojo.services.route.Route.*;

public interface RouteProcessor {

    void validateTurnModeEnabled();

    void route(Route route);

    Route route();

    void direction(Direction direction);

    Direction direction();

    boolean canMove(Point pt);

    void beforeMove();

    void doMove(Point pt);

    boolean isSliding();

    boolean isModeSideView();

    default void change(Direction direction) {
        // если вид сбоку, то работает route=direction
        // если режим вид сверху - route=FORWARD + direction
        if (isModeSideView()) {
            route(Route.get(direction));
        } else {
            direction(direction);
            route(FORWARD);
        }
    }

    default void forward() {
        validateTurnModeEnabled();

        route(FORWARD);
    }

    default void backward() {
        validateTurnModeEnabled();

        route(BACKWARD);
    }

    default void turnLeft() {
        validateTurnModeEnabled();

        route(TURN_LEFT);
    }

    default void turnRight() {
        validateTurnModeEnabled();

        route(TURN_RIGHT);
    }

    default void tryMove(Direction direction) {
        Point pt = direction.change((Point) this);
        if (canMove(pt)) {
            doMove(pt);
        }
    }

    default void processMove() {
        boolean willMove = route() != null || isSliding();
        if (!willMove) return;

        if (route() == null) {
            // если занос, то полный ход, куда бы не были направлены
            route(FORWARD);
        }

        switch (route()) {
            // (вид сбоку) всплываем/погружаемся
            case UP:
            case DOWN:
                break;

            // (вид сбоку) движемся влево
            case LEFT:
                direction(Direction.LEFT);
                break;

            // (вид сбоку) движемся вправо
            case RIGHT:
                direction(Direction.RIGHT);
                break;

            // (вид сверху) поворот налево
            case TURN_LEFT:
                direction(direction().counterClockwise());
                break;

            // (вид сверху) поворот налево
            case TURN_RIGHT:
                direction(direction().clockwise());
                break;

            // (вид сверху) полный ход
            case FORWARD:
                break;

            // (вид сверху) задний ход
            case BACKWARD:
                direction(direction().inverted());
                break;
        }

        beforeMove();

        switch (route()) {
            // (вид сбоку) всплываем сохраняя ориентацию
            case UP:
                tryMove(Direction.UP);
                break;

            // (вид сбоку) погружаемся сохраняя ориентацию
            case DOWN:
                tryMove(Direction.DOWN);
                break;

            // (вид сбоку) движемся влево/вправо
            case LEFT:
            case RIGHT:
                tryMove(direction());
                break;
            
            // (вид сверху) повороты не влияют на изменения положения
            case TURN_LEFT:
            case TURN_RIGHT:
                break;

            // (вид сверху) полный ход (в направлении direction)
            case FORWARD:
                tryMove(direction());
                break;

            // (вид сверху) задний ход (в направлении противоположном direction)
            case BACKWARD:
                tryMove(direction());
                direction(direction().inverted());
                break;
        }

        route(null);
    }
}