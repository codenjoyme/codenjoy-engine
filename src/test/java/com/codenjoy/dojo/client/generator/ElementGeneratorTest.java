package com.codenjoy.dojo.client.generator;

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

import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.Arrays;

public class ElementGeneratorTest {

    @Rule
    public TestName test = new TestName();

    @Test
    public void shouldGenerate_sampleGame_goLanguage() {
        assertEquals(new ElementGenerator("sample", "go").generate());
    }

    @Test
    public void shouldGenerate_testGame_goLanguage() {
        assertEquals(new ElementGenerator("test", "go").generate());
    }

    @Test
    public void shouldGenerate_testGame_cppLanguage() {
        assertEquals(new ElementGenerator("test", "cpp").generate());
    }

    @Test
    public void shouldGenerate_testGame_jsLanguage() {
        assertEquals(new ElementGenerator("test", "js").generate());
    }

    @Test
    public void shouldGenerate_testGame_phpLanguage() {
        assertEquals(new ElementGenerator("test", "php").generate());
    }

    @Test
    public void shouldGenerate_testGame_javaLanguage() {
        assertEquals(new ElementGenerator("test", "java").generate());
    }

    @Test
    public void shouldGenerate_testGame_pythonLanguage() {
        assertEquals(new ElementGenerator("test", "python").generate());
    }

    @Test
    public void shouldGenerate_testGame_markdownLanguage() {
        assertEquals(new ElementGenerator("test", "md").generate());
    }

    @Test
    public void shouldGenerate_sampleGame_markdownLanguage() {
        assertEquals(new ElementGenerator("sample", "md").generate());
    }

    private void assertEquals(String actual) {
        TestUtils.assertSmokeFile(this.getClass().getSimpleName()
                + "/" + test.getMethodName() +  ".data",
                Arrays.asList(actual.split("\n")));
    }
}