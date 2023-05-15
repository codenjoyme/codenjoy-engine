package com.codenjoy.dojo.utils.test;

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

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.MultiplayerType;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import lombok.experimental.UtilityClass;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@UtilityClass
public class DealsUtils {

    public static class Env {
        public Deal deal;
        public Joystick joystick;
        public GamePlayer gamePlayer;
        public GameType gameType;
        public PrinterFactory printerFactory;
    }

    public static Env getDeal(Deals deals,
                              Player player,
                              String room,
                              Answer<Object> getGame,
                              MultiplayerType type,
                              PlayerSave save,
                              Printer printer)
    {
        Joystick joystick = mock(Joystick.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getJoystick()).thenReturn(joystick);

        GameType gameType = player.getGameType();
        if (gameType == null) {
            gameType = mock(GameType.class);
            if (player.getClass().getSimpleName().contains("Mock")) {
                when(player.getGameType()).thenReturn(gameType);
            } else {
                player.setGameType(gameType);
            }
        }

        if (gameType instanceof RoomGameType) {
            gameType = ((RoomGameType)gameType).getWrapped();
        }

        when(gameType.getMultiplayerType(any())).thenReturn(type);
        doAnswer(getGame).when(gameType).createGame(anyInt(), any());
        PrinterFactory printerFactory = mock(PrinterFactory.class);
        when(gameType.getPrinterFactory()).thenReturn(printerFactory);
        when(printerFactory.getPrinter(any(BoardReader.class), any()))
                .thenAnswer(inv1 -> printer);
        when(gameType.createPlayer(any(EventListener.class), anyInt(), anyString(), any()))
                .thenAnswer(inv -> gamePlayer);

        Deal deal = deals.add(player, room, save);
        Env result = new Env();
        result.gamePlayer = gamePlayer;
        result.gameType = gameType;
        result.joystick = joystick;
        result.deal = deal;
        result.printerFactory = printerFactory;
        return result;
    }
}
