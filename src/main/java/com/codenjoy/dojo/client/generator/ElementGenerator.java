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

import com.codenjoy.dojo.client.generator.language.Go;
import com.codenjoy.dojo.games.sample.Element;
import com.codenjoy.dojo.services.printer.CharElement;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class ElementGenerator {

    public static final int COMMENT_MAX_LENGTH = 60;

    public String generate(String game, String language) {
        return language(game, language).apply(elements(game));
    }

    @SneakyThrows
    private CharElement[] elements(String game) {
        String className = Element.class.getCanonicalName().replace("sample", game);

        return (CharElement[]) getClass().getClassLoader().loadClass(className)
                .getEnumConstants();
    }

    private Function<CharElement[], String> language(String game, String language) {
        return elements -> build(game, template(language), elements);
    }

    @SneakyThrows
    private Template template(String language) {
        String className = Go.class.getPackageName() + "."
                + capitalize(language);

        Class<?> clazz = getClass().getClassLoader().loadClass(className);
        return (Template) clazz.getConstructor().newInstance();
    }

    private String build(String game, Template template, CharElement[] elements) {

        String header = template.header()
                .replace("%s", game)
                .replace("%S", capitalize(game));

        List<String> lines = Arrays.stream(elements)
                .map(el -> format(template.line(), el.name(), el.ch()))
                .collect(toList());

        List<List<String>> infos = Arrays.stream(elements)
                .map(el -> splitLength(el.info(), COMMENT_MAX_LENGTH))
                .collect(toList());

        StringBuilder middle = new StringBuilder();
        for (int index = 0; index < lines.size(); index++) {
            List<String> comments = infos.get(index);
            if (!comments.isEmpty()) {
                comments.forEach(comment ->
                        middle.append('\n')
                                .append(template.comment())
                                .append(comment));
            }
            middle.append('\n');

            String line = lines.get(index);
            middle.append('\n')
                    .append(line);
        }

        String footer = template.footer();

        return header
                + middle
                + footer;
    }

    private List<String> splitLength(String text, int length) {
        return new LinkedList<>(){{
            int index = 0;
            while (index < text.length()) {
                add(text.substring(index, Math.min(index + length, text.length())));
                index += length;
            }
        }};
    }
}
