package com.codenjoy.dojo.utils.generator;

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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

public class GameManualGeneratorTest extends AbstractManualGeneratorTest {
    private static final String LANGUAGE = "en";
    private static final String GAME = "mollymage";
    private static final String MANUAL_TYPE = "codenjoy";
    private static final String RELATIVE_PASTH_TO_MISSING_SOURCES = "target\\test-classes\\generator\\missOneFile";

    private ByteArrayOutputStream baos;
    private PrintStream old;

    @Before
    public void setUp() {
        super.setUp();
        old = System.out;
    }

    @After
    public void clear() {
        System.out.flush();
        System.setOut(old);
    }

    private void prepareMissingSources() {
        base = new File(RELATIVE_PASTH_TO_MISSING_SOURCES).getAbsolutePath() + "\\";
        generator = getGenerator(GAME, LANGUAGE, MANUAL_TYPE);
        logState();
        redirectOut();
    }

    private void redirectOut() {
        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }

    @Test
    public void shouldPrintErrorToConsole_whenMissingSource() {
        // when resource missed
        prepareMissingSources();
        // then generator should print to console error message
        generator.generate();
        clear();
        Assert.assertEquals("\u001B[45;93mFile is missing: " + base + "games\\" + GAME + "\\part3.md\n" +
                "\u001B[0m\u001B[41;93m[ERROR] Can't find resources for manualType{" + MANUAL_TYPE + "}, game{" + GAME + "}, language{" + LANGUAGE + "}\n" +
                "\u001B[0m", baos.toString());
    }
}
