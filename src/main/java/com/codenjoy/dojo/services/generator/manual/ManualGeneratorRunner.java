package com.codenjoy.dojo.services.generator.manual;

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

import com.codenjoy.dojo.services.generator.ElementGenerator;
import com.codenjoy.dojo.services.printer.CharElement;
import com.codenjoy.dojo.utils.PrintUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.codenjoy.dojo.utils.PrintUtils.Color.INFO;
import static java.util.stream.Collectors.toList;

public class ManualGeneratorRunner {

    private static final String ALL = "all";
    public static List<String> ALL_GAMES = games();
    public static List<String> ALL_LOCALES = Arrays.asList("en", "ru");

    private static String base;
    private static String games;
    private static String locales;

    public static void main(String[] args) {
        System.out.println("+-----------------------------+");
        System.out.println("| Starting elements generator |");
        System.out.println("+-----------------------------+");

        if (args != null && args.length == 3) {
            base = args[0];
            games = args[1];
            locales = args[2];
            printInfo("Environment");
        } else {
            base = "";
            games = ALL;
            locales = ALL;
            printInfo("Runner");
        }
        if (ALL.equalsIgnoreCase(games)) {
            games = StringUtils.join(ALL_GAMES, ",");
        }
        if (ALL.equalsIgnoreCase(locales)) {
            locales = StringUtils.join(ALL_LOCALES, ",");
        }
        if (!new File(base).isAbsolute()) {
            base = new File(base).getAbsoluteFile().getPath();
            PrintUtils.printf("\t   absolute:'%s'",
                    INFO,
                    base);
        }
        printInfo("Processed parameters");

        for (String game : games.split(",")) {
            for (String language : locales.split(",")) {
                new CodenjoyManual(game, language, base).generate();
                new DojorenaManual(game, language, base).generate();
            }
        }
    }

    private static void printInfo(String source) {
        PrintUtils.printf(
                "Got from %s:\n" +
                        "\t 'GAMES':   '%s'\n" +
                        "\t 'LOCALES': '%s'\n" +
                        "\t 'BASE':    '%s'",
                INFO,
                source,
                games,
                locales,
                base);
    }

    private static List<String> games() {
        String packageName = "com.codenjoy.dojo.games";
        return new Reflections(packageName).getSubTypesOf(CharElement.class).stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .filter(clazz -> !Modifier.isInterface(clazz.getModifiers()))
                .filter(clazz -> Modifier.isPublic(clazz.getModifiers()))
                .filter(clazz -> !clazz.toString().contains("test"))
                .map(Class::getCanonicalName)
                .map(name -> StringUtils.substringBetween(name,
                        "com.codenjoy.dojo.games.", ".Element"))
                .filter(Objects::nonNull)
                .map(ElementGenerator::getCanonicalGame)
                .sorted()
                .collect(toList());
    }
}
