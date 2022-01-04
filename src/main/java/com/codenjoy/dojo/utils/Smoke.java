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

import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.local.DiceGenerator;
import com.codenjoy.dojo.client.local.LocalGameRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.GameType;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Smoke {

    private static Smoke SMOKE;

    private final List<String> messages;
    private LocalGameRunner runner;
    private DiceGenerator dice;

    /**
     * Испоользуется для отладки. Из любого места игры можно скидывать
     * сюда любую отладочную информацию. Она попадет в результирующий файл.
     * @param message Сообщение.
     */
    public static void print(String message) {
        if (SMOKE == null) return;

        SMOKE.messages.add(message);
    }

    public Smoke() {
        SMOKE = this;
        messages = new LinkedList<>();

        runner = new LocalGameRunner();
        runner.timeout(0);
        runner.out(messages::add);

        dice = new DiceGenerator(messages::add);
        dice.printConversions(false);
        dice.printDice(false);
    }

    public void play(int ticks,
                     String fileName,
                     GameType gameRunner,
                     List<Solver> solvers,
                     List<ClientBoard> boards)
    {
        play(ticks, fileName, true,
                gameRunner, solvers, boards);
    }

    public void play(int ticks,
                     String fileName,
                     GameType gameRunner,
                     int players,
                     Supplier<Solver> solver,
                     Supplier<ClientBoard> board)
    {
        play(ticks, fileName, true,
                gameRunner,
                Stream.generate(solver)
                        .limit(players).collect(toList()),
                Stream.generate(board)
                        .limit(players).collect(toList()));
    }

    public void play(int ticks,
                     String fileName,
                     boolean printBoardOnly,
                     GameType gameRunner,
                     List<Solver> solvers,
                     List<ClientBoard> boards)
    {
        // given
        runner.with(gameRunner).add(solvers, boards);
        runner.countIterations(ticks);
        runner.printBoardOnly(printBoardOnly);
        runner.printTick(true);

        // when
        runner.run();

        // then
        SmokeUtils.assertSmokeFile(fileName, messages);
    }

    public Dice dice(long max, long count) {
        return dice.getDice(DiceGenerator.SOUL, max, count);
    }

    public Dice dice() {
        return dice.getDice();
    }

    public LocalGameRunner settings() {
        return runner;
    }
}
