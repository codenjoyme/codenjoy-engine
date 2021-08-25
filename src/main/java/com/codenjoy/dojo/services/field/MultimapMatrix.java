package com.codenjoy.dojo.services.field;

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
        Multimap<K, V> list = field[x][y];
        if (list == null) {
            list = field[x][y] = new Multimap<>();
        }
        return list;
    }

    public String toString(Multimap<K, V>[][] field) {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                Multimap<K, V> list = field[x][y];
                result.append(pt(x, y))
                        .append(":")
                        .append(list == null || list.isEmpty() ? "{}" : list.toString())
                        .append('\n');
            }
        }
        return result.toString();
    }
}
