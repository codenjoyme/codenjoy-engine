package com.codenjoy.dojo.services.multiplayer;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2021 Codenjoy
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

import com.codenjoy.dojo.services.round.RoundSettings;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.SelectBox;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.services.multiplayer.MultiplayerSettings.Keys.GAME_MODE;
import static com.codenjoy.dojo.services.multiplayer.MultiplayerSettings.Keys.ROOM_SIZE;
import static com.codenjoy.dojo.services.multiplayer.MultiplayerType.*;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_ENABLED;
import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_PLAYERS_PER_ROOM;

public interface MultiplayerSettings<T extends SettingsReader> extends SettingsReader<T> {

    String MULTIPLAYER = "[Multiplayer]";

    public enum Keys implements Key {

        GAME_MODE(MULTIPLAYER + " Mode"),
        ROOM_SIZE(MULTIPLAYER + " Room size");

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    static List<SettingsReader.Key> allMultiplayerKeys() {
        return Arrays.asList(Keys.values());
    }

    default void initMultiplayer() {
        initMultiplayer(playersPerRooms(), Mode.keys());
    }

    private Integer playersPerRooms() {
        return integer(ROUNDS_PLAYERS_PER_ROOM);
    }

    // TODO убрать после того как icancode не будет как все
    default void initMultiplayer(int roomSize, List<String> options) {
        options(GAME_MODE, options, options.get(0));

        // TODO два связанных параметра, надо как-то развязать их
        Parameter<Integer> parameter = add(ROOM_SIZE, roomSize);
        if (RoundSettings.is((Settings) this)) {
            Parameter<Integer> playersPerRoom = RoundSettings.get((Settings) this).playersPerRoom();

            parameter.onChange((old, updated) -> playersPerRoom.justSet(updated));
            playersPerRoom.onChange((old, updated) -> parameter.justSet(updated));
        }
    }

    default int roomSize() {
        return integer(ROOM_SIZE) == 0
                ? Integer.MAX_VALUE
                : integer(ROOM_SIZE);
    }

    // метод для получения enum Mode из настроек
    default Mode modeValue() {
        if (!hasParameter(GAME_MODE.key())) {
            return Mode.SINGLE;
        }
        return Mode.values()[mode().index()];
    }

    // метод для получения parameter Mode из настроек
    default SelectBox mode() {
        return parameter(GAME_MODE, SelectBox.class);
    }

    default MultiplayerType multiplayerType(int levelsCount) {
        switch (modeValue()) {
            case TRAINING:
                return TRAINING.apply(levelsCount);

            case ALL_SINGLE:
                return ALL_SINGLE.apply(levelsCount);

            case ALL_IN_ROOMS:
                return ALL_IN_ROOMS.apply(roomSize(), levelsCount);

            case TRAINING_FINAL_IN_ROOMS:
                return TRAINING_FINAL_IN_ROOMS.apply(roomSize(), levelsCount);

            default:
            case SINGLE:
                return SINGLE;

            case TOURNAMENT:
                return TOURNAMENT;

            case TRIPLE:
                return TRIPLE;

            case QUADRO:
                return QUADRO;

            case MULTIPLE:
                return bool(ROUNDS_ENABLED)
                    ? TEAM.apply(roomSize(), DISPOSABLE)
                    : MULTIPLE;
        }
    }
}