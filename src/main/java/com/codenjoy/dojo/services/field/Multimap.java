package com.codenjoy.dojo.services.field;

import java.util.*;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

public class Multimap<K, V> {

    private Map<K, List<V>> map = new LinkedHashMap<>();

    public List<V> get(K key) {
        return map.computeIfAbsent(key, k -> new LinkedList<>());
    }

    public <T> T getAnd(K key, T defaultValue, Function<List<V>, T> function) {
        List<V> list = map.get(key);
        if (list == null || list.isEmpty()) {
            return defaultValue;
        }
        return function.apply(list);
    }

    public void removeKey(K key) {
        map.remove(key);
    }

    public List<V> all() { // TODO test me
        return map.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(toList());
    }

    @Override
    public String toString() {
        return map.entrySet().stream()
                .collect(toMap(entry -> "\n\t" + string(entry.getKey()) + ".class",
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

    private String string(K key) {
        // TODO тут может быть любой тип, но нам хотелось бы в toString получать именно короткое имя
        return ((Class)key).getSimpleName();
    }

    public boolean isEmpty() {
        return map.entrySet().stream()
                .allMatch(entry -> entry.getValue().isEmpty());
    }

    public boolean removeAllExact(K key, V value) {
        return getAnd(key, false, list -> {
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
}
