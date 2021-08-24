package com.codenjoy.dojo.services;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointFieldTest {

    static class One extends PointImpl {
        public One(int x, int y) {
            super(x, y);
        }

        @Override
        public String toString() {
            return String.format("one(%s,%s)",
                    getX(),
                    getY());
        }
    }

    static class Two extends PointImpl {
        public Two(int x, int y) {
            super(x, y);
        }

        @Override
        public String toString() {
            return String.format("two(%s,%s)",
                    getX(),
                    getY());
        }
    }

    static class Three extends PointImpl {
        public Three(int x, int y) {
            super(x, y);
        }

        @Override
        public String toString() {
            return String.format("three(%s,%s)",
                    getX(),
                    getY());
        }
    }

    static class Four extends PointImpl {
        public Four(int x, int y) {
            super(x, y);
        }

        @Override
        public String toString() {
            return String.format("four(%s,%s)",
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
                "                one(1,1)]}]\n" +
                "\n" +
                "[field=[0,0]:null\n" +
                "[0,1]:null\n" +
                "[0,2]:null\n" +
                "[1,0]:null\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one(1,1)]}\n" +
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
                "                one(1,1)\n" +
                "                one(1,2)]}]\n" +
                "\n" +
                "[field=[0,0]:null\n" +
                "[0,1]:null\n" +
                "[0,2]:null\n" +
                "[1,0]:null\n" +
                "[1,1]:{\n" +
                "        One.class=[\n" +
                "                one(1,1)]}\n" +
                "[1,2]:{\n" +
                "        One.class=[\n" +
                "                one(1,2)]}\n" +
                "[2,0]:null\n" +
                "[2,1]:null\n" +
                "[2,2]:null\n" +
                "]", field.toString());
    }

}