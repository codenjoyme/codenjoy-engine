package com.codenjoy.dojo.services.field;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Generator {

    public static <T> void generate(Accessor<T> list,
                                    SettingsReader settings,
                                    SettingsReader.Key key,
                                    Function<? extends GamePlayer, Optional<Point>> freeRandom,
                                    Function<Point, T> creator)
    {
        if (settings.integer(key) < 0) {
            settings.integer(key, 0);
        }

        int count = Math.max(0, settings.integer(key));
        int added = count - list.size();
        if (added == 0) {
            return;
        } else if (added < 0) {
            // удаляем из существующих
            // важно оставить текущие, потому что метод работает каждый тик
            list.remove(count, list.size());
        } else {
            // добавляем недостающих к тем что есть
            for (int index = 0; index < added; index++) {
                Optional<Point> pt = freeRandom.apply(null);
                if (pt.isPresent()) {
                    list.add(creator.apply(pt.get()));
                }
            }
        }
    }

    // TODO удалить после того как Loderunner будет переведен на PointField
    public static <T> void generate(List<T> list,
                                    SettingsReader settings,
                                    SettingsReader.Key key,
                                    Function<? extends GamePlayer, Optional<Point>> freeRandom,
                                    Function<Point, T> creator)
    {
        generate(accessor(list), settings, key, freeRandom, creator);
    }

    // TODO удалить после того как Loderunner будет переведен на PointField
    private static <E> Accessor<E> accessor(List<E> list) {
        return new Accessor<E>() {
            @Override
            public boolean contains(Point point) {
                throw exception();
            }

            private UnsupportedOperationException exception() {
                return new UnsupportedOperationException();
            }

            @Override
            public boolean removeExact(E element) {
                throw exception();
            }

            @Override
            public boolean removeAt(Point point) {
                throw exception();
            }

            @Override
            public List<E> all() {
                throw exception();
            }

            @Override
            public Stream<E> stream() {
                throw exception();
            }

            @Override
            public void removeNotSame(List<E> valid) {
                throw exception();
            }

            @Override
            public void add(E element) {
                list.add(element);
            }

            @Override
            public int size() {
                return list.size();
            }

            @Override
            public void clear() {
                throw exception();
            }

            @Override
            public void removeIn(List<? extends Point> points) {
                throw exception();
            }

            @Override
            public void addAll(List<E> elements) {
                throw exception();
            }

            @Override
            public List<E> getAt(Point point) {
                throw exception();
            }

            @Override
            public List<E> copy() {
                throw exception();
            }

            @Override
            public void tick() {
                throw exception();
            }

            @Override
            public void remove(int from, int to) {
                list.subList(from, to).clear();
            }

            @Override
            public Iterator<E> iterator() {
                throw exception();
            }
        };
    }
}
