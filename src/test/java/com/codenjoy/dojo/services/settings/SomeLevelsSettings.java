package com.codenjoy.dojo.services.settings;

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

import com.codenjoy.dojo.services.level.LevelsSettings;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.services.settings.SomeLevelsSettings.Keys.*;

public class SomeLevelsSettings extends SettingsImpl
        implements LevelsSettings<SomeLevelsSettings> {

    public enum Keys implements Key {

        PARAMETER1("[Level] Map[1,1]"),
        PARAMETER2("[Level] Map[1,2]"),
        PARAMETER3("[Level] Map[1,3]"),
        PARAMETER4("[Level] Map[2]"),
        PARAMETER5("[Level] Map[3,1]"),
        PARAMETER6("[Level] Map[3,2]"),
        PARAMETER7("[Level] Map[4,1]"),
        PARAMETER8("[Level] Map[5]");

        private String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    @Override
    public List<Key> allKeys() {
        return Arrays.asList(Keys.values());
    }

    public SomeLevelsSettings() {
        initLevels();

        string(PARAMETER1, "map1");
        string(PARAMETER2, "map2");
        string(PARAMETER3, "map3");
        string(PARAMETER4, "map4");
        string(PARAMETER5, "map5");
        string(PARAMETER6, "map6");
        string(PARAMETER7, "map7");
        string(PARAMETER8, "map8");
    }

    @Override
    public String toString() {
        return "Some" + super.toStringShort();
    }
}
