package com.codenjoy.dojo.services.event;

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

public class SingleValueEvent<T, V> implements EventObject<T, V> {

    private T type;
    private V value;

    public SingleValueEvent(T type) {
        this.type = type;
        this.value = null;
    }

    public SingleValueEvent(T type, V value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public T type() {
        return type;
    }

    @Override
    public String toString() {
        return _toString();
    }

    @Override
    public int hashCode() {
        return _hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return _equals(object);
    }
}