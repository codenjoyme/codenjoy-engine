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

public class DoubleValueEvent<T, V1, V2> extends SingleValueEvent<T, V1> implements EventObject<T, V1> {

    private V2 value2;

    public DoubleValueEvent(T type) {
        super(type);
    }

    public DoubleValueEvent(T type, V1 value) {
        super(type, value);
    }

    public DoubleValueEvent(T type, V1 value1, V2 value2) {
        super(type, value1);
        this.value2 = value2;
    }

    public V2 value2() {
        return value2;
    }

    @Override
    public boolean multiValue() {
        return true;
    }

    @Override
    public String toString() {
        return (value2 == null)
                ? _toString()
                : String.format("%s(%s, %s)", type(), value(), value2());
    }

    @Override
    public int hashCode() {
        return _hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return _equals(object)
                && ((DoubleValueEvent) object).value2() == value2();
    }
}