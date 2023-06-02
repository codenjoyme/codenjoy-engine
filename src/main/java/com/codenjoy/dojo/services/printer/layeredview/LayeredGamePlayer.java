package com.codenjoy.dojo.services.printer.layeredview;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2023 Codenjoy
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

import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.settings.SettingsReader;

public abstract class LayeredGamePlayer<H extends PlayerHero, F extends GameField> extends GamePlayer<H, F> {

    private Printer<PrinterData> printer;

    public LayeredGamePlayer(EventListener listener, SettingsReader settings) {
        super(listener, settings);
        setupPrinter();
    }

    void setupPrinter() {
        printer = new LayeredViewPrinter<>(
                () -> (LayeredField) field,
                () -> this);
    }

    public Printer<PrinterData> getPrinter() {
        return printer;
    }
}
