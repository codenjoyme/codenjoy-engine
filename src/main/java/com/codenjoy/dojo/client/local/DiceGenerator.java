package com.codenjoy.dojo.client.local;

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

import com.codenjoy.dojo.services.Dice;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class DiceGenerator {

    public static final String SOUL = "435874345435874365843564398";
    private final Consumer<String> out;
    private boolean printConversions = false;
    private boolean printDice = false;
    private boolean printSeed = false;

    public DiceGenerator(Consumer<String> out) {
        this.out = out;
    }

    public Dice getDice(String soul, long max, long count) {
        return generate(generateXorShift(soul, max, count));
    }

    public Dice getDice() {
        return getDice(SOUL, 100, 200);
    }

    public Dice generate(int... numbers) {
        int[] index = {0};
        return (n) -> {
            int next = numbers[index[0]];
            if (printDice) {
                out.accept("DICE:" + next);
            }
            if (next >= n) {
                next = next % n;
                if (printConversions) {
                    out.accept("DICE_CORRECTED < " + n + " :" + next);
                }
            }
            if (++index[0] == numbers.length) {
                index[0] = 0; // начинать с начала, если мы дошли до конца
            }
            return next;
        };
    }

    private int[] generateXorShift(String seed, long max, long count) {
        long[] current = new long[] { seed.hashCode() };
        if (printSeed) {
            out.accept("Seed = " + seed + "\n");
        }
        int[] result = IntStream.generate(() -> {
            long a0 = current[0] % seed.length();
            int a1 = seed.charAt((int)Math.abs(a0));
            long a2 = (current[0] << (a1 % 5)) ^ current[0];
            long a3 = (current[0] >>> (a1 % 6)) ^ (current[0] << (a1 % 2));
            current[0] = a2 ^ a3;
            return (int) Math.abs(current[0] % max);
        }).limit(count).toArray();
        return result;
    }

    public void printDice(boolean input) {
        printDice = input;
    }

    public void printConversions(boolean input) {
        printConversions = input;
    }

    public void printSeed(boolean input) {
        printSeed = input;
    }

}
