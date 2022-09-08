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

import com.codenjoy.dojo.utils.PrintUtils;
import com.codenjoy.dojo.utils.SmokeUtils;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.utils.PrintUtils.Color.*;

public abstract class GameManualGenerator {

    // используется для тестирования, этим флагом отключаем реальное сохранение файлов
    public static boolean READONLY = false;

    private static final String FILE_SEPARATOR = "\n\n";
    private static final String TARGET_FILE = "{$path}{$manualType}-{$language}.md";
    private static final String GAME = "{$game}";
    private static final String GLOBAL = "{$global}";
    private static final String LANGUAGE = "{$language}";
    private static final String PATH = "{$path}";
    private static final String MANUAL_TYPE = "{$manualType}";
    private static final String SLASH = "/";
    private static final String NOT_EDIT_NOTICE = "<!-- Code generated by ManualGeneratorRunner.java\n  !!!DO NOT EDIT!!! -->\n";

    private final String game;
    private final String language;
    private final String basePath;
    private final String globalSources;
    private final String gameSources;

    public GameManualGenerator(String game, String language, String basePath, String globalSources, String gameSources) {
        this.game = game;
        this.language = language;
        this.basePath = basePath;
        this.globalSources = globalSources;
        this.gameSources = gameSources;
    }

    /**
     * Перечень и порядок файлов, которые участвуют в сборке мануала.
     * Список должен содержать только имя файла без всяких дополнительных тегов.
     * Порядок работы с файлом такой, мы его ищем в:
     * 1. в директории с игрой, с привязкой к языку
     * 2. в директории с игрой, но без привязки к языку
     * 3. в глобальной папке с привязкой к языку
     * 4. в глобальной папке, без привязки к языку.
     */
    protected abstract List<String> getManualParts();

    protected abstract String getManualType();

    protected String getTargetFileTemplate() {
        return TARGET_FILE;
    }

    public void generate() {
        String targetFile = getTargetFile();

        List<String> preparedManualPartPaths = getPreparedManualPartPaths();

        if (preparedManualPartPaths.size() != getManualParts().size()) {
            PrintUtils.printf("[ERROR] Can't find resources for manualType{%s}, " +
                            "game{%s}, language{%s}",
                    ERROR, getManualType(), game, language);
            return;
        }
        String data = build(preparedManualPartPaths);
        save(targetFile, data);
    }

    private List<String> getPreparedManualPartPaths() {
        List<String> preparedManualsPartsPath = new ArrayList<>();
        for (String fileName : getManualParts()) {
            PrintUtils.printf("Trying to find the file: %s", TEXT, fileName);
            String found = null;
            for (String file : getFilePathVariants(fileName)) {
                String pathToFile = createPathToFile(file);
                if (isFilePresent(pathToFile)) {
                    preparedManualsPartsPath.add(pathToFile);
                    found = pathToFile;
                    PrintUtils.printf("Found the file: %s", TEXT, pathToFile);
                    break;
                } else {
                    PrintUtils.printf("File not found: %s", TEXT, pathToFile);
                }
            }
            if (StringUtils.isNoneEmpty(found)) {
                PrintUtils.printf("File accepted: %s", INFO, found);
            } else {
                PrintUtils.printf("File is missing: %s", WARNING, fileName);
            }
        }
        return preparedManualsPartsPath;
    }

    private List<String> getFilePathVariants(String fileName) {
        return Arrays.asList(
                GAME + LANGUAGE + fileName,
                GAME + fileName,
                GLOBAL + LANGUAGE + fileName,
                GLOBAL + fileName);
    }

    private final String build(List<String> preparedManualPartPaths) {
        StringBuilder data = new StringBuilder();
        data.append(notificationText());
        for (String path : preparedManualPartPaths) {
            String part = load(path);
            if (!Strings.isNullOrEmpty(part)) {
                part = part.replace(GAME, game);
                data.append(part);
                data.append(FILE_SEPARATOR);
            }
        }
        return data.toString();
    }

    private String notificationText() {
        return NOT_EDIT_NOTICE;
    }

    private boolean isFilePresent(String filePath) {
        return Files.isRegularFile(Path.of(filePath));
    }

    private String makeAbsolutePath(String base, String additional) {
        return new File(base + SLASH + additional).getAbsolutePath() + SLASH;
    }

    private final String makePathToGameFolder() {
        return makeAbsolutePath(basePath, gameSources.replace(GAME, game));
    }

    private final String makePathToGlobalFolder() {
        return makeAbsolutePath(basePath, globalSources);
    }

    private String createPathToFile(String fileMask) {
        return fileMask
                .replace(GLOBAL, makePathToGlobalFolder())
                .replace(GAME, makePathToGameFolder())
                .replace(LANGUAGE, language + SLASH);
    }

    private String getTargetFile() {
        return getTargetFileTemplate()
                .replace(PATH, makePathToGameFolder())
                .replace(LANGUAGE, language)
                .replace(MANUAL_TYPE, getManualType());
    }

    private String load(String path) {
        return SmokeUtils.load(new File(path));
    }

    private void save(String path, String data) {
        if (!READONLY) {
            SmokeUtils.saveToFile(new File(path), data);
        }
        PrintUtils.printf("Manual for [%s] type:[%s] saved:[%s]",
                SUMMARY,
                game, getManualType(), path);
    }
}