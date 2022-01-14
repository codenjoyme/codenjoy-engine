package com.codenjoy.dojo.services.level;

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

import com.codenjoy.dojo.services.dice.MockDice;
import com.codenjoy.dojo.services.settings.*;
import org.junit.Test;

import static com.codenjoy.dojo.client.Utils.split;
import static org.junit.Assert.assertEquals;

public class LevelsSettingsTest {
    
    @Test
    public void testIs_caseNull() {
        assertEquals(false, LevelsSettings.is(null));
    }

    @Test
    public void testIs_caseNotLevelsInstance() {
        assertEquals(false, LevelsSettings.is(new SettingsImpl()));
    }

    @Test
    public void testIs_caseLevelsInstance() {
        assertEquals(true, LevelsSettings.is(new LevelsSettingsImpl()));
    }

    @Test
    public void testIs_caseAllPropertiesCopied() {
        // given
        SettingsImpl settings = givenAllPropertiesCopied();

        // when then
        assertEquals(true, LevelsSettings.is(settings));
    }

    @Test
    public void testIs_caseNotAllPropertiesCopied() {
        // given
        SettingsImpl settings = givenAllPropertiesCopied();

        settings.removeParameter(settings.getParameters().iterator().next().getName());

        // when then
        assertEquals(true, LevelsSettings.is(settings));
    }

    @Test
    public void testGet_caseAllPropertiesCopied() {
        // given 
        SettingsImpl settings = givenAllPropertiesCopied();

        // when then
        assertEquals("SettingsImpl(map={[Level] Map[1,1]=[[Level] Map[1,1]:String = multiline[false] def[map1] val[map1]], \n" +
                        "[Level] Map[1,2]=[[Level] Map[1,2]:String = multiline[false] def[map2] val[map2]], \n" +
                        "[Level] Map[1,3]=[[Level] Map[1,3]:String = multiline[false] def[map3] val[map3]], \n" +
                        "[Level] Map[2]=[[Level] Map[2]:String = multiline[false] def[map4] val[map4]], \n" +
                        "[Level] Map[3,1]=[[Level] Map[3,1]:String = multiline[false] def[map5] val[map5]], \n" +
                        "[Level] Map[3,2]=[[Level] Map[3,2]:String = multiline[false] def[map6] val[map6]], \n" +
                        "[Level] Map[4,1]=[[Level] Map[4,1]:String = multiline[false] def[map7] val[map7]], \n" +
                        "[Level] Map[5]=[[Level] Map[5]:String = multiline[false] def[map8] val[map8]]})",
                split(LevelsSettings.get(settings), "], \n["));
    }

    @Test
    public void testCopyFrom_updateInBothPlace() {
        // given
        SomeLevelsSettings source = new SomeLevelsSettings();
        
        assertEquals("Some[[Level] Map[1,1]=map1, \n" +
                        "[Level] Map[1,2]=map2, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]",
                split(source, ", \n["));

        // when
        LevelsSettingsImpl wrapper = LevelsSettings.get(source);

        wrapper.setLevelMap(1, 2, "updatedMap");

        // then
        assertEquals("Some[[Level] Map[1,1]=map1, \n" +
                        "[Level] Map[1,2]=updatedMap, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]",
                split(source, ", \n["));
    }

    @Test
    public void testGet_caseOtherPropertiesCopied() {
        // given
        SettingsImpl settings = new SomeInactivitySettings();

        // when then
        assertEquals("SettingsImpl(map={})",
                split(LevelsSettings.get(settings), "], \n["));
    }

    private SettingsImpl givenAllPropertiesCopied() {
        SettingsImpl result = new SettingsImpl();
        result.copyFrom(new SomeLevelsSettings().getLevelsParams());
        return result;
    }

    @Test
    public void testGet_caseLevelsInstance() {
        // given
        SettingsImpl settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                "[Level] Map[1,2]=map2, \n" +
                "[Level] Map[1,3]=map3, \n" +
                "[Level] Map[2]=map4, \n" +
                "[Level] Map[3,1]=map5, \n" +
                "[Level] Map[3,2]=map6, \n" +
                "[Level] Map[4,1]=map7, \n" +
                "[Level] Map[5]=map8]");

