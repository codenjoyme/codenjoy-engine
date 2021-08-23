package com.codenjoy.dojo.services;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

public class PointField {

    private PointList[][] field;

    static class PointList {

        private Multimap<Class, Point> elements = ArrayListMultimap.create();

        public void add(Point element) {
            elements.put(element.getClass(), element);
        }

        public boolean contains(Class<?> filter) {
            return elements.containsKey(filter);
        }

        public void removeAll(Class<?> filter) {
            elements.removeAll(filter);
        }

        public void remove(Class<?> filter, Point element) {
            elements.remove(filter, element);
        }

        public <T> List<T> get(Class<T> filter) {
            ArrayList<T> result = new ArrayList<>();
            result.addAll((Collection) elements.get(filter));
            return result;
        }
    }

    public PointField(int size) {
        field = new PointList[size][];
        for (int x = 0; x < size; x++) {
            field[x] = new PointList[size];
            for (int y = 0; y < size; y++) {
                field[x][y] = new PointList();
            }
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

        point.onChange((from, to) -> {
            get(from).remove(point.getClass(), from); // TODO проверить что удаляется именно 1 элемент
            get(to).add(to);
        });
    }

    private PointList get(Point point) {
        if (point.isOutOf(size())) {
            return new PointList(); // TODO а точно тут так надо?
        }
        return field[point.getX()][point.getY()];
    }

    public interface Accessor<T> extends Iterable<T> {
        <E extends Point> boolean contains(E element);

        <E extends Point> void remove(E element);

        List<T> all();

        // TODO added stream method

        void removeNotIn(List<T> valid);

        void add(T element);

        int size();

        void clear();

        void removeIn(List<T> elements);

        void addAll(List<T> elements);
    }

    public <T extends Point> Accessor<T> of(Class<T> filter) {
        return new Accessor<>() {
            @Override
            public Iterator<T> iterator() {
                return all().iterator();
            }

            @Override
            public <E extends Point> boolean contains(E element) {
                return get(element).contains(filter);
            }

            @Override
            public <E extends Point> void remove(E element) { // TODO проверить что уделяется именно 1 элемент
                get(element).remove(filter, element);
            }

            @Override
            public List<T> all() {
                List<T> result = new LinkedList<>();
                for (int x = 0; x < PointField.this.size(); x++) {
                    for (int y = 0; y < PointField.this.size(); y++) {
                        List<T> elements = field[x][y].get(filter);
                        result.addAll(elements);
                    }
                }
                return result;
            }

            @Override
            public void removeNotIn(List<T> elements) {
                all().stream()
                        .filter(it -> !elements.contains(it))
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
                for (int x = 0; x < PointField.this.size(); x++) {
                    for (int y = 0; y < PointField.this.size(); y++) {
                        field[x][y].removeAll(filter);
                    }
                }
            }

            @Override
            public void removeIn(List<T> elements) {
                all().stream()
                        .filter(elements::contains)
                        .forEach(this::remove);
            }

            @Override
            public void addAll(List<T> elements) {
                elements.forEach(this::add);
            }
        };
    }
}