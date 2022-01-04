package com.codenjoy.dojo.client;

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

import lombok.SneakyThrows;
import org.junit.Test;

import java.io.*;

import static com.codenjoy.dojo.client.KeyboardSolver.WELCOME_MESSAGE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class KeyboardSolverTest {

    private ByteArrayOutputStream outputStream;

    @Test
    public void testInfoMessage() {
        getAnswer("");

        assertEquals(WELCOME_MESSAGE + '\n',
                Utils.clean(outputStream.toString()));
    }

    @Test
    public void testNoCommand() {
        assertEquals("", getAnswer(""));
    }

    @Test
    public void testAct() {
        assertEquals("ACT", getAnswer("f"));
    }

    @Test
    public void testActWithOneParameter() {
        assertEquals("ACT(0)", getAnswer("f0"));
    }

    @Test
    public void testActWithTwoParameters() {
        assertEquals("ACT(1,2)", getAnswer("f12"));
    }

    @Test
    public void testActWithThreeParameters() {
        assertEquals("ACT(9,8,7)", getAnswer("f987"));
    }

    @Test
    public void testActThenDirection() {
        assertEquals("ACT,LEFT", getAnswer("fa"));
    }

    @Test
    public void testDirectionThenAct() {
        assertEquals("LEFT,ACT", getAnswer("af"));
    }

    @Test
    public void testMultiAct() {
        assertEquals("ACT,ACT(1),ACT(2,3),ACT(4,5,6,7),ACT(8,9,0)",
                getAnswer("ff1f23f4567f890"));
    }

    @Test
    public void testSmoke() {
        assertEquals("ACT,LEFT,ACT(1),DOWN,ACT(2,3),RIGHT,ACT(4,5,6,7),UP,ACT(8,9,0),LEFT",
                getAnswer("faf1sf23df4567wf890a"));
    }

    @Test
    public void testLeft() {
        assertEquals("LEFT", getAnswer("a"));
    }

    @Test
    public void testRight() {
        assertEquals("RIGHT", getAnswer("d"));
    }

    @Test
    public void testUp() {
        assertEquals("UP", getAnswer("w"));
    }

    @Test
    public void testDown() {
        assertEquals("DOWN", getAnswer("s"));
    }

    @Test
    public void testTwoDirections() {
        assertEquals("LEFT,UP", getAnswer("aw"));
    }

    @Test
    public void testThreeDirections() {
        assertEquals("LEFT,UP,DOWN", getAnswer("aws"));
    }

    private String getAnswer(String input) {
        KeyboardSolver solver = new KeyboardSolver(){
            @Override
            protected InputStream in() {
                return new ByteArrayInputStream(input.getBytes());
            }

            @Override
            @SneakyThrows
            protected PrintStream out() {
                outputStream = new ByteArrayOutputStream();
                return new PrintStream(outputStream, true, UTF_8.name());
            }
        };

        return solver.get(null);
    }

}