package com.codenjoy.dojo.services.field;

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

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Optional;
import java.util.function.Function;

public class Generator {

    public static <T> void generate(Accessor<T> list,
                                    SettingsReader settings,
                                    SettingsReader.Key key,
                                    Function<? extends GamePlayer, Optional<Point>> freeRandom,
                                    Function<Point, T> creator) {
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
            generate(list, added, freeRandom, creator);
        }
    }

    public static <T> void generate(Accessor<T> list, int count,
                                    Function<? extends GamePlayer, Optional<Point>> freeRandom,
                                    Function<Point, T> creator) {
        // добавляем недостающих к тем что есть
        for (int index = 0; index < count; index++) {
            Optional<Point> pt = freeRandom.apply(null);
            pt.ifPresent(point -> list.add(creator.apply(point)));
        }
    }
}
