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
        String data = new ElementGenerator().generate("sample", "go");

        assertEquals("package go\n" +
                "\n" +
                "var Elements = map[string]rune{\n" +
                "    \"NONE\": ' ',              // Empty place where the hero can go.\n" +
                "    \"WALL\": '☼',              // Wall you can't walk through.\n" +
                "    \"HERO\": '☺',              // My hero.\n" +
                "    \"OTHER_HERO\": '☻',        // Heroes of other players.\n" +
                "    \"DEAD_HERO\": 'X',         // My hero died. His body will disappear in the next tick.\n" +
                "    \"OTHER_DEAD_HERO\": 'Y',   // Another player's hero died.\n" +
                "    \"GOLD\": '$',              // Gold. It must be picked up.\n" +
                "    \"BOMB\": 'x',              // Bomb planted by the hero. You can blow up on it.\n" +
                "}\n", data);
    }

}