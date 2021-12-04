package com.codenjoy.dojo.profile;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2021 Codenjoy
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


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.leftPad;

@Slf4j
public class Profiler {

    public static PrintStream OUT = System.out;
    public static boolean PRINT_SOUT = false;

    @Getter
    public static class AverageTime {

        private int count;
        private long time;
        private double average;

        public void add(long delta, boolean append) {
            time += delta;
            if (!append) {
                count++;
            }
            average = ((double)time)/count;
        }

        @Override
        public String toString() {
            return String.format("AVG{count:%s, time:%s, average: %.2f}",
                    leftPad(String.valueOf(count), 7),
                    leftPad(String.valueOf(time), 7),
                    average);
        }
    }

    protected Supplier<Long> getTime =
            () -> Calendar.getInstance().getTimeInMillis();

    private Map<String, AverageTime> phases = Collections.synchronizedMap(new LinkedHashMap<>());
    private long time;

    public synchronized void start() {
        time = now();
    }

    private long now() {
        return getTime.get();
    }

    public synchronized void done(String phase) {
        done(phase, false);
    }

    public synchronized void doneAppend(String phase) {
        done(phase, true);
    }

    private void done(String phase, boolean append) {
        long delta = now() - time;

        if (!phases.containsKey(phase)) {
            phases.put(phase, new AverageTime());
            append = false;
        }
        phases.get(phase).add(delta, append);

        start();
    }

    @Override
    public String toString() {
        int maxLength = phases.keySet().stream()
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElse(0);

        return phases.entrySet().stream()
                .map(entry ->
                        StringUtils.rightPad(entry.getKey(), maxLength, " ") + " = " +
                        entry.getValue().toString())
                .collect(joining("\n"));
    }

    public void print() {
        if (isDebugEnabled()) {
            debug(this.toString());
            debug("--------------------------------------------------");
        }
    }

    public boolean isDebugEnabled() {
        return PRINT_SOUT || log.isDebugEnabled();
    }

    public void debug(String message) {
        if (PRINT_SOUT) {
            OUT.println(message);
        } else {
            log.debug(message);
        }
    }

    public String get(String phase) {
        AverageTime info = info(phase);
        if (info == null) {
            return "phase not found: " + phase;
        }
        return info.toString();
    }

    public AverageTime info(String phase) {
        if (!phases.containsKey(phase)) {
            return null;
        }
        return phases.get(phase);
    }
}
