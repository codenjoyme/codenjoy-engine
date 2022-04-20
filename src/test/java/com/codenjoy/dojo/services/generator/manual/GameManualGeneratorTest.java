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

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.Assert.*;

public class GameManualGeneratorTest {
    private static final String TEST_LANGUAGE = "en";
    private static final String TEST_GAME = "molly";
    private static final String TEST_MANUAL_TYPE = "codenjoy";
    private static final List<String> TEST_MANUAL_PARTS = ImmutableList.of(
            "{$global}part1.md",
            "{$game}{$language}part2.md",
            "{$game}part3.md",
            "{$global}{$language}part4.md"
    );
    private static final String TEST_BASE = "target/generated-test-sources/manual-generator";
    private static final String TEST_RELATIVE_PATH_TO_GLOBAL_SOURCES = "global/";
    private static final String TEST_RELATIVE_PATH_TO_GAME_SOURCES = "games/{$game}/";

    private static final String PREPARED_FILE_PATH_PART1 = "target/generated-test-sources/manual-generator/global/part1.md";
    private static final String PREPARED_FILE_PATH_PART2 = "target/generated-test-sources/manual-generator/games/molly/en/part2.md";
    private static final String PREPARED_FILE_PATH_PART3 = "target/generated-test-sources/manual-generator/games/molly/part3.md";
    private static final String PREPARED_FILE_PATH_PART4 = "target/generated-test-sources/manual-generator/global/en/part4.md";

    private static final String PREPARED_DATA_PART1 = "Part1 file from Global Path";
    private static final String PREPARED_DATA_PART2 = "Part2 file from Game Path, Language directory";
    private static final String PREPARED_DATA_PART3 = "Part3 file from Game Path";
    private static final String PREPARED_DATA_PART4 = "Part4 file from Global Path, Language directory";

    private GameManualGenerator generator;
    private ByteArrayOutputStream baos;
    private PrintStream old;

    @Before
    public void setUp() {
        generator = getGenerator();
        old = System.out;
        redirectOut();
    }

    private void redirectOut() {
        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }

    private GameManualGenerator getGenerator() {
        return new GameManualGenerator(TEST_GAME, TEST_LANGUAGE, TEST_BASE, TEST_RELATIVE_PATH_TO_GLOBAL_SOURCES, TEST_RELATIVE_PATH_TO_GAME_SOURCES) {
            @Override
            protected List<String> getManualParts() {
                return TEST_MANUAL_PARTS;
            }

            @Override
            protected String getManualType() {
                return TEST_MANUAL_TYPE;
            }
        };
    }

    @After
    public void clear() {
        System.out.flush();
        System.setOut(old);
        System.out.println(baos);
    }

    @Test
    public void whenAllSourceFilesPresented_shouldGenerateTargetFileCorrect() {
        // given generate correct files for generation
        generateCorrectTestFiles();

        // when we generate complete manual
        generator.generate();

        // then generated manual should be present in source folder
        assertTrue(baos.toString().contains(
                String.format("[INFO] Manual for [%s] type:[%s] saved",
                        TEST_GAME, TEST_MANUAL_TYPE)
        ));
        assertTrue(Files.isRegularFile(Path.of(getTargetFilePath())));
        assertEquals("Part1 file from Global Path\n" +
                        "\n" +
                        "Part2 file from Game Path, Language directory\n" +
                        "\n" +
                        "Part3 file from Game Path\n" +
                        "\n" +
                        "Part4 file from Global Path, Language directory\n" +
                        "\n",
                load(getTargetFilePath()));
    }

    @Test
    public void whenOneSourceFileMissed_shouldPrintErrorMessageToConsole() {
        // given generate source files for generation without one file
        generateMissedTestFiles();

        // when we generate complete manual
        generator.generate();

        // then target file should not be created
        //      and error message printed at console
        assertTrue(baos.toString().contains(
                String.format(
                        "[ERROR] Can't find resources for manualType{%s}, game{%s}, language{%s}",
                        TEST_MANUAL_TYPE, TEST_GAME, TEST_LANGUAGE)
        ));
        assertFalse(Files.isRegularFile(Path.of(getTargetFilePath())));
    }

    private void generateCorrectTestFiles() {
        delete(TEST_BASE);
        createFile(PREPARED_FILE_PATH_PART1, PREPARED_DATA_PART1);
        createFile(PREPARED_FILE_PATH_PART2, PREPARED_DATA_PART2);
        createFile(PREPARED_FILE_PATH_PART3, PREPARED_DATA_PART3);
        createFile(PREPARED_FILE_PATH_PART4, PREPARED_DATA_PART4);
    }

    private void generateMissedTestFiles() {
        delete(TEST_BASE);
        createFile(PREPARED_FILE_PATH_PART1, PREPARED_DATA_PART1);
        createFile(PREPARED_FILE_PATH_PART2, PREPARED_DATA_PART2);
        createFile(PREPARED_FILE_PATH_PART3, PREPARED_DATA_PART3);
        // we didn't create last file and should see message about its missing in error logs
    }

    private void createFile(String path, String data) {
        try {
            write(getFile(path), data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTargetFilePath() {
        return TEST_BASE + "/games/" + TEST_GAME + "/"
                + TEST_MANUAL_TYPE + "-" + TEST_LANGUAGE + ".md";
    }

    @SneakyThrows
    String load(String path) {
        return Files.readString(Path.of(path), Charsets.UTF_8).replace(System.lineSeparator(), "\n");
    }

    void delete(String path) {
        try {
            deleteDirectory(getFile(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
