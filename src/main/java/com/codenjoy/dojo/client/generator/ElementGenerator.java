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
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class ElementGenerator {

    private String generate(String game, String language) {
        return language(language).apply(elements(game));
    }

    @SneakyThrows
    private CharElement[] elements(String game) {
        return (CharElement[]) getClass().getClassLoader().loadClass(
                        format("com.codenjoy.dojo.games.%s.Element", game))
                .getEnumConstants();
    }

    private Function<CharElement[], String> language(String language) {
        switch (language) {
            case "go" :
                Template template = new Go();
                return elements -> {
                    String header = format(template.header(), language);

                    List<String> lines = Arrays.stream(elements)
                                    .map(el -> format(template.line(), el.name(), el.ch()))
                                    .collect(toList());

                    List<String> infos = Arrays.stream(elements)
                            .map(el -> format(template.info(), el.info()))
                            .collect(toList());

                    int maxLength = lines.stream()
                            .mapToInt(String::length)
                            .max()
                            .getAsInt() + 3;

                    StringBuilder middle = new StringBuilder();
                    for (int index = 0; index < lines.size(); index++) {
                        String line = lines.get(index);
                        middle.append(line)
                                .append(StringUtils.rightPad("", maxLength - line.length()))
                                .append(infos.get(index));
                    }

                    String footer = template.footer();

                    return header
                            + middle
                            + footer;
                };

            default:
                throw new UnsupportedOperationException("Unknown language:" + language);
        }
    }

    public static void main(String[] args) {
        String data = new ElementGenerator().generate("a2048", "go");
        System.out.println(data);
    }
}
