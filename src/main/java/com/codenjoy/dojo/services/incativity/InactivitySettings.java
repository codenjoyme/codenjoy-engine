package com.codenjoy.dojo.services.incativity;

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

import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.codenjoy.dojo.services.incativity.InactivitySettings.Keys.*;
import static com.codenjoy.dojo.services.incativity.InactivitySettingsImpl.INACTIVITY;

public interface InactivitySettings<T extends SettingsReader> extends SettingsReader<T> {

    String INACTIVITY = "[Inactivity]";

    public enum Keys implements SettingsReader.Key {

        INACTIVITY_ENABLED(INACTIVITY + " Kick inactive players"),
        INACTIVITY_TIMEOUT(INACTIVITY + " Inactivity timeout ticks");

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    static boolean is(Settings settings) {
        if (settings == null) return false;

        return settings instanceof InactivitySettings
                || allInactivityKeys().stream()
                        .map(Key::key)
                        .allMatch(settings::hasParameter);
    }

    static InactivitySettingsImpl get(Settings settings) {
        if (InactivitySettings.is(settings)) {
            return new InactivitySettingsImpl(settings);
        }

        return new InactivitySettingsImpl(null);
    }

    static List<SettingsReader.Key> allInactivityKeys() {
        return Arrays.asList(Keys.values());
    }

    default void initInactivity() {
        bool(INACTIVITY_ENABLED, false);
        integer(INACTIVITY_TIMEOUT, 5*60);
    }

    // parameters getters

    default Parameter<Boolean> kickEnabled() {
        return boolValue(INACTIVITY_ENABLED);
    }

    default Parameter<Integer> inactivityTimeout() {
        return integerValue(INACTIVITY_TIMEOUT);
    }

    // update methods

    // TODO test me
    default List<Parameter> getInactivityParams() {
        if (getParameters().isEmpty()) {
            return Arrays.asList();
        }
        return new LinkedList<>(){{
            add(kickEnabled());
            add(inactivityTimeout());
        }};
    }

    default InactivitySettings update(InactivitySettings input) {
        setKickEnabled(input.isKickEnabled());
        setInactivityTimeout(input.getInactivityTimeout());
        return this;
    }

    default InactivitySettings updateInactivity(Settings input) {
        if (input != null) {
            allInactivityKeys().stream()
                    .map(Key::key)
                    .forEach(key -> getParameter(key).update(input.getParameter(key).getValue()));
        }
        return this;
    }

    // getters

    default boolean isKickEnabled() {
        return kickEnabled().getValue();
    }

    default int getInactivityTimeout() {
        return inactivityTimeout().getValue();
    }

    // setters

    default InactivitySettings setKickEnabled(boolean input) {
        kickEnabled().update(input);
        return this;
    }

    default InactivitySettings setInactivityTimeout(int input) {
        inactivityTimeout().update(input);
        return this;
    }
}
