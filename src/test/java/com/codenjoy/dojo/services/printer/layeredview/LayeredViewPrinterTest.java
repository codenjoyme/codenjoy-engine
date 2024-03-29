package com.codenjoy.dojo.services.printer.layeredview;

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
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;
import com.codenjoy.dojo.services.multiplayer.TriFunction;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.printer.state.State;
import com.codenjoy.dojo.services.settings.SettingsReader;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class LayeredViewPrinterTest {

    private Point viewCenter;
    private Printer printer;
    private Point heroPosition;
    private Point enemyPosition;
    private PrinterData board;

    enum Element {

        HERO('☺'), ENEMY('☻'), DOT('.'), CIRCLE('o'),
        AIR_OVER_CIRCLE('+'), AIR_OVER_DOT('*'), AIR_OVER_PLAYERS('~');

        private char ch;

        Element(char ch) {
            this.ch = ch;
        }


        @Override
        public String toString() {
            return String.valueOf(ch);
        }
    }

    static class Enemy extends PointImpl implements State {

        public Enemy(Point point) {
            super(point);
        }

        @Override
        public Element state(Object player, Object... alsoAtPoint) {
            return Element.ENEMY;
        }
    }

    static class Hero extends PointImpl implements State {

        public Hero(Point point) {
            super(point);
        }

        @Override
        public Element state(Object player, Object... alsoAtPoint) {
            return Element.HERO;
        }
    }

    static class Dot extends PointImpl implements State {

        public Dot(Point point) {
            super(point);
        }

        @Override
        public Element state(Object player, Object... alsoAtPoint) {
            return Element.DOT;
        }
    }

    static class Circle extends PointImpl implements State {

        public Circle(Point point) {
            super(point);
        }

        @Override
        public Element state(Object player, Object... alsoAtPoint) {
            return Element.CIRCLE;
        }
    }

    static class Air extends PointImpl implements State {

        public Air(Point point) {
            super(point);
        }

        @Override
        public Element state(Object player, Object... alsoAtPoint) {
            if (alsoAtPoint[0] instanceof Hero || alsoAtPoint[0] instanceof Enemy) {
                return Element.AIR_OVER_PLAYERS;
            } else if (alsoAtPoint[0] instanceof Circle) {
                return Element.AIR_OVER_CIRCLE;
            } else if (alsoAtPoint[0] instanceof Dot) {
                return Element.AIR_OVER_DOT;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @Before
    public void setup() {
        // given
        int boardSize = 20;
        int viewSize = 7;
        int countLayers = 2;
        heroPosition = pt(13, 14);
        enemyPosition = pt(11, 16);

        LayeredField<LayeredGamePlayer, PlayerHero> reader = new LayeredField<>() {
            @Override
            public int countLayers() {
                return countLayers;
            }

            @Override
            public int size() {
                return boardSize;
            }

            @Override
            public int viewSize() {
                return viewSize;
            }

            @Override
            public TriFunction<Integer, Integer, Integer, State> elements() {
                return (x, y, layer) -> {
                    Point pt = pt(x, y);
                    if (layer == 1) {
                        return new Air(pt);
                    }

                    if (pt.itsMe(heroPosition)) {
                        return new Hero(pt);
                    }
                    if (pt.itsMe(enemyPosition)) {
                        return new Enemy(pt);
                    } else if (pt.getX() % 2 == 0 ^ pt.getY() % 2 == 0) {
                        return new Dot(pt);
                    } else {
                        return new Circle(pt);
                    }
                };
            }

            @Override
            public Point viewCenter(LayeredGamePlayer player) {
                return viewCenter;
            }

            @Override
            public Object[] itemsInSameCell(State item, int layer) {
                Point pt = (Point) item;
                State inCell = elements().apply(pt.getX(), pt.getY(), 0);
                if (inCell == item) {
                    return new Object[0];
                }
                return new Object[]{inCell};
            }

            @Override
            public void tick() {
                // do nothing
            }

            @Override
            public void newGame(LayeredGamePlayer player) {
                // do nothing
            }

            @Override
            public void remove(LayeredGamePlayer player) {
                // do nothing
            }

            @Override
            public SettingsReader settings() {
                return null; // do nothing
            }
        };
        LayeredGamePlayer player = mock(LayeredGamePlayer.class);

        printer = new LayeredViewPrinter<>(
                () -> reader,
                () -> player);
    }

    @Test
    public void printHeroView() {
        // when
        viewCenter = heroPosition;
        board = (PrinterData) printer.print();

        // then
        assertEquals("[9,10]", board.getOffset().toString());

        assertL(".o☻o.o.\n" +
                "o.o.o.o\n" +
                ".o.o☺o.\n" +
                "o.o.o.o\n" +
                ".o.o.o.\n" +
                "o.o.o.o\n" +
                ".o.o.o.\n", 0);

        assertL("*+~+*+*\n" +
                "+*+*+*+\n" +
                "*+*+~+*\n" +
                "+*+*+*+\n" +
                "*+*+*+*\n" +
                "+*+*+*+\n" +
                "*+*+*+*\n", 1);
    }

    @Test
    public void printEnemyView() {
        // when
        viewCenter = enemyPosition;
        board = (PrinterData) printer.print();

        // then
        assertEquals("[7,12]", board.getOffset().toString());

        assertL(".o.o.o.\n" +
                "o.o.o.o\n" +
                ".o.o☻o.\n" +
                "o.o.o.o\n" +
                ".o.o.o☺\n" +
                "o.o.o.o\n" +
                ".o.o.o.\n", 0);

        assertL("*+*+*+*\n" +
                "+*+*+*+\n" +
                "*+*+~+*\n" +
                "+*+*+*+\n" +
                "*+*+*+~\n" +
                "+*+*+*+\n" +
                "*+*+*+*\n", 1);
    }

    @Test
    public void printNearBorder() {
        // when
        viewCenter = pt(19, 19);
        board = (PrinterData) printer.print();

        // then
        assertEquals("[13,13]", board.getOffset().toString());

        assertL("o.o.o.o\n" +
                ".o.o.o.\n" +
                "o.o.o.o\n" +
                ".o.o.o.\n" +
                "o.o.o.o\n" +
                "☺o.o.o.\n" +
                "o.o.o.o\n", 0);

        assertL("+*+*+*+\n" +
                "*+*+*+*\n" +
                "+*+*+*+\n" +
                "*+*+*+*\n" +
                "+*+*+*+\n" +
                "~+*+*+*\n" +
                "+*+*+*+\n", 1);
    }

    @Test
    public void printMovingView() {
        // given
        printHeroView();

        // when
        viewCenter = enemyPosition;
        board = (PrinterData) printer.print();

        // then
        assertEquals("[7,13]", board.getOffset().toString());

        assertL("o.o.o.o\n" +
                ".o.o.o.\n" +
                "o.o.o.o\n" +
                ".o.o☻o.\n" +
                "o.o.o.o\n" +
                ".o.o.o☺\n" +
                "o.o.o.o\n", 0);

        assertL("+*+*+*+\n" +
                "*+*+*+*\n" +
                "+*+*+*+\n" +
                "*+*+~+*\n" +
                "+*+*+*+\n" +
                "*+*+*+~\n" +
                "+*+*+*+\n", 1);
    }

    void assertL(String expected, int layer) {
        assertEquals(expected, TestUtils.injectN(board.getLayers().get(layer)));
    }

}
