package com.codenjoy.dojo.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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

import com.codenjoy.dojo.services.printer.BoardReader;

import java.util.*;
import java.util.stream.Stream;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/**
 * Квадратное поле заданного размера. В каждой ячейке которого содержится
 * список из нескольких корзинок для объектов заданного типа (определяется по классу
 * объекта-наследника PointImpl). Так же существует отдельный список корзинок
 * для всех объектов независимо от их координат - сделано для оптимизации производительности
 * при работе со всеми данными. Важно держать этих два контейнера в консистентном состоянии.
 * Класс умеет реагировать на изменениие координаты объекта за пределами хранилища
 * - он разместит объект в новом месте, предварительно удалив его из старого
 * (я очень надеюсь на это). Пожелания принимаются, если получится сделать более
 * производительную реализацию этого контракта - буду признателен.
 */
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

        public List<Point> all() { // TODO test me
            return map.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(toList());
        }

        @Override
        public String toString() {
            return map.entrySet().stream()
                    .collect(toMap(entry -> "\n\t" + entry.getKey().getSimpleName() + ".class",
                            Map.Entry::getValue))
                    .entrySet().stream()
                    .sorted(comparing(Map.Entry::getKey))
                    .map(entry -> String.format("{%s=[\n\t\t%s]}",
                            entry.getKey(),
                            entry.getValue().stream()
                                    .map(Object::toString)
                                    .collect(joining("\n\t\t"))))
                    .collect(joining("\n\t"))
                    .replace("\t", "        ");
        }

        public boolean isEmpty() {
            return map.entrySet().stream()
                    .allMatch(entry -> entry.getValue().isEmpty());
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

        public boolean isEmpty() {
            return map.isEmpty();
        }
    }

    public PointField(int size) {
        field = new PointList[size][];
        for (int x = 0; x < size; x++) {
            field[x] = new PointList[size];
        }
    }

    public BoardReader reader() { // TODO test me
        return new BoardReader() {

            @Override
            public int size() {
                return PointField.this.size();
            }

            @Override
            public Iterable<? extends Point> elements(Object player) {
                return PointField.this.all.all();
            }
        };
    }

    /**
     * @return Размер поля.
     */
    public int size() {
        return field.length;
    }

    /**
     * Добавляет все элементы из списка каждый в корзинку своего типа
     * (тип будет извлечен из класса передаваемого объекта).
     * @param elements Элементы к добавлению.
     */
    public void addAll(List<? extends Point> elements) {
        elements.forEach(this::add);
    }

    /**
     * Добавляет текущий элемент в кординку его типа (тип будет извлечен из
     * класса передаваемого объекта).
     * @param point Добавляемые элемент.
     */
    public void add(Point point) {
        get(point).add(point);
        all.get(point.getClass()).add(point);

        point.beforeChange(from -> {
            // TODO проверить что удаляется именно 1 элемент
            if (get(from).remove(point.getClass(), from)) {
                all.get(point.getClass()).remove(from);
            }
        });

        point.onChange((from, to) -> {
            get(to).add(to);
            all.get(point.getClass()).add(to);
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

        /**
         * @param element Любой элемент типа Point, у которого будут взяты только координаты.
         * @return true - если заданного типа элемент содержится в этой клетке.
         */
        <E extends Point> boolean contains(E element);

        <E extends Point> boolean remove(E element);

        List<T> all(); // TODO test me

        Stream<T> stream(); // TODO test me

        void removeNotIn(List<? extends Point> valid); // TODO test me

        void add(T element); // TODO test me

        int size(); // TODO test me

        void clear(); // TODO test me

        void removeIn(List<? extends Point> elements); // TODO test me

        void addAll(List<T> elements); // TODO test me

        List<T> getAt(Point point); // TODO test me

        List<T> copy(); // TODO test me

        void tick(); // TODO test me

        void remove(int index); // TODO test me
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
                List<T> toRemove = stream().filter(it -> !elements.contains(it))
                        .collect(toList());
                toRemove.forEach(this::remove);
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

            @Override
            public List<T> copy() {
                return new ArrayList<>(all());
            }

            @Override
            public void tick() {
                copy().stream()
                        .filter(it -> it instanceof Tickable)
                        .forEach(it -> ((Tickable)it).tick());
            }

            @Override
            public void remove(int index) {
                Iterator<Point> iterator = all.get(filter).iterator();
                if (iterator.hasNext()) {
                    remove(iterator.next());
                }
            }
        };
    }

    public String toString() {
        return String.format("[map=%s]\n\n[field=%s]",
                all.toString(), toString(field));
    }

    private String toString(PointList[][] field) {
        StringBuilder result = new StringBuilder();
        int size = PointField.this.size();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                PointList list = field[x][y];
                result.append(pt(x, y))
                        .append(":")
                        .append(list == null || list.isEmpty() ? "{}" : list.toString())
                        .append('\n');
            }
        }
        return result.toString();
    }
}