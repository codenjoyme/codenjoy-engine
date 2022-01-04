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

import java.util.function.BiConsumer;

public abstract class Updatable<T> {

    protected T value;
    protected boolean changed = false;
    private BiConsumer<T, T> onChange;

    protected T get() {
        return value;
    }

    protected T getOrDefault() {
        return (value == null) ? def() : value;
    }

    protected abstract T def();

    protected Parameter<T> setOnly(T value) {
        changed = ((this.value == null && value != null) || (this.value != null && !this.value.equals(value)));
        this.value = value;
        return (Parameter<T>) this;
    }

    protected Parameter<T> set(T value) {
        T old = getOrDefault();
        setOnly(value);
        if (onChange != null) {
            onChange.accept(old, value);
        }
        return (Parameter<T>) this;
    }

    public Parameter onChange(BiConsumer consumer) {
        this.onChange = consumer;
        return (Parameter<T>) this;
    }

    public boolean changed() {
        return changed;
    }

    public void changesReacted() {
        changed = false;
    }
}
