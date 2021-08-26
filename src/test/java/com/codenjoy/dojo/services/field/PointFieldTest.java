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

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.printer.BoardReader;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;

public class PointFieldTest {

    private PointField field;
    
    private GamePlayer player = null;

    private static int id;
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

    @Test
    public void testOf_add_oneElement() {
        // given
        field = new PointField(3);

        // when
        field.of(One.class).add(new One(1, 1));

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
    public void testSameOf_add_twoElements_sameType_differentCells() {
        // given
        field = new PointField(3);

        // when
        Accessor<One> of = field.of(One.class);

        of.add(new One(1, 1));
        of.add(new One(1, 2));

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testDifferentOf_add_twoElements_sameType_differentCells() {
        // given
        field = new PointField(3);

        // when
        field.of(One.class).add(new One(1, 1));
        field.of(One.class).add(new One(1, 2));

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

    @Test
    public void testSameOf_add_twoElements_sameType_sameCell() {
        // given
        field = new PointField(3);

        // when
        Accessor<One> of = field.of(One.class);
        of.add(new One(1, 1));
        of.add(new One(1, 1));

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testDifferentOf_add_twoElements_sameType_sameCell() {
        // given
        field = new PointField(3);

        // when
        field.of(One.class).add(new One(1, 1));
        field.of(One.class).add(new One(1, 1));

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testAdd_twoElements_sameType_sameCell_afterReplaceCoordinates() {
        // given
        field = new PointField(3);

        One one = new One(1, 1);
        One another = new One(1, 1);
        field.add(one);
        field.add(another);

        assert_twoElements_sameType_sameCell();

        // when
        another.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates();
    }

    @Test
    public void testSameOf_add_twoElements_sameType_sameCell_afterReplaceCoordinates() {
        // given
        field = new PointField(3);

        One one = new One(1, 1);
        One another = new One(1, 1);
        Accessor<One> of = field.of(One.class);
        of.add(one);
        of.add(another);

        assert_twoElements_sameType_sameCell();

        // when
        another.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates();
    }

    @Test
    public void testDifferentOf_Add_twoElements_sameType_sameCell_afterReplaceCoordinates() {
        // given
        field = new PointField(3);

        One one = new One(1, 1);
        One another = new One(1, 1);
        field.of(One.class).add(one);
        field.of(One.class).add(another);

        assert_twoElements_sameType_sameCell();

        // when
        another.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates();
    }

    @Test
    public void testAdd_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded() {
        // given
        field = new PointField(3);

        One one = new One(1, 1);
        One another = new One(1, 1);
        field.add(one);
        field.add(another);

        assert_twoElements_sameType_sameCell();

        // when
        one.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded();
    }

    @Test
    public void testSameOf_add_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded() {
        // given
        field = new PointField(3);

        One one = new One(1, 1);
        One another = new One(1, 1);
        Accessor<One> of = field.of(One.class);
        of.add(one);
        of.add(another);

        assert_twoElements_sameType_sameCell();

        // when
        one.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded();
    }

    @Test
    public void testDifferentOf_add_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded() {
        // given
        field = new PointField(3);

        One one = new One(1, 1);
        One another = new One(1, 1);
        Accessor<One> of = field.of(One.class);
        of.add(one);
        of.add(another);

        assert_twoElements_sameType_sameCell();

        // when
        one.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded();
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

    private void assert_twoElements_sameType_sameCell_afterReplaceCoordinates() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(2,1)]}]\n" +
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
                "[2,1]:{\n" +
                "        One.class=[\n" +
                "                one2(2,1)]}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    private void assert_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one2(1,1)\n" +
                "                one1(2,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one2(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
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
    public void testOf_add_twoElements_differentTypes_sameCell() {
        // given
        field = new PointField(3);

        // when
        Accessor<One> of1 = field.of(One.class);
        Accessor<Two> of2 = field.of(Two.class);
        of1.add(new One(2, 1));
        of2.add(new Two(2, 1));

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

    @Test
    public void testOf_add_twoElements_differentTypes_differentCells() {
        // given
        field = new PointField(3);

        // when
        Accessor<One> of1 = field.of(One.class);
        Accessor<Two> of2 = field.of(Two.class);
        of1.add(new One(2, 1));
        of2.add(new Two(2, 0));

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testOf_add_twoElements_differentTypes_differentCells_tryToChangeAccessorType() {
        // given
        field = new PointField(3);

        // when
        Accessor of = field.of(Three.class);
        // don't worry, accessor is used as syntactic sugar
        of.add(new One(2, 1));
        of.add(new Two(2, 0));

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
    public void testAdd_differentTypes_differentCells_afterReplaceCoordinates() {
        // given
        field = new PointField(3);

        field.add(new One(2, 1));
        Two two = new Two(2, 0);
        field.add(two);

        assert_twoElements_differentTypes_differentCells();

        // when
        two.move(1, 2);

        // then
        assert_differentTypes_differentCells_afterReplaceCoordinates();
    }


    @Test
    public void testOf_add_differentTypes_differentCells_afterReplaceCoordinates() {
        // given
        field = new PointField(3);

        Accessor<One> of1 = field.of(One.class);
        Accessor<Two> of2 = field.of(Two.class);
        of1.add(new One(2, 1));
        Two two = new Two(2, 0);
        of2.add(two);

        assert_twoElements_differentTypes_differentCells();

        // when
        two.move(1, 2);

        // then
        assert_differentTypes_differentCells_afterReplaceCoordinates();
    }

    private void assert_differentTypes_differentCells_afterReplaceCoordinates() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two2(1,2)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{}\n" +
                "[1,2]:{\n" +
                "        Two.class=[\n" +
                "                two2(1,2)]}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_severalElements_mixed() {
        // given
        field = new PointField(3);

        // when
        field.add(new One(1, 1));
        field.add(new One(1, 1));
        field.add(new One(1, 2));
        field.add(new Two(1, 2));
        field.add(new Three(2, 2));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSameOf_add_severalElements_mixed() {
        // given
        field = new PointField(3);

        // when
        Accessor<One> of1 = field.of(One.class);
        Accessor<Two> of2 = field.of(Two.class);
        Accessor<Three> of3 = field.of(Three.class);
        of1.add(new One(1, 1));
        of1.add(new One(1, 1));
        of1.add(new One(1, 2));
        of2.add(new Two(1, 2));
        of3.add(new Three(2, 2));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSame2Of_add_severalElements_mixed() {
        // given
        field = new PointField(3);

        // when
        Accessor<One> of1 = field.of(One.class);
        of1.add(new One(1, 1));
        of1.add(new One(1, 1));
        of1.add(new One(1, 2));

        Accessor<Two> of2 = field.of(Two.class);
        of2.add(new Two(1, 2));

        Accessor<Three> of3 = field.of(Three.class);
        of3.add(new Three(2, 2));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testDifferentOf_add_severalElements_mixed() {
        // given
        field = new PointField(3);

        // when
        field.of(One.class).add(new One(1, 1));
        field.of(One.class).add(new One(1, 1));
        field.of(One.class).add(new One(1, 2));
        field.of(Two.class).add(new Two(1, 2));
        field.of(Three.class).add(new Three(2, 2));

        // then
        assert_severalElements_mixed();
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
    public void testAddAll_twoElements_sameType_sameCell_afterReplaceCoordinates() {
        // given
        field = new PointField(3);

        One one = new One(1, 1);
        One another = new One(1, 1);
        field.addAll(Arrays.asList(one, another));

        assert_twoElements_sameType_sameCell();

        // when
        another.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates();
    }

    @Test
    public void testAddAll_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded() {
        // given
        field = new PointField(3);

        One one = new One(1, 1);
        One another = new One(1, 1);
        field.addAll(Arrays.asList(one, another));

        assert_twoElements_sameType_sameCell();

        // when
        one.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded();
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
    public void testAddAll_differentTypes_differentCells_afterReplaceCoordinates() {
        // given
        field = new PointField(3);

        One one = new One(2, 1);
        Two two = new Two(2, 0);
        field.addAll(Arrays.asList(one, two));

        // when
        two.move(1, 2);

        // then
        assert_differentTypes_differentCells_afterReplaceCoordinates();
    }

    @Test
    public void testAddAll_severalElements_mixed() {
        // given
        field = new PointField(3);

        // when
        field.addAll(Arrays.asList(new One(1, 1),
                new One(1, 1),
                new One(1, 2),
                new Two(1, 2),
                new Three(2, 2)));

        // then
        assert_severalElements_mixed();
    }

    private void assert_severalElements_mixed() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)\n" +
                "                one3(1,2)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(2,2)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,2)]}]\n" +
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
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,2)]}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{\n" +
                "        Three.class=[\n" +
                "                three5(2,2)]}\n" +
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

    @Test
    public void testReader_getOnlyOneType() {
        // given
        testAdd_severalElements_mixed();
        GamePlayer player = null;

        // when then
        List<Point> all = getReader(player, One.class);

        assertEquals("[one1(1,1), one2(1,1), one3(1,2)]",
                all.toString());

        // then
        assert_severalElements_mixed();
    }

    private List<Point> getReader(GamePlayer player, Class... classes) {
        // when
        BoardReader reader = field.reader(classes);

        // then
        List<Point> result = new LinkedList<>();
        Consumer<Collection<Point>> processor = list -> result.addAll(list);
        reader.addAll(player, processor);
        return result;
    }

    @Test
    public void testReader_getTwoTypesFromDifferentPoints() {
        // given
        testAdd_severalElements_mixed();

        // when then
        List<Point> all = getReader(player, One.class, Two.class);

        assertEquals("[one1(1,1), one2(1,1), one3(1,2), two4(1,2)]",
                all.toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testReader_getTwoTypesFromDifferentPoints_inverseOrder() {
        // given
        testAdd_severalElements_mixed();

        // when then
        List<Point> all = getReader(player, Two.class, One.class);

        assertEquals("[two4(1,2), one1(1,1), one2(1,1), one3(1,2)]",
                all.toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testReader_allTypes() {
        // given
        testAdd_severalElements_mixed();

        // when then
        List<Point> all = getReader(player, Three.class, Two.class, One.class);

        assertEquals("[three5(2,2), two4(1,2), one1(1,1), one2(1,1), one3(1,2)]",
                all.toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testReader_notExists_only() {
        // given
        testAdd_severalElements_mixed();

        // when then
        List<Point> all = getReader(player, Four.class);

        assertEquals("[]",
                all.toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testReader_notExists_withExists() {
        // given
        testAdd_severalElements_mixed();

        // when then
        List<Point> all = getReader(player, Four.class, Three.class, Two.class, One.class);

        assertEquals("[three5(2,2), two4(1,2), one1(1,1), one2(1,1), one3(1,2)]",
                all.toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testReader_emptyList() {
        // given
        testAdd_severalElements_mixed();

        // when then
        List<Point> all = getReader(player);

        assertEquals("[]",
                all.toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testReader_classThatIsOfTheWrongType() {
        // given
        testAdd_severalElements_mixed();

        // when then
        List<Point> all = getReader(player, Object.class, String.class, Byte.class);

        assertEquals("[]",
                all.toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testReader_objectsAreAddedByWaves() {
        // given
        testAdd_severalElements_mixed();

        // when
        BoardReader reader = field.reader(One.class, Two.class, Three.class);

        // then
        List<Collection<Point>> all = new LinkedList<>();
        Consumer<Collection<Point>> processor = list -> all.add(list);
        reader.addAll(player, processor);
        assertEquals("[[one1(1,1), one2(1,1), one3(1,2)],\n" +
                        "[two4(1,2)],\n" +
                        "[three5(2,2)]]",
                all.toString().replace("], [", "],\n["));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testReader_getFieldSize() {
        // given
        testAdd_severalElements_mixed();

        // when
        BoardReader reader = field.reader(One.class, Two.class, Three.class);

        // then
        assertEquals(3, reader.size());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSize() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assertEquals(3, field.size());

        // then
        assert_severalElements_mixed();
    }

}