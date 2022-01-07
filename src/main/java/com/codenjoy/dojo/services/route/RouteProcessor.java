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

    default void tryMove(Point pt) {
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
            case TURN_LEFT:  // поворот налево
                direction(direction().counterClockwise());
                break;

            case TURN_RIGHT: // поворот налево
                direction(direction().clockwise());
                break;

            case FORWARD:   // полный ход
                break;

            case BACKWARD: // задний ход
                direction(direction().inverted());
                break;
        }

        beforeMove();

        switch (route()) {
            // повороты не влияют на изменения положения
            case TURN_LEFT:
            case TURN_RIGHT:
                break;

            // полный ход (в направлении direction)
            case FORWARD:
                tryMove(direction().change((Point) this));
                break;

            // задний ход (в направлении противоположном direction)
            case BACKWARD:
                tryMove(direction().change((Point) this));
                direction(direction().inverted());
                break;
        }

        route(null);
    }
}