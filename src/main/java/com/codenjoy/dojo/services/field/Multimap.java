package com.codenjoy.dojo.services.field;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.annotations.PerformanceOptimized;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

public class Multimap<K, V> {

    private Map<K, List<V>> map = new LinkedHashMap<>();

    @PerformanceOptimized
    public List<V> get(K key) {
        return map.computeIfAbsent(key, k -> new LinkedList<>());
    }

    @PerformanceOptimized
    public boolean contains(K key) {
        return ifPresent(key, false,
                list -> !list.isEmpty());
    }

    @PerformanceOptimized
    public boolean remove(K key, Point element) {
        return ifPresent(key, false,
                list -> list.removeIf(it -> it.equals(element)));
    }

    @PerformanceOptimized
    public <T> T ifPresent(K key, T defaultValue, Function<List<V>, T> function) {
        List<V> list = map.get(key);
        if (list == null || list.isEmpty()) {
            return defaultValue;
        }
        return function.apply(list);
    }

    public void removeKey(K key) {
        map.remove(key);
    }

    public void forEach(Consumer<V> action) { // TODO test me
        for (Collection<V> list : map.values()) {
           list.forEach(action);
        }
    }

    @Override
    public String toString() {
        return map.entrySet().stream()
                .collect(toMap(entry -> "\n\t" + string(entry.getKey()) + ".class",
                        Map.Entry::getValue))
                .entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .sorted(comparing(Map.Entry::getKey))
                .map(entry -> String.format("{%s=[\n\t\t%s]}",
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(Object::toString)
                                .collect(joining("\n\t\t"))))
                .collect(joining("\n\t"))
                .replace("\t", "        ");
    }

    private String string(K key) {
        // TODO тут может быть любой тип, но нам хотелось бы в toString получать именно короткое имя
        return ((Class)key).getSimpleName();
    }

    public boolean isEmpty() {
        return map.entrySet().stream()
                .allMatch(entry -> entry.getValue().isEmpty());
    }

    @PerformanceOptimized
    public boolean removeAllExact(K key, V value) {
        return ifPresent(key, false, list -> {
            boolean result = false;
            Iterator<?> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == value) {
                    iterator.remove();
                    result = true;
                }
            }
            return result;
        });
    }

    @PerformanceOptimized
    public void clear(K key) {
        get(key).clear();
    }

    public List<K> keys() {
        return new LinkedList<>(map.keySet());
    }
}
