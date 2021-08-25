package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;

import java.util.List;
import java.util.stream.Stream;

/**
 * С помощью этого интерфейса мы работаем с элементами на поле определенного типа.
 * @param <E> Тип выбранного объекта.
 */
public interface Accessor<E> extends Iterable<E> {

    /**
     * @param element Любой элемент типа Point, у которого будут взяты только координаты.
     * @return True - если элемент содержится в клетке с заданными координатами.
     */
    <E2 extends Point> boolean contains(E2 element);

    /**
     * Удаляются все вхождения (дубликаты) этого элемента. Проверка осуществляется
     * не по координатам, но по ссылке на объект. Удаление производится во всех
     * клетках поля, где будет найден этот объект.
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
     * @return Возвращает всю коллекцию.
     */
    List<E> all(); // TODO test me

    /**
     * @return Stream над всеми элементами коллекции.
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
    void add(E element); // TODO test me

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
     * @return Коллекция всех элементов в заданной ячейке.
     */
    <E2 extends Point> List<E> getAt(E2 point); // TODO test me

    /**
     * @return Копия коллекции всех элементов.
     */
    List<E> copy(); // TODO test me

    /**
     * Возможность тикнуть каждый элемент коллекции, если у него есть метод tick.
     */
    void tick(); // TODO test me

    /**
     * Удаление первого попавшегося элемента списка (в любой клетке).
     */
    void removeAny(); // TODO test me
}
