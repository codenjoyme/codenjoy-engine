package com.codenjoy.dojo.utils;

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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Позволяет временно переопределить System.out для целей тестирования.
 */
public class RedirectOutput {

    private ByteArrayOutputStream output;
    private PrintStream oldPrintStream;

    public void redirect() {
        oldPrintStream = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    public void rollback() {
        System.out.flush();
        System.setOut(oldPrintStream);
        System.out.println(output);
    }

    public boolean contains(String message) {
        return output.toString().contains(message);
    }
}
