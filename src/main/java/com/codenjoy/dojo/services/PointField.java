package com.codenjoy.dojo.services;

import java.util.*;
import java.util.stream.Stream;

public class PointField {

    private PointList[][] field;

    static class PointList {

        private Map<Class, List<Point>> elements = new LinkedHashMap<>();

        public void add(Point element) {
            List list = get(element.getClass());
            list.add(element);
        }

        public boolean contains(Class<?> filter) {
            List<Point> list = elements.get(filter);
            return list != null && !list.isEmpty();
        }

        public void removeAll(Class<?> filter) {
            elements.remove(filter);
        }

        public boolean remove(Class<?> filter, Point element) {
            List<Point> list = elements.get(filter);
            return list != null && list.remove(element);
        }

        public <T> List<T> get(Class<T> filter) {
            List<Point> list = elements.get(filter);
            if (list == null) {
                elements.put(filter, list = new LinkedList<>());
            }
            return (List<T>) list;
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
            // TODO проверить что удаляется именно 1 элемент
            if (get(from).remove(point.getClass(), from)) {
                get(to).add(to);
            }
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

    public static class ListOfList<E> extends AbstractList<E> {

        private int size;
        private List<Collection> lists = new ArrayList<>(100);
        private int currentList;
        private Iterator<E> iterator;

        @Override
        public Iterator<E> iterator() {
            init();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    if (size == 0) {
                        return false;
                    }

                    while (iterator == null || !iterator.hasNext()) {
                        currentList++;
                        if (currentList == lists.size()) {
                            return false;
                        }
                        iterator = lists.get(currentList).iterator();
                    }

                    return true;
                }

                @Override
                public E next() {
                    return iterator.next();
                }
            };
        }

        private void init() {
            currentList = -1;
            iterator = null;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            size += c.size();
            lists.add(c);
            init();
            return true;
        }

        @Override
        public E get(int index) {
            throw new RuntimeException("Not implemented");
        }
    }

    public <T extends Point> Accessor<T> of(Class<T> filter) {
        return new Accessor<>() {
            @Override
            public Iterator<T> iterator() {
                return all().iterator();
            }

            @Override
            public <E extends Point> boolean contains(E element) {
                PointList list = get(element);
                return list.contains(filter);
            }

            @Override
            public <E extends Point> boolean remove(E element) { // TODO проверить что уделяется именно 1 элемент
                return get(element).remove(filter, element);
            }

            @Override
            public List<T> all() {
                List<T> result = new ListOfList<>();
                int size = PointField.this.size();
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        PointList list = field[x][y];
                        if (list.contains(filter)) {
                            List<T> elements = list.get(filter);
                            result.addAll(elements);
                        }
                    }
                }
                return result;
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
                        field[x][y].removeAll(filter);
                    }
                }
            }

            @Override
            public void removeIn(List<? extends Point> elements) {
                stream().filter(elements::contains)
                        .forEach(this::remove);
            }

            @Override
            public void addAll(List<T> elements) {
                elements.forEach(this::add);
            }

            @Override
            public List<T> getAt(Point point) {
                return get(point).get(filter);
            }
        };
    }
}