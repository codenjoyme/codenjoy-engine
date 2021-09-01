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
import com.codenjoy.dojo.services.Tickable;

import java.util.List;
import java.util.stream.Stream;

/**
 * С помощью этого интерфейса мы работаем с элементами на поле определенного типа.
 * @param <E> Тип выбранного объекта.
 */
public interface Accessor<E> extends Iterable<E>, Tickable {

    /**
     * @param element Любой элемент типа Point, у которого
     * будут взяты только координаты.
     * @return True - если есть зоть один элемент выбранного
     * в Accessor типа в клетке с заданными координатами.
     */
    <E2 extends Point> boolean contains(E2 element);

    /**
     * Удаляются все вхождения (дубликаты) этого элемента.
     * Проверка осуществляется не по координатам, но по
     * ссылке на объект. Удаление производится во всех
     * клетках поля, где будет найден этот объект,
     * независимо от выбранного в Accessor типа.
     *
     * @param element Удаляемый элемент.
     * @return true - если было удаление.
     */
    <E2 extends Point> boolean removeExact(E2 element); // TODO test me

    /**
     * Удаляюеся первый найденный в конкретной клетке поля (по координатам) элемент.
     *
     * @param element Координаты удаляемого элемента.
     * @return true - если было удаление.
     */
    <E2 extends Point> boolean remove(E2 element); // TODO test me

    /**
     * @return Возвращает всю коллекцию в неизменяемом виде.
     * Важно понимать что хоть изменение самой коллекции вызовет исключение,
     * то изменение координат элементов коллекции приведет к изменению содержимого
     * всего контейнера (при этом он останется консистентным, т.к. умеет
     * обрабатывать подобные случаи).
     */
    List<E> all();

    /**
     * @return Stream над всеми элементами коллекции.
     * Изменение координат элементов коллекции приведет к изменению содержимого
     * всего контейнера (при этом он останется консистентным, т.к. умеет
     * обрабатывать подобные случаи).
     */
    Stream<E> stream(); // TODO test me

    /**
     * Удаляет все невалидные объекты, не содержащиеся в заданном списке.
     * Проверка осуществляется не по координгатам, а по ссылкам на объект.
     * Все что не находится в этом списке будет удалено со всех ячеек поля.
     *
     * @param valid валидные объекты, которые должны остаться на поле.
     */
    void removeNotSame(List<E> valid); // TODO test me

    /**
     * Элемент добавится в конкретную ячейку поля в соответствии с его координатами.
     *
     * @param element Добавляемый в коллекцию элемент.
     */
    void add(E element);

    /**
     * @return Количество элементов в этой коллекции.
     */
    int size(); // TODO test me

    /**
     * Очищает все элементы этой коллекции.
     */
    void clear(); // TODO test me

    /**
     * Удаляются первые найденные по координатам (из заданного списка) элементы.
     * То есть в каждой клеточке, указанной в elements будет удален первый элемент
     * из коллекции.
     *
     * @param elements Координаты ячеек в которых будет проводится удаление.
     */
    <E2 extends Point> void removeIn(List<E2> elements); // TODO test me

    /**
     * Каждый элемент исходного списка добавится
     * в конкретную ячейку поля в соответствии с его координатами.
     *
     * @param elements Добавляемые элементы.
     */
    void addAll(List<E> elements); // TODO test me

    /**
     * @param point Координата ячейки которой интересуемся.
     * @return Копия коллекции всех элементов в заданной ячейке.
     * Важно понимать, что хоть изменение самой коллекции не
     * приведет к изменению содержимого контейнера, то изменение
     * координат элементов коллекции приведет к такому изменению
     * (при этом он останется консистентным, т.к. умеет
     * обрабатывать подобные случаи).
     */
    <E2 extends Point> List<E> getAt(E2 point); // TODO test me

    /**
     * @return Копия коллекции всех элементов, изменение которой
     * не приведет к изменению содержимого всего контейнера.
     */
    List<E> copy();

    /**
     * Возможность тикнуть каждый элемент коллекции, если у него есть метод tick.
     */
    @Override
    void tick(); // TODO test me

    /**
     * Удаление элементов из диапазона (в любой клетке).
     * @param from Начальный индекс (включительно).
     * @param from Конечный индекс (не включается).
     */
    void remove(int from, int to); // TODO test me
}
