package com.codenjoy.dojo.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import com.codenjoy.dojo.services.Direction;

import java.util.Scanner;
import java.util.function.Consumer;

public class KeyboardSolver implements Solver {

    private Consumer<String> out;
    private Scanner scanner;

    public KeyboardSolver() {
        out = System.out::println;
        scanner = new Scanner(System.in);
        out.accept("\n\n\tTo play press char then Enter: \n" +
                "\t'a' - LEFT, 'd' - RIGHT, 'w' - UP, 's' - DOWN, 'f' - ACT. \n" +
                "\tAny other command you must enter explicitly, \n" +
                "\tfor example 'ACT(1,2,3)' then pres Enter.\n\n");
    }

    @Override
    public String get(ClientBoard board) {
        String line = scanner.nextLine();

        if (line.equalsIgnoreCase("w")) {
            return Direction.UP.name();
        }

        if (line.equalsIgnoreCase("s")) {
            return Direction.DOWN.name();
        }

        if (line.equalsIgnoreCase("a")) {
            return Direction.LEFT.name();
        }

        if (line.equalsIgnoreCase("d")) {
            return Direction.RIGHT.name();
        }

        if (line.equalsIgnoreCase("f")) {
            return Direction.ACT.name();
        }

        return line;
    }
}
