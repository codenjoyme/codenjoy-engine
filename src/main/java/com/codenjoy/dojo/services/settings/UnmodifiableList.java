package com.codenjoy.dojo.services.settings;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
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

import java.util.*;

/**
 * Класс обертка дает возможность увидеть коллекцию под видом немодифицируемого списка
 * с несколько ограниченным набором методов.
 */
public class UnmodifiableList<T> extends AbstractList<T> implements List<T> {

    private Collection<T> collection;

    public UnmodifiableList(Collection<T> collection) {
        this.collection = collection;
    }

    @Override
    public T get(int index) {
        return iterator(index).next();
    }

    private Iterator<T> iterator(int index) {
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (index == 0) {
                return iterator;
            }
            iterator.next();
            index--;
        }
        throw new IndexOutOfBoundsException(index);
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        int[] ii = {index};
        Iterator<T> i = iterator(index);
        return new ListIterator<>() {
            public boolean hasNext()     {return i.hasNext();}
            public T next()              {ii[0]++; return i.next();}
            public boolean hasPrevious() {throw new UnsupportedOperationException();}
            public T previous()          {throw new UnsupportedOperationException();}
            public int nextIndex()       {return ii[0] + 1;}
            public int previousIndex()   {return ii[0] - 1;}
            public void remove() {throw new UnsupportedOperationException();}
            public void set(T e) {throw new UnsupportedOperationException();}
            public void add(T e) {throw new UnsupportedOperationException();}
        };
    }

    @Override
    public int size() {
        return collection.size();
    }
}
