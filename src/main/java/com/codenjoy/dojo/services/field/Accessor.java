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
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * С помощью этого интерфейса мы работаем с элементами на поле определенного типа.
 * @param <E> Тип выбранного объекта.
 */
public interface Accessor<E> extends Iterable<E>, Tickable {

    /**
     * @param point Любой элемент типа Point, у которого
     * будут взяты только координаты.
     *
     * @return True - если в коллекции есть хоть один элемент (выбранного
     * в Accessor типа) в ячейке с заданными в point координатами.
     */
    boolean contains(Point point);

    /**
     * Удаляются все вхождения (дубликаты) этого элемента.
     * Проверка осуществляется не по координатам, но по
     * ссылке на объект. Удаление производится во всех
     * ячейках поля, где будет найден этот объект,
     * в соответствии с выбранным в Accessor типом.
     *
     * @param element Удаляемый элемент.
     * @return true - если было удаление.
     */
    boolean removeExact(E element);

    /**
     * Удаляются все найденные (по выбранному в Accessor типу)
     * в конкретной ячейке поля элементы.
     * Поиск клетки осуществляется по координатам point.
     *
     * @param point Координаты удаляемого элемента.
     * @return true - если было удаление.
     */
    boolean removeAt(Point point);

    /**
     * @return Возвращает все найденные (по выбранному в Accessor типу)
     * элементы во всех ячейках поля в виде неизменяемого списка.
     * Важно понимать, что хоть изменение самого списка вызовет исключение,
     * то изменение координат элементов списка приведет к изменению содержимого
     * всего контейнера (при этом он останется консистентным, т.к. умеет
     * обрабатывать подобные случаи).
     */
    List<E> all();

    /**
     * @return Stream над всеми найденными элементами (по выбранному в Accessor типу)
     * во всех ячейках поля.
     *
     * Изменение координат элементов полученных в ходе отработки stream
     * приведет к изменению содержимого всего контейнера (при этом он
     * останется консистентным, т.к. умеет обрабатывать подобные случаи).
     */
    Stream<E> stream();

    /**
     * Удаляет все невалидные объекты (выбранного в Accessor типа)
     * не содержащиеся в заданном списке.
     *
     * Проверка осуществляется не по координатам, а по ссылкам на объект.
     *
     * Все что не находится в этом списке будет удалено со всех ячеек поля
     * но только среди объектов в соответствии с выбранным в Accessor типом.
     *
     * @param valid валидные объекты, которые должны остаться на поле.
     */
    void removeNotSame(List<E> valid);

    /**
     * Элемент добавится в конкретную ячейку поля в соответствии
     * с его координатами. Тип добавляемого элемента будет
     * выбран в соответствии с типом элемента
     * (игнорируя выбранный в Accessor тип).
     *
     * @param element Добавляемый в коллекцию элемент.
     */
    void add(E element);

    /**
     * @return Количество элементов (выбранного
     * в Accessor типа) во всех ячейках поля.
     */
    int size();

    /**
     * Очищает все элементы (выбранного
     * в Accessor типа) во всех ячейках поля.
     */
    void clear();

    /**
     * Удаляются все найденные по координатам (из заданного списка) элементы
     * (выбранного в Accessor типа).
     * То есть в каждой клеточке, указанной в points будет удалены все элементы
     * выбранного в Accessor типа.
     *
     * @param points Координаты ячеек в которых будет проводится удаление.
     */
    void removeIn(List<? extends Point> points);

    /**
     * Каждый элемент исходного списка добавится в конкретную ячейку
     * поля в соответствии с его координатами. Тип добавляемого элемента
     * будет выбран в соответствии с типом элемента
     * (игнорируя выбранный в Accessor тип).
     *
     * @param elements Добавляемые элементы.
     */
    void addAll(List<E> elements);

    /**
     * @param point Координата ячейки которой интересуемся.
     *
     * @return Копия списка всех элементов в заданной ячейке поля
     * (выбранного в Accessor типа).
     *
     * Важно понимать, что хоть изменение самого списка не
     * приведет к изменению содержимого контейнера, то изменение
     * координат элементов списка приведет к такому изменению
     * (при этом он останется консистентным, т.к. умеет
     * обрабатывать подобные случаи).
     */
    List<E> getAt(Point point);

    /**
     * @param point Координата ячейки которой интересуемся.
     *
     * @return Один единственный элемент (первый если их там несколько)
     * в заданной ячейке поля (выбранного в Accessor типа).
     *
     * Если в ячейке ничего нет - null вернется.
     */
    E getFirstAt(Point point);

    /**
     * @return Копия списка всех элементов во всех ячейках поля
     * (выбранного в Accessor типа), изменение которого
     * не приведет к изменению содержимого всего контейнера.
     */
    List<E> copy();

    /**
     * Возможность тикнуть каждый элемент (выбранного в Accessor типа),
     * если у него есть метод tick. При этом в тике может быть изменено
     * содержимое всего контейнера, при этом не будет
     * ConcurrentModificationException.
     */
    @Override
    void tick();

    /**
     * Удаление некоторого числа элементов во всех ячейках поля
     * (выбранного в Accessor типа).
     *
     * @param from Начальный индекс (включительно).
     * @param from Конечный индекс (не включается).
     * @throws IndexOutOfBoundsException for an illegal endpoint index value
     *         ({@code from < 0 || to > size ||
     *         from > to})
     */
    void remove(int from, int to);

    /**
     * Метод-синтаксический сахар над this.stream().filter().collect(toList()).
     * @param filter Фильтр для извлечения коллекции.
     * @return Отфильтрованная коллекция.
     */
    List<E> filter(Predicate<E> filter);

    String toString();
}
