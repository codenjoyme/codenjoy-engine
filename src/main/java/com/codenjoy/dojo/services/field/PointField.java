package com.codenjoy.dojo.services.field;

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

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.printer.BoardReader;

import java.util.*;
import java.util.stream.Stream;

import static com.codenjoy.dojo.services.PointImpl.pt;
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

    public PointField(int size) {
        field = new PointList[size][];
        for (int x = 0; x < size; x++) {
            field[x] = new PointList[size];
        }
    }

    public BoardReader reader(Class<? extends Point>... classes) { // TODO test me
        return new BoardReader() {
            @Override
            public int size() {
                return PointField.this.size();
            }

            @Override
            public Iterable<?> elements(Object player) {
                return Arrays.stream(classes)
                        .flatMap(clazz -> PointField.this.of(clazz).stream())
                        .collect(toList());
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
            if (get(from).removeAllExact(point.getClass(), from)) {
                all.removeAllExact(point.getClass(), from);
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

    public <T extends Point> Accessor<T> of(Class<T> filter) {
        return new Accessor<>() {

            @Override
            public Iterator<T> iterator() {
                return (Iterator) all.get(filter).iterator();
            }

            @Override
            public <P extends Point> boolean contains(P element) {
                PointList list = get(element);
                return list.contains(filter);
            }

            @Override
            public <P extends Point> boolean removeExact(P element) {
                all.removeAllExact(filter, element);
                return get(element).removeAllExact(filter, element);
            }

            @Override
            public <P extends Point> boolean remove(P element) {
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
            public void removeNotSame(List<T> elements) {
                List<T> toRemove = stream()
                        .filter(it -> elements.stream()
                                .noneMatch(el -> el == it))
                        .collect(toList());
                toRemove.forEach(this::removeExact);
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
            public <P extends Point> void removeIn(List<P> elements) {
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