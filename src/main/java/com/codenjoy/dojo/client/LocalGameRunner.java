package com.codenjoy.dojo.client;

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


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.GameType;
import com.codenjoy.dojo.services.PlayerCommand;
import com.codenjoy.dojo.services.hash.Hash;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public class LocalGameRunner {

    public static final String SEP = "------------------------------------------";

    public static int timeout = 10;
    public static Consumer<String> out = System.out::println;
    public static Integer countIterations = null;

    private GameField field;
    private List<Game> games;
    private GameType gameType;
    private List<Solver> solvers;
    private List<ClientBoard> boards;

    public static LocalGameRunner run(GameType gameType, Solver solver, ClientBoard board) {
        return run(gameType, Arrays.asList(solver), Arrays.asList(board));
    }

    public static LocalGameRunner run(GameType gameType,
                           List<Solver> solvers,
                           List<ClientBoard> boards)
    {
        LocalGameRunner runner = new LocalGameRunner(gameType);

        for (int i = 0; i < solvers.size(); i++) {
            runner.add(solvers.get(i), boards.get(i));
        }

        return runner.run(() -> {});
    }

    public LocalGameRunner(GameType gameType) {
        this.gameType = gameType;

        solvers = new LinkedList<>();
        boards = new LinkedList<>();
        games = new LinkedList<>();

        field = gameType.createGame(0);
    }

    public LocalGameRunner run(Runnable tick) {
        Integer count = countIterations;
        while (count == null || count-- > 0) {
            if (timeout > 0) {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            synchronized (this) {
                List<String> answers = new LinkedList<>();
                for (Game game : games) {
                    answers.add(askAnswer(games.indexOf(game)));
                }

                for (Game game : games) {
                    int index = games.indexOf(game);
                    String answer = answers.get(index);

                    if (answer != null) {
                        new PlayerCommand(game.getJoystick(), answer).execute();
                    }
                }

                field.tick();
                for (int index = 0; index < games.size(); index++) {
                    Game single = games.get(index);
                    if (single.isGameOver()) {
                        out.accept(player(index, "PLAYER_GAME_OVER -> START_NEW_GAME"));
                        single.newGame();
                    }
                }

                out.accept(SEP);
            }

            tick.run();
        }
        return this;
    }

    private String askAnswer(int index) {
        ClientBoard board = board(index);

        Object data = game(index).getBoardAsString();
        board.forString(data.toString());

        out.accept(player(index, board.toString()));

        String answer = solver(index).get(board);

        out.accept(player(index, "Answer: " + answer));
        return answer;
    }

    private Solver solver(int index) {
        return solvers.get(index);
    }

    private ClientBoard board(int index) {
        return boards.get(index);
    }

    public synchronized void add(Solver solver, ClientBoard board) {
        solvers.add(solver);
        boards.add(board);
        games.add(createGame());
    }

    public synchronized void remove(Solver solver) {
        int index = solvers.indexOf(solver);
        solvers.remove(index);
        boards.remove(index);
        Game game = games.remove(index);
        field.remove(game.getPlayer());
    }

    private Game game(int index) {
        return games.get(index);
    }

    public static Dice getDice(int... numbers) {
        int[] index = {0};
        return (n) -> {
            int next = numbers[index[0]];
            out.accept("DICE:" + next);
            if (next >= n) {
                next = next % n;
                out.accept("DICE_CORRECTED < " + n + " :" + next);
            }
            if (++index[0] == numbers.length) {
                index[0]--; // повторять последнее число если мы в конце массива
            }
            return next;
        };
    }

    private String player(int index, String message) {
        String preffix = (index + 1) + ":";
        return preffix + message.replaceAll("\\n", "\n" + preffix);
    }

    private Game createGame() {
        GamePlayer gamePlayer = gameType.createPlayer(
                event -> out.accept("Fire Event: " + event.toString()),
                Hash.getRandomId());

        PrinterFactory factory = gameType.getPrinterFactory();

        Game game = new Single(gamePlayer, factory);
        game.on(field);
        game.newGame();
        return game;
    }

}
