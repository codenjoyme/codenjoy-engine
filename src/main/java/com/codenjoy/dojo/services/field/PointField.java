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
import java.util.function.Function;
import java.util.stream.Stream;

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

    private final MultimapMatrix<Class<? extends Point>, Point> field;
    private final Multimap<Class<? extends Point>, Point> all;

    public PointField(int size) {
        field = new MultimapMatrix<>(size);
        all = new Multimap<>();
    }

    /**
     * @param classes Порядок отрисовки типов элементов.
     * @return BoardReader для отрисовки элементов на поле в заданном порядке.
     */
    public BoardReader<?> reader(Class<? extends Point>... classes) { // TODO test me
        return new BoardReader<>() {
            @Override
            public int size() {
                return PointField.this.size();
            }

            @Override
            public List<Class<? extends Point>> order() {
                return Arrays.asList(classes);
            }

            @Override
            public PointField elements(Object player) {
                return PointField.this;
            }
        };
    }

    /**
     * @return Размер квадратного поля.
     */
    public int size() {
        return field.size();
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
     * Метод помогает работать в связке с двумя контейнерами данных.
     * Если выполнение операции на первом вернул false, со вторым (из за
     * опатимизаций производительности) операция не будет осуществляться.
     * @param point Координата с которой работаем.
     * @param function Осуществляемая операция.
     * @return Результат выполнения операции на первом контейнере.
     */
    private boolean with(Point point, Function<Multimap<Class<? extends Point>, Point>, Boolean> function) {
        boolean result = function.apply(get(point));
        if (result) {
            function.apply(all);
        }
        return result;
    }

    /**
     * Добавляет текущий элемент в кординку его типа (тип будет извлечен из
     * класса передаваемого объекта).
     * @param pt Добавляемые элемент.
     */
    public void add(Point pt) {
        with(pt, map -> map.get(pt.getClass()).add(pt));
        pt.beforeChange(from ->
                with(from, map -> map.removeAllExact(pt.getClass(), from)));
        pt.onChange((from, to) ->
                with(to, map -> map.get(pt.getClass()).add(to)));
    }

    public Multimap<Class<? extends Point>, Point> getOnly(int x, int y) {
        return field.getOnly(x, y);
    }

    private Multimap<Class<? extends Point>, Point> get(Point pt) {
        return field.get(pt.getX(), pt.getY());
    }

    /**
     * @param filter Класс объекта коллекцию которых в этой клетке хотим получить.
     * @param <E> Тип объекта наследуемого от PointImpl.
     * @return Удобный интерфейс для работы с коллекцией выбранных элементов.
     */
    public <E extends Point> Accessor<E> of(Class<E> filter) {
        return new Accessor<>() {

            @Override
            public Iterator<E> iterator() {
                return (Iterator) all.get(filter).iterator();
            }

            @Override
            public <E2 extends Point> boolean contains(E2 element) {
                return get(element).contains(filter);
            }

            @Override
            public <E2 extends Point> boolean removeExact(E2 element) {
                return with(element, map -> map.removeAllExact(filter, element));
            }

            @Override
            public <E2 extends Point> boolean remove(E2 element) {
                return with(element, map -> map.remove(filter, element));
            }

            @Override
            public List<E> all() {
                return (List) all.get(filter);
            }

            @Override
            public Stream<E> stream() {
                return all().stream();
            }

            @Override
            public void removeNotSame(List<E> elements) {
                List<E> toRemove = stream()
                        .filter(it -> elements.stream()
                                .noneMatch(el -> el == it))
                        .collect(toList());
                toRemove.forEach(this::removeExact);
            }

            @Override
            public void add(E element) {
                PointField.this.add(element);
            }

            @Override
            public int size() {
                return all().size();
            }

            @Override
            public void clear() {
                field.clear(filter);
                all.clear(filter);
            }

            @Override
            public <E2 extends Point> void removeIn(List<E2> elements) {
                elements.forEach(this::remove);
            }

            @Override
            public void addAll(List<E> elements) {
                elements.forEach(this::add);
            }

            @Override
            public <E2 extends Point> List<E> getAt(E2 point) {
                return (List) get(point).get(filter);
            }

            @Override
            public List<E> copy() {
                return new ArrayList<>(all());
            }

            @Override
            public void tick() {
                copy().stream()
                        .filter(it -> it instanceof Tickable)
                        .forEach(it -> ((Tickable)it).tick());
            }

            @Override
            public void removeAny() {
                Iterator<Point> iterator = all.get(filter).iterator();
                if (iterator.hasNext()) {
                    remove(iterator.next());
                }
            }
        };
    }

    public String toString() {
        return String.format("[map=%s]\n\n[field=%s]",
                all.toString(), field.toString());
    }

    public List<Point> all() {
        return all.all();
    }
}