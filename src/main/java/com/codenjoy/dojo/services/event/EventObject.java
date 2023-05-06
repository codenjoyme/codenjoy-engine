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

import java.util.Objects;

public interface EventObject<T, V> {

    T type();

    default V value() {
        return (V) this;
    }

    default boolean _equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventObject events = (EventObject) o;
        return type() == events.type() &&
                value() == events.value();
    }

    default int _hashCode() {
        return Objects.hash(type());
    }

    default String _toString() {
        if (value() instanceof Integer && (int)value() == 0) {
            return type().toString();
        }
        return String.format("%s(%s)", type(), value());
    }
}