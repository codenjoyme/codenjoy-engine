package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;

import java.util.List;

class PointList {

    private Multimap map = new Multimap();

    public void add(Point element) {
        List list = map.get(element.getClass());
        list.add(element);
    }

    public boolean contains(Class<?> filter) {
        List<Point> list = map.getOnly(filter);
        return list != null && !list.isEmpty();
    }

    public void removeAll(Class<?> filter) {
        map.remove(filter);
    }

    public boolean removeAllExact(Class<?> filter, Point element) {
        List<Point> list = map.getOnly(filter);
        return Utils.removeAllExact(list, element);
    }

    public boolean remove(Class<?> filter, Point element) {
        List<Point> list = map.getOnly(filter);
        return list != null && list.remove(element);
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
