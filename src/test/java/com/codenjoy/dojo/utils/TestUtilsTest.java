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


import com.codenjoy.dojo.services.joystick.NoActJoystick;
import com.codenjoy.dojo.services.joystick.NoDirectionJoystick;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;
import com.codenjoy.dojo.services.printer.CharElement;
import com.codenjoy.dojo.services.printer.state.State;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestUtilsTest {

    @Test
    public void testInject() {
        assertEquals("1234^*5678^*90AB^*CDEF^*HIJK^*LMNO^*PQRS^*TUVW^*XYZ",
                TestUtils.inject("1234567890ABCDEFHIJKLMNOPQRSTUVWXYZ", 4, "^*"));

        assertEquals("1234^*5678^*90AB^*CDEF^*HIJK^*LMNO^*PQRS^*TUVW^*",
                TestUtils.inject("1234567890ABCDEFHIJKLMNOPQRSTUVW", 4, "^*"));

        assertEquals("1234^*5678^*90AB^*CDEF^*HIJK^*LMNO^*PQRS^*TUV",
                TestUtils.inject("1234567890ABCDEFHIJKLMNOPQRSTUV", 4, "^*"));
    }

    @Test
    public void testInjectN() {
        assertEquals("12345\n" +
                    "67890\n" +
                    "ABCDE\n" +
                    "FHIJK\n" +
                    "LMNOP\n" +
                    "QRSTU\n" +
                    "VWXYZ\n",
                TestUtils.injectN("1234567890ABCDEFHIJKLMNOPQRSTUVWXYZ"));

        assertEquals("1234\n" +
                    "5678\n" +
                    "90AB\n" +
                    "CDEF\n" +
                    "HIJK\n" +
                    "LMNO\n",
                TestUtils.injectN("1234567890ABCDEFHIJKLMNO"));
    }

    static class Hero extends PlayerHero implements NoActJoystick, NoDirectionJoystick, State<CharElement, Player> {

        private int property1;
        private String property2;

        public Hero(int property1, String property2) {
            super(pt(1, 1));
            this.property1 = property1;
            this.property2 = property2;
        }

        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public void tick() {

        }

        @Override
        public CharElement state(Player player, Object... alsoAtPoint) {
            return new CharElement() {
                @Override
                public char ch() {
                    return '1';
                }

                @Override
                public String name() {
                    return "NAME";
                }
            };
        }

        public int property1() {
            return property1;
        }

        public String property2() {
            return property2;
        }
    }

    static class Player extends GamePlayer {

        Hero hero;

        public Player(Hero hero) {
            super(null, null);
            this.hero = hero;
        }

        @Override
        public PlayerHero getHero() {
            return hero;
        }

    };

    @Test
    public void testCollectHeroesData() {
        // given
        List<Player> players = new ArrayList<>(){{
            add(new Player(new Hero(12, "s12")));
            add(new Player(new Hero(23, "s23")));
            add(new Player(new Hero(34, "s34")));
            add(new Player(new Hero(45, "s45")));
            add(new Player(new Hero(56, "")));    // default String data
            add(new Player(new Hero(0, "s67")));  // default Integer data
        }};

        // when then
        boolean processAll = false;
        assertEquals(
                "hero(0)=12\n" +
                "hero(1)=23\n" +
                "hero(2)=34\n" +
                "hero(3)=45\n" +
                "hero(4)=56\n" +
                "hero(5)=0",
                TestUtils.collectHeroesData(players, "property1", processAll));

        assertEquals(
                "hero(0)=s12\n" +
                "hero(1)=s23\n" +
                "hero(2)=s34\n" +
                "hero(3)=s45\n" +
                "hero(4)=\n" +
                "hero(5)=s67",
                TestUtils.collectHeroesData(players, "property2", processAll));

        // when then
        boolean skipEmpty = true;
        assertEquals(
                "hero(0)=12\n" +
                "hero(1)=23\n" +
                "hero(2)=34\n" +
                "hero(3)=45\n" +
                "hero(4)=56",
                TestUtils.collectHeroesData(players, "property1", skipEmpty));

        assertEquals(
                "hero(0)=s12\n" +
                "hero(1)=s23\n" +
                "hero(2)=s34\n" +
                "hero(3)=s45\n" +
                "hero(5)=s67",
                TestUtils.collectHeroesData(players, "property2", skipEmpty));
    }

    @Test
    public void testIsMatch() {
        assertEquals(true,  TestUtils.isMatch("qwe-asd-*",    "qwe-asd-zxc"));
        assertEquals(false, TestUtils.isMatch("qwe-Asd-*",    "qwe-asd-zxc"));
        assertEquals(true,  TestUtils.isMatch("*-asd-zxc",    "qwe-asd-zxc"));
        assertEquals(false, TestUtils.isMatch("*-aSd-zxc",    "qwe-asd-zxc"));
        assertEquals(true,  TestUtils.isMatch("*-asd-*",      "qwe-asd-zxc"));
        assertEquals(false, TestUtils.isMatch("*-asd-*A",     "qwe-asd-zxc"));
        assertEquals(true,  TestUtils.isMatch("*-*-*",        "qwe-asd-zxc"));
        assertEquals(false, TestUtils.isMatch("A*-*-*",       "qwe-asd-zxc"));
        assertEquals(true,  TestUtils.isMatch("*",            "qwe-asd-zxc"));
        assertEquals(false, TestUtils.isMatch("*A",           "qwe-asd-zxc"));
        assertEquals(true,  TestUtils.isMatch("qwe-*-zxc",    "qwe-asd-zxc"));
        assertEquals(false, TestUtils.isMatch("qwe-A*-zxc",   "qwe-asd-zxc"));
        assertEquals(true,  TestUtils.isMatch("q*-*-zx*-wer", "qwe-asd-zxc-wer"));
        assertEquals(false, TestUtils.isMatch("q*-*-Zx*-wer", "qwe-asd-zxc-wer"));

        assertEquals(true,  TestUtils.isMatch("a*c", "abbbc"));
        assertEquals(false, TestUtils.isMatch("a*b", "abbbc"));
        assertEquals(true,  TestUtils.isMatch("*a*", "banana"));
        assertEquals(true,  TestUtils.isMatch("*a*", "ananas"));
        assertEquals(true,  TestUtils.isMatch("**a*", "ananas"));
        assertEquals(true,  TestUtils.isMatch("*a**", "ananas"));
        assertEquals(true,  TestUtils.isMatch("**a**", "ananas"));
        assertEquals(true,  TestUtils.isMatch("*a*b", "ab"));
        assertEquals(true,  TestUtils.isMatch("*a*b*", "acbabcab"));
        assertEquals(true,  TestUtils.isMatch("a*", "a"));
        assertEquals(true,  TestUtils.isMatch("a", "a"));
        assertEquals(true,  TestUtils.isMatch("*a", "ba"));
        assertEquals(true,  TestUtils.isMatch("*", ""));
        assertEquals(true,  TestUtils.isMatch("a*b", "acbabcab"));
        assertEquals(true,  TestUtils.isMatch("a**b", "acbabcab"));
        assertEquals(true,  TestUtils.isMatch("a***b", "acbabcab"));
        assertEquals(false, TestUtils.isMatch("b*", "ab"));
        assertEquals(false, TestUtils.isMatch("b**", "ab"));
        assertEquals(false, TestUtils.isMatch("*a", "b"));
        assertEquals(false, TestUtils.isMatch("**a", "b"));
        assertEquals(false, TestUtils.isMatch("*a*b", "abbc"));
        assertEquals(true,  TestUtils.isMatch("*a*b*", "acbabc"));
        assertEquals(true,  TestUtils.isMatch("*a*c", "abbbc"));
        assertEquals(true,  TestUtils.isMatch("*a*c*", "acbabc"));
    }
}
