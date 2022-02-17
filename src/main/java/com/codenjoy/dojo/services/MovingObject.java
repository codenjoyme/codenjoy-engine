package com.codenjoy.dojo.services;

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

import com.codenjoy.dojo.services.multiplayer.GameField;

public abstract class MovingObject extends PointImpl {

    protected Direction direction;
    protected int speed;
    protected boolean moving;

    public MovingObject(Point pt, Direction direction) {
        super(pt);
        this.direction = direction;
        moving = false;
    }

    public Direction direction() {
        return direction;
    }

    public void move() {
        for (int tick = 0; tick < speed; tick++) {
            if (!moving) {
                return;
            }

            tryMove();
        }
    }

    protected void tryMove() {
        Point to = direction.change(this);
        if (to.isOutOf(field().size())) {
            remove();
        } else {
            moving(to);
        }
    }

    protected abstract void moving(Point pt);

    protected abstract GameField field();

    protected abstract void remove();
}
