package com.codenjoy.dojo.client.generator;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2021 Codenjoy
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
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.capitalize;

@AllArgsConstructor
public class ElementGenerator {

    public static final int COMMENT_MAX_LENGTH = 60;

    private final String game;
    private final String language;

    public String generate() {
        return build(elements());
    }

    @SneakyThrows
    private CharElement[] elements() {
        String className = Element.class.getCanonicalName().replace("sample", game);

        return (CharElement[]) getClass().getClassLoader().loadClass(className)
                .getEnumConstants();
    }

    @SneakyThrows
    private Template template() {
        String className = Go.class.getPackageName() + "."
                + capitalize(language);

        Class<?> clazz = getClass().getClassLoader().loadClass(className);
        return (Template) clazz.getConstructor().newInstance();
    }

    private String build(CharElement[] elements) {
        Template template = template();

        String header = replace(template.header());

        List<String> lines = Arrays.stream(elements)
                .map(el -> replace(template.line(), el))
                .collect(toList());

        List<List<String>> infos = Arrays.stream(elements)
                .map(el -> splitLength(el.info(), COMMENT_MAX_LENGTH))
                .collect(toList());

        StringBuilder body = new StringBuilder();
        for (int index = 0; index < lines.size(); index++) {
            if (template.printComment()) {
                List<String> comments = infos.get(index);
                if (!comments.isEmpty()) {
                    comments.forEach(comment ->
                            body.append('\n')
                                    .append(template.comment())
                                    .append(comment));
                }
                if (template.printNewLine()) {
                    body.append('\n');
                }
            }

            if (template.printNewLine()) {
                body.append('\n');
            }
            String line = lines.get(index);
            if (template.lastDelimiter() != null && index == lines.size() - 1) {
                int count = (line.charAt(line.length() - 1) == '\n') ? 2 : 1;
                line = line.substring(0, line.length() - count)
                        + template.lastDelimiter();
            }
            body.append(line);
        }

        String footer = replace(template.footer());

        return header
                + body
                + footer;
    }

    private String replace(String template, CharElement element) {
        return replace(template)
                .replace("${element-lower}", element.name().toLowerCase())
                .replace("${element}", element.name())
                .replace("${char}", String.valueOf(element.ch()))
                .replace("${info}", element.info());
    }

    private String replace(String template) {
        return template
                .replace("${language}", language)
                .replace("${game}", game)
                .replace("${game-capitalize}", capitalize(game));
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
