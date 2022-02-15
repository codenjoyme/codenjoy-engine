package com.codenjoy.dojo.services.semifinal;

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

import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.List;
import java.util.function.Supplier;

public class SemifinalSettingsImpl extends SettingsImpl
        implements SettingsReader<SemifinalSettingsImpl>,
                SemifinalSettings<SemifinalSettingsImpl> {

    private SemifinalSettings settings;

    public SemifinalSettingsImpl() {
        initSemifinal();
    }

    public SemifinalSettingsImpl(Settings settings) {
        if (settings == null || settings instanceof SemifinalSettings) {
            // используем как декоратор
            this.settings = (SemifinalSettings) settings;
        } else {
            // инициализируем и копируем
            initSemifinal();
            updateSemifinal(settings);
        }
    }

    @Override
    public Parameter<?> getParameter(String name, Supplier<Parameter<?>> ifNull) {
        if (settings != null) {
            return settings.getParameter(name, ifNull);
        } else {
            return super.getParameter(name, ifNull);
        }
    }

    @Override
    public List<Parameter> getParameters() {
        if (settings != null) {
            return settings.getParameters();
        } else {
            return super.getParameters();
        }
    }

    @Override
    public String toString() {
        return (settings != null) ? settings.toString() : super.toString();
    }

    @Override
    public List<Key> allKeys() {
        return SemifinalSettings.allSemifinalKeys();
    }
}
