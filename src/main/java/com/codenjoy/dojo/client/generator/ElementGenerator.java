package com.codenjoy.dojo.client.generator;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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

import com.codenjoy.dojo.services.printer.CharElement;

import java.util.Arrays;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

public class ElementGenerator {

    private String generate(String game, String language) {
        return language(language).apply(elements(game));
    }

    private CharElement[] elements(String game) {
        switch (game) {
            case "a2048" :
                return com.codenjoy.dojo.games.a2048.Element.values();
            case "sample" :
                return com.codenjoy.dojo.games.sample.Element.values();
            default:
                throw new UnsupportedOperationException("Unknown game:" + game);
        }
    }

    private Function<CharElement[], String> language(String language) {
        switch (language) {
            case "go" :
                return elements -> String.format(
                            "package %s\n" +
                            "\n" +
                            "var Elements = map[string]rune{\n" +
                            "%s" +
                            "}\n",
                            language,
                        Arrays.stream(elements)
                                .map(element ->
                                        String.format("    \"%s\": '%s', // %s\n",
                                                element.name(),
                                                element.ch(),
                                                element.info()))
                                .collect(joining()));
            default:
                throw new UnsupportedOperationException("Unknown language:" + language);
        }
    }

    public static void main(String[] args) {
        String data = new ElementGenerator().generate("a2048", "go");
        System.out.println(data);
    }
}
