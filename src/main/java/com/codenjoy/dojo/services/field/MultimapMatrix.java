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

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.annotations.PerformanceOptimized;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class MultimapMatrix<K, V> {

    public static final boolean CREATE_POINT = true;

    private int size;
    private Multimap<K, V>[][] field;

    public MultimapMatrix(int size) {
        this.size = size;
        clearAll();
    }

    private void clearAll() {
        field = new Multimap[size][];
        for (int x = 0; x < size; x++) {
            field[x] = new Multimap[size];
        }
    }

    public int size() {
        return size;
    }

    @PerformanceOptimized
    public Multimap<K, V> get(int x, int y) {
        if (Point.isOutOf(x, y, size)) {
            return new Multimap<>(); // TODO а точно тут так надо?
        }
        Multimap<K, V> map = field[x][y];
        if (map == null) {
            map = field[x][y] = new Multimap<>();
        }
        return map;
    }

    public void forEach(boolean createPoint, BiConsumer<Point, Multimap<K, V>> consumer) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                consumer.accept(
                        createPoint ? pt(x, y) : null,
                        field[x][y]);
            }
        }
    }

    public void forEach(Consumer<Multimap<K, V>> consumer) {
        forEach(!CREATE_POINT,
                (pt, map) -> {
                    if (map != null) {
                        consumer.accept(map);
                    }
                });
    }

    public List<Point> pointsMatch(Predicate<List<V>> filter) {
        List<Point> result = new LinkedList<>();
        forEach(CREATE_POINT,
                (pt, map) -> {
                    List<V> list = (map == null) ? null : map.allValues();
                    if (filter.test(list)) {
                        result.add(pt);
                    }
                });
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        forEach(CREATE_POINT,
                (pt, map) -> result.append(pt)
                        .append(":")
                        .append(map == null || map.isEmpty() ? "{}" : map.toString())
                        .append('\n'));
        return result.toString();
    }

    @PerformanceOptimized
    public void clear(K key) {
        forEach(map -> map.removeKey(key));
    }

    public void remove(K key, Predicate<V> predicate) {
        forEach(map -> map.remove(key, predicate));
    }
}