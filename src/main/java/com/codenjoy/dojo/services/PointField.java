package com.codenjoy.dojo.services;

import java.util.*;
import java.util.stream.Stream;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.toMap;

public class PointField {

    private PointList[][] field;
    private Multimap all = new Multimap();

    static class Multimap {

        private Map<Class, List<Point>> map = new LinkedHashMap<>();

        public List<Point> get(Class<?> key) {
            return map.computeIfAbsent(key, k -> new LinkedList<>());
        }

        public List<Point> getOnly(Class<?> key) {
            return map.get(key);
        }

        public void remove(Class<?> key) {
            map.remove(key);
        }

        @Override
        public String toString() {
            return map.entrySet().stream()
                    .collect(toMap(entry -> entry.getKey().getSimpleName() + ".class",
                            entry -> entry.getValue().toString()))
                    .toString();
        }
    }

    static class PointList {

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
    }

    public PointField(int size) {
        field = new PointList[size][];
        for (int x = 0; x < size; x++) {
            field[x] = new PointList[size];
        }
    }

    public int size() {
        return field.length;
    }

    public void addAll(List<? extends Point> elements) {
        elements.forEach(this::add);
    }

    public void add(Point point) {
        get(point).add(point);
        all.get(point.getClass()).add(point);

        point.onChange((from, to) -> {
            // TODO проверить что удаляется именно 1 элемент
            if (get(from).remove(point.getClass(), from)) {
                get(to).add(to);

                all.get(point.getClass()).remove(from);
                all.get(point.getClass()).add(to);
            }
        });
    }

    private PointList get(Point pt) {
        if (pt.isOutOf(size())) {
            return new PointList(); // TODO а точно тут так надо?
        }
        return get(pt.getX(), pt.getY());
    }

    private PointList get(int x, int y) {
        PointList list = field[x][y];
        if (list == null) {
            list = field[x][y] = new PointList();
        }
        return list;
    }

    public interface Accessor<T> extends Iterable<T> {
        <E extends Point> boolean contains(E element);

        <E extends Point> boolean remove(E element);

        List<T> all();

        Stream<T> stream();

        void removeNotIn(List<? extends Point> valid);

        void add(T element);

        int size();

        void clear();

        void removeIn(List<? extends Point> elements);

        void addAll(List<T> elements);

        List<T> getAt(Point point);
    }

    public <T extends Point> Accessor<T> of(Class<T> filter) {
        return new Accessor<>() {
            @Override
            public Iterator<T> iterator() {
                return (Iterator) all.get(filter).iterator();
            }

            @Override
            public <E extends Point> boolean contains(E element) {
                PointList list = get(element);
                return list.contains(filter);
            }

            @Override
            public <E extends Point> boolean remove(E element) { // TODO проверить что уделяется именно 1 элемент
                all.get(filter).remove(element);
                return get(element).remove(filter, element);
            }

            @Override
            public List<T> all() {
                return (List) all.get(filter);
            }

            @Override
            public Stream<T> stream() {
                return all().stream();
            }

            @Override
            public void removeNotIn(List<? extends Point> elements) {
                stream().filter(it -> !elements.contains(it))
                        .forEach(this::remove);
            }

            @Override
            public void add(T element) {
                PointField.this.add(element);
            }

            @Override
            public int size() {
                return all().size();
            }

            @Override
            public void clear() {
                // TODO устранить дублирование с циклом выше
                int size = PointField.this.size();
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        get(x, y).removeAll(filter);
                    }
                }
                all.get(filter).clear();
            }

            @Override
            public void removeIn(List<? extends Point> elements) {
                elements.forEach(this::remove);
            }

            @Override
            public void addAll(List<T> elements) {
                elements.forEach(this::add);
            }

            @Override
            public List<T> getAt(Point point) {
                return (List) get(point).get(filter);
            }
        };
    }

    public String toString() {
        return String.format("[map=%s]\n[field=%s]",
                all, toString(field));
    }

    private String toString(PointList[][] field) {
        StringBuilder result = new StringBuilder();
        int size = PointField.this.size();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                result.append(pt(x, y))
                        .append(":")
                        .append(field[x][y])
                        .append('\n');
            }
        }
        return result.toString();
    }
}