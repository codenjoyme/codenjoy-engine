package com.codenjoy.dojo.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
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

import com.codenjoy.dojo.services.multiplayer.GamePlayer;

@FunctionalInterface
public interface PrinterFactory <E extends CharElements, P extends GamePlayer> {
    Printer getPrinter(BoardReader reader, P player);

    /**
     * @param printer Кастомный принтер, который будет прорисовывать борду.
     * @param <P> Тип объекта-игрока в игре (не путать с Player во время регистрации пользователя)
     * @return Фабрика, которая потом создаст кастомный принтер.
     */
    static <P extends GamePlayer> PrinterFactory get(GraphicPrinter<Object, P> printer) {
        // во закрутил :)
        return (PrinterFactory<CharElements, GamePlayer>) (reader, player)
                -> parameters -> printer.print(reader, (P)player);
    }
}
