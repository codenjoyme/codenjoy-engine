package com.codenjoy.dojo.utils.check;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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

public class Caller {

    private String name;
    private Object wrapper;
    private Object[] args;

    public Caller(String name, Object wrapper, Object... args) {
        this.name = name;
        this.wrapper = wrapper;
        this.args = args;
    }

    public Object wrapper() {
        return wrapper;
    }

    public Object[] args() {
        return args;
    }

    public String name() {
        return name;
    }
}
