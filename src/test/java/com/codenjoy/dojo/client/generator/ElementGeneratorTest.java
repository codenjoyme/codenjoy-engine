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

import com.codenjoy.dojo.utils.JsonUtils;
import com.codenjoy.dojo.utils.smart.SmartAssert;
import org.junit.After;
import org.junit.Test;

import static com.codenjoy.dojo.utils.smart.SmartAssert.assertEquals;

public class ElementGeneratorTest {

    @After
    public void after() {
        SmartAssert.checkResult();
    }

    @Test
    public void shouldGenerate_go_sample() {
        String actual = new ElementGenerator().generate("test", "go");

        assertEquals("package go\n" +
                "\n" +
                "var Elements = map[string]rune{\n" +
                "    'NONE': ' ',                                       // Short comment.\n" +
                "    'WALL': '☼',                                       // Long long long long long long long long long long long longl\n" +
                "                                                       // ong long long long long long long long long long comment.\n" +
                "    'HERO': '☺',                                       // Another short comment.\n" +
                "    'OTHER_HERO': '☻',                                 // One more time.\n" +
                "    'DEAD_HERO': 'X',                                  \n" +
                "    'OTHER_DEAD_HERO_LONG_LONG_LONG_LONG_LONG': 'Y',   // Long name.\n" +
                "    'G': '$',                                          // Short name.\n" +
                "}\n", JsonUtils.clean(actual));
    }

}