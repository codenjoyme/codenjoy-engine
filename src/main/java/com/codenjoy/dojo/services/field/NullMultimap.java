package com.codenjoy.dojo.services.field;

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

import com.codenjoy.dojo.services.Point;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class NullMultimap extends Multimap<Object, Object> {

    public static final Multimap INSTANCE = new NullMultimap();

    @Override
    public List get(Object key) {
        return Arrays.asList();
    }

    @Override
    public boolean contains(Object key) {
        return false;
    }

    @Override
    public boolean remove(Object key, Point pt) {
        return false;
    }

    @Override
    public boolean remove(Object key, Predicate predicate) {
        return false;
    }

    @Override
    public Object ifPresent(Object key, Object defaultValue, Function function) {
        return defaultValue;
    }

    @Override
    public void removeKey(Object key) {
        // do nothing
    }

    @Override
    public void forEach(Consumer action) {
        // do nothing
    }

    @Override
    public String toString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean removeAllExact(Object key, Object value) {
        return false;
    }

    @Override
    public void clear(Object key) {
        // do nothing
    }

    @Override
    public Set keys() {
        return Sets.newHashSet();
    }

    @Override
    public List allValues() {
        return Arrays.asList();
    }
}
