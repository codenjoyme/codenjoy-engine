package com.codenjoy.dojo.services;

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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointFieldTest {

    static int id;

    private static int id() {
        return ++id;
    }

    @Before
    public void setup() {
        id = 0;
    }

    static class One extends PointImpl {
        private final int id;

        public One(int x, int y) {
            super(x, y);
            this.id = id();
        }

        @Override
        public String toString() {
            return String.format("one%s(%s,%s)",
                    id,
                    getX(),
                    getY());
        }
    }

    static class Two extends PointImpl {
        private final int id;

        public Two(int x, int y) {
            super(x, y);
            this.id = id();
        }

        @Override
        public String toString() {
            return String.format("two%s(%s,%s)",
                    id,
                    getX(),
                    getY());
        }
    }

    static class Three extends PointImpl {
        private final int id;

        public Three(int x, int y) {
            super(x, y);
            this.id = id();
        }

        @Override
        public String toString() {
            return String.format("three%s(%s,%s)",
                    id,
                    getX(),
                    getY());
        }
    }

    static class Four extends PointImpl {
        private final int id;

        public Four(int x, int y) {
            super(x, y);
            this.id = id();
        }

        @Override
        public String toString() {
            return String.format("four%s(%s,%s)",
                    id,
                    getX(),
                    getY());
        }
    }

    @Test
    public void testAdd_oneElement() {
        // given
        PointField field = new PointField(3);

        // when
        field.add(new One(1, 1));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:null\n" +
                "[0,1]:null\n" +
                "[0,2]:null\n" +
                "[1,0]:null\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)]}\n" +
                "[1,2]:null\n" +
                "[2,0]:null\n" +
                "[2,1]:null\n" +
                "[2,2]:null\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_twoElements_inDifferentCell() {
        // given
        PointField field = new PointField(3);

        // when
        field.add(new One(1, 1));
        field.add(new One(1, 2));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,2)]}]\n" +
                "\n" +
                "[field=[0,0]:null\n" +
                "[0,1]:null\n" +
                "[0,2]:null\n" +
                "[1,0]:null\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)]}\n" +
                "[1,2]:{\n" +
                "        One.class=[\n" +
                "                one2(1,2)]}\n" +
                "[2,0]:null\n" +
                "[2,1]:null\n" +
                "[2,2]:null\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_twoElements_inSameCell() {
        // given
        PointField field = new PointField(3);

        // when
        field.add(new One(1, 1));
        field.add(new One(1, 1));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:null\n" +
                "[0,1]:null\n" +
                "[0,2]:null\n" +
                "[1,0]:null\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)]}\n" +
                "[1,2]:null\n" +
                "[2,0]:null\n" +
                "[2,1]:null\n" +
                "[2,2]:null\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_twoElements_differentTypes_sameCell() {
        // given
        PointField field = new PointField(3);

        // when
        field.add(new One(2, 1));
        field.add(new Two(2, 1));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two2(2,1)]}]\n" +
                "\n" +
                "[field=[0,0]:null\n" +
                "[0,1]:null\n" +
                "[0,2]:null\n" +
                "[1,0]:null\n" +
                "[1,1]:null\n" +
                "[1,2]:null\n" +
                "[2,0]:null\n" +
                "[2,1]:{\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two2(2,1)]}\n" +
                "[2,2]:null\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_threeElements_mixed() {
        // given
        PointField field = new PointField(3);

        // when
        field.add(new One(1, 1));
        field.add(new One(1, 1));
        field.add(new One(1, 2));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)\n" +
                "                one3(1,2)]}]\n" +
                "\n" +
                "[field=[0,0]:null\n" +
                "[0,1]:null\n" +
                "[0,2]:null\n" +
                "[1,0]:null\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)]}\n" +
                "[1,2]:{\n" +
                "        One.class=[\n" +
                "                one3(1,2)]}\n" +
                "[2,0]:null\n" +
                "[2,1]:null\n" +
                "[2,2]:null\n" +
                "]", field.toString());
    }

}