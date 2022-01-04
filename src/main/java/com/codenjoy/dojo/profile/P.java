package com.codenjoy.dojo.profile;

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

public class P {

    protected static Profiler p;

    static {
        P.reset();
    }

    public static synchronized void reset() {
        p = new Profiler(){{
            PRINT_SOUT = true;
        }};
    }

    public static synchronized void start() {
        p.start();
    }

    public static synchronized void done(String phase) {
        p.done(phase);
    }

    public static synchronized void beginCycle(String preffix) {
        p.beginCycle(preffix);
    }

    public static synchronized void endCycle() {
        p.endCycle();
    }

    public static synchronized void print() {
        p.print();
    }
}
