package com.codenjoy.dojo.services.multiplayer;

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

import com.codenjoy.dojo.services.settings.SomeRoundSettings;
import org.junit.Before;
import org.junit.Test;

import static com.codenjoy.dojo.client.Utils.split;
import static org.junit.Assert.*;

public class MultiplayerSettingsTest {

    private SomeRoundSettings settings;

    @Before
    public void setup() {
        settings = new SomeRoundSettings();
    }

    @Test
    public void testMultiplayerType_default() {
        // given
        settings.setRoundsEnabled(false);
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "MultipleType{roomSize=2147483647, levelsCount=1, \n" +
                "disposable=false, shouldReloadAlone=false}");
    }

    @Test
    public void testMultiplayerType_training() {
        // given
        settings.mode().select(Mode.TRAINING.value());
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "TrainingType{roomSize=1, levelsCount=13, \n" +
                "disposable=false, shouldReloadAlone=true}");
    }

    @Test
    public void testMultiplayerType_allSingle() {
        // given
        settings.mode().select(Mode.ALL_SINGLE.value());
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "SingleLevelsType{roomSize=1, levelsCount=13, \n" +
                "disposable=true, shouldReloadAlone=true}");
    }

    @Test
    public void testMultiplayerType_allInRooms_unlimitedRoom() {
        // given
        settings.mode().select(Mode.ALL_IN_ROOMS.value());
        settings.setPlayersPerRoom(0);

        // when then
        assertType(13, "MultipleLevelsType{roomSize=2147483647, levelsCount=13, \n" +
                "disposable=false, shouldReloadAlone=false}");
    }

    @Test
    public void testMultiplayerType_allInRooms_normalRoom() {
        // given
        settings.mode().select(Mode.ALL_IN_ROOMS.value());
        settings.setPlayersPerRoom(15);

        // when then
        MultiplayerType type = assertType(13, "MultipleLevelsType{roomSize=15, levelsCount=13, \n" +
                "disposable=false, shouldReloadAlone=false}");
        assertEquals(15, type.getRoomSize(new LevelProgress()));
    }

    @Test
    public void testMultiplayerType_trainingFinalInRooms_unlimitedRoom() {
        // given
        settings.mode().select(Mode.TRAINING_FINAL_IN_ROOMS.value());
        settings.setPlayersPerRoom(0); // ignored

        // when then
        MultiplayerType type = assertType(13, "MultipleLevelsMultiroomType{roomSize=1, levelsCount=13, \n" +
                "disposable=false, shouldReloadAlone=false}");
        assertEquals(2147483647, type.getRoomSize(new LevelProgress()));
    }

    @Test
    public void testMultiplayerType_trainingFinalInRooms_normalRoom() {
        // given
        settings.mode().select(Mode.TRAINING_FINAL_IN_ROOMS.value());
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "MultipleLevelsMultiroomType{roomSize=1, levelsCount=13, \n" +
                "disposable=false, shouldReloadAlone=false}");
    }

    @Test
    public void testMultiplayerType_single() {
        // given
        settings.mode().select(Mode.SINGLE.value());
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "SingleType{roomSize=1, levelsCount=1, \n" +
                "disposable=true, shouldReloadAlone=true}");
    }

    private MultiplayerType assertType(int levelsCount, String expected) {
        MultiplayerType type = settings.multiplayerType(levelsCount);
        assertEquals(expected, split(type, ", \ndisposable="));
        return type;
    }

    @Test
    public void testMultiplayerType_multiple_allInOne() {
        // given
        settings.mode().select(Mode.MULTIPLE.value());
        settings.setRoundsEnabled(false);
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "MultipleType{roomSize=2147483647, levelsCount=1, \n" +
                "disposable=false, shouldReloadAlone=false}");
    }

    @Test
    public void testMultiplayerType_multiple_allInRooms() {
        // given
        settings.mode().select(Mode.MULTIPLE.value());
        settings.setRoundsEnabled(true);
        settings.setPlayersPerRoom(15);

        // when then
        assertType(13, "TeamType{roomSize=15, levelsCount=1, \n" +
                "disposable=true, shouldReloadAlone=true}");
    }

    @Test
    public void testMultiplayerType_tournament() {
        // given
        settings.mode().select(Mode.TOURNAMENT.value());
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "TournamentType{roomSize=2, levelsCount=1, \n" +
                "disposable=true, shouldReloadAlone=true}");
    }

    @Test
    public void testMultiplayerType_triple() {
        // given
        settings.mode().select(Mode.TRIPLE.value());
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "TripleType{roomSize=3, levelsCount=1, \n" +
                "disposable=true, shouldReloadAlone=true}");
    }

    @Test
    public void testMultiplayerType_quadro(){
        // given
        settings.mode().select(Mode.QUADRO.value());
        settings.setPlayersPerRoom(15); // ignored

        // when then
        assertType(13, "QuadroType{roomSize=4, levelsCount=1, \n" +
                "disposable=true, shouldReloadAlone=true}");
    }
}
