package com.codenjoy.dojo.services.level;

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

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.field.AbstractLevel;
import com.codenjoy.dojo.services.multiplayer.LevelProgress;
import com.codenjoy.dojo.services.settings.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.codenjoy.dojo.services.level.LevelsSettings.Keys.LEVELS_MAP;
import static java.util.stream.Collectors.toList;

public interface LevelsSettings<T extends SettingsReader> extends SettingsReader<T> {

    String LEVELS = "[Level]";

    public enum Keys implements Key {

        LEVELS_MAP(LEVELS + " Map");

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    static boolean is(Settings settings) {
        if (settings == null) return false;

        return settings instanceof LevelsSettings
                || allLevelsKeys().stream()
                .map(Key::key)
                .allMatch(settings::hasParameterPrefix);
    }

    static String getKey(int levelNumber, Integer mapNumber) {
        return String.format("%s[%s%s]",
                LEVELS_MAP.key(),
                levelNumber,
                (mapNumber == null) ? "" : ("," + mapNumber));
    }

    static String getName(int levelNumber, Integer mapNumber) {
        return String.format("%s_%s%s",
                LEVELS_MAP.name(),
                levelNumber,
                (mapNumber == null) ? "" : ("_" + mapNumber));
    }

    static LevelsSettingsImpl get(Settings settings) {
        if (LevelsSettings.is(settings)) {
            return new LevelsSettingsImpl(settings);
        }

        return new LevelsSettingsImpl(null);
    }

    static List<Key> allLevelsKeys() {
        return Arrays.asList(Keys.values());
    }

    static Optional<? extends String> keyToName(List<Key> keys, String key) {
        if (keys.stream().noneMatch(it -> key.startsWith(it.key()))) {
            return Optional.empty();
        }

        if (key.startsWith(LEVELS_MAP.key())) {
            int level = parseLevelNumberFromKey(key);
            Integer map = parseLevelMapNumberFromKey(key);
            return Optional.of(getName(level, map));
        } else {
            return Optional.empty();
        }
    }

    static Optional<? extends String> nameToKey(List<Key> keys, String name) {
        if (keys.stream().noneMatch(it -> name.startsWith(it.toString()))) {
            return Optional.empty();
        }

        if (name.startsWith(LEVELS_MAP.name())) {
            int level = parseLevelNumberFromName(name);
            Integer map = parseMapNumberFromName(name);
            return Optional.of(getKey(level, map));
        } else {
            return Optional.empty();
        }
    }

    default void initLevels() {
        // TODO do we need this
    }

    default <L extends AbstractLevel> L level(int level, Dice dice, Function<String, L> constructor) {
        return constructor.apply(getRandomLevelMap(level, dice));
    }

    // parameters getters

    default Parameter<String> levelMap(int levelNumber) {
        return levelMap(levelNumber, null);
    }

    default Parameter<String> levelMap(int levelNumber, Integer mapNumber) {
        String name = getKey(levelNumber, mapNumber);

        if (!hasParameter(name)) {
            return new SimpleParameter<>(null);
        }
        return stringValue(() -> name);
    }

    // update methods

    default List<Parameter> getLevelsParams() {
        if (getParameters().isEmpty()) {
            return Arrays.asList();
        }
        return getParameters().stream()
                .filter(parameter -> isLevelsMap(parameter.getName()))
                .collect(toList());
    }

    default void updateFrom(List<Parameter> parameters) {
        parameters.stream()
                .filter(parameter -> isLevelsMap(parameter.getName()))
                .forEach(parameter -> {
                    String key = parameter.getName();
                    Parameter<String> source = parameter.type(String.class);
                    String def = source.getDefault();
                    String value = source.getValue();
                    if (hasParameter(key)) {
                        getParameter(key).update(value);
                    } else {
                        EditBox<String> box = add(() -> key, def);
                        if (!def.equals(value)) {
                            box.update(value);
                        }
                    }
                });
    }

    // getters

    default String getRandomLevelMap(int levelNumber, Dice dice) {
        int count = getLevelMapsCount(levelNumber);
        if (count == 1) {
            String result = getLevelMap(levelNumber);
            if (result == null) {
                result = getLevelMap(levelNumber, 1); // TODO test me
            }
            return result;
        } else {
            int mapNumber = toNumber(dice.next(count));
            return getLevelMap(levelNumber, mapNumber);
        }
    }

    default String getLevelMap(int levelNumber) {
        return getLevelMap(levelNumber, null);
    }

    default String getLevelMap(int levelNumber, Integer mapNumber) {
        return levelMap(levelNumber, mapNumber).getValue();
    }

    default int getLevelMapsCount(int levelNumber) {
       return (int) getParameters().stream()
               .map(Parameter::getName)
               .filter(name -> name.startsWith(LEVELS_MAP.key() + "[" + levelNumber))
               .count();
    }

    default int getLevelsCount() {
        return (int) getParameters().stream()
                .map(Parameter::getName)
                .filter(LevelsSettings::isLevelsMap)
                .map(name -> parseLevelNumberFromKey(name))
                .distinct()
                .count();
    }

    private static Integer parseLevelNumberFromKey(String name) {
        String part = name.split("\\[")[2];
        return Integer.parseInt(part.split("[,\\]]")[0]);
    }

    private static Integer parseLevelMapNumberFromKey(String name) {
        String part = name.split("\\[")[2];
        return (part.contains(",")) ? Integer.parseInt(part.split("[,\\]]")[1]) : null;

    }

    private static Integer parseMapNumberFromName(String key) {
        String[] split = key.split("_");
        return (split.length == 4) ? Integer.parseInt(split[3]) : null;
    }

    private static int parseLevelNumberFromName(String key) {
        return Integer.parseInt(key.split("_")[2]);
    }

    private static boolean isLevelsMap(String name) {
        return name.startsWith(LEVELS_MAP.key() + "[");
    }

    // setters

    default T clearLevelMaps(int levelNumber) {
        int mapsCount = getLevelMapsCount(levelNumber);
        for (int index = 0; index < mapsCount; index++) {
            int mapNumber = toNumber(index);
            String name = getKey(levelNumber, mapNumber);
            removeParameter(name);
        }
        String name = getKey(levelNumber, null);
        removeParameter(name);
        return (T) this;
    }

    private int toNumber(int index) {
        return index + LevelProgress.levelsStartsFrom1;
    }

    default T setLevelMaps(int levelNumber, String... maps) {
        clearLevelMaps(levelNumber);
        for (int index = 0; index < maps.length; index++) {
            String value = maps[index];
            int mapNumber = toNumber(index);
            setLevelMap(levelNumber, mapNumber, value);
        }
        return (T) this;
    }

    default T setLevelMap(int levelNumber, String map) {
        return setLevelMap(levelNumber, null, map);
    }

    default T setLevelMap(int levelNumber, Integer mapNumber, String map) {
        String name = getKey(levelNumber, mapNumber);
        if (!hasParameter(name)) {
            multiline(() -> name, map);
        } else {
            stringValue(() -> name).update(map);
        }
        return (T) this;
    }
}
