package com.codenjoy.dojo.client.local;

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


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.client.AbstractTextBoard;
import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.LevelProgress;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.settings.Settings;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.codenjoy.dojo.services.multiplayer.GamePlayer.DEFAULT_TEAM_ID;
import static java.util.stream.Collectors.toList;

public class LocalGameRunner {

    public static final String SEP = "------------------------------------------";

    private int timeout = 10;
    private boolean printScores = true;
    private boolean printWelcome = false;
    private boolean printBoardOnly = false;
    private Consumer<String> out = System.out::println;
    private Integer countIterations = null;
    private boolean printTick = false;
    private String showPlayers = null;
    private boolean exit = false;
    private int waitForPlayers = 1;
    private final int levelNumber = LevelProgress.levelsStartsFrom1;

    /**
     * Будем ли мы удалять (true) игрока с поля, когда он gameover
     * или всего лишь делать ему newGame (false).
     */
    private boolean removeWhenGameOver = false;

    /**
     * Будем ли мы удалять (true) игрока с поля, когда он win
     * или ничего предпринимать не будем (false).
     */
    private boolean removeWhenWin = false;

    /**
     * Будем ли мы возобновлять (true) игру всех игроков,
     * если они все покинули игру или нет (false).
     */
    private boolean reloadPlayersWhenGameOverAll = false;

    private Settings settings;
    private GameField field;
    private List<Game> games;
    private GameType gameType;
    private List<Solver<ClientBoard>> solvers;
    private List<ClientBoard> boards;
    private List<PlayerScores> scores;
    private Integer tick;

    public LocalGameRunner() {
        if (printWelcome) {
            print(VersionReader.getWelcomeMessage());
        }

        solvers = new LinkedList<>();
        boards = new LinkedList<>();
        games = new LinkedList<>();
        scores = new LinkedList<>();
    }

    public LocalGameRunner with(GameType gameType) {
        this.gameType = gameType;
        settings = gameType.getSettings();
        field = gameType.createGame(levelNumber, settings);
        return this;
    }

    public LocalGameRunner add(List<Solver> solvers, List<ClientBoard> boards) {
        for (int index = 0; index < solvers.size(); index++) {
            add(solvers.get(index), boards.get(index));
        }
        return this;
    }

    public void run() {
        run(tick -> {});
    }

