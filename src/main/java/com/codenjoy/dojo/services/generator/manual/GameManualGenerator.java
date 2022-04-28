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

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.utils.PrintUtils.Color.*;
import static com.codenjoy.dojo.utils.PrintUtils.printf;
import static org.apache.commons.io.FileUtils.*;

public abstract class GameManualGenerator {
    private static final String FILE_SEPARATOR = "\n\n";
    private static final String TARGET_FILE_TEMPLATE = "{$path}{$manualType}-{$language}.md";
    private static final String $_GAME = "{$game}";
    private static final String $_GLOBAL = "{$global}";
    private static final String $_LANGUAGE = "{$language}";
    private static final String $_PATH = "{$path}";
    private static final String $_MANUAL_TYPE = "{$manualType}";
    private static final String SLASH = "/";

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

    /*
        Перечень и порядок файлов, которые участвуют в сборке мануала.
        Файлы могут быть глобальны для всех игр либо уникальные для каждой.
        Варианты имени файла:
        {$global}{$language}filename.ext
        {$global}filename.ext
        {$game}{$language}filename.ext
        {$game}filename.ext
     */
    protected abstract List<String> getManualParts();

    protected abstract String getManualType();

    protected String getTargetFileTemplate() {
        return TARGET_FILE_TEMPLATE;
    }

    public void generate() {
        String targetFile = getTargetFile();

        List<String> preparedManualsPartsPath = getPreparedManualsPartsPath();
        if (!isResourcesPresent(preparedManualsPartsPath)) {
            printf(
                    "[ERROR] Can't find resources for manualType{%s}, game{%s}, language{%s}\n"
                    , ERROR,
                    getManualType(),
                    game,
                    language);
            return;
        }
        String data = build(preparedManualsPartsPath);
        // TODO: 4/27/2022 добавить генерацию инструкции как и откуда взять этот мануал
        save(targetFile, data);
    }

    private final String build(List<String> preparedManualsPartsPath) {
        StringBuilder data = new StringBuilder();
        // TODO: 4/27/2022 добавить в начало файла информацию о том, что это автоматически генерируемый файл
        //                 и ссылку на инструкцию
        for (String path : preparedManualsPartsPath) {
            String partOfData = load(path);
            if (!Strings.isNullOrEmpty(partOfData)) {
                partOfData = partOfData.replace($_GAME, game);
                data.append(partOfData);
                data.append(FILE_SEPARATOR);
            }
        }
        return data.toString();
    }

    private boolean isResourcesPresent(List<String> preparedManualsPartsPath) {
        boolean result = true;
        for (String filePath : preparedManualsPartsPath) {
            if (!Files.isRegularFile(Path.of(filePath))) {
                printf("File is missing: %s\n", WARNING, filePath);
                result = false;
            }
        }
        return result;
    }

    private String makeAbsolutePath(String base, String additional) {
        return new File(base + SLASH + additional).getAbsolutePath() + SLASH;
    }

    private final String makePathToGameFolder() {
        return makeAbsolutePath(basePath, gameSources.replace($_GAME, game));
    }

    private final String makePathToGlobalFolder() {
        return makeAbsolutePath(basePath, globalSources);
    }

    private final List<String> getPreparedManualsPartsPath() {
        return getManualParts().stream()
                .map(
                        path -> path
                                .replace($_GLOBAL, makePathToGlobalFolder())
                                .replace($_GAME, makePathToGameFolder())
                                .replace($_LANGUAGE, language + SLASH)
                )
                .collect(Collectors.toList());
    }

    private String getTargetFile() {
        return getTargetFileTemplate()
                .replace($_PATH, makePathToGameFolder())
                .replace($_LANGUAGE, language)
                .replace($_MANUAL_TYPE, getManualType());
    }

    private String load(String path) {
        String fileData;
        try {
            fileData = readFileToString(getFile(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            fileData = StringUtils.EMPTY;
        }
        return fileData;
    }

    private void save(String path, String data) {
        try {
            write(getFile(path), data, StandardCharsets.UTF_8);
            printf("[INFO] Manual for [%s] type:[%s] saved:[%s]",
                    INFO,
                    game, getManualType(), path
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
