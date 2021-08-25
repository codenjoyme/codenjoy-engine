package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;

import java.util.List;
import java.util.stream.Stream;

public interface Accessor<T> extends Iterable<T> {

    /**
     * @param element Любой элемент типа Point, у которого будут взяты только координаты.
     * @return true - если заданного типа элемент содержится в этой клетке.
     */
    <P extends Point> boolean contains(P element);

    /**
     * Удаляются все вхождения (дубликаты) этого элемента. Проверка осуществляется
     * не по координатам, но по ссылке на объект.
     *
     * @param element Удаляемый элемент.
     * @return true - если было удаление.
     */
    <P extends Point> boolean removeExact(P element); // TODO test me

    /**
     * Удаляются первый найденный по координатам элемент.
     *
     * @param element Координаты удаляемого элемента.
     * @return true - если было удаление.
     */
    <P extends Point> boolean remove(P element); // TODO test me

    List<T> all(); // TODO test me

    Stream<T> stream(); // TODO test me

    /**
     * Удаляет все невалидные объекты, не содержащиеся в заданном списке.
     *
     * @param valid валидные объекты, которые должны остаться.
     */
    void removeNotSame(List<T> valid); // TODO test me

    void add(T element); // TODO test me

    int size(); // TODO test me

    void clear(); // TODO test me

    <P extends Point> void removeIn(List<P> elements); // TODO test me

    void addAll(List<T> elements); // TODO test me

    <P extends Point> List<T> getAt(P point); // TODO test me

    List<T> copy(); // TODO test me

    void tick(); // TODO test me

    void remove(int index); // TODO test me
}
