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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Contains {

    private Collection<Class<? extends Point>> keys;

    public Contains(Collection<Class<? extends Point>> keys) {
        this.keys = keys;
    }

    /**
     * @param filters Список классов, любой из которых достаточно чтобы был найден в ячейке.
     * @return Содержит ли заданная ячейка хоть один элемент типа
     *         заданного в списке классов filters.
     */
    @PerformanceOptimized
    public boolean anyOf(Class<? extends Point>... filters) {
        for (Class key : keys) {
            for (Class filter : filters) {
                if (key.equals(filter)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param filters Список классов, которых не должно быть в этой клетке.
     * @return true - если в ячейке нет ничего из filters.
     */
    @PerformanceOptimized
    public boolean noneOf(Class<? extends Point>... filters) {
        return !anyOf(filters);
    }

    /**
     * @param filters Список классов, все из которых должны быть найдены в ячейке.
     * @return true - если все, что указано в filters было найдено, при этом
     *       ячейка может содержать и элементы других типов.
     */
    @PerformanceOptimized
    public boolean allOf(Class<? extends Point>... filters) {
        for (Class filter : filters) {
            boolean contains = false;
            for (Class key : keys) {
                if (key.equals(filter)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) return false;
        }
        return true;
    }

    /**
     * @param filters Список классов, все из которых (и только они, больше
     *                никаких других) должны быть найдены в ячейке.
     * @return true - если все, что указано в filters было найдено, при этом
     *         ячейка должна содержать только указанные типы.
     */
    @PerformanceOptimized
    public boolean exactlyAllOf(Class<? extends Point>... filters) {
        List<Class<? extends Point>> all = new ArrayList<>(keys);
        for (Class filter : filters) {
            boolean contains = false;
            for (Class key : all) {
                if (key.equals(filter)) {
                    contains = true;
                    all.remove(key);
                    break;
                }
            }
            if (!contains) return false;
        }
        return all.isEmpty();
    }
}
