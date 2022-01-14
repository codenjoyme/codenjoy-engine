package com.codenjoy.dojo.services.field;

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

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.settings.SettingsReader;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@UtilityClass
public class Generator {

    public static <T> void generate2(Accessor<T> list,
                                    Dice dice,
                                    SettingsReader settings,
                                    SettingsReader.Key key,
                                    Supplier<List<Point>> free,
                                    Function<Point, T> creator) {
        if (settings.integer(key) < 0) {
            settings.integer(key, 0);
        }

        int count = Math.max(0, settings.integer(key));
        int added = count - list.size();
        if (added == 0) {
            return;
        }

        if (added < 0) {
            // удаляем из существующих
            // важно оставить текущие, потому что метод работает каждый тик
            list.remove(count, list.size());
            return;
        }

        // added > 0
        generate2(list, dice, added, free, creator);
    }

    private static <T> void generate2(Accessor<T> list,
                                    Dice dice,
                                    int count,
                                    Supplier<List<Point>> free,
                                    Function<Point, T> creator) {
        List<Point> points = free.get();
        // добавляем недостающих к тем что есть
        for (int index = 0; index < count; index++) {
            if (points.isEmpty()) return;

            int randomIndex = dice.next(points.size());

            // используется для тестов, чтобы остановить генерацию объектов
            if (randomIndex == -1) return;

            Point pt = points.remove(randomIndex);
            list.add(creator.apply(pt));
        }
    }

    public static <T> void generate(Accessor<T> list,
                                    int fieldSize,
                                    SettingsReader settings,
                                    SettingsReader.Key key,
                                    Function<? extends GamePlayer, Optional<Point>> freeRandom,
                                    Function<Point, T> creator) {
        if (settings.integer(key) < 0) {
            settings.integer(key, 0);
        }

        int count = Math.max(0, settings.integer(key));
        int added = count - list.size();
        if (added == 0) {
            return;
        }

        if (added < 0) {
            // удаляем из существующих
            // важно оставить текущие, потому что метод работает каждый тик
            list.remove(count, list.size());
            return;
        }

        // added > 0
        generate(list, fieldSize, added, freeRandom, creator);
    }

    public static <T> void generate(Accessor<T> list,
                                    int fieldSize,
                                    int count,
                                    Function<? extends GamePlayer, Optional<Point>> freeRandom,
                                    Function<Point, T> creator) {
        // добавляем недостающих к тем что есть
        for (int index = 0; index < count; index++) {
            Optional<Point> option = freeRandom.apply(null);
            if (option.isEmpty()) break;

            Point pt = option.get();
            if (pt.isOutOf(fieldSize)) break;

            list.add(creator.apply(pt));
        }
    }

    public static Optional<Point> freeRandom(int size, Dice dice, Predicate<Point> isFree) {
        return freeRandom(
                () -> dice.next(size),
                () -> dice.next(size),
                isFree);
    }

    public static Optional<Point> freeRandom(Supplier<Integer> getX,
                                             Supplier<Integer> getY,
                                             Predicate<Point> isFree)
    {
        Point pt = new PointImpl();
        int count = 0;
        int max = 100;
        do {
            pt.setX(getX.get());
            pt.setY(getY.get());
        } while (!isFree.test(pt) && count++ < max);

        if (count >= max) {
            return Optional.empty();
        }

        return Optional.of(pt);
    }
}
