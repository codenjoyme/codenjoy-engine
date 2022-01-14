package com.codenjoy.dojo.services.nullobj;

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


import com.codenjoy.dojo.services.settings.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public final class NullParameter<T> implements Parameter<T> {

    private static final Parameter INSTANCE = new NullParameter();

    public static Parameter INSTANCE() {
        warn("Attempting to use NullParameter");
        return INSTANCE;
    }

    private NullParameter() {
        // do nothing
    }

    private static void warn() {
        warn("You are working with NullParameter");
    }

    private static void warn(String message) {
        log.warn(message);
    }

    @Override
    public T getValue() {
        warn();
        return (T)new Object();
    }

    @Override
    public String getType() {
        warn();
        return StringUtils.EMPTY;
    }

    @Override
    public Class<?> getValueType() {
        warn();
        return Object.class;
    }

    @Override
    public String getName() {
        warn();
        return StringUtils.EMPTY;
    }

    @Override
    public Parameter<T> update(Object value) {
        warn();
        return this;
    }

    @Override
    public Parameter<T> justSet(Object value) {
        warn();
        return this;
    }

    @Override
    public Parameter<T> def(T value) {
        warn();
        return this;
    }

    @Override
    public <V> Parameter<V> type(Class<V> type) {
        warn();
        return (Parameter) this;
    }

    @Override
    public Parameter<T> parser(Function<String, T> parser) {
        warn();
        return this;
    }

    @Override
    public void select(int index) {
        warn();
    }

    @Override
    public Parameter<T> onChange(BiConsumer<T, T> consumer) {
        warn();
        return this;
    }

    @Override
    public boolean changed() {
        warn();
        return false;
    }

    @Override
    public void changesReacted() {
        warn();
    }

    @Override
    public List<T> getOptions() {
        warn();
        return Arrays.asList();
    }

    @Override
    public T getDefault() {
        warn();
        return null;
    }

    @Override
    public void reset() {
        warn();
    }

    @Override
    public Parameter<T> clone(String newName) {
        warn();
        return this;
    }
}
