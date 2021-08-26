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
import com.codenjoy.dojo.services.annotations.PerformanceOptimized;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class MultimapMatrix<K, V> {

    private final Multimap<K, V>[][] field;

    public MultimapMatrix(int size) {
        field = new Multimap[size][];
        for (int x = 0; x < size; x++) {
            field[x] = new Multimap[size];
        }
    }

    public int size() {
        return field.length;
    }

    @PerformanceOptimized
    public Multimap<K, V> get(int x, int y) {
        if (Point.isOutOf(x, y, 0, 0, size())) {
            return new Multimap<>(); // TODO а точно тут так надо?
        }
        Multimap<K, V> map = field[x][y];
        if (map == null) {
            map = field[x][y] = new Multimap<>();
        }
        return map;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                Multimap<K, V> map = field[x][y];
                result.append(pt(x, y))
                        .append(":")
                        .append(map == null || map.isEmpty() ? "{}" : map.toString())
                        .append('\n');
            }
        }
        return result.toString();
    }

    @PerformanceOptimized
    public void clear(K key) {
        // TODO устранить дублирование с циклом выше
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                Multimap<K, V> map = field[x][y];
                if (map != null) {
                    map.removeKey(key);
                }
            }
        }
    }
}
