package com.codenjoy.dojo.services.printer;

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

import com.codenjoy.dojo.games.sample.Element;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.printer.state.State;
import com.codenjoy.dojo.utils.JsonUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PrinterFactoryTest {

    @Test
    public void testGet_caseGraphicPrinter1() {
        // given
        PrinterFactory factory = PrinterFactory.get((BoardReader reader, GamePlayer player, Object... parameters) -> {
            JSONObject result = new JSONObject();
            result.put("board", Arrays.toString(parameters));
            return result;
        });

        BoardReader reader = mock(BoardReader.class);
        GamePlayer player = mock(GamePlayer.class);

        // when
        Printer printer = factory.getPrinter(reader, player);
        Object string = printer.print(true, 1, "string");

        // then
        assertEquals("{\n" +
                        "  'board':'[true, 1, string]'\n" +
                        "}",
                JsonUtils.prettyPrint(string));
    }

    public static class Wall extends PointImpl implements State<Element, GamePlayer> {

        public Wall(Point point) {
            super(point);
        }

        @Override
        public Element state(GamePlayer player, Object... alsoAtPoint) {
            return Element.WALL;
        }
    }

    @Test
    public void testGet_caseGraphicPrinter2() {
        // given
        PrinterFactory factory = PrinterFactory.get((BoardReader reader, Printer<String> printer, GamePlayer player, Object... parameters) -> {
            String data = printer.print();

            JSONObject result = new JSONObject();
            result.put("board", Arrays.toString(parameters));
            result.put("printer", data);
            return result;
        });

        BoardReader<GamePlayer> reader = new BoardReader<>() {
            @Override
            public int size() {
                return 3;
            }

            @Override
            public void addAll(GamePlayer player, Consumer<Iterable<? extends Point>> processor) {
                processor.accept(Arrays.asList(
                        new Wall(pt(0, 0)),
                        new Wall(pt(1, 1)),
                        new Wall(pt(2, 2))));
            }
        };

        GamePlayer player = mock(GamePlayer.class);

        // when
        Printer printer = factory.getPrinter(reader, player);
        Object string = printer.print(true, 1, "string");

        // then
        assertEquals("{\n" +
                        "  'board':'[true, 1, string]',\n" +
                        "  'printer':'  ☼\\n ☼ \\n☼  \\n'\n" +
                        "}",
                JsonUtils.prettyPrint(string));
    }
}