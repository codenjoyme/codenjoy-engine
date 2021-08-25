package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

class Multimap {

    private Map<Class, List<Point>> map = new LinkedHashMap<>();

    public List<Point> get(Class<?> key) {
        return map.computeIfAbsent(key, k -> new LinkedList<>());
    }

    public List<Point> getOnly(Class<?> key) {
        return map.get(key);
    }

    public void remove(Class<?> key) {
        map.remove(key);
    }

    public List<Point> all() { // TODO test me
        return map.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(toList());
    }

    @Override
    public String toString() {
        return map.entrySet().stream()
                .collect(toMap(entry -> "\n\t" + entry.getKey().getSimpleName() + ".class",
                        Map.Entry::getValue))
                .entrySet().stream()
                .sorted(comparing(Map.Entry::getKey))
                .map(entry -> String.format("{%s=[\n\t\t%s]}",
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(Object::toString)
                                .collect(joining("\n\t\t"))))
                .collect(joining("\n\t"))
                .replace("\t", "        ");
    }

    public boolean isEmpty() {
        return map.entrySet().stream()
                .allMatch(entry -> entry.getValue().isEmpty());
    }
}
