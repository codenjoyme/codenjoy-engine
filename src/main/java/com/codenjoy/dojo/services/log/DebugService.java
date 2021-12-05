package com.codenjoy.dojo.services.log;

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


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.codenjoy.dojo.client.Encoding;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static ch.qos.logback.classic.Level.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trim;

public class DebugService extends Suspendable {

    private static final String JAVA_CLASS_WITH_PACKAGE = "^(?:\\w+|\\w+\\.\\w+)+$";
    public static final Level DEFAULT_LEVEL = INFO;
    public static final List<Level> LEVELS =
            Arrays.asList(ALL, TRACE, DEBUG, INFO, WARN, ERROR, OFF);
    public static final String LEVEL_SEPARATOR = ":";
    public static final int NAME = 0;
    public static final int LEVEL = 1;

    private final Pattern javaClassWithPackage;
    private List<String> filter;

    public DebugService(boolean active, List<String> filter) {
        this.active = active;
        this.filter = filter;
        setDebugEnable(active);
        javaClassWithPackage = Pattern.compile(JAVA_CLASS_WITH_PACKAGE);
    }

    public void setDebugEnable(boolean active) {
        super.setActive(active);
    }

    @Override
    public void pause() {
        changePackageLoggingLevels(DEFAULT_LEVEL);
    }

    @Override
    public boolean isWorking() {
        return loggers()
                .map(Logger::getLevel)
                .anyMatch(DEBUG::equals);
    }

    private Stream<Logger> loggers() {
        return filter.stream()
                .map(LoggerFactory::getLogger)
                .map(Logger.class::cast);
    }

    @Override
    public void resume() {
        changePackageLoggingLevels(DEBUG);
    }

    private void changePackageLoggingLevels(Level level) {
        loggers().forEach(logger -> logger.setLevel(level));
    }

    public String getLoggersLevels() {
        return loggers()
                .map(logger -> String.format("%s%s %s",
                        logger.getName(),
                        LEVEL_SEPARATOR,
                        levelName(logger)))
                .collect(joining("\n"));
    }

    private String levelName(Logger logger) {
        if (logger.getLevel() == null) {
            return DEFAULT_LEVEL.levelStr;
        }

        return logger.getLevel().levelStr;
    }

    public void setLoggersLevels(String input) {
        List<String> lines = Arrays.asList(Encoding.replaceN(input).split("\n"));

        lines = lines.stream()
                .filter(this::validate)
                .collect(toList());

        List<String> updated = new LinkedList<>();
        for (int index = 0; index < lines.size(); index++) {
            String[] split = lines.get(index).split(LEVEL_SEPARATOR);
            Level level = Level.toLevel(level(split));
            updated.add(name(split));
            setLevel(name(split), level);
        }

        defaultLevelForRemoved(updated);

        merge(updated);
    }

    private void defaultLevelForRemoved(List<String> updated) {
        List<String> copy = new LinkedList<>(filter);
        copy.removeAll(updated);
        copy.forEach(name -> setLevel(name, DEFAULT_LEVEL));
    }

    private void merge(List<String> lines) {
        Set<String> set = new LinkedHashSet<>(filter);
        set.addAll(lines);
        filter = new ArrayList<>(set);
    }

    private boolean validate(String line) {
        String[] split = line.split(LEVEL_SEPARATOR);
        if (split.length != 2) {
            return false;
        }

        if (!javaClassWithPackage.matcher(name(split)).matches()) {
            return false;
        }

        if (!LEVELS.contains(Level.toLevel(level(split), null))) {
            return false;
        }

        return true;
    }

    private String name(String[] split) {
        return split[NAME];
    }

    private String level(String[] split) {
        return trim(split[LEVEL]);
    }

    public static void setLevel(String name, Level level) {
        logger(name).setLevel(level);
    }

    public static Level getLevel(String name) {
        return logger(name).getLevel();
    }

    public static Logger logger(String name) {
        return (Logger) LoggerFactory.getLogger(name);
    }
}