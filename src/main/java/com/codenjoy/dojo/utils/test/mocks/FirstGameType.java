package com.codenjoy.dojo.utils.test.mocks;

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
import com.codenjoy.dojo.services.multiplayer.MultiplayerType;
import com.codenjoy.dojo.services.printer.CharElement;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SimpleParameter;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class FirstGameType extends FakeGameType {

    @Override
    public Parameter<Integer> getBoardSize(Settings settings) {
        return new SimpleParameter<>(6);
    }

    @Override
    public SettingsImpl getSettings() {
        return new FirstGameSettings();
    }

    @Override
    public String name() {
        return "first";
    }

    public enum Element implements CharElement {

        NONE(' '),
        WALL('☼'),
        HERO('☺');

        final char ch;

        Element(char ch) {
            this.ch = ch;
        }

        @Override
        public char ch() {
            return ch;
        }

        @Override
        public String toString() {
            return String.valueOf(ch);
        }

    }

    @Override
    public Element[] getPlots() {
        return Element.values();
    }

    @Override
    public MultiplayerType getMultiplayerType(Settings settings) {
        return MultiplayerType.SINGLE;
    }

    @Override
    public Point heroAt() {
        return pt(1, 1);
    }

    @Override
    public CharElement getHeroElement() {
        return Element.HERO;
    }

    @Override
    public String getVersion() {
        return "version 1.11b";
    }
}
