package com.codenjoy.dojo.services.printer;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.field.Multimap;
import com.codenjoy.dojo.services.field.PointField;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.toList;

/**
 * Этот малый умеет печатать состояние борды на экране.
 * @see PrinterImpl#print(Object...)
  */
class PrinterImpl implements Printer<String> {
    public static final char ERROR_SYMBOL = 'Ъ';
    private char[][] field;
    private GamePrinter printer;

    public static <E extends CharElement, P> Printer getPrinter(BoardReader reader, P player) {
        return new PrinterImpl(new GamePrinterImpl<E, P>(reader, player));
    }

    public PrinterImpl(GamePrinter printer) {
        this.printer = printer;
    }

    /**
     * @return Строковое представление борды будет отправлено фреймворку
     * и на его основе будет отрисована игра на клиенте.
     */
    @Override
    public String print(Object... parameters) {
        fillField();

        StringBuilder string = new StringBuilder();
        for (char[] row : field) {
            for (char ch : row) {
                if (ch == '\0') ch = ' '; // TODO а это так для всех игр?
                string.append(ch);
            }
            string.append("\n");
        }
        return string.toString();
    }

    private void fillField() {
        int size = printer.size();
        field = new char[size][size];
        printer.init();

        printer.printAll(PrinterImpl.this::set);
    }

    private void set(int x, int y, char ch) {
        if (x == -1 || y == -1) { // TODO убрать это
            return;
        }

        if (ch == ERROR_SYMBOL) {
            throw new IllegalArgumentException(String.format("Обрати внимание на поле - в месте %s появился " +
                    "null Element. И как только он туда попал?\n", pt(x, y)));
        }

        field[printer.size() - 1 - y][x] = ch;
    }

    static class GamePrinterImpl<E extends CharElement, P> implements GamePrinter {

        private final BoardReader board;
        private P player;
        private PointField field;
        private List<Class> order;

        public GamePrinterImpl(BoardReader board, P player) {
            this.board = board;
            this.player = player;
        }

        @Override
        public void init() {
            field = board.elements(player);
            order = board.order();
        }

        @Override
        public int size() {
            return board.size();
        }

        @Override
        public void printAll(Filler filler) {
            for (int x = 0; x < size(); x++) {
                for (int y = 0; y < size(); y++) {
                    Multimap<Class<? extends Point>, Point> map = field.getOnly(x, y);

                    if (map == null || map.isEmpty()) {
                        continue;
                    }

                    List<Point> elements = new LinkedList<>();
                    for (Class clazz : order) {
                        List list = map.getOnly(clazz);
                        if (list != null) {
                            elements.addAll(list);
                        }
                    }

                    for (Point pt : elements) {
                        E el = (E)((State) pt).state(player, elements);
                        if (el != null) {
                            filler.set(pt.getX(), pt.getY(), el.ch());
                            break;
                        }
                    }
                }
            }
        }
    }
}
