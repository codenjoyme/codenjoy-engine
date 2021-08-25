package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;

import java.util.List;
import java.util.stream.Stream;

public interface Accessor<E> extends Iterable<E> {

    /**
     * @param element Любой элемент типа Point, у которого будут взяты только координаты.
     * @return true - если заданного типа элемент содержится в этой клетке.
     */
    <E2 extends Point> boolean contains(E2 element);

    /**
     * Удаляются все вхождения (дубликаты) этого элемента. Проверка осуществляется
     * не по координатам, но по ссылке на объект.
     *
     * @param element Удаляемый элемент.
     * @return true - если было удаление.
     */
    <E2 extends Point> boolean removeExact(E2 element); // TODO test me

    /**
     * Удаляются первый найденный по координатам элемент.
     *
     * @param element Координаты удаляемого элемента.
     * @return true - если было удаление.
     */
    <E2 extends Point> boolean remove(E2 element); // TODO test me

    List<E> all(); // TODO test me

    Stream<E> stream(); // TODO test me

    /**
     * Удаляет все невалидные объекты, не содержащиеся в заданном списке.
     *
     * @param valid валидные объекты, которые должны остаться.
     */
    void removeNotSame(List<E> valid); // TODO test me

    void add(E element); // TODO test me

    int size(); // TODO test me

    void clear(); // TODO test me

    <E2 extends Point> void removeIn(List<E2> elements); // TODO test me

    void addAll(List<E> elements); // TODO test me

    <E2 extends Point> List<E> getAt(E2 point); // TODO test me

    List<E> copy(); // TODO test me

    void tick(); // TODO test me

    void removeAny(); // TODO test me
}
