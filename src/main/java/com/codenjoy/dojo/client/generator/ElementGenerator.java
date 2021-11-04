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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.rightPad;

public class ElementGenerator {

    public static final int COMMENT_MAX_LENGTH = 60;
    public static final int SPACES_BEFORE_COMMENT = 3;

    public String generate(String game, String language) {
        return language(language).apply(elements(game));
    }

    @SneakyThrows
    private CharElement[] elements(String game) {
        return (CharElement[]) getClass().getClassLoader().loadClass(
                        format("com.codenjoy.dojo.games.%s.Element", game))
                .getEnumConstants();
    }

    private Function<CharElement[], String> language(String language) {
        return elements -> build(language, template(language), elements);
    }

    private Template template(String language) {
        switch (language) {
            case "go" :
                return new Go();
            default:
                throw new UnsupportedOperationException("Unknown language:" + language);
        }
    }

    private String build(String language, Template template, CharElement[] elements) {
        String header = format(template.header(), language);

        List<String> lines = Arrays.stream(elements)
                .map(el -> format(template.line(), el.name(), el.ch()))
                .collect(toList());

        List<List<String>> infos = Arrays.stream(elements)
                .map(el -> splitLength(el.info(), COMMENT_MAX_LENGTH))
                .collect(toList());

        int maxLength = lines.stream()
                .mapToInt(String::length)
                .max()
                .getAsInt() + SPACES_BEFORE_COMMENT;

        StringBuilder middle = new StringBuilder();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            middle.append(line)
                    .append(rightPad("", maxLength - line.length()));

            List<String> comments = infos.get(index);
            if (!comments.isEmpty()) {
                middle.append(template.comment())
                        .append(comments.remove(0));
                comments.forEach(comment ->
                        middle.append('\n')
                                .append(rightPad("", maxLength))
                                .append(template.comment())
                                .append(comment));
            }
            middle.append('\n');
        }

        String footer = template.footer();

        return header
                + middle
                + footer;
    }

    private List<String> splitLength(String text, int length) {
        List<String> strings = new LinkedList<>();
        int index = 0;
        while (index < text.length()) {
            strings.add(text.substring(index, Math.min(index + length, text.length())));
            index += length;
        }
        return strings;
    }
}
