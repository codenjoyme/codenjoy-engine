package com.codenjoy.dojo.services.settings;

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


import com.codenjoy.dojo.services.annotations.PerformanceOptimized;
import com.google.common.collect.Lists;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ToString
public class SettingsImpl implements Settings {

    protected Map<String, Parameter<?>> map = new LinkedHashMap<>();

    @Override
    @PerformanceOptimized
    public List<Parameter> getParameters() {
        return new UnmodifiableList(map.values());
    }

    @Override
    public EditBox<?> addEditBox(String name) {
        Parameter<?> parameter = map.get(name);
        return (EditBox<?>) (parameter != null
                ? parameter
                : put(name, new EditBox(name)));
    }

    @Override
    public SelectBox<?> addSelect(String name, List<Object> options) {
        Parameter<?> parameter = map.get(name);
        return (SelectBox<?>) (parameter != null
                ? parameter
                : put(name, new SelectBox(name, options)));
    }

    @Override
    public CheckBox<Boolean> addCheckBox(String name) {
        Parameter<?> parameter = map.get(name);
        return (CheckBox<Boolean>) (parameter != null
                ? parameter
                : put(name, new CheckBox<Boolean>(name).type(Boolean.class)));
    }

    private Parameter put(String name, Parameter parameter) {
        map.put(name, parameter);
        return parameter;
    }

    @Override
    public boolean hasParameter(String name) {
        // TODO do not use map.containsKey just check that map.get() != null
        return map.containsKey(name);
    }

    @Override
    public boolean hasParameterPrefix(String name) {
        return map.keySet().stream()
                .anyMatch(key -> key.startsWith(name));
    }

    @Override
    public Parameter<?> getParameter(String name, Supplier<Parameter<?>> ifNull) {
        Parameter<?> parameter = map.get(name);
        if (parameter == null) {
            parameter = ifNull.get();
        }
        return parameter;
    }

    @Override
    public Parameter<?> parameter(String name) {
        return getParameter(name, () -> {
            throw new IllegalArgumentException("Parameter not found with name:" + name);
        });
    }

    @Override
    public void removeParameter(String name) {
        map.remove(name);
    }

    @Override
    public void replaceParameter(Parameter parameter) {
        put(parameter.getName(), parameter);
    }

    @Override
    public boolean changed() {
        return map.values().stream()
                .anyMatch(Parameter::changed);
    }

    @Override
    public List<String> whatChanged() {
        return map.values().stream()
                .filter(Parameter::changed)
                .map(Parameter::getName)
                .collect(toList());
    }

    @Override
    public void changesReacted() {
        map.values()
                .forEach(Parameter::changesReacted);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public void updateAll(List<Parameter> parameters) {
        parameters.forEach(parameter -> {
            String name = parameter.getName();

            Parameter<?> exists = map.get(name);
            if (exists != null) {
                exists.update(parameter.getValue());
            } else {
                replaceParameter(parameter);
            }
        });
    }

    @Override
    public void copyFrom(List<Parameter> parameters) {
        parameters.forEach(this::replaceParameter);
    }

    @Override
    public void replaceAll(List<String> keysToRemove, List<Parameter> parameters) {
        // TODO make this synchronized (and others)
        keysToRemove.forEach(this::removeParameter);
        parameters.forEach(this::replaceParameter);
    }

    @Override
    public void reset() {
        map.values().forEach(Parameter::reset);
    }

    public String toStringShort() {
        return getParameters().stream()
                .map(parameter -> String.format("%s=%s",
                        parameter.getName(), parameter.getValue()))
                .collect(toList())
                .toString();
    }

    @Override
    public void updateAll(Predicate<Parameter> filter,
                          List<Object> keys,
                          List<Object> newKeys,
                          List<Object> values)
    {
        // исходные параметры, мы их сохраняем потому как там есть default
        // и другие базовые настройки
        Map<String, Parameter> source = getParameters().stream()
                .filter(filter)
                .collect(toMap(Parameter::getName, identity()));

        // валидация, мало ли придет с фронта несвязанные списки
        if (keys.size() != newKeys.size() || keys.size() != values.size()) {
            throw new IllegalStateException(String.format(
                    "Found inconsistent Levels settings state. " +
                            "There are three lists with different size: " +
                            "keys:%s, new-keys:%s, values:%s",
                    keys.size(), newKeys.size(), values.size()));
        }

        // карта превращений
        Map<String, Pair<String, String>> transform = new HashMap<>();
        for (int index = 0; index < keys.size(); index++) {
            String key = (String) keys.get(index);
            String newKey = (String) newKeys.get(index);
            String value = (String) values.get(index);

            transform.put(key, Pair.of(newKey, value));
        }

        // создаем список новых (клонированных) параметров
        // с уже измененными именами и значениями
        List<Parameter> destination = transform.entrySet().stream()
                .filter(entry -> StringUtils.isNotEmpty(entry.getValue().getKey()))
                .map(entry -> {
                    String key = entry.getKey();
                    Pair<String, String> pair = entry.getValue();
                    String newKey = pair.getKey();
                    String value = pair.getValue();

                    Parameter from = source.get(key);
                    if (from == null) {
                        return new EditBox(newKey)
                                .type(String.class)
                                .multiline()
                                .def(value);
                    }

                    return from.clone(newKey)
                            .update(value);
                })
                .sorted(comparing(Parameter::getName))
                .collect(toList());

        // удаляем старые параметры и добавляем новые
        replaceAll(Lists.newArrayList(source.keySet()), destination);
    }
}
