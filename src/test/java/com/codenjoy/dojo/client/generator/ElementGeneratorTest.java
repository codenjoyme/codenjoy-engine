package com.codenjoy.dojo.client.generator;

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