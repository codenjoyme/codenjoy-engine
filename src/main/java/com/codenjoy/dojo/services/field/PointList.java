package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;

import java.util.List;

public class PointList {

    private Multimap<Class<?>, Point> map = new Multimap<>();

    public void add(Point element) {
        map.get(element.getClass()).add(element);
    }

    public boolean contains(Class<?> filter) {
        return map.ifPresent(filter, false,
                list -> !list.isEmpty());
    }

    public void removeAll(Class<?> filter) {
        map.removeKey(filter);
    }

    public boolean removeAllExact(Class<?> filter, Point element) {
        return map.removeAllExact(filter, element);
    }

    public boolean remove(Class<?> filter, Point element) {
        return map.ifPresent(filter, false,
                list -> list.remove(element));
    }

    public List<Point> get(Class<?> filter) {
        return map.get(filter);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
