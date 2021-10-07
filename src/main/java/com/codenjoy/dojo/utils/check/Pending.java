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

public class Pending {

    private boolean enabled = false;
    private String value = null;

    public void value(String value) {
        this.value = value;
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean hasValue() {
        return value != null;
    }

    public String value() {
        return value;
    }

    private Pending copy() {
        Pending result = new Pending();
        result.value = value;
        result.enabled = enabled;
        return result;
    }

    public Pending disable() {
        Pending result = copy();
        enabled = false;
        value = null;
        return result;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }
}
