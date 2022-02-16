package com.codenjoy.dojo.utils.generator;

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

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class AbstractManualGeneratorTest {
    static final String RELATIVE_PATH_TO_GLOBAL_SOURCES = "global\\";
    static final String RELATIVE_PATH_TO_GAME_SOURCES = "games\\{$game}\\";
    final static List<String> CODENJOY_MANUAL_PARTS = ImmutableList.of(
            "{$global}part1.md",
            "{$game}{$language}part2.md",
            "{$game}part3.md",
            "{$global}{$language}part4.md"
    );
    String base;
    String actual;
    String expected;
    AbstractGameManualGenerator generator;

    @Before
    public void setUp() {
        base = StringUtils.EMPTY;
        actual = StringUtils.EMPTY;
        expected = StringUtils.EMPTY;
    }

    AbstractGameManualGenerator getGenerator(String game, String language, String manualType) {
        return new AbstractGameManualGenerator(game, language, base, RELATIVE_PATH_TO_GLOBAL_SOURCES, RELATIVE_PATH_TO_GAME_SOURCES) {
            @Override
            protected List<String> getManualParts() {
                return CODENJOY_MANUAL_PARTS;
            }

            @Override
            protected String getManualType() {
                return manualType;
            }
        };
    }

    @SneakyThrows
    String load(String path) {
        return Files.readString(Path.of(path), Charsets.UTF_8).replace(System.lineSeparator(), "\n");
    }

    void delete(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void logState() {
        log.info("Source files: " + base);
        log.info("Generated file: " + actual);
        log.info("Expected file: " + expected);
    }
}
