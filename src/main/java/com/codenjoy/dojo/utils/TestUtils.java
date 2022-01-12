package com.codenjoy.dojo.utils;

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


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.profile.Profiler;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.algs.DeikstraFindWay;
import com.codenjoy.dojo.services.dice.RandomDice;
import com.codenjoy.dojo.services.multiplayer.*;
import com.codenjoy.dojo.services.printer.CharElement;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.services.settings.Settings;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.codenjoy.dojo.client.Utils.split;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static com.codenjoy.dojo.services.multiplayer.GamePlayer.DEFAULT_TEAM_ID;
import static com.codenjoy.dojo.utils.core.MockitoJunitTesting.testing;
import static java.util.stream.Collectors.joining;

@UtilityClass
public class TestUtils {

    public static final int COUNT_NUMBERS = 3;

    public static String injectN(String expected) {
        int size = (int) Math.sqrt(expected.length());
        return inject(expected, size, "\n");
    }

    public static String injectNN(String expected) {
        int size = (int) Math.sqrt(expected.length()/COUNT_NUMBERS)*COUNT_NUMBERS;
        return inject(expected, size, "\n");
    }

    public static String inject(String string, int position, String substring) {
        StringBuilder result = new StringBuilder();
        for (int index = 1; index < string.length() / position + 1; index++) {
            result.append(string, (index - 1)*position, index*position).append(substring);
        }
        result.append(string.substring((string.length() / position) * position));
        return result.toString();
    }

    public static List<Game> getGames(int players, GameType runner, PrinterFactory factory, Supplier<EventListener> listener) {
        GameField field = TestUtils.buildField(runner);
        List<Game> games = new LinkedList<>();
        for (int i = 0; i < players; i++) {
            games.add(TestUtils.buildSingle(runner, field, listener.get(), factory));
        }
        return games;
    }

    public static Game buildGame(GameType gameType, EventListener listener, PrinterFactory factory) {
        GameField gameField = buildField(gameType);
        return buildSingle(gameType, gameField, listener, factory);
    }

    public static Game buildSingle(GameType gameType, GameField gameField, EventListener listener, PrinterFactory factory) {
        Settings settings = gameType.getSettings();
        GamePlayer gamePlayer = gameType.createPlayer(listener, DEFAULT_TEAM_ID, null, settings);
        Game game = new Single(gamePlayer, factory);
        game.on(gameField);
        game.newGame();
        return game;
    }

    public static String getWay(String inputBoard,
                                CharElement[] elements,
                                Function<AbstractBoard, DeikstraFindWay.Possible> possible)
    {
        AbstractBoard board = getBoard(elements);
        board = (AbstractBoard) board.forString(inputBoard);

        Map<Point, List<Direction>> ways = new DeikstraFindWay().getPossibleWays(board.size(), possible.apply(board)).toMap();

        Map<Point, List<Direction>> map = new TreeMap<>();
        for (Map.Entry<Point, List<Direction>> entry : ways.entrySet()) {
            List<Direction> value = entry.getValue();
            if (!value.isEmpty()) {
                map.put(entry.getKey(), value);
            }
        }

        return split(map, "], \n[");
    }

    public static AbstractBoard getBoard(CharElement[] elements) {
        return new AbstractBoard() {
            @Override
            public CharElement[] elements() {
                return elements;
            }

            @Override
                protected int inversionY(int y) {
                    return size - 1 - y;
                }
            };
    }

