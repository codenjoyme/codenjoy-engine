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

import com.codenjoy.dojo.utils.PrintUtils;
import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.utils.PrintUtils.Color.ERROR;
import static com.codenjoy.dojo.utils.PrintUtils.Color.WARNING;

public abstract class AbstractGameManualGenerator {
    private static final String FILE_SEPARATOR = "\n\n";
    private static final String TARGET_FILE_TEMPLATE = "{$path}{$manualType}-{$language}.md";

    private final String game;
    private final String language;
    private final String base;
    private final String globalSources;
    private final String gameSources;


    public AbstractGameManualGenerator(String game, String language, String base, String globalSources, String gameSources) {
        this.game = game;
        this.language = language;
        this.base = base;
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

    public void generate() {
        String globalPath = makeAbsolutePath(base, globalSources);
        String gamePath = makeAbsolutePath(base, gameSources.replace("{$game}", game));
        String targetFile = getTargetFile(gamePath);

        List<String> preparedManualsPartsPath = getPreparedManualsPartsPath(getManualParts(), globalPath, gamePath);
        if (!isResourcesPresent(preparedManualsPartsPath)) {
            PrintUtils.printf(
                    "[ERROR] Can't find resources for manualType{%s}, game{%s}, language{%s}\n"
                    , ERROR,
                    getManualType(),
                    game,
                    language);
            return;
        }
        String data = build(preparedManualsPartsPath);
        save(targetFile, data);
    }

    private String build(List<String> preparedManualsPartsPath) {
        StringBuilder data = new StringBuilder();
        for (String path : preparedManualsPartsPath) {
            data.append(load(path));
            data.append(FILE_SEPARATOR);
        }
        return data.toString();
    }

    private boolean isResourcesPresent(List<String> preparedManualsPartsPath) {
        boolean result = true;
        for (String filePath : preparedManualsPartsPath) {
            if (!Files.isRegularFile(Path.of(filePath))) {
                PrintUtils.printf("File is missing: %s\n", WARNING, filePath);
                result = false;
            }
        }
        return result;
    }

    private String makeAbsolutePath(String base, String additional) {
        return new File(base + additional).getAbsolutePath() + "\\";
    }

    private List<String> getPreparedManualsPartsPath(List<String> rawList, String globalPath, String gamePath) {
        return rawList.stream()
                .map(
                        file -> file
                                .replace("{$global}", globalPath)
                                .replace("{$game}", gamePath)
                                .replace("{$language}", language + "\\")
                )
                .collect(Collectors.toList());
    }

    protected String getTargetFile(String path) {
        return TARGET_FILE_TEMPLATE
                .replace("{$path}", path)
                .replace("{$language}", language)
                .replace("{$manualType}", getManualType());
    }

    private String load(String path) {
        String fileData;
        try {
            fileData = Files.readString(Path.of(path), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            fileData = StringUtils.EMPTY;
        }
        return fileData;
    }

    private void save(String path, String data) {
        try {
            Files.writeString(Path.of(path), data, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
