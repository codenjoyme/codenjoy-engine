package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class Generator {

    public static <T> void generate(Accessor<T> list,
                                    SettingsReader settings,
                                    SettingsReader.Key key,
                                    Function<? extends GamePlayer, Optional<Point>> freeRandom,
                                    Function<Point, T> creator)
    {
        if (settings.integer(key) < 0) {
            settings.integer(key, 0);
        }

        int count = Math.max(0, settings.integer(key));
        int added = count - list.size();
        if (added == 0) {
            return;
        } else if (added < 0) {
            // удаляем из существующих
            // важно оставить текущие, потому что метод работает каждый тик
            list.remove(count, list.size());
        } else {
            // добавляем недостающих к тем что есть
            for (int index = 0; index < added; index++) {
                Optional<Point> pt = freeRandom.apply(null);
                if (pt.isPresent()) {
                    list.add(creator.apply(pt.get()));
                }
            }
        }
    }

    // TODO удалить после того как Loderunner будет переведен на PointField
    public static <T> void generate(List<T> list,
                                    SettingsReader settings,
                                    SettingsReader.Key key,
                                    Function<? extends GamePlayer, Optional<Point>> freeRandom,
                                    Function<Point, T> creator)
    {
        generate(accessor(list), settings, key, freeRandom, creator);
    }

    // TODO удалить после того как Loderunner будет переведен на PointField
    private static <T> Accessor<T> accessor(List<T> list) {
        return new Accessor<T>() {
            @Override
            public <E2 extends Point> boolean contains(E2 element) {
                throw exception();
            }

            private UnsupportedOperationException exception() {
                return new UnsupportedOperationException();
            }

            @Override
            public <E2 extends Point> boolean removeExact(E2 element) {
                throw exception();
            }

            @Override
            public <E2 extends Point> boolean remove(E2 element) {
                throw exception();
            }

            @Override
            public List<T> all() {
                throw exception();
            }

            @Override
            public Stream<T> stream() {
                throw exception();
            }

            @Override
            public void removeNotSame(List<T> valid) {
                throw exception();
            }

            @Override
            public void add(T element) {
                list.add(element);
            }

            @Override
            public int size() {
                return list.size();
            }

            @Override
            public void clear() {
                throw exception();
            }

            @Override
            public <E2 extends Point> void removeIn(List<E2> elements) {
                throw exception();
            }

            @Override
            public void addAll(List<T> elements) {
                throw exception();
            }

            @Override
            public <E2 extends Point> List<T> getAt(E2 point) {
                throw exception();
            }

            @Override
            public List<T> copy() {
                throw exception();
            }

            @Override
            public void tick() {
                throw exception();
            }

            @Override
            public void remove(int from, int to) {
                list.subList(from, to).clear();
            }

            @Override
            public Iterator<T> iterator() {
                throw exception();
            }
        };
    }
}