    public void run(Consumer<Integer> onTick) {
        tick = 0;
        while (!exit && (countIterations == null || this.tick++ < countIterations)) {
            try {
                if (timeout > 0) {
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (reloadPlayersWhenGameOverAll && activeGames().isEmpty()) {
                    reloadAllGames();
                }

                if (games.size() < waitForPlayers) {
                    continue;
                }

                synchronized (this) {
                    debugAt(tick);

                    List<String> answers = new LinkedList<>();
                    for (Game game : games) {
                        int index = games.indexOf(game);
                        if (game.getField() == null) {
                            answers.add(null);
                        } else {
                            answers.add(askAnswer(index));
                        }
                    }

                    for (Game game : activeGames()) {
                        int index = games.indexOf(game);
                        String answer = answers.get(index);

                        if (answer != null) {
                            new PlayerCommand(game.getJoystick(), answer).execute();
                        }
                    }

                    for (Game game : activeGames()) {
                        int index = games.indexOf(game);
                        if (removeWhenWin) {
                            if (game.isWin() && game.shouldLeave()) {
                                print(index, "PLAYER_WIN -> REMOVE_FROM_GAME");
                                game.close();
                                continue;
                            }
                        }
                        if (game.isGameOver()) {
                            if (removeWhenGameOver) {
                                print(index, "PLAYER_GAME_OVER -> REMOVE_FROM_GAME");
                                game.close();
                            } else {
                                print(index, "PLAYER_GAME_OVER -> START_NEW_GAME");
                                game.newGame();
                            }
                        }
                    }

                    field.tick();

                    print(SEP);
                }

                if (onTick != null) {
                    onTick.accept(tick);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Game> activeGames() {
        return games.stream()
                .filter(game -> game.getField() != null)
                .collect(toList());
    }

    private void debugAt(int tick) {
        // breakpoint here
    }

    private String askAnswer(int index) {
        ClientBoard board = board(index);

        Object data = game(index).getBoardAsString();
        board.forString(data.toString());

        if (!printBoardOnly || board instanceof AbstractTextBoard) {
            print(index, board.toString());
        } else {
            print(index, ((AbstractBoard) board).boardAsString());
        }

        String answer = solver(index).get(board);

        if (printScores) {
            print(index, "Scores: " + scores.get(index).getScore());
        }

        print(index, "Answer:" + ((StringUtils.isEmpty(answer))?"":" ") + answer);
        return answer;
    }

    private void print(int index, String message) {
        if (StringUtils.isEmpty(showPlayers)
                || Arrays.asList(showPlayers.split(","))
                        .contains(String.valueOf(index + 1)))
        {
            print(player(index, message));
        }
    }

    private Solver solver(int index) {
        return solvers.get(index);
    }

    private ClientBoard board(int index) {
        return boards.get(index);
    }

    public synchronized LocalGameRunner add(Solver solver, ClientBoard board) {
        solvers.add(solver);
        boards.add(board);
        games.add(createGame());
        return this;
    }

    public synchronized void reloadAllGames() {
        games.clear();
        field = gameType.createGame(levelNumber, settings);
        solvers.forEach(solver -> games.add(createGame()));
    }

    public synchronized void remove(Solver solver) {
        int index = solvers.indexOf(solver);
        solvers.remove(index);
        boards.remove(index);
        scores.remove(index);
        Game game = games.remove(index);
        field.remove(game.getPlayer());
    }

    private Game game(int index) {
        return games.get(index);
    }

    private String player(int index, String message) {
        String preffix = (index + 1) + ":";
        if (printTick) {
            preffix = tick + ": " + preffix;
        }
        return preffix + message.replaceAll("\\n", "\n" + preffix);
    }

    private Game createGame() {
        PlayerScores score = getScores();
        int index = scores.indexOf(score);

        GamePlayer gamePlayer = gameType.createPlayer(
                event -> {
                    print(index, "Fire Event: " + event.toString());
                    score.event(event);
                },
                DEFAULT_TEAM_ID,
                getPlayerId(), settings);

        PrinterFactory factory = gameType.getPrinterFactory();

        Game game = new Single(gamePlayer, factory);
        game.on(field);
        game.newGame();
        return game;
    }

    private PlayerScores getScores() {
        // если мы чистили игры, то скоров больше и мы можем взять уже существующий
        if (games.size() < scores.size()) {
            return scores.get(games.size());
        }

        // иначе создаем новый скор для новой игры
        PlayerScores score = gameType.getPlayerScores(0, settings);
        scores.add(score);
        return score;
    }

    private String getPlayerId() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public void timeout(int input) {
        this.timeout = input;
    }

    public void printBoardOnly(boolean input) {
        printBoardOnly = input;
    }

    public void printWelcome(boolean input) {
        printWelcome = input;
    }

    public void exit() {
        exit = true;
    }

    public void out(Consumer<String> input) {
        out = input;
    }

    public void print(String message) {
        out.accept(message);
    }

    public void waitForPlayers(int input) {
        waitForPlayers = input;
    }

    public void showPlayers(String input) {
        showPlayers = input;
    }

    public void countIterations(int input) {
        countIterations = input;
    }

    public void printTick(boolean input) {
        printTick = input;
    }

    public void printScores(boolean input) {
        printScores = input;
    }

    public void removeWhenWin(boolean input) {
        removeWhenWin = input;
    }

    public void removeWhenGameOver(boolean input) {
        removeWhenGameOver = input;
    }

    public void reloadPlayersWhenGameOverAll(boolean input) {
        reloadPlayersWhenGameOverAll = input;
    }

    public Consumer<String> out() {
        return out;
    }
}
