package com.codenjoy.dojo.services.field;

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

import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.field.PointField;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;

public class PointFieldTest {

    private PointField field;
    
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
        field = new PointField(3);

        // when
        field.add(new One(1, 1));

        // then
        assert_oneElement();
    }

    private void assert_oneElement() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_twoElements_sameType_differentCells() {
        // given
        field = new PointField(3);

        // when
        field.add(new One(1, 1));
        field.add(new One(1, 2));

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testAdd_twoElements_sameType_sameCell() {
        // given
        field = new PointField(3);

        // when
        field.add(new One(1, 1));
        field.add(new One(1, 1));

        // then
        assert_twoElements_sameType_sameCell();
    }

    private void assert_twoElements_sameType_sameCell() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_twoElements_differentTypes_sameCell() {
        // given
        field = new PointField(3);

        // when
        field.add(new One(2, 1));
        field.add(new Two(2, 1));

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testAdd_twoElements_differentTypes_differentCells() {
        // given
        field = new PointField(3);

        // when
        field.add(new One(2, 1));
        field.add(new Two(2, 0));

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    private void assert_twoElements_differentTypes_differentCells() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two2(2,0)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{\n" +
                "        Two.class=[\n" +
                "                two2(2,0)]}\n" +
                "[2,1]:{\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_threeElements_mixed() {
        // given
        field = new PointField(3);

        // when
        field.add(new One(1, 1));
        field.add(new One(1, 1));
        field.add(new One(1, 2));

        // then
        assert_threeElements_mixed();
    }

    @Test
    public void testAddAll_oneElement() {
        // given
        field = new PointField(3);

        // when
        field.addAll(Arrays.asList(new One(1, 1)));

        // then
        assert_oneElement();
    }

    @Test
    public void testAddAll_twoElements_sameType_differentCells() {
        // given
        field = new PointField(3);

        // when
        field.addAll(Arrays.asList(new One(1, 1),
                new One(1, 2)));

        // then
        assert_twoElements_sameType_differentCells();
    }

    private void assert_twoElements_sameType_differentCells() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,2)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)]}\n" +
                "[1,2]:{\n" +
                "        One.class=[\n" +
                "                one2(1,2)]}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testAddAll_twoElements_sameType_sameCell() {
        // given
        field = new PointField(3);

        // when
        field.addAll(Arrays.asList(new One(1, 1),
                new One(1, 1)));

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testAddAll_twoElements_differentTypes_sameCell() {
        // given
        field = new PointField(3);

        // when
        field.addAll(Arrays.asList(new One(2, 1),
                new Two(2, 1)));

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    private void assert_twoElements_differentTypes_sameCell() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two2(2,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two2(2,1)]}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testAddAll_twoElements_differentTypes_differentCells() {
        // given
        field = new PointField(3);

        // when
        field.addAll(Arrays.asList(new One(2, 1),
                new Two(2, 0)));

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testAddAll_threeElements_mixed() {
        // given
        field = new PointField(3);

        // when
        field.addAll(Arrays.asList(new One(1, 1),
                new One(1, 1),
                new One(1, 2)));

        // then
        assert_threeElements_mixed();
    }

    private void assert_threeElements_mixed() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)\n" +
                "                one3(1,2)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)]}\n" +
                "[1,2]:{\n" +
                "        One.class=[\n" +
                "                one3(1,2)]}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testContains_oneElement() {
        // given
        testAdd_oneElement();

        // when then
        assertContains_oneElement();

        // then
        assert_oneElement();
    }

    private void assertContains_oneElement() {
        assertEquals(true, field.of(One.class).contains(new One(1, 1)));
        assertEquals(true, field.of(One.class).contains(pt(1, 1)));
        assertEquals(true, field.of(One.class).contains(new Two(1, 1)));
        assertEquals(true, field.of(One.class).contains(new Three(1, 1)));

        assertEquals(false, field.of(Two.class).contains(new One(1, 1)));
        assertEquals(false, field.of(Two.class).contains(pt(1, 1)));
        assertEquals(false, field.of(Two.class).contains(new Two(1, 1)));
        assertEquals(false, field.of(Two.class).contains(new Three(1, 1)));

        assertEquals(false, field.of(One.class).contains(new One(1, 2)));
        assertEquals(false, field.of(One.class).contains(new Two(2, 2)));
        assertEquals(false, field.of(One.class).contains(new Three(2, 2)));
    }

    @Test
    public void testContains_twoElements_sameType_sameCell() {
        // given
        testAdd_twoElements_sameType_sameCell();

        // when then
        assertContains_oneElement();

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testContains_twoElements_sameType_differentCells() {
        // given
        testAdd_twoElements_sameType_differentCells();

        // when then
        assertEquals(true, field.of(One.class).contains(new One(1, 1)));
        assertEquals(true, field.of(One.class).contains(pt(1, 1)));
        assertEquals(true, field.of(One.class).contains(new Two(1, 1)));
        assertEquals(true, field.of(One.class).contains(new Three(1, 1)));

        assertEquals(true, field.of(One.class).contains(new One(1, 2)));
        assertEquals(true, field.of(One.class).contains(pt(1, 2)));
        assertEquals(true, field.of(One.class).contains(new Two(1, 2)));
        assertEquals(true, field.of(One.class).contains(new Three(1, 2)));

        assertEquals(false, field.of(Two.class).contains(new One(1, 2)));
        assertEquals(false, field.of(Two.class).contains(pt(1, 2)));
        assertEquals(false, field.of(Two.class).contains(new Two(1, 2)));
        assertEquals(false, field.of(Two.class).contains(new Three(1, 2)));

        assertEquals(false, field.of(One.class).contains(new Two(2, 2)));
        assertEquals(false, field.of(One.class).contains(new Three(2, 2)));

        assertEquals(false, field.of(Two.class).contains(new Two(2, 2)));
        assertEquals(false, field.of(Two.class).contains(new Three(2, 2)));

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testContains_twoElements_differentTypes_sameCell() {
        // given
        testAdd_twoElements_differentTypes_sameCell();

        // when then
        assertEquals(true, field.of(One.class).contains(new One(2, 1)));
        assertEquals(true, field.of(One.class).contains(pt(2, 1)));
        assertEquals(true, field.of(One.class).contains(new Two(2, 1)));
        assertEquals(true, field.of(One.class).contains(new Three(2, 1)));

        assertEquals(false, field.of(One.class).contains(new One(1, 2)));
        assertEquals(false, field.of(One.class).contains(pt(1, 2)));
        assertEquals(false, field.of(One.class).contains(new Two(1, 2)));
        assertEquals(false, field.of(One.class).contains(new Three(1, 2)));
        assertEquals(false, field.of(One.class).contains(new Two(2, 2)));
        assertEquals(false, field.of(One.class).contains(new Three(2, 2)));

        assertEquals(false, field.of(Two.class).contains(new One(1, 2)));
        assertEquals(false, field.of(Two.class).contains(pt(1, 2)));
        assertEquals(false, field.of(Two.class).contains(new Two(1, 2)));
        assertEquals(false, field.of(Two.class).contains(new Three(1, 2)));
        assertEquals(false, field.of(Two.class).contains(new Two(2, 2)));
        assertEquals(false, field.of(Two.class).contains(new Three(2, 2)));

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testContains_twoElements_differentTypes_differentCells() {
        // given
        testAdd_twoElements_differentTypes_differentCells();

        // when then
        assertEquals(true, field.of(One.class).contains(new One(2, 1)));
        assertEquals(true, field.of(One.class).contains(pt(2, 1)));
        assertEquals(true, field.of(One.class).contains(new Two(2, 1)));
        assertEquals(true, field.of(One.class).contains(new Three(2, 1)));

        assertEquals(true, field.of(Two.class).contains(new One(2, 0)));
        assertEquals(true, field.of(Two.class).contains(pt(2, 0)));
        assertEquals(true, field.of(Two.class).contains(new Two(2, 0)));
        assertEquals(true, field.of(Two.class).contains(new Three(2, 0)));

        assertEquals(false, field.of(Two.class).contains(new Two(2, 2)));
        assertEquals(false, field.of(Two.class).contains(new Three(2, 2)));
        assertEquals(false, field.of(Two.class).contains(new Two(2, 2)));
        assertEquals(false, field.of(Two.class).contains(new Three(2, 2)));

        // then
        assert_twoElements_differentTypes_differentCells();
    }


}