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

import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class LevelsSettingsImpl extends SettingsImpl
        implements SettingsReader<LevelsSettingsImpl>,
                   LevelsSettings<LevelsSettingsImpl> {

    public LevelsSettingsImpl() {
        initLevels();
    }

    public LevelsSettingsImpl(Settings settings) {
        initLevels();
        if (settings != null) {
            copyFrom(settings.getParameters().stream()
                    .filter(parameter -> parameter.getName().startsWith(LEVELS))
                    .collect(toList()));
        }
    }

    @Override
    public List<Key> allKeys() {
        return LevelsSettings.allLevelsKeys();
    }
}