        // when then
        assertAll(LevelsSettings.get(settings),
                "SettingsImpl(map={[Level] Map[1,1]=[[Level] Map[1,1]:String = multiline[false] def[map1] val[map1]], \n" +
                "[Level] Map[1,2]=[[Level] Map[1,2]:String = multiline[false] def[map2] val[map2]], \n" +
                "[Level] Map[1,3]=[[Level] Map[1,3]:String = multiline[false] def[map3] val[map3]], \n" +
                "[Level] Map[2]=[[Level] Map[2]:String = multiline[false] def[map4] val[map4]], \n" +
                "[Level] Map[3,1]=[[Level] Map[3,1]:String = multiline[false] def[map5] val[map5]], \n" +
                "[Level] Map[3,2]=[[Level] Map[3,2]:String = multiline[false] def[map6] val[map6]], \n" +
                "[Level] Map[4,1]=[[Level] Map[4,1]:String = multiline[false] def[map7] val[map7]], \n" +
                "[Level] Map[5]=[[Level] Map[5]:String = multiline[false] def[map8] val[map8]]})");
    }

    private void assertAll(Settings settings, String expected) {
        assertEquals(expected,
                split(settings, ", \n"));
    }

    @Test
    public void testGet_caseNotLevelsInstance() {
        // given
        SettingsImpl settings = new SomeRoundSettings();

        // when then
        assertEquals("SettingsImpl(map={})",
                LevelsSettings.get(settings).toString());
    }

    @Test
    public void testGet_caseNull() {
        // given
        SettingsImpl settings = null;

        // when then
        assertEquals("SettingsImpl(map={})",
                LevelsSettings.get(settings).toString());
    }

    @Test
    public void testGetLevelMapsCount() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                    "[Level] Map[1,2]=map2, \n" +
                    "[Level] Map[1,3]=map3, \n" +
                    "[Level] Map[2]=map4, \n" +
                    "[Level] Map[3,1]=map5, \n" +
                    "[Level] Map[3,2]=map6, \n" +
                    "[Level] Map[4,1]=map7, \n" +
                    "[Level] Map[5]=map8]");

        // when then
        assertEquals(0, settings.getLevelMapsCount(0));
        assertEquals(3, settings.getLevelMapsCount(1));
        assertEquals(1, settings.getLevelMapsCount(2));
        assertEquals(2, settings.getLevelMapsCount(3));
        assertEquals(1, settings.getLevelMapsCount(4));
        assertEquals(1, settings.getLevelMapsCount(5));
        assertEquals(0, settings.getLevelMapsCount(6));
    }

    @Test
    public void testGetLevelsCount() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                    "[Level] Map[1,2]=map2, \n" +
                    "[Level] Map[1,3]=map3, \n" +
                    "[Level] Map[2]=map4, \n" +
                    "[Level] Map[3,1]=map5, \n" +
                    "[Level] Map[3,2]=map6, \n" +
                    "[Level] Map[4,1]=map7, \n" +
                    "[Level] Map[5]=map8]");

        // when then
        assertEquals(5, settings.getLevelsCount());
    }

    @Test
    public void testGetRandomLevelMap() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                        "[Level] Map[1,2]=map2, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");
        MockDice dice = new MockDice();

        // when then
        dice.then(2);
        assertEquals("map3", settings.getRandomLevelMap(1, dice));

        dice.then(0);
        assertEquals("map5", settings.getRandomLevelMap(3, dice));

        dice.then(max -> {
            throw new RuntimeException();
        });
        assertEquals("map8", settings.getRandomLevelMap(5, dice));

        assertEquals("map7", settings.getRandomLevelMap(4, dice));
    }
    
    @Test
    public void testGetLevelMap() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                    "[Level] Map[1,2]=map2, \n" +
                    "[Level] Map[1,3]=map3, \n" +
                    "[Level] Map[2]=map4, \n" +
                    "[Level] Map[3,1]=map5, \n" +
                    "[Level] Map[3,2]=map6, \n" +
                    "[Level] Map[4,1]=map7, \n" +
                    "[Level] Map[5]=map8]");

        // when then
        assertEquals(null, settings.getLevelMap(0, null));
        assertEquals(null, settings.getLevelMap(0));
        assertEquals(null, settings.getLevelMap(0, 1));
        assertEquals(null, settings.getLevelMap(0, 0));

        assertEquals(null, settings.getLevelMap(1, null));
        assertEquals(null, settings.getLevelMap(1));
        assertEquals(null, settings.getLevelMap(1, 0));
        assertEquals("map1", settings.getLevelMap(1, 1));
        assertEquals("map2", settings.getLevelMap(1, 2));
        assertEquals("map3", settings.getLevelMap(1, 3));
        assertEquals(null, settings.getLevelMap(1, 4));

        assertEquals("map4", settings.getLevelMap(2, null));
        assertEquals("map4", settings.getLevelMap(2));
        assertEquals(null, settings.getLevelMap(2, 0));
        assertEquals(null, settings.getLevelMap(2, 1));
        assertEquals(null, settings.getLevelMap(2, 2));
        assertEquals(null, settings.getLevelMap(2, 3));
        assertEquals(null, settings.getLevelMap(2, 4));

        assertEquals(null, settings.getLevelMap(3, null));
        assertEquals(null, settings.getLevelMap(3));
        assertEquals(null, settings.getLevelMap(3, 0));
        assertEquals("map5", settings.getLevelMap(3, 1));
        assertEquals("map6", settings.getLevelMap(3, 2));
        assertEquals(null, settings.getLevelMap(3, 3));
        assertEquals(null, settings.getLevelMap(3, 4));

        assertEquals(null, settings.getLevelMap(4, null));
        assertEquals(null, settings.getLevelMap(4));
        assertEquals(null, settings.getLevelMap(4, 0));
        assertEquals("map7", settings.getLevelMap(4, 1));
        assertEquals(null, settings.getLevelMap(4, 2));
        assertEquals(null, settings.getLevelMap(4, 3));
        assertEquals(null, settings.getLevelMap(4, 4));

        assertEquals("map8", settings.getLevelMap(5, null));
        assertEquals("map8", settings.getLevelMap(5));
        assertEquals(null, settings.getLevelMap(5, 0));
        assertEquals(null, settings.getLevelMap(5, 1));
        assertEquals(null, settings.getLevelMap(5, 2));
        assertEquals(null, settings.getLevelMap(5, 3));
        assertEquals(null, settings.getLevelMap(5, 4));

        assertEquals(null, settings.getLevelMap(6, null));
        assertEquals(null, settings.getLevelMap(6));
        assertEquals(null, settings.getLevelMap(6, 0));
        assertEquals(null, settings.getLevelMap(6, 1));
        assertEquals(null, settings.getLevelMap(6, 2));
        assertEquals(null, settings.getLevelMap(6, 3));
        assertEquals(null, settings.getLevelMap(6, 4));
    }

    @Test
    public void testSetLevelMap() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                        "[Level] Map[1,2]=map2, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");

        // when
        settings.setLevelMap(1, 1, "updatedMap1");
        settings.setLevelMap(2, null, "updatedMap4");
        settings.setLevelMap(6, 1, "updatedMap9");
        settings.setLevelMap(7, "updatedMap10");

        // then
        assertAll(settings,
                "Some[[Level] Map[1,1]=updatedMap1, \n" +
                        "[Level] Map[1,2]=map2, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=updatedMap4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8, \n" +
                        "[Level] Map[6,1]=updatedMap9, \n" +
                        "[Level] Map[7]=updatedMap10]");
    }

    @Test
    public void testSetLevelMaps() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                        "[Level] Map[1,2]=map2, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");

        // when
        settings.setLevelMaps(1, "updatedMap1", "updatedMap2");

        // then
        assertAll(settings,
                "Some[[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8, \n" +
                        "[Level] Map[1,1]=updatedMap1, \n" +
                        "[Level] Map[1,2]=updatedMap2]");
    }

    @Test
    public void testClearLevelMaps() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                        "[Level] Map[1,2]=map2, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");

        // when
        settings.clearLevelMaps(1);

        // then
        assertAll(settings,
                "Some[[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");

        // when
        settings.clearLevelMaps(3);

        // then
        assertAll(settings,
                "Some[[Level] Map[2]=map4, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");

        // when
        settings.clearLevelMaps(2);

        // then
        assertAll(settings,
                "Some[[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");

        // when
        settings.clearLevelMaps(5);

        // then
        assertAll(settings,
                "Some[[Level] Map[4,1]=map7]");

        // when
        settings.clearLevelMaps(4);

        // then
        assertAll(settings,
                "Some[]");
    }

    @Test
    public void testLevelMapParameter() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                        "[Level] Map[1,2]=map2, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");

        // when then
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(0, null).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(0).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(0, 1).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(0, 0).toString());

        assertEquals("[null:Object = val[null]]",
                settings.levelMap(1, null).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(1).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(1, 0).toString());
        assertEquals("[[Level] Map[1,1]:String = multiline[false] def[map1] val[map1]]",
                settings.levelMap(1, 1).toString());
        assertEquals("[[Level] Map[1,2]:String = multiline[false] def[map2] val[map2]]",
                settings.levelMap(1, 2).toString());
        assertEquals("[[Level] Map[1,3]:String = multiline[false] def[map3] val[map3]]",
                settings.levelMap(1, 3).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(1, 4).toString());

        assertEquals("[[Level] Map[2]:String = multiline[false] def[map4] val[map4]]",
                settings.levelMap(2, null).toString());
        assertEquals("[[Level] Map[2]:String = multiline[false] def[map4] val[map4]]",
                settings.levelMap(2).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(2, 0).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(2, 1).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(2, 2).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(2, 3).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(2, 4).toString());

        assertEquals("[null:Object = val[null]]",
                settings.levelMap(3, null).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(3).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(3, 0).toString());
        assertEquals("[[Level] Map[3,1]:String = multiline[false] def[map5] val[map5]]",
                settings.levelMap(3, 1).toString());
        assertEquals("[[Level] Map[3,2]:String = multiline[false] def[map6] val[map6]]",
                settings.levelMap(3, 2).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(3, 3).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(3, 4).toString());

        assertEquals("[null:Object = val[null]]",
                settings.levelMap(4, null).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(4).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(4, 0).toString());
        assertEquals("[[Level] Map[4,1]:String = multiline[false] def[map7] val[map7]]",
                settings.levelMap(4, 1).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(4, 2).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(4, 3).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(4, 4).toString());

        assertEquals("[[Level] Map[5]:String = multiline[false] def[map8] val[map8]]",
                settings.levelMap(5, null).toString());
        assertEquals("[[Level] Map[5]:String = multiline[false] def[map8] val[map8]]",
                settings.levelMap(5).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(5, 0).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(5, 1).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(5, 2).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(5, 3).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(5, 4).toString());

        assertEquals("[null:Object = val[null]]",
                settings.levelMap(6, null).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(6).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(6, 0).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(6, 1).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(6, 2).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(6, 3).toString());
        assertEquals("[null:Object = val[null]]",
                settings.levelMap(6, 4).toString());
    }

    @Test
    public void testLevelMapParameter_onChangeAffectSettings() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                        "[Level] Map[1,2]=map2, \n" +
                        "[Level] Map[1,3]=map3, \n" +
                        "[Level] Map[2]=map4, \n" +
                        "[Level] Map[3,1]=map5, \n" +
                        "[Level] Map[3,2]=map6, \n" +
                        "[Level] Map[4,1]=map7, \n" +
                        "[Level] Map[5]=map8]");

        // when
        settings.levelMap(1, 2).update("updatedMap2");
        settings.levelMap(4, 1).update("updatedMap7");
        settings.levelMap(5, 5).update("newMap?");

        // then
        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                    "[Level] Map[1,2]=updatedMap2, \n" + // updated
                    "[Level] Map[1,3]=map3, \n" +
                    "[Level] Map[2]=map4, \n" +
                    "[Level] Map[3,1]=map5, \n" +
                    "[Level] Map[3,2]=map6, \n" +
                    "[Level] Map[4,1]=updatedMap7, \n" + // updated
                    "[Level] Map[5]=map8]");
    }

    @Test
    public void testGetParameters_caseWrapper() {
        // given
        LevelsSettings settings = LevelsSettings.get(new SomeLevelsSettings());

        // when then
        assertEquals("[[[Level] Map[1,1]:String = multiline[false] def[map1] val[map1]], \n" +
                        "[[Level] Map[1,2]:String = multiline[false] def[map2] val[map2]], \n" +
                        "[[Level] Map[1,3]:String = multiline[false] def[map3] val[map3]], \n" +
                        "[[Level] Map[2]:String = multiline[false] def[map4] val[map4]], \n" +
                        "[[Level] Map[3,1]:String = multiline[false] def[map5] val[map5]], \n" +
                        "[[Level] Map[3,2]:String = multiline[false] def[map6] val[map6]], \n" +
                        "[[Level] Map[4,1]:String = multiline[false] def[map7] val[map7]], \n" +
                        "[[Level] Map[5]:String = multiline[false] def[map8] val[map8]]]",
                split(settings.getParameters(), ", \n"));
    }

    @Test
    public void testGetParameters_caseCopied() {
        // given
        SettingsImpl source = new SettingsImpl();
        EditBox<String> parameter = source.addEditBox("[Level] Map[1,2]").type(String.class).def("map");
        String expected = "[[Level] Map[1,2]:String = multiline[false] def[map] val[null]]";
        assertEquals(expected, parameter.toString());

        LevelsSettings settings = LevelsSettings.get(source);

        // when then
        assertEquals("[" + expected + "]",
                split(settings.getParameters(), ", \n"));
    }

    @Test
    public void testGetParameter_caseWrapper() {
        // given
        LevelsSettings settings = LevelsSettings.get(new SomeLevelsSettings());

        // when then
        assertEquals("[[Level] Map[1,2]:String = multiline[false] def[map2] val[map2]]",
                settings.getParameter("[Level] Map[1,2]").toString());
    }

    @Test
    public void testGetParameter_caseCopied_withUpdatedAndDefault() {
        // given
        SettingsImpl source = new SettingsImpl();
        EditBox<String> parameter = source.addEditBox("[Level] Map[1,2]").type(String.class).def("map").update("updated");
        String expected = "[[Level] Map[1,2]:String = multiline[false] def[map] val[updated]]";
        assertEquals(expected, parameter.toString());

        LevelsSettings settings = LevelsSettings.get(source);

        // when then
        assertEquals(expected,
                settings.getParameter("[Level] Map[1,2]").toString());
    }

    @Test
    public void testGetParameter_caseCopied_withDefaultOnly() {
        // given
        SettingsImpl source = new SettingsImpl();
        EditBox<String> parameter = source.addEditBox("[Level] Map[1,2]").type(String.class).def("map");
        String expected = "[[Level] Map[1,2]:String = multiline[false] def[map] val[null]]";
        LevelsSettings settings = LevelsSettings.get(source);

        // when then
        assertEquals(expected,
                settings.getParameter("[Level] Map[1,2]").toString());
    }

    @Test
    public void testGetLevelMapsParams() {
        // given
        SomeLevelsSettings settings = new SomeLevelsSettings();

        assertAll(settings,
                "Some[[Level] Map[1,1]=map1, \n" +
                    "[Level] Map[1,2]=map2, \n" +
                    "[Level] Map[1,3]=map3, \n" +
                    "[Level] Map[2]=map4, \n" +
                    "[Level] Map[3,1]=map5, \n" +
                    "[Level] Map[3,2]=map6, \n" +
                    "[Level] Map[4,1]=map7, \n" +
                    "[Level] Map[5]=map8]");

        // when then
        assertEquals("[[[Level] Map[1,1]:String = multiline[false] def[map1] val[map1]], \n" +
                        "[[Level] Map[1,2]:String = multiline[false] def[map2] val[map2]], \n" +
                        "[[Level] Map[1,3]:String = multiline[false] def[map3] val[map3]], \n" +
                        "[[Level] Map[2]:String = multiline[false] def[map4] val[map4]], \n" +
                        "[[Level] Map[3,1]:String = multiline[false] def[map5] val[map5]], \n" +
                        "[[Level] Map[3,2]:String = multiline[false] def[map6] val[map6]], \n" +
                        "[[Level] Map[4,1]:String = multiline[false] def[map7] val[map7]], \n" +
                        "[[Level] Map[5]:String = multiline[false] def[map8] val[map8]]]",
                split(settings.getLevelsParams(), "], \n["));

    }
}