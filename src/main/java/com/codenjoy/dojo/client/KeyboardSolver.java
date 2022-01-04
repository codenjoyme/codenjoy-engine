package com.codenjoy.dojo.client;

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

import com.codenjoy.dojo.services.Direction;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;

public class KeyboardSolver implements Solver {

    public static final String WELCOME_MESSAGE = "\n\n\tTo play press char then Enter:\n" +
            "\t'a' - LEFT, 'd' - RIGHT, 'w' - UP, 's' - DOWN, 'f' - ACT.\n" +
            "\tYou can combine commands together, for example:\n" +
            "\t'af' - LEFT,ACT, 'fa' - ACT,LEFT, 'wf' - UP,ACT.\n" +
            "\tYou can also call ACT with parameters:\n" +
            "\t'f12' - ACT(1,2), 'f0' - ACT(0), f123a - ACT(1,2,3),LEFT.\n" +
            "\tAny other command you must enter explicitly,\n" +
            "\tfor example 'LEFT,ACT(1,2,3)' then pres Enter.\n\n";

    private Consumer<String> out;
    private Scanner scanner;

    public KeyboardSolver() {
        out = out()::println;
        scanner = new Scanner(in());
        out.accept(WELCOME_MESSAGE);
    }

    protected InputStream in() {
        return System.in;
    }

    protected PrintStream out() {
        return System.out;
    }

    @Override
    public String get(ClientBoard board) {
        String line;
        try {
            line = scanner.nextLine();
        } catch (NoSuchElementException exception) {
            return StringUtils.EMPTY;
        }

        line = line.toLowerCase();

        List<String> commands = new LinkedList<>();
        int index = 0;
        while (index < line.length()) {
            Character ch = line.charAt(index);
            if (ch.equals('f')) {
                List<Integer> parameters = new LinkedList<>();
                boolean isNumber;
                do {
                    index++;
                    if (index >= line.length()) break;
                    ch = line.charAt(index);
                    isNumber = ch >= '0' && ch <= '9';
                    if (isNumber) {
                        parameters.add(Integer.valueOf(String.valueOf(ch)));
                    }
                } while (isNumber);
                commands.add(Direction.ACT(
                        parameters.stream()
                                .mapToInt(i -> i)
                                .toArray()));
                continue;
            }

            if (ch.equals('w')) {
                commands.add(Direction.UP.toString());
            }

            if (ch.equals('s')) {
                commands.add(Direction.DOWN.toString());
            }

            if (ch.equals('a')) {
                commands.add(Direction.LEFT.toString());
            }

            if (ch.equals('d')) {
                commands.add(Direction.RIGHT.toString());
            }
            index++;
        }

        return String.join(",", commands);
    }
}
