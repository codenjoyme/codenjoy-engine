package com.codenjoy.dojo.utils;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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
import com.codenjoy.dojo.client.local.LocalGameRunner;
import com.codenjoy.dojo.services.GameType;
import lombok.experimental.UtilityClass;

import java.util.LinkedList;
import java.util.List;

@UtilityClass
public class Smoke {

    public static void play(int ticks,
                            String fileName,
                            GameType gameRunner,
                            List<Solver> solvers,
                            List<ClientBoard> boards)
    {
        play(ticks, fileName, true,
                gameRunner, solvers, boards);
    }

    public static void play(int ticks,
                            String fileName,
                            boolean printBoardOnly,
                            GameType gameRunner,
                            List<Solver> solvers,
                            List<ClientBoard> boards)
    {
        // given
        List<String> messages = new LinkedList<>();

        LocalGameRunner.timeout = 0;
        LocalGameRunner.out = messages::add;
        LocalGameRunner.countIterations = ticks;
        LocalGameRunner.printConversions = false;
        LocalGameRunner.printBoardOnly = printBoardOnly;
        LocalGameRunner.printDice = false;
        LocalGameRunner.printTick = true;

        // when
        LocalGameRunner.run(gameRunner, solvers, boards);

        // then
        TestUtils.assertSmokeFile(fileName, messages);
    }

}
