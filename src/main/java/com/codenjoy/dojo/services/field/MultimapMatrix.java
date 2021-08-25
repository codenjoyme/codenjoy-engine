package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class MultimapMatrix<K, V> {

    private Multimap<K, V>[][] field;

    public MultimapMatrix(int size) {
        field = new Multimap[size][];
        for (int x = 0; x < size; x++) {
            field[x] = new Multimap[size];
        }
    }

    public int size() {
        return field.length;
    }

    public Multimap<K, V> get(int x, int y) {
        if (Point.isOutOf(x, y, 0, 0, size())) {
            return new Multimap<>(); // TODO а точно тут так надо?
        }
        Multimap<K, V> map = field[x][y];
        if (map == null) {
            map = field[x][y] = new Multimap<>();
        }
        return map;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                Multimap<K, V> map = field[x][y];
                result.append(pt(x, y))
                        .append(":")
                        .append(map == null || map.isEmpty() ? "{}" : map.toString())
                        .append('\n');
            }
        }
        return result.toString();
    }

    public void clear(K key) {
        // TODO устранить дублирование с циклом выше
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                Multimap<K, V> map = field[x][y];
                if (map != null) {
                    map.removeKey(key);
                }
            }
        }
    }
}
