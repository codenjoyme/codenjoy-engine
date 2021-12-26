package com.codenjoy.dojo.services.field;

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
    public boolean any(Class<? extends Point>... filters) {
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
     * @param filters Список классов, все из которых необходимо чтобы были найдены в ячейке.
     * @return Содержит ли заданная ячейка все элементы типы которых
     *         заданы в списке классов filters. Ячейка может содержать и элементы других типов.
     */
    @PerformanceOptimized
    public boolean all(Class<? extends Point>... filters) {
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
     * @param filters Список классов, все (и только они) из которых необходимо
     *                чтобы были найдены в ячейке.
     * @return Содержит ли заданная ячейка все элементы типы которых
     *         заданы в списке классов filters. Ячейка должна содержать только указанные типы.
     */
    @PerformanceOptimized
    public boolean exact(Class<? extends Point>... filters) {
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