    public static String drawPossibleWays(int delta,
                                          Map<Point, List<Direction>> possibleWays,
                                          int size,
                                          Function<Point, Character> getAt)
    {
        char[][] chars = new char[size * delta][size * delta];
        for (int x = 0; x < chars.length; x++) {
            Arrays.fill(chars[x], ' ');
        }

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int cx = x * delta + 1;
                int cy = y * delta + 1;

                char ch = getAt.apply(pt(x, y));
                chars[cx][cy] = (ch == ' ') ? '.' : ch;
                try {
                    for (Direction direction : possibleWays.get(pt(x, y))) {
                        chars[direction.changeX(cx)][direction.changeY(cy)] = directionChar(direction);
                    }
                } catch (NullPointerException e) {
                    // do nothing
                }
            }
        }

        return toString(chars);
    }

    private static char directionChar(Direction direction) {
        switch (direction) {
            case UP: return '↑';
            case LEFT: return '←';
            case RIGHT: return '→';
            case DOWN: return '↓';
            default: throw new IllegalArgumentException();
        }
    }

    public static String drawShortestWay(Point from,
                                         List<Direction> shortestWay,
                                         int size,
                                         Function<Point, Character> getAt)
    {
        Map<Point, List<Direction>> map = new HashMap<>();

        Point current = from;
        while (!shortestWay.isEmpty()) {
            Direction direction = shortestWay.remove(0);
            map.put(current, Arrays.asList(direction));
            current = direction.change(current);
        }

        return drawPossibleWays(2, map, size, getAt);
    }

    private static String toString(char[][] chars) {
        StringBuffer buffer = new StringBuffer();
        for (int x = 0; x < chars.length; x++) {
            for (int y = 0; y < chars.length; y++) {
                buffer.append(chars[y][chars.length - 1 - x]);
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }

    public static GameField buildField(GameType gameType) {
        Settings settings = gameType.getSettings();
        return gameType.createGame(LevelProgress.levelsStartsFrom1, settings);
    }

    public static String printWay(String expected,
                                  CharElement from, CharElement to,
                                  CharElement none, CharElement wayChar,
                                  AbstractBoard board,
                                  Function<AbstractBoard, DeikstraFindWay.Possible> possible)
    {
        expected = expected.replace(wayChar.ch(), none.ch())
                    .replaceAll("\n", "");
        board = (AbstractBoard) board.forString(expected);
        List<Point> starts = board.get(from);
        Point start = starts.get(0);
        List<Point> goals = board.get(to);
        List<Direction> way = new DeikstraFindWay()
                .getShortestWay(board.size(),
                        start, goals,
                        possible.apply(board));

        Point current = start;
        for (int index = 0; index < way.size(); index++) {
            Direction direction = way.get(index);
            current = direction.change(current);

            CharElement element = (index == way.size() - 1) ? to : wayChar;
            board.set(current.getX(), current.getY(), element.ch());
        }

        return board.boardAsString();
    }

    /**
     * Метод удобно пробежится по всем Player достанет из них Hero и дернет по рефлексии
     * запрашиваемый метод, подготовит карту и приведет ее к строке.
     * @param players Список игроков Player.
     * @param methodName Имя вызываемого метода в Hero.
     * @param skipDefault Пропускать ли default значения (Integer = 0, String = "")
     * @return Карта индексов игроков и связанных с ней результатов выполнения
     *         метода соответствующего Hero.
     */
    @SneakyThrows
    public static String collectHeroesData(List<? extends GamePlayer> players, String methodName, boolean skipDefault) {
        Map<Integer, Object> map = new LinkedHashMap<>();
        for (int index = 0; index < players.size(); index++) {
            GamePlayer player = players.get(index);
            PlayerHero hero = player.getHero();

            Method method = hero.getClass().getMethod(methodName);
            Object result = method.invoke(hero);

            if (skipDefault) {
                if ((result instanceof String && StringUtils.isEmpty((String)result))
                        || (result instanceof Integer && (Integer)result == 0))
                    continue;
            }

            map.put(index, result);
        }
        return map.entrySet().stream()
                .map(entry -> String.format("hero(%s)=%s",
                        entry.getKey(),
                        entry.getValue()))
                .collect(joining("\n"));
    }

    public static int[] asArray(List<? extends Point> points) {
        return points.stream()
                .flatMap(Point::stream)
                .mapToInt(value -> value)
                .toArray();
    }

    /**
     * Метод проверяет есть ли все изображения спрайтов в формате png в сырцах игры.
     * @param game Проверяемая игра.
     * @param elements Elements этой игры.
     */
    public static void assertSprites(String game, CharElement[] elements) {
        List<String> errors = new LinkedList<>();

        // when then
        for (CharElement element : elements) {
            String path = "./src/main/webapp/resources/%s/sprite/%s.png";
            File file = new File(String.format(path, game, element.name().toLowerCase()));
            if (!file.exists()) {
                errors.add("Sprite not found: " + file.getAbsolutePath());
            }
        }

        // then
        testing().assertEquals("[]", split(errors, ", \nSprite"));
    }

    /**
     * Метод прогоняет всю игру как интеграционный тест для заданного количества
     * юзеров на протяжении заданного количества тиков и подсчитывает
     * сумарное время определенных операций.
     * @param runner Игра.
     * @param players Количество игроков.
     * @param ticks Количество тиков.
     * @param expectedCreation Ожидаемое время создания игры.
     * @param expectedTick Ожидаемое время тиков.
     * @param expectedPrint Ожидаемое время выполнения печати поля на экране.
     * @param printBoard Печатать ли борду в консоли каждый тик (для отладки).
     */
    public static void assertPerformance(AbstractGameType runner,
                                         int players,
                                         int ticks,
                                         int expectedCreation,
                                         int expectedTick,
                                         int expectedPrint,
                                         boolean printBoard)
    {
        Profiler profiler = new Profiler(){{
            PRINT_SOUT = true;
        }};
        profiler.start();

        PrinterFactory factory = new PrinterFactoryImpl();

        List<Game> games = TestUtils.getGames(players, runner,
                factory, () -> testing().mock(EventListener.class));

        profiler.done("creation");

        for (int i = 0; i < ticks; i++) {
            for (Game game : games) {
                Joystick joystick = game.getJoystick();
                int next = new RandomDice().next(5);
                if (next % 2 == 0) {
                    joystick.act();
                }
                switch (next) {
                    case 0: joystick.left(); break;
                    case 1: joystick.right(); break;
                    case 2: joystick.up(); break;
                    case 3: joystick.down(); break;
                }
            }
            // because of MULTIPLE there is only one tick for all
            games.get(0).getField().tick();
            for (Game game : games) {
                if (game.isGameOver()) {
                    game.newGame();
                }
            }
            profiler.done("tick");

            Object board = null;
            for (int j = 0; j < games.size(); j++) {
                board = games.get(j).getBoardAsString();
            }
            if (printBoard) {
                System.out.println(board);
            }
            profiler.done("print");
        }

        profiler.print();

        int reserve = 3;
        // выполнялось единожды
        assertLess(profiler, "creation", expectedCreation * reserve);
        // сколько пользователей - столько раз выполнялось
        assertLess(profiler, "print", expectedPrint * reserve);
        assertLess(profiler, "tick", expectedTick * reserve);
    }

    private void assertLess(Profiler profiler, String phase, double expected) {
        double actual = profiler.info(phase).getTime();
        testing().assertEquals(actual + " > " + expected, true, actual < expected);
    }
}