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
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.printer.BoardReader;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.codenjoy.dojo.client.Utils.split;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PointFieldTest {

    private PointField field;
    private GamePlayer player;
    private List<String> messages;

    private static int id;
    private static int id() {
        return ++id;
    }

    @Before
    public void setup() {
        id = 0;
        field = new PointField().size(3);
        messages = new LinkedList<>();
        player = null;
    }

    class One extends PointImpl implements Tickable {
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

        @Override
        public void tick() {
            messages.add(toString());
        }
    }

    class Two extends PointImpl {
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

    class Three extends PointImpl  {
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

    class Four extends PointImpl implements Tickable {
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

        @Override
        public void tick() {
            messages.add(toString());
        }
    }

    @Test
    public void testEmptyCollection() {
        // when then
        assert_emptyCollection();
    }

    private void assert_emptyCollection() {
        assertEquals("[map=]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testAdd_oneElement() {
        // when
        field.add(new One(1, 1));

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testOf_add_oneElement() {
        // when
        field.of(One.class).add(new One(1, 1));

        // then
        assert_oneElement_one1_at1_1();
    }

    private void assert_oneElement_one1_at1_1() {
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
        // when
        field.add(new One(1, 1));
        field.add(new One(1, 2));

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testAdd_twoElements_sameObjects() {
        // when
        One one = new One(1, 1);
        field.add(one);
        field.add(one);

        // then
        assert_twoElements_sameObjects();
    }

    private void assert_twoElements_sameObjects() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one1(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one1(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testSameOf_add_twoElements_sameType_differentCells() {
        // when
        Accessor<One> of = field.of(One.class);

        of.add(new One(1, 1));
        of.add(new One(1, 2));

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_add_twoElements_sameObjects() {
        // when
        Accessor<One> of = field.of(One.class);

        One one = new One(1, 1);
        of.add(one);
        of.add(one);

        // then
        assert_twoElements_sameObjects();
    }

    @Test
    public void testDifferentOf_add_twoElements_sameType_differentCells() {
        // when
        field.of(One.class).add(new One(1, 1));
        field.of(One.class).add(new One(1, 2));

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testDifferentOf_add_twoElements_sameObjects() {
        // when
        One one = new One(1, 1);
        field.of(One.class).add(one);
        field.of(One.class).add(one);

        // then
        assert_twoElements_sameObjects();
    }

    @Test
    public void testAdd_twoElements_sameType_sameCell() {
        // when
        field.add(new One(1, 1));
        field.add(new One(1, 1));

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_add_twoElements_sameType_sameCell() {
        // when
        Accessor<One> of = field.of(One.class);
        of.add(new One(1, 1));
        of.add(new One(1, 1));

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testDifferentOf_add_twoElements_sameType_sameCell() {
        // when
        field.of(One.class).add(new One(1, 1));
        field.of(One.class).add(new One(1, 1));

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testAdd_twoElements_sameType_sameCell_afterReplaceCoordinates() {
        // given
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
        // when
        field.add(new One(2, 1));
        field.add(new Two(2, 1));

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testOf_add_twoElements_differentTypes_sameCell() {
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
        // when
        field.add(new One(2, 1));
        field.add(new Two(2, 0));

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testOf_add_twoElements_differentTypes_differentCells() {
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
        // when
        field.addAll(Arrays.asList(new One(1, 1)));

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testAddAll_twoElements_sameType_differentCells() {
        // when
        field.addAll(Arrays.asList(new One(1, 1),
                new One(1, 2)));

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testAddAll_twoElements_sameObjects() {
        // when
        One one = new One(1, 1);
        field.addAll(Arrays.asList(one, one));

        // then
        assert_twoElements_sameObjects();
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
        // when
        field.addAll(Arrays.asList(new One(1, 1),
                new One(1, 1)));

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testAddAll_twoElements_sameType_sameCell_afterReplaceCoordinates() {
        // given
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
        // when
        field.addAll(Arrays.asList(new One(2, 1),
                new Two(2, 0)));

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testAddAll_differentTypes_differentCells_afterReplaceCoordinates() {
        // given
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
    public void testSameOf_addAll_oneElement() {
        // when
        field.of(One.class).addAll(Arrays.asList(new One(1, 1)));

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_addAll_twoElements_sameType_differentCells() {
        // when
        field.of(One.class).addAll(Arrays.asList(new One(1, 1),
                new One(1, 2)));

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_addAll_twoElements_sameObjects() {
        // when
        One one = new One(1, 1);
        field.of(One.class).addAll(Arrays.asList(one, one));

        // then
        assert_twoElements_sameObjects();
    }

    @Test
    public void testSameOf_addAll_twoElements_sameType_sameCell() {
        // when
        field.of(One.class).addAll(Arrays.asList(new One(1, 1),
                new One(1, 1)));

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_addAll_twoElements_sameType_sameCell_afterReplaceCoordinates() {
        // given
        One one = new One(1, 1);
        One another = new One(1, 1);
        field.of(One.class).addAll(Arrays.asList(one, another));

        assert_twoElements_sameType_sameCell();

        // when
        another.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates();
    }

    @Test
    public void testSameOf_addAll_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded() {
        // given
        One one = new One(1, 1);
        One another = new One(1, 1);
        field.of(One.class).addAll(Arrays.asList(one, another));

        assert_twoElements_sameType_sameCell();

        // when
        one.move(2, 1);

        // then
        assert_twoElements_sameType_sameCell_afterReplaceCoordinates_changeFirstAdded();
    }

    @Test
    public void testSameOf_addAll_twoElements_differentTypes_sameCell() {
        // when
        field.of(One.class)
                .addAll((List) Arrays.asList(new One(2, 1), new Two(2, 1)));

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testSameOf_addAll_twoElements_differentTypes_differentCells() {
        // when
        field.of(Two.class)
                .addAll((List) Arrays.asList(new One(2, 1), new Two(2, 0)));

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_addAll_differentTypes_differentCells_afterReplaceCoordinates() {
        // given
        One one = new One(2, 1);
        Two two = new Two(2, 0);
        field.of(Two.class).addAll((List) Arrays.asList(one, two));

        // when
        two.move(1, 2);

        // then
        assert_differentTypes_differentCells_afterReplaceCoordinates();
    }

    @Test
    public void testSameOf_addAll_severalElements_mixed() {
        // when
        field.of(Three.class)
                .addAll((List) Arrays.asList(new One(1, 1),
                        new One(1, 1),
                        new One(1, 2),
                        new Two(1, 2),
                        new Three(2, 2)));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testContains_oneElement() {
        // given
        testAdd_oneElement();

        // when then
        assertContains_oneElement();

        // then
        assert_oneElement_one1_at1_1();
    }

    private <T extends Point> T get(Class<T> filter, int index) {
        return field.of(filter).all().get(index);
    }

    private <T extends Point> T get(Class<T> filter, int x, int y) {
        return field.of(filter).getAt(pt(x, y)).get(0);
    }

    private void assertContains_oneElement() {
        One one = get(One.class, 0);
        assertEquals(true, field.of(One.class).contains(one));
        assertEquals(false, field.of(Two.class).contains(one));

        assertException(() -> field.of(One.class).contains(null),
                NullPointerException.class);

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
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        assertEquals(true, field.of(One.class).contains(one1));
        assertEquals(false, field.of(Two.class).contains(one2));
        assertEquals(true, field.of(One.class).contains(one2));
        assertEquals(false, field.of(Two.class).contains(one1));

        assertException(() -> field.of(One.class).contains(null),
                NullPointerException.class);

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
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        assertEquals(true, field.of(One.class).contains(one));
        assertEquals(true, field.of(Two.class).contains(two));
        // true потому что contains(pt) а не same object
        assertEquals(true, field.of(One.class).contains(two));
        // true потому что contains(pt) а не same object
        assertEquals(true, field.of(Two.class).contains(one));

        assertException(() -> field.of(One.class).contains(null),
                NullPointerException.class);

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
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        assertEquals(true, field.of(One.class).contains(one));
        assertEquals(true, field.of(Two.class).contains(two));
        assertEquals(false, field.of(One.class).contains(two));
        assertEquals(false, field.of(Two.class).contains(one));

        assertException(() -> field.of(One.class).contains(null),
                NullPointerException.class);
        assertException(() -> field.of(Two.class).contains(null),
                NullPointerException.class);

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
    public void testReader_twoElements_sameObjects() {
        // given
        testAdd_twoElements_sameObjects();

        // when then
        List<Point> all = getReader(player, One.class);

        assertEquals("[one1(1,1), one1(1,1)]",
                all.toString());

        // then
        assert_twoElements_sameObjects();
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
        assertEquals("[[one1(1,1), one2(1,1), one3(1,2)], \n" +
                        "[two4(1,2)], \n" +
                        "[three5(2,2)]]",
                split(all,
                        "], \n["));

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

    @Test
    public void testOf_stream_severalElements_mixed() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assertEquals("[one1(1,1), one2(1,1), one3(1,2)]",
                toString(field.of(One.class).stream()));

        assertEquals("[two4(1,2)]",
                toString(field.of(Two.class).stream()));

        assertEquals("[three5(2,2)]",
                toString(field.of(Three.class).stream()));

        assertEquals("[]",
                toString(field.of(Four.class).stream()));

        // then
        assert_severalElements_mixed();

        // when
        get(One.class, 1, 1).move(2, 2);
        get(Two.class, 1, 2).move(1, 1);

        // when then
        assertEquals("[one2(1,1), one3(1,2), one1(2,2)]",
                toString(field.of(One.class).stream()));

        assertEquals("[two4(1,1)]",
                toString(field.of(Two.class).stream()));

        assertEquals("[three5(2,2)]",
                toString(field.of(Three.class).stream()));

        assertEquals("[]",
                toString(field.of(Four.class).stream()));
    }

    private String toString(Stream stream) {
        return stream.collect(toList()).toString();
    }

    @Test
    public void testOf_stream_severalElements_mixed_inOneCell() {
        // given
        One some = givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when then
        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                toString(field.of(One.class).stream()));

        assertEquals("[two4(1,1)]",
                toString(field.of(Two.class).stream()));

        assertEquals("[three5(1,1)]",
                toString(field.of(Three.class).stream()));

        assertEquals("[]",
                toString(field.of(Four.class).stream()));

        // when
        some.move(2, 2);
        some.move(1, 1);

        // when then
        assertEquals("[one1(1,1), one3(1,1), one2(1,1)]",
                toString(field.of(One.class).stream()));

        assertEquals("[two4(1,1)]",
                toString(field.of(Two.class).stream()));

        assertEquals("[three5(1,1)]",
                toString(field.of(Three.class).stream()));

        assertEquals("[]",
                toString(field.of(Four.class).stream()));

        // then
        assert_severalElements_mixed_inOneCell_changedOrder();
    }

    @Test
    public void testOf_size_severalElements_mixed() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assert_size();

        // then
        assert_severalElements_mixed();

        // when
        get(One.class, 1, 1).move(2, 2);
        get(Two.class, 1, 2).move(1, 1);

        // when then
        assert_size();
    }

    private void assert_size() {
        assertEquals(3, field.of(One.class).size());
        assertEquals(1, field.of(Two.class).size());
        assertEquals(1, field.of(Three.class).size());
        assertEquals(0, field.of(Four.class).size());
    }

    @Test
    public void testOf_size_severalElements_mixed_inOneCell() {
        // given
        One some = givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when then
        assert_size();

        // when
        some.move(2, 2);
        some.move(1, 1);

        // when then
        assert_size();

        // then
        assert_severalElements_mixed_inOneCell_changedOrder();
    }

    @Test
    public void testOf_clear_severalElements_mixed() {
        // given
        testAdd_severalElements_mixed();

        // when
        field.of(Four.class).size();

        // then
        assert_severalElements_mixed();

        // when
        field.of(One.class).clear();

        // then
        assert_mixed_without_any_one();

        // when
        field.of(Two.class).clear();

        // then
        assert_oneElement_three5_at2_2();

        // when
        field.of(Three.class).clear();

        // then
        assert_emptyCollection();
    }

    @Test
    public void testOf_clear_severalElements_mixed_inOneCell() {
        // given
        givenSeveralElements_mixed_inOneCell();

        // when
        field.of(Four.class).size();

        // then
        assert_severalElements_mixed_inOneCell();

        // when
        field.of(One.class).clear();

        // then
        assert_mixed_inOneCell_withoutAny_one();

        // when
        field.of(Two.class).clear();

        // then
        assert_oneElement_three5_at1_1();

        // when
        field.of(Three.class).clear();

        // then
        assert_emptyCollection();
    }

    @Test
    public void testOf_all_severalElements_mixed() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assertEquals("[one1(1,1), one2(1,1), one3(1,2)]",
                field.of(One.class).all().toString());

        assertEquals("[two4(1,2)]",
                field.of(Two.class).all().toString());

        assertEquals("[three5(2,2)]",
                field.of(Three.class).all().toString());

        assertEquals("[]",
                field.of(Four.class).all().toString());

        // then
        assert_severalElements_mixed();

        // when
        get(One.class, 1, 1).move(2, 2);
        get(Two.class, 1, 2).move(1, 1);

        // when then
        assertEquals("[one2(1,1), one3(1,2), one1(2,2)]",
                field.of(One.class).all().toString());

        assertEquals("[two4(1,1)]",
                field.of(Two.class).all().toString());

        assertEquals("[three5(2,2)]",
                field.of(Three.class).all().toString());

        assertEquals("[]",
                field.of(Four.class).all().toString());
    }

    @Test
    public void testOf_all_severalElements_mixed_inOneCell() {
        // given
        One some = givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when then
        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                field.of(One.class).all().toString());

        assertEquals("[two4(1,1)]",
                field.of(Two.class).all().toString());

        assertEquals("[three5(1,1)]",
                field.of(Three.class).all().toString());

        assertEquals("[]",
                field.of(Four.class).all().toString());

        // when
        some.move(2, 2);
        some.move(1, 1);

        // when then
        assertEquals("[one1(1,1), one3(1,1), one2(1,1)]",
                field.of(One.class).all().toString());

        assertEquals("[two4(1,1)]",
                field.of(Two.class).all().toString());

        assertEquals("[three5(1,1)]",
                field.of(Three.class).all().toString());

        assertEquals("[]",
                field.of(Four.class).all().toString());

        // then
        assert_severalElements_mixed_inOneCell_changedOrder();
    }

    private One givenSeveralElements_mixed_inOneCell() {
        field.add(new One(1, 1));
        One some = new One(1, 1);
        field.add(some);
        field.add(new One(1, 1));
        field.add(new Two(1, 1));
        field.add(new Three(1, 1));
        return some;
    }

    private void assert_severalElements_mixed_inOneCell() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)\n" +
                "                one3(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)\n" +
                "                one3(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    private void assert_severalElements_mixed_inOneCell_changedOrder() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one3(1,1)\n" +
                "                one2(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one3(1,1)\n" +
                "                one2(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testOf_all_methodReturnsReadOnlyCollectionNotACopy_caseWrite() {
        // given
        givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when then
        List<One> all = field.of(One.class).all();
        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                all.toString());

        // when then
        assertUnsupported(() -> all.remove(1));
        assertUnsupported(() -> all.add(new One(1, 1)));
        assertUnsupported(() -> all.addAll(Arrays.asList(new One(1, 1))));
        assertUnsupported(() -> all.remove(new One(1, 1)));
        assertUnsupported(() -> all.clear());
        assertUnsupported(() -> all.removeAll(Arrays.asList(new One(1, 1))));
        assertUnsupported(() -> all.sort(Comparator.naturalOrder()));
        assertUnsupported(() -> all.replaceAll(UnaryOperator.identity()));
        assertUnsupported(() -> all.subList(0, 1).clear());
        assertUnsupported(() -> {
            Iterator<One> iterator = all.iterator();
            iterator.hasNext();
            iterator.remove();
        });

        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                all.toString());

        // then
        assert_severalElements_mixed_inOneCell();
    }

    private void assertUnsupported(Runnable runnable) {
        assertException(runnable, UnsupportedOperationException.class);
    }

    private void assertException(Runnable runnable, Class<? extends Throwable> expected) {
        try {
            runnable.run();
            fail("Expected exception");
        } catch (Throwable actual) {
            assertEquals(expected, actual.getClass());
        }
    }

    @Test
    public void testOf_all_methodReturnsReadOnlyCollectionNotACopy_caseUpdateElements() {
        // given
        givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when then
        List<One> all = field.of(One.class).all();
        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                all.toString());

        // when then
        all.get(1).move(2, 2);


        all = field.of(One.class).all();
        assertEquals("[one1(1,1), one3(1,1), one2(2,2)]",
                all.toString());

        // when then
        all.get(2).move(1, 1);

        all = field.of(One.class).all();
        assertEquals("[one1(1,1), one3(1,1), one2(1,1)]",
                all.toString());

        // then
        assert_severalElements_mixed_inOneCell_changedOrder();
    }

    @Test
    public void testOf_copy_methodReturnsCopyOfTheOriginalCollection() {
        // given
        givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when then
        List<One> all = field.of(One.class).copy();
        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                all.toString());

        // when then
        all.remove(1);

        all = field.of(One.class).copy();
        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                all.toString());

        // then
        assert_severalElements_mixed_inOneCell();
    }

    @Test
    public void testOf_copy_methodReturnsCopyOfTheOriginalCollection_butEveryElementCanBeChanged() {
        // given
        givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when then
        List<One> all = field.of(One.class).copy();
        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                all.toString());

        // when then
        all.get(1).move(2, 2);

        all = field.of(One.class).copy();
        assertEquals("[one1(1,1), one3(1,1), one2(2,2)]",
                all.toString());

        // when then
        all.get(2).move(1, 1);

        all = field.of(One.class).copy();
        assertEquals("[one1(1,1), one3(1,1), one2(1,1)]",
                all.toString());

        // then
        assert_severalElements_mixed_inOneCell_changedOrder();
    }

    @Test
    public void testOf_copy_severalElements_mixed() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assertEquals("[one1(1,1), one2(1,1), one3(1,2)]",
                field.of(One.class).copy().toString());

        assertEquals("[two4(1,2)]",
                field.of(Two.class).copy().toString());

        assertEquals("[three5(2,2)]",
                field.of(Three.class).copy().toString());

        assertEquals("[]",
                field.of(Four.class).copy().toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testOf_copy_severalElements_mixed_inOneCell() {
        // given
        One some = givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when then
        assertEquals("[one1(1,1), one2(1,1), one3(1,1)]",
                field.of(One.class).copy().toString());

        assertEquals("[two4(1,1)]",
                field.of(Two.class).copy().toString());

        assertEquals("[three5(1,1)]",
                field.of(Three.class).copy().toString());

        assertEquals("[]",
                field.of(Four.class).copy().toString());

        // when
        some.move(2, 2);
        some.move(1, 1);

        // when then
        assertEquals("[one1(1,1), one3(1,1), one2(1,1)]",
                field.of(One.class).copy().toString());

        assertEquals("[two4(1,1)]",
                field.of(Two.class).copy().toString());

        assertEquals("[three5(1,1)]",
                field.of(Three.class).copy().toString());

        assertEquals("[]",
                field.of(Four.class).copy().toString());

        // then
        assert_severalElements_mixed_inOneCell_changedOrder();
    }

    @Test
    public void testSameOf_removeExact_oneElement() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);

        // when
        field.of(One.class).removeExact(one);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeExact_oneElement_cantRemove_typeIsNotSame() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);

        // when
        Class<Two> otherType = Two.class;
        Accessor of = field.of(otherType);
        of.removeExact((Point)one);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeExact_oneElement_cantRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        One anotherOne = new One(one.getX(), one.getY());

        // when
        field.of(One.class).removeExact(anotherOne);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeExact_oneElement_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        One anotherOne = new One(one.getX() + 1, one.getY() + 1);

        // when
        field.of(One.class).removeExact(anotherOne);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeExact_oneElement_cantRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());

        // when
        Accessor of = field.of(One.class);
        of.removeExact(anotherTwo);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeExact_oneElement_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        One anotherOne = new One(one.getX(), one.getY());

        // when
        Accessor of = field.of(Two.class);
        of.removeExact(anotherOne);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeExact_oneElement_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        Two another = new Two(one.getX(), one.getY());

        // when
        field.of(Two.class).removeExact(another);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeExact_exceptionIfNull() {
        // given
        testAdd_oneElement();

        // when then
        assertException(() -> field.of(One.class).removeExact(null),
                NullPointerException.class);
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_sameCell() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);

        // when
        field.of(One.class).removeExact(one2);

        // then
        assert_oneElement_one1_at1_1();

        // when
        field.of(One.class).removeExact(one1);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_sameCell_cantRemove_typeIsNotSame() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);

        // when
        Class<Two> otherType = Two.class;
        Accessor of = field.of(otherType);
        of.removeExact((Point)one1);
        of.removeExact((Point)one2);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_sameCell_cantRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX(), one1.getY());
        One anotherOne2 = new One(one2.getX(), one2.getY());

        // when
        field.of(One.class).removeExact(anotherOne1);
        field.of(One.class).removeExact(anotherOne2);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_sameCell_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX() + 1, one1.getY() + 1);
        One anotherOne2 = new One(one2.getX() + 1, one2.getY() + 1);

        // when
        field.of(One.class).removeExact(anotherOne1);
        field.of(One.class).removeExact(anotherOne2);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_sameCell_cantRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        Two anotherTwo1 = new Two(one1.getX(), one1.getY());
        Two anotherTwo2 = new Two(one2.getX(), one2.getY());

        // when
        Accessor of = field.of(One.class);
        of.removeExact(anotherTwo1);
        of.removeExact(anotherTwo2);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_sameCell_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX(), one1.getY());
        One anotherOne2 = new One(one2.getX(), one2.getY());

        // when
        Accessor of = field.of(Two.class);
        of.removeExact(anotherOne1);
        of.removeExact(anotherOne2);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_sameCell_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        Two anotherTwo1 = new Two(one1.getX(), one1.getY());
        Two anotherTwo2 = new Two(one2.getX(), one2.getY());

        // when
        field.of(Two.class).removeExact(anotherTwo1);
        field.of(Two.class).removeExact(anotherTwo2);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_differentCells() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);

        // when
        field.of(One.class).removeExact(one2);

        // then
        assert_oneElement_one1_at1_1();

        // when
        field.of(One.class).removeExact(one1);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_differentCells_cantRemove_typeIsNotSame() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);

        // when
        Class<Two> otherType = Two.class;
        Accessor of = field.of(otherType);
        of.removeExact((Point)one1);
        of.removeExact((Point)one2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_differentCells_cantRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX(), one1.getY());
        One anotherOne2 = new One(one2.getX(), one2.getY());

        // when
        field.of(One.class).removeExact(anotherOne1);
        field.of(One.class).removeExact(anotherOne2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_differentCells_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX() + 1, one1.getY() + 1);
        One anotherOne2 = new One(one2.getX() + 1, one2.getY() + 1);

        // when
        field.of(One.class).removeExact(anotherOne1);
        field.of(One.class).removeExact(anotherOne2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        Two anotherTwo1 = new Two(one1.getX(), one1.getY());
        Two anotherTwo2 = new Two(one2.getX(), one2.getY());

        // when
        Accessor of = field.of(One.class);
        of.removeExact(anotherTwo1);
        of.removeExact(anotherTwo2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX(), one1.getY());
        One anotherOne2 = new One(one2.getX(), one2.getY());

        // when
        Accessor of = field.of(Two.class);
        of.removeExact(anotherOne1);
        of.removeExact(anotherOne2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_sameType_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        Two anotherTwo1 = new Two(one1.getX(), one1.getY());
        Two anotherTwo2 = new Two(one2.getX(), one2.getY());

        // when
        field.of(Two.class).removeExact(anotherTwo1);
        field.of(Two.class).removeExact(anotherTwo2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_sameCell() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one1 = get(One.class, 0);
        Two two = get(Two.class, 0);

        // when
        field.of(Two.class).removeExact(two);

        // then
        assert_oneElement_one1_at2_1();

        // when
        field.of(One.class).removeExact(one1);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_sameCell_cantRemove_typeIsNotSame() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);

        // when
        Class<Two> twoType = Two.class;
        Accessor of1 = field.of(twoType);
        of1.removeExact((Point)one);

        Class<One> ontType = One.class;
        Accessor of2 = field.of(ontType);
        of2.removeExact((Point)two);

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_sameCell_cantRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX(), one.getY());
        Two anotherTwo = new Two(two.getX(), two.getY());

        // when
        field.of(One.class).removeExact(anotherOne);
        field.of(Two.class).removeExact(anotherTwo);

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_sameCell_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX() + 1, one.getY() + 1);
        Two anotherTwo = new Two(two.getX() + 1, two.getY() + 1);

        // when
        field.of(One.class).removeExact(anotherOne);
        field.of(Two.class).removeExact(anotherTwo);

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_sameCell_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());
        One anotherOne = new One(two.getX(), two.getY());

        // when
        field.of(Two.class).removeExact(anotherTwo);
        field.of(One.class).removeExact(anotherOne);

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_sameCell_cantRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());
        One anotherOne = new One(two.getX(), two.getY());

        // when
        Accessor ofOne = field.of(One.class);
        ofOne.removeExact(anotherTwo);

        Accessor ofTwo = field.of(Two.class);
        ofTwo.removeExact(anotherOne);

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_sameCell_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX(), one.getY());
        Two anotherTwo = new Two(two.getX(), two.getY());

        // when
        Accessor ofTwo = field.of(Two.class);
        ofTwo.removeExact(anotherOne);

        Accessor ofOne = field.of(One.class);
        ofOne.removeExact(anotherTwo);

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    private void assert_oneElement_one1_at2_1() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(2,1)]}]\n" +
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
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    private void assert_oneElement_two2_at2_1() {
        assertEquals("[map={\n" +
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
                "        Two.class=[\n" +
                "                two2(2,1)]}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    private void assert_oneElement_two2_at2_0() {
        assertEquals("[map={\n" +
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
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_differentCells() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one1 = get(One.class, 0);
        Two two = get(Two.class, 0);

        // when
        field.of(Two.class).removeExact(two);

        // then
        assert_oneElement_one1_at2_1();

        // when
        field.of(One.class).removeExact(one1);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_differentCells_cantRemove_typeIsNotSame() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);

        // when
        Class<Two> twoType = Two.class;
        Accessor ofTwo = field.of(twoType);
        ofTwo.removeExact((Point)one);

        Class<One> ontType = One.class;
        Accessor ofOne = field.of(ontType);
        ofOne.removeExact((Point)two);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_differentCells_cantRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX(), one.getY());
        Two anotherTwo = new Two(two.getX(), two.getY());

        // when
        field.of(One.class).removeExact(anotherOne);
        field.of(Two.class).removeExact(anotherTwo);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_differentCells_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX() + 1, one.getY() + 1);
        Two anotherTwo = new Two(two.getX() + 1, two.getY() + 1);

        // when
        field.of(One.class).removeExact(anotherOne);
        field.of(Two.class).removeExact(anotherTwo);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());
        One anotherOne = new One(two.getX(), two.getY());

        // when
        field.of(Two.class).removeExact(anotherTwo);
        field.of(One.class).removeExact(anotherOne);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());
        One anotherOne = new One(two.getX(), two.getY());

        // when
        Accessor ofOne = field.of(One.class);
        ofOne.removeExact(anotherTwo);

        Accessor ofTwo = field.of(Two.class);
        ofTwo.removeExact(anotherOne);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeExact_twoElements_differentTypes_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX(), one.getY());
        Two anotherTwo = new Two(two.getX(), two.getY());

        // when
        Accessor ofTwo = field.of(Two.class);
        ofTwo.removeExact(anotherOne);

        Accessor ofOne = field.of(One.class);
        ofOne.removeExact(anotherTwo);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeExact_severalElements_mixed_inOneCell() {
        // given
        One one = givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when
        field.of(One.class).removeExact(get(One.class, 1, 1));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one2(1,1)\n" +
                "                one3(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one2(1,1)\n" +
                "                one3(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());

        // when
        field.of(One.class).removeExact(get(One.class, 1, 1));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one3(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one3(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());

        // when
        field.of(One.class).removeExact(get(One.class, 1, 1));

        // then
        assert_mixed_inOneCell_withoutAny_one();

        // when
        field.of(Two.class).removeExact(get(Two.class, 1, 1));

        // then
        assert_oneElement_three5_at1_1();

        // when
        field.of(Three.class).removeExact(get(Three.class, 1, 1));

        // then
        assert_emptyCollection();
    }

    private void assert_mixed_inOneCell_withoutAny_one() {
        assertEquals("[map={\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testSameOf_removeExact_severalElements_mixed() {
        // given
        testAdd_severalElements_mixed();


        // when
        field.of(One.class).removeExact(get(One.class, 1, 1));
        field.of(One.class).removeExact(get(One.class, 1, 1));

        // then
        assert_mixed_without_one_at1_1();

        // when
        field.of(One.class).removeExact(get(One.class, 1, 2));

        // then
        assert_mixed_without_any_one();

        // when
        field.of(Two.class).removeExact(get(Two.class, 1, 2));

        // then
        assert_oneElement_three5_at2_2();

        // when
        field.of(Three.class).removeExact(get(Three.class, 2, 2));

        // then
        assert_emptyCollection();
    }

    private void assert_oneElement_three5_at2_2() {
        assertEquals("[map={\n" +
                "        Three.class=[\n" +
                "                three5(2,2)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{\n" +
                "        Three.class=[\n" +
                "                three5(2,2)]}\n" +
                "]", field.toString());
    }

    private void assert_oneElement_three5_at1_1() {
        assertEquals("[map={\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        Three.class=[\n" +
                "                three5(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    private void assert_mixed_without_one_at1_1() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
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
                "[1,1]:{}\n" +
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
    public void testSameOf_removeAt_oneElement() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);

        // when
        field.of(One.class).removeAt(one);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_oneElement_cantRemove_typeIsNotSame() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);

        // when
        Class<Two> otherType = Two.class;
        field.of(otherType).removeAt((Point)one);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeAt_oneElement_shouldRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        One anotherOne = new One(one.getX(), one.getY());

        // when
        field.of(One.class).removeAt(anotherOne);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_oneElement_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        One anotherOne = new One(one.getX() + 1, one.getY() + 1);

        // when
        field.of(One.class).removeAt(anotherOne);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeAt_oneElement_shouldRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());

        // when
        field.of(One.class).removeAt(anotherTwo);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_oneElement_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        One anotherOne = new One(one.getX(), one.getY());

        // when
        field.of(Two.class).removeAt(anotherOne);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeAt_oneElement_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_oneElement();
        One one = get(One.class, 0);
        Two another = new Two(one.getX(), one.getY());

        // when
        field.of(Two.class).removeAt(another);

        // then
        assert_oneElement_one1_at1_1();
    }

    @Test
    public void testSameOf_removeAt_exceptionIfNull() {
        // given
        testAdd_oneElement();

        // when then
        assertException(() -> field.of(One.class).removeAt(null),
                NullPointerException.class);
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_sameCell() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);

        // when
        field.of(One.class).removeAt(one2);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_sameCell_cantRemove_typeIsNotSame() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);

        // when
        Class<Two> otherType = Two.class;
        field.of(otherType).removeAt((Point)one1);
        field.of(otherType).removeAt((Point)one2);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_sameCell_shouldRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        assertEquals(one1, one2);
        One anotherOne = new One(one1.getX(), one1.getY());

        // when
        field.of(One.class).removeAt(anotherOne);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_sameCell_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX() + 1, one1.getY() + 1);
        One anotherOne2 = new One(one2.getX() + 1, one2.getY() + 1);

        // when
        field.of(One.class).removeAt(anotherOne1);
        field.of(One.class).removeAt(anotherOne2);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_sameCell_shouldRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        assertEquals(one1, one2);
        Two anotherTwo = new Two(one1.getX(), one1.getY());

        // when
        field.of(One.class).removeAt(anotherTwo);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_sameCell_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        assertEquals(one1, one2);
        One anotherOne = new One(one1.getX(), one1.getY());

        // when
        field.of(Two.class).removeAt(anotherOne);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_sameCell_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_twoElements_sameType_sameCell();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        assertEquals(one1, one2);
        Two anotherTwo = new Two(one1.getX(), one1.getY());

        // when
        field.of(Two.class).removeAt(anotherTwo);

        // then
        assert_twoElements_sameType_sameCell();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_differentCells() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);

        // when
        field.of(One.class).removeAt(one2);

        // then
        assert_oneElement_one1_at1_1();

        // when
        field.of(One.class).removeAt(one1);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_differentCells_cantRemove_typeIsNotSame() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);

        // when
        Class<Two> otherType = Two.class;
        field.of(otherType).removeAt((Point)one1);
        field.of(otherType).removeAt((Point)one2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_differentCells_shouldRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX(), one1.getY());
        One anotherOne2 = new One(one2.getX(), one2.getY());

        // when
        field.of(One.class).removeAt(anotherOne1);

        // then
        assert_oneElement_one2_at1_2();

        // when
        field.of(One.class).removeAt(anotherOne2);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_differentCells_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX() + 1, one1.getY() + 1);
        One anotherOne2 = new One(one2.getX() + 1, one2.getY() + 1);

        // when
        field.of(One.class).removeAt(anotherOne1);
        field.of(One.class).removeAt(anotherOne2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_differentCells_shouldRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        Two anotherTwo1 = new Two(one1.getX(), one1.getY());
        Two anotherTwo2 = new Two(one2.getX(), one2.getY());

        // when
        field.of(One.class).removeAt(anotherTwo1);

        // then
        assert_oneElement_one2_at1_2();

        // when
        field.of(One.class).removeAt(anotherTwo2);

        // then
        assert_emptyCollection();
    }

    private void assert_oneElement_one2_at1_2() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one2(1,2)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{}\n" +
                "[1,2]:{\n" +
                "        One.class=[\n" +
                "                one2(1,2)]}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        One anotherOne1 = new One(one1.getX(), one1.getY());
        One anotherOne2 = new One(one2.getX(), one2.getY());

        // when
        field.of(Two.class).removeAt(anotherOne1);
        field.of(Two.class).removeAt(anotherOne2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeAt_twoElements_sameType_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_twoElements_sameType_differentCells();
        One one1 = get(One.class, 0);
        One one2 = get(One.class, 1);
        Two anotherTwo1 = new Two(one1.getX(), one1.getY());
        Two anotherTwo2 = new Two(one2.getX(), one2.getY());

        // when
        field.of(Two.class).removeAt(anotherTwo1);
        field.of(Two.class).removeAt(anotherTwo2);

        // then
        assert_twoElements_sameType_differentCells();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_sameCell() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one1 = get(One.class, 0);
        Two two = get(Two.class, 0);

        // when
        field.of(Two.class).removeAt(two);

        // then
        assert_oneElement_one1_at2_1();

        // when
        field.of(One.class).removeAt(one1);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_sameCell_shouldRemove_typeIsNotSame() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);

        // when
        Class<Two> twoType = Two.class;
        field.of(twoType).removeAt((Point)one);

        // then
        assert_oneElement_one1_at2_1();

        // when
        Class<One> oneType = One.class;
        field.of(oneType).removeAt((Point)two);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_sameCell_shouldRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX(), one.getY());
        Two anotherTwo = new Two(two.getX(), two.getY());

        // when
        field.of(One.class).removeAt(anotherOne);

        // then
        assert_oneElement_two2_at2_1();

        // when
        field.of(Two.class).removeAt(anotherTwo);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_sameCell_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX() + 1, one.getY() + 1);
        Two anotherTwo = new Two(two.getX() + 1, two.getY() + 1);

        // when
        field.of(One.class).removeAt(anotherOne);
        field.of(Two.class).removeAt(anotherTwo);

        // then
        assert_twoElements_differentTypes_sameCell();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_shouldCell_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());
        One anotherOne = new One(two.getX(), two.getY());

        // when
        field.of(Two.class).removeAt(anotherTwo);
        field.of(One.class).removeAt(anotherOne);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_sameCell_shouldRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());
        One anotherOne = new One(two.getX(), two.getY());

        // when
        field.of(One.class).removeAt(anotherTwo);

        // then
        assert_oneElement_two2_at2_1();

        // when
        field.of(Two.class).removeAt(anotherOne);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_sameCell_shouldRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_twoElements_differentTypes_sameCell();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX(), one.getY());
        Two anotherTwo = new Two(two.getX(), two.getY());

        // when
        field.of(Two.class).removeAt(anotherOne);

        // then
        assert_oneElement_one1_at2_1();

        // when
        field.of(One.class).removeAt(anotherTwo);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_differentCells() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one1 = get(One.class, 0);
        Two two = get(Two.class, 0);

        // when
        field.of(Two.class).removeAt(two);

        // then
        assert_oneElement_one1_at2_1();

        // when
        field.of(One.class).removeAt(one1);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_differentCells_cantRemove_typeIsNotSame() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);

        // when
        Class<Two> twoType = Two.class;
        field.of(twoType).removeAt((Point)one);

        Class<One> ontType = One.class;
        field.of(ontType).removeAt((Point)two);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_differentCells_shouldRemove_objectIsNotSame_samePoint_sameType() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX(), one.getY());
        Two anotherTwo = new Two(two.getX(), two.getY());

        // when
        field.of(One.class).removeAt(anotherOne);

        // then
        assert_oneElement_two2_at2_0();

        // when
        field.of(Two.class).removeAt(anotherTwo);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_differentCells_cantRemove_objectIsNotSame_otherPoint_sameType() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX() + 1, one.getY() + 1);
        Two anotherTwo = new Two(two.getX() + 1, two.getY() + 1);

        // when
        field.of(One.class).removeAt(anotherOne);
        field.of(Two.class).removeAt(anotherTwo);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inOfAndObject() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());
        One anotherOne = new One(two.getX(), two.getY());

        // when
        field.of(Two.class).removeAt(anotherTwo);
        field.of(One.class).removeAt(anotherOne);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_differentCells_shouldRemove_objectIsNotSame_samePoint_otherType_inObject() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        Two anotherTwo = new Two(one.getX(), one.getY());
        One anotherOne = new One(two.getX(), two.getY());

        // when
        field.of(One.class).removeAt(anotherTwo);

        // then
        assert_oneElement_two2_at2_0();

        // when
        field.of(Two.class).removeAt(anotherOne);

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_twoElements_differentTypes_differentCells_cantRemove_objectIsNotSame_samePoint_otherType_inOf() {
        // given
        testAdd_twoElements_differentTypes_differentCells();
        One one = get(One.class, 0);
        Two two = get(Two.class, 0);
        One anotherOne = new One(one.getX(), one.getY());
        Two anotherTwo = new Two(two.getX(), two.getY());

        // when
        field.of(Two.class).removeAt(anotherOne);
        field.of(One.class).removeAt(anotherTwo);

        // then
        assert_twoElements_differentTypes_differentCells();
    }

    @Test
    public void testSameOf_removeAt_severalElements_mixed_inOneCell() {
        // given
        One one = givenSeveralElements_mixed_inOneCell();

        assert_severalElements_mixed_inOneCell();

        // when
        field.of(One.class).removeAt(one.copy());

        // then
        assert_mixed_inOneCell_withoutAny_one();

        // when
        field.of(Three.class).removeAt(one.copy());

        // then
        assertEquals("[map={\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        Two.class=[\n" +
                "                two4(1,1)]}\n" +
                "[1,2]:{}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{}\n" +
                "]", field.toString());

        // when
        field.of(Two.class).removeAt(one.copy());

        // then
        assert_emptyCollection();
    }

    @Test
    public void testSameOf_removeAt_severalElements_mixed() {
        // given
        testAdd_severalElements_mixed();

        // when
        field.of(One.class).removeAt(pt(1, 1));

        // then
        assert_mixed_without_one_at1_1();

        // when
        field.of(One.class).removeAt(pt(1, 2));

        // then
        assert_mixed_without_any_one();

        // when
        field.of(Two.class).removeAt(pt(1, 2));

        // then
        assert_oneElement_three5_at2_2();

        // when
        field.of(Three.class).removeAt(pt(2, 2));

        // then
        assert_emptyCollection();
    }

    private void assert_mixed_without_any_one() {
        assertEquals("[map={\n" +
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
                "[1,1]:{}\n" +
                "[1,2]:{\n" +
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
    public void testSameOf_removeNotSame_mixed_removeAllExceptOne_severalObjects() {
        // given
        testAdd_severalElements_mixed();
        One one = get(One.class, 0);
        assertEquals("one1(1,1)", one.toString());

        // when
        field.of(One.class).removeNotSame(Arrays.asList(one));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)]}\n" +
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
                "                one1(1,1)]}\n" +
                "[1,2]:{\n" +
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
    public void testSameOf_removeNotSame_mixed_removeOnlyOne_severalObjects() {
        // given
        testAdd_severalElements_mixed();

        One one1 = get(One.class, 0);
        assertEquals("one1(1,1)", one1.toString());

        One one2 = get(One.class, 1);
        assertEquals("one2(1,1)", one2.toString());

        // when
        field.of(One.class).removeNotSame(Arrays.asList(one1, one2));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)]}\n" +
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
    public void testSameOf_removeNotSame_mixed_doNotRemove_onlyOne() {
        // given
        testAdd_severalElements_mixed();
        Two two = get(Two.class, 0);
        assertEquals("two4(1,2)", two.toString());

        // when
        field.of(Two.class).removeNotSame(Arrays.asList(two));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSameOf_removeNotSame_mixed_doNotRemove_otherType() {
        // given
        testAdd_severalElements_mixed();
        Four four = new Four(5, 5);

        // when
        field.of(Four.class).removeNotSame(Arrays.asList(four));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSameOf_removeNotSame_mixed_removeAll_becausePassedNotExists() {
        // given
        testAdd_severalElements_mixed();
        One one = new One(5, 5); // not exists

        // when
        field.of(One.class).removeNotSame(Arrays.asList(one));

        // then
        assert_mixed_without_any_one();
    }

    @Test
    public void testSameOf_removeIn_mixed_severalObjects() {
        // given
        testAdd_severalElements_mixed();
        One one = get(One.class, 0);
        assertEquals("one1(1,1)", one.toString());

        // when
        field.of(One.class).removeIn(Arrays.asList(one));

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
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
                "[1,1]:{}\n" +
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
    public void testSameOf_removeIn_mixed_oneObject() {
        // given
        testAdd_severalElements_mixed();
        Two two = get(Two.class, 0);
        assertEquals("two4(1,2)", two.toString());

        // when
        field.of(Two.class).removeIn(Arrays.asList(two));

        // then
        assert_mixed_without_any_two();
    }

    @Test
    public void testSameOf_removeIn_mixed_twoCoordinates() {
        // given
        testAdd_severalElements_mixed();

        One one1 = get(One.class, 0);
        assertEquals("one1(1,1)", one1.toString());

        One one3 = get(One.class, 2);
        assertEquals("one3(1,2)", one3.toString());

        // when
        field.of(One.class).removeIn(Arrays.asList(one1, one3));

        // then
        assert_mixed_without_any_one();
    }

    @Test
    public void testSameOf_removeIn_mixed_doNotRemove_notExistsCoords() {
        // given
        testAdd_severalElements_mixed();
        Two two = new Two(5, 5);

        // when
        field.of(Two.class).removeIn(Arrays.asList(two));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSameOf_removeIn_mixed_doNotRemove_otherType() {
        // given
        testAdd_severalElements_mixed();
        Four four = new Four(5, 5);

        // when
        field.of(Four.class).removeIn(Arrays.asList(four));

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSameOf_remove_mixed_severalObjects_removeNone() {
        // given
        testAdd_severalElements_mixed();
        assertEquals("[one1(1,1), one2(1,1), one3(1,2)]",
                field.of(One.class).all().toString());

        // when
        field.of(One.class).remove(0, 0);

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSameOf_remove_mixed_severalObjects_removeOnlyOne() {
        // given
        testAdd_severalElements_mixed();
        assertEquals("[one1(1,1), one2(1,1), one3(1,2)]",
                field.of(One.class).all().toString());

        // when
        field.of(One.class).remove(0, 1);

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
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
    public void testSameOf_remove_mixed_severalObjects_removeTwo() {
        // given
        testAdd_severalElements_mixed();
        assertEquals("[one1(1,1), one2(1,1), one3(1,2)]",
                field.of(One.class).all().toString());

        // when
        field.of(One.class).remove(0, 2);

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
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
                "[1,1]:{}\n" +
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
    public void testSameOf_remove_mixed_severalObjects_removeAll() {
        // given
        testAdd_severalElements_mixed();
        assertEquals("[one1(1,1), one2(1,1), one3(1,2)]",
                field.of(One.class).all().toString());

        // when
        field.of(One.class).remove(0, 3);

        // then
        assert_mixed_without_any_one();
    }

    @Test
    public void testSameOf_remove_mixed_oneObject() {
        // given
        testAdd_severalElements_mixed();
        assertEquals("[two4(1,2)]",
                field.of(Two.class).all().toString());

        // when
        field.of(Two.class).remove(0, 1);

        // then
        assert_mixed_without_any_two();
    }

    private void assert_mixed_without_any_two() {
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one1(1,1)\n" +
                "                one2(1,1)\n" +
                "                one3(1,2)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three5(2,2)]}]\n" +
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
                "[2,2]:{\n" +
                "        Three.class=[\n" +
                "                three5(2,2)]}\n" +
                "]", field.toString());
    }

    @Test
    public void testSameOf_remove_mixed_doNotRemove_otherType() {
        // given
        testAdd_severalElements_mixed();

        // when
        field.of(Four.class).remove(0, 1);

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSameOf_remove_mixed_indexOfBound_fromIsLessThanZero() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assertException(() -> field.of(One.class).remove(-1, 1),
                IndexOutOfBoundsException.class);
    }

    @Test
    public void testSameOf_remove_mixed_indexOfBound_toIsMoreThanSize() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assertException(() -> field.of(One.class).remove(0, 1000),
                IndexOutOfBoundsException.class);
    }

    @Test
    public void testSameOf_remove_mixed_indexOfBound_fromIsMoreThanTo() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assertException(() -> field.of(One.class).remove(2, 1),
                IndexOutOfBoundsException.class);
    }
    
    @Test
    public void testSameOf_tick_tickOnlyTickable_ofThisType() {
        // given
        givenTickData();

        // when
        field.of(One.class).tick();

        // then
        assertMessages(
                "one1(1,1)\n" +
                "one2(1,1)\n" +
                "one3(1,2)\n" +
                "one4(2,2)");

        // given
        messages.clear();

        // when
        field.of(Four.class).tick();

        // then
        assertMessages(
                "four13(1,1)\n" +
                "four14(1,1)\n" +
                "four15(1,2)\n" +
                "four16(2,2)");

        // given
        messages.clear();

        // when
        field.of(Two.class).tick();

        // then
        assertMessages("");
    }

    private void givenTickData() {
        field.add(new One(1, 1));
        field.add(new One(1, 1));
        field.add(new One(1, 2));
        field.add(new One(2, 2));

        field.add(new Two(1, 1));
        field.add(new Two(1, 1));
        field.add(new Two(1, 2));
        field.add(new Two(2, 2));

        field.add(new Three(1, 1));
        field.add(new Three(1, 1));
        field.add(new Three(1, 2));
        field.add(new Three(2, 2));

        field.add(new Four(1, 1));
        field.add(new Four(1, 1));
        field.add(new Four(1, 2));
        field.add(new Four(2, 2));
    }

    private void assertMessages(String expected) {
        assertEquals(expected,
                messages.stream()
                    .collect(joining("\n")));
    }

    @Test
    public void testSameOf_tick_ableToChangeCollectionInTick() {
        // given
        givenTickData();

        messages = new LinkedList<>(){
            @Override
            public boolean add(String s) {
                // this can trow ConcurrentModificationException
                field.of(One.class).remove(0, 1);
                return super.add(s);
            }
        };

        // when
        field.of(One.class).tick();

        // then
        assertMessages(
                "one1(1,1)\n" +
                "one2(1,1)\n" +
                "one3(1,2)\n" +
                "one4(2,2)");

        assertEquals("[map={\n" +
                "        Four.class=[\n" +
                "                four13(1,1)\n" +
                "                four14(1,1)\n" +
                "                four15(1,2)\n" +
                "                four16(2,2)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three9(1,1)\n" +
                "                three10(1,1)\n" +
                "                three11(1,2)\n" +
                "                three12(2,2)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two5(1,1)\n" +
                "                two6(1,1)\n" +
                "                two7(1,2)\n" +
                "                two8(2,2)]}]\n" +
                "\n" +
                "[field=[0,0]:{}\n" +
                "[0,1]:{}\n" +
                "[0,2]:{}\n" +
                "[1,0]:{}\n" +
                "[1,1]:{\n" +
                "        Four.class=[\n" +
                "                four13(1,1)\n" +
                "                four14(1,1)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three9(1,1)\n" +
                "                three10(1,1)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two5(1,1)\n" +
                "                two6(1,1)]}\n" +
                "[1,2]:{\n" +
                "        Four.class=[\n" +
                "                four15(1,2)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three11(1,2)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two7(1,2)]}\n" +
                "[2,0]:{}\n" +
                "[2,1]:{}\n" +
                "[2,2]:{\n" +
                "        Four.class=[\n" +
                "                four16(2,2)]}\n" +
                "        {\n" +
                "        Three.class=[\n" +
                "                three12(2,2)]}\n" +
                "        {\n" +
                "        Two.class=[\n" +
                "                two8(2,2)]}\n" +
                "]", field.toString());
    }

    @Test
    public void testSameOf_getAt_mixed() {
        // given
        testAdd_severalElements_mixed();

        // when then
        assertEquals("[one1(1,1), one2(1,1)]",
                field.of(One.class).getAt(pt(1, 1)).toString());

        assertEquals("[one3(1,2)]",
                field.of(One.class).getAt(pt(1, 2)).toString());

        assertEquals("[]",
                field.of(One.class).getAt(pt(2, 2)).toString());

        assertEquals("[]",
                field.of(Two.class).getAt(pt(1, 1)).toString());

        assertEquals("[two4(1,2)]",
                field.of(Two.class).getAt(pt(1, 2)).toString());

        assertEquals("[]",
                field.of(Two.class).getAt(pt(1, 1)).toString());

        assertEquals("[three5(2,2)]",
                field.of(Three.class).getAt(pt(2, 2)).toString());

        assertEquals("[]",
                field.of(Four.class).getAt(pt(1, 1)).toString());

        // then
        assert_severalElements_mixed();
    }

    @Test
    public void testSameOf_getAt_mixed_modifyElement() {
        // given
        testSameOf_getAt_mixed();
        One one = get(One.class, 1, 1);
        assertEquals("one1(1,1)", one.toString());

        // when
        one.move(1, 2);

        // then
        assertEquals("[one2(1,1)]",
                field.of(One.class).getAt(pt(1, 1)).toString());

        assertEquals("[one3(1,2), one1(1,2)]",
                field.of(One.class).getAt(pt(1, 2)).toString());

        assertEquals("[]",
                field.of(One.class).getAt(pt(2, 2)).toString());

        assertEquals("[]",
                field.of(Two.class).getAt(pt(1, 1)).toString());

        assertEquals("[two4(1,2)]",
                field.of(Two.class).getAt(pt(1, 2)).toString());

        assertEquals("[]",
                field.of(Two.class).getAt(pt(1, 1)).toString());

        assertEquals("[three5(2,2)]",
                field.of(Three.class).getAt(pt(2, 2)).toString());

        assertEquals("[]",
                field.of(Four.class).getAt(pt(1, 1)).toString());

        // then
        assertEquals("[map={\n" +
                "        One.class=[\n" +
                "                one2(1,1)\n" +
                "                one3(1,2)\n" +
                "                one1(1,2)]}\n" +
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
                "                one2(1,1)]}\n" +
                "[1,2]:{\n" +
                "        One.class=[\n" +
                "                one3(1,2)\n" +
                "                one1(1,2)]}\n" +
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
}