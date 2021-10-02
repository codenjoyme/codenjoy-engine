package com.codenjoy.dojo.services.settings;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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

import com.codenjoy.dojo.services.level.LevelsSettingsImpl;
import com.codenjoy.dojo.utils.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static com.codenjoy.dojo.utils.TestUtils.split;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SettingsReaderTest {

    @Test
    public void shouldAddObject_fail_caseNull() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when then
        try {
            settings.add(() -> "newKey", (Object) null);
            fail("Expected exception");
        } catch (Exception exception) {
            assertEquals("java.lang.IllegalArgumentException: Type is not recognized: null",
                    exception.toString());
        }

        // then
        assertEquals("Some[Parameter 1=15, \n" +
                        "Parameter 2=true, \n" +
                        "Parameter 3=0.5, \n" +
                        "Parameter 4=string]",
                split(settings, ", \n"));
    }

    @Test
    public void shouldAddObject_fail_caseUnsuppertedType() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when then
        try {
            settings.add(() -> "newKey", (Object) Long.valueOf(1));
            fail("Expected exception");
        } catch (Exception exception) {
            assertEquals("java.lang.IllegalArgumentException: Type is not supported: class java.lang.Long",
                    exception.toString());
        }

        // then
        assertEquals("Some[Parameter 1=15, \n" +
                        "Parameter 2=true, \n" +
                        "Parameter 3=0.5, \n" +
                        "Parameter 4=string]",
                split(settings, ", \n"));
    }


    @Test
    public void shouldAddObject_success_caseInteger() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when then
        assertEquals("[newKey:Integer = multiline[false] def[24] val[null]]",
                settings.add(() -> "newKey", (Object) Integer.valueOf(24)).toString());

        // then
        assertEquals("Some[Parameter 1=15, \n" +
                        "Parameter 2=true, \n" +
                        "Parameter 3=0.5, \n" +
                        "Parameter 4=string, \n" +
                        "newKey=24]",
                split(settings, ", \n"));
    }

    @Test
    public void shouldAddObject_success_caseBoolean() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when then
        assertEquals("[newKey:Boolean = def[true] val[null]]",
                settings.add(() -> "newKey", (Object) Boolean.valueOf(true)).toString());

        // then
        assertEquals("Some[Parameter 1=15, \n" +
                        "Parameter 2=true, \n" +
                        "Parameter 3=0.5, \n" +
                        "Parameter 4=string, \n" +
                        "newKey=true]",
                split(settings, ", \n"));
    }

    @Test
    public void shouldAddObject_success_caseDouble() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when then
        assertEquals("[newKey:Double = multiline[false] def[0.24] val[null]]",
                settings.add(() -> "newKey", (Object) Double.valueOf(0.24)).toString());

        // then
        assertEquals("Some[Parameter 1=15, \n" +
                        "Parameter 2=true, \n" +
                        "Parameter 3=0.5, \n" +
                        "Parameter 4=string, \n" +
                        "newKey=0.24]",
                split(settings, ", \n"));
    }

    @Test
    public void shouldAddObject_success_caseString() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when then
        assertEquals("[newKey:String = multiline[false] def[someString] val[null]]",
                settings.add(() -> "newKey", (Object) "someString").toString());
        // then
        assertEquals("Some[Parameter 1=15, \n" +
                        "Parameter 2=true, \n" +
                        "Parameter 3=0.5, \n" +
                        "Parameter 4=string, \n" +
                        "newKey=someString]",
                split(settings, ", \n"));
    }

    @Test
    public void shouldConvertToJson() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when then
        assertEquals("{\n" +
                        "  'PARAMETER1':15,\n" +
                        "  'PARAMETER2':true,\n" +
                        "  'PARAMETER3':0.5,\n" +
                        "  'PARAMETER4':'string'\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldConvertToJson_caseRound() {
        // given
        SettingsReader settings = new SomeRoundSettings();

        // when then
        assertEquals("{\n" +
                        "  'PARAMETER1':15,\n" +
                        "  'PARAMETER2':true,\n" +
                        "  'PARAMETER3':0.5,\n" +
                        "  'PARAMETER4':'string',\n" +
                        "  'ROUNDS_ENABLED':true,\n" +
                        "  'ROUNDS_MIN_TICKS_FOR_WIN':1,\n" +
                        "  'ROUNDS_PER_MATCH':1,\n" +
                        "  'ROUNDS_PLAYERS_PER_ROOM':5,\n" +
                        "  'ROUNDS_TEAMS_PER_ROOM':1,\n" +
                        "  'ROUNDS_TIME':200,\n" +
                        "  'ROUNDS_TIME_BEFORE_START':5,\n" +
                        "  'ROUNDS_TIME_FOR_WINNER':1\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldConvertToJson_caseSemifinal() {
        // given
        SettingsReader settings = new SomeSemifinalSettings();

        // when then
        assertEquals("{\n" +
                        "  'PARAMETER1':15,\n" +
                        "  'PARAMETER2':true,\n" +
                        "  'PARAMETER3':0.5,\n" +
                        "  'PARAMETER4':'string',\n" +
                        "  'SEMIFINAL_CLEAR_SCORES':false,\n" +
                        "  'SEMIFINAL_ENABLED':false,\n" +
                        "  'SEMIFINAL_LIMIT':50,\n" +
                        "  'SEMIFINAL_PERCENTAGE':true,\n" +
                        "  'SEMIFINAL_RESET_BOARD':true,\n" +
                        "  'SEMIFINAL_SHUFFLE_BOARD':true,\n" +
                        "  'SEMIFINAL_TIMEOUT':900\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldConvertToJson_caseSemifinalRound() {
        // given
        SettingsReader settings = new SomeSemifinalRoundSettings();

        // when then
        assertEquals("{\n" +
                        "  'PARAMETER1':15,\n" +
                        "  'PARAMETER2':true,\n" +
                        "  'PARAMETER3':0.5,\n" +
                        "  'PARAMETER4':'string',\n" +
                        "  'ROUNDS_ENABLED':true,\n" +
                        "  'ROUNDS_MIN_TICKS_FOR_WIN':1,\n" +
                        "  'ROUNDS_PER_MATCH':1,\n" +
                        "  'ROUNDS_PLAYERS_PER_ROOM':5,\n" +
                        "  'ROUNDS_TEAMS_PER_ROOM':1,\n" +
                        "  'ROUNDS_TIME':200,\n" +
                        "  'ROUNDS_TIME_BEFORE_START':5,\n" +
                        "  'ROUNDS_TIME_FOR_WINNER':1,\n" +
                        "  'SEMIFINAL_CLEAR_SCORES':false,\n" +
                        "  'SEMIFINAL_ENABLED':false,\n" +
                        "  'SEMIFINAL_LIMIT':50,\n" +
                        "  'SEMIFINAL_PERCENTAGE':true,\n" +
                        "  'SEMIFINAL_RESET_BOARD':true,\n" +
                        "  'SEMIFINAL_SHUFFLE_BOARD':true,\n" +
                        "  'SEMIFINAL_TIMEOUT':900\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_whenNameNotFound() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when
        try {
            settings.update(new JSONObject("{\n" +
                    "  'NON_EXISTS':1,\n" +
                    "}"));
            fail("Expected exception");
        } catch (Exception e) {
            assertEquals("Parameter not found: NON_EXISTS", e.getMessage());
        }
    }

    @Test
    public void shouldParseFromJson() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when
        settings.update(new JSONObject("{\n" +
                "  'PARAMETER4':'updated',\n" +
                "  'PARAMETER2':false,\n" +
                "  'PARAMETER1':23,\n" +
                "  'PARAMETER3':0.1\n" +
                "}"));

        // then
        assertEquals("{\n" +
                        "  'PARAMETER1':23,\n" +
                        "  'PARAMETER2':false,\n" +
                        "  'PARAMETER3':0.1,\n" +
                        "  'PARAMETER4':'updated'\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_updateNotExists_butValidParameters() {
        // given
        SettingsReader settings = new SomeGameSettings();

        // when
        settings.update(new JSONObject("{\n" +
                "  'PARAMETER4':'updated',\n" +
                "  'PARAMETER2':false,\n" +
                "  'PARAMETER1':23,\n" +
                "  'PARAMETER3':0.1,\n" +
                "  'ROUNDS_ENABLED':true,\n" +          // not exists but valid Round settings
                "  'ROUNDS_MIN_TICKS_FOR_WIN':4,\n" +   // not exists but valid Round settings
                "  'ROUNDS_TIME_FOR_WINNER':10\n" +     // not exists but valid Round settings
                "}"));

        // then
        assertEquals("{\n" +
                        "  'PARAMETER1':23,\n" +
                        "  'PARAMETER2':false,\n" +
                        "  'PARAMETER3':0.1,\n" +
                        "  'PARAMETER4':'updated',\n" +
                        "  'ROUNDS_ENABLED':true,\n" +
                        "  'ROUNDS_MIN_TICKS_FOR_WIN':4,\n" +
                        "  'ROUNDS_TIME_FOR_WINNER':10\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_caseRoundSettings() {
        // given
        SettingsReader settings = new SomeRoundSettings();

        // when
        settings.update(new JSONObject("{\n" +
                "  'PARAMETER4':'updated',\n" +
                "  'PARAMETER2':false,\n" +
                "  'PARAMETER1':23,\n" +
                "  'PARAMETER3':0.1,\n" +
                "  'ROUNDS_MIN_TICKS_FOR_WIN':4,\n" +
                "  'ROUNDS_TIME_FOR_WINNER':10\n" +
                "}"));

        // then
        assertEquals("{\n" +
                        "  'PARAMETER1':23,\n" +
                        "  'PARAMETER2':false,\n" +
                        "  'PARAMETER3':0.1,\n" +
                        "  'PARAMETER4':'updated',\n" +
                        "  'ROUNDS_ENABLED':true,\n" +
                        "  'ROUNDS_MIN_TICKS_FOR_WIN':4,\n" +
                        "  'ROUNDS_PER_MATCH':1,\n" +
                        "  'ROUNDS_PLAYERS_PER_ROOM':5,\n" +
                        "  'ROUNDS_TEAMS_PER_ROOM':1,\n" +
                        "  'ROUNDS_TIME':200,\n" +
                        "  'ROUNDS_TIME_BEFORE_START':5,\n" +
                        "  'ROUNDS_TIME_FOR_WINNER':10\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_caseSemifinalSettings() {
        // given
        SettingsReader settings = new SomeSemifinalSettings();

        // when
        settings.update(new JSONObject("{\n" +
                "  'PARAMETER4':'updated',\n" +
                "  'PARAMETER2':false,\n" +
                "  'PARAMETER1':23,\n" +
                "  'PARAMETER3':0.1,\n" +
                "  'SEMIFINAL_ENABLED':true,\n" +
                "  'SEMIFINAL_LIMIT':10\n" +
                "}"));

        // then
        assertEquals("{\n" +
                        "  'PARAMETER1':23,\n" +
                        "  'PARAMETER2':false,\n" +
                        "  'PARAMETER3':0.1,\n" +
                        "  'PARAMETER4':'updated',\n" +
                        "  'SEMIFINAL_CLEAR_SCORES':false,\n" +
                        "  'SEMIFINAL_ENABLED':true,\n" +
                        "  'SEMIFINAL_LIMIT':10,\n" +
                        "  'SEMIFINAL_PERCENTAGE':true,\n" +
                        "  'SEMIFINAL_RESET_BOARD':true,\n" +
                        "  'SEMIFINAL_SHUFFLE_BOARD':true,\n" +
                        "  'SEMIFINAL_TIMEOUT':900\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_caseSemifinalRoundSettings() {
        // given
        SettingsReader settings = new SomeSemifinalRoundSettings();

        // when
        settings.update(new JSONObject("{\n" +
                "  'PARAMETER4':'updated',\n" +
                "  'PARAMETER2':false,\n" +
                "  'PARAMETER1':23,\n" +
                "  'PARAMETER3':0.1,\n" +
                "  'ROUNDS_MIN_TICKS_FOR_WIN':4,\n" +
                "  'ROUNDS_TIME_FOR_WINNER':10,\n" +
                "  'SEMIFINAL_ENABLED':true,\n" +
                "  'SEMIFINAL_LIMIT':10\n" +
                "}"));

        // then
        assertEquals("{\n" +
                        "  'PARAMETER1':23,\n" +
                        "  'PARAMETER2':false,\n" +
                        "  'PARAMETER3':0.1,\n" +
                        "  'PARAMETER4':'updated',\n" +
                        "  'ROUNDS_ENABLED':true,\n" +
                        "  'ROUNDS_MIN_TICKS_FOR_WIN':4,\n" +
                        "  'ROUNDS_PER_MATCH':1,\n" +
                        "  'ROUNDS_PLAYERS_PER_ROOM':5,\n" +
                        "  'ROUNDS_TEAMS_PER_ROOM':1,\n" +
                        "  'ROUNDS_TIME':200,\n" +
                        "  'ROUNDS_TIME_BEFORE_START':5,\n" +
                        "  'ROUNDS_TIME_FOR_WINNER':10,\n" +
                        "  'SEMIFINAL_CLEAR_SCORES':false,\n" +
                        "  'SEMIFINAL_ENABLED':true,\n" +
                        "  'SEMIFINAL_LIMIT':10,\n" +
                        "  'SEMIFINAL_PERCENTAGE':true,\n" +
                        "  'SEMIFINAL_RESET_BOARD':true,\n" +
                        "  'SEMIFINAL_SHUFFLE_BOARD':true,\n" +
                        "  'SEMIFINAL_TIMEOUT':900\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_caseLevelsSettings_usedClassImpl() {
        // given
        SettingsReader settings = new LevelsSettingsImpl();

        // when
        settings.update(new JSONObject("{\n" +
                "  'LEVELS_MAP_2':'updatedMap4',\n" +   // will update PARAMETER4 because of synonym
                "  'LEVELS_MAP_1_2':'updatedMap2',\n" + // will update PARAMETER2 because of synonym
                "  'LEVELS_MAP_1_1':'updatedMap1',\n" + // will update PARAMETER1 because of synonym
                "  'LEVELS_MAP_1_3':'updatedMap3',\n" + // will update PARAMETER3 because of synonym
                "}"));

        // then
        assertEquals("{\n" +
                        "  'LEVELS_MAP_1_1':'updatedMap1',\n" +
                        "  'LEVELS_MAP_1_2':'updatedMap2',\n" +
                        "  'LEVELS_MAP_1_3':'updatedMap3',\n" +
                        "  'LEVELS_MAP_2':'updatedMap4'\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_caseLevelsSettings_namesAreEqualsToParameters() {
        // given
        SettingsReader settings = new SomeLevelsSettings();

        // when
        settings.update(new JSONObject("{\n" +
                "  'PARAMETER4':'updatedMap4',\n" +
                "  'PARAMETER2':'updatedMap2',\n" +
                "  'PARAMETER1':'updatedMap1',\n" +
                "  'PARAMETER3':'updatedMap3',\n" +
                "}"));

        // then
        assertEquals("{\n" +
                        "  'PARAMETER1':'updatedMap1',\n" +
                        "  'PARAMETER2':'updatedMap2',\n" +
                        "  'PARAMETER3':'updatedMap3',\n" +
                        "  'PARAMETER4':'updatedMap4',\n" +
                        "  'PARAMETER5':'map5',\n" +
                        "  'PARAMETER6':'map6',\n" +
                        "  'PARAMETER7':'map7',\n" +
                        "  'PARAMETER8':'map8'\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_caseLevelsSettings_namesAreNotEqualsToParameters_butSynonyms() {
        // given
        SettingsReader settings = new SomeLevelsSettings();

        // when
        settings.update(new JSONObject("{\n" +
                "  'LEVELS_MAP_2':'updatedMap4',\n" +   // will update PARAMETER4 because of synonym
                "  'LEVELS_MAP_1_2':'updatedMap2',\n" + // will update PARAMETER2 because of synonym
                "  'LEVELS_MAP_1_1':'updatedMap1',\n" + // will update PARAMETER1 because of synonym
                "  'LEVELS_MAP_1_3':'updatedMap3',\n" + // will update PARAMETER3 because of synonym
                "}"));

        // then
        assertEquals("{\n" +
                        "  'PARAMETER1':'updatedMap1',\n" +
                        "  'PARAMETER2':'updatedMap2',\n" +
                        "  'PARAMETER3':'updatedMap3',\n" +
                        "  'PARAMETER4':'updatedMap4',\n" +
                        "  'PARAMETER5':'map5',\n" +
                        "  'PARAMETER6':'map6',\n" +
                        "  'PARAMETER7':'map7',\n" +
                        "  'PARAMETER8':'map8'\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

    @Test
    public void shouldParseFromJson_caseLevelsSettings_namesAreNotEqualsToParameters_andNotSynonyms() {
        // given
        SettingsReader settings = new SomeLevelsSettings();

        // when
        settings.update(new JSONObject("{\n" +
                "  'LEVELS_MAP_1_4':'newMap9',\n" +
                "  'LEVELS_MAP_3_3':'newMap10',\n" +
                "  'LEVELS_MAP_4_2':'newMap11',\n" +
                "  'LEVELS_MAP_6':'newMap12'\n" +
                "}"));

        // then
        assertEquals("{\n" +
                        "  'LEVELS_MAP_1_4':'newMap9',\n" +
                        "  'LEVELS_MAP_3_3':'newMap10',\n" +
                        "  'LEVELS_MAP_4_2':'newMap11',\n" +
                        "  'LEVELS_MAP_6':'newMap12',\n" +
                        "  'PARAMETER1':'map1',\n" +
                        "  'PARAMETER2':'map2',\n" +
                        "  'PARAMETER3':'map3',\n" +
                        "  'PARAMETER4':'map4',\n" +
                        "  'PARAMETER5':'map5',\n" +
                        "  'PARAMETER6':'map6',\n" +
                        "  'PARAMETER7':'map7',\n" +
                        "  'PARAMETER8':'map8'\n" +
                        "}",
                JsonUtils.prettyPrint(settings.asJson()));
    }

}
