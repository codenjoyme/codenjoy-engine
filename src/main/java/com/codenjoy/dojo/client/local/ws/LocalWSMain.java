package com.codenjoy.dojo.client.local.ws;

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

import com.codenjoy.dojo.client.local.DiceGenerator;
import com.codenjoy.dojo.services.AbstractGameType;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.dice.RandomDice;
import com.codenjoy.dojo.services.round.RoundSettings;
import com.codenjoy.dojo.services.settings.SettingsReader;
import com.codenjoy.dojo.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.codenjoy.dojo.services.round.RoundSettings.Keys.ROUNDS_ENABLED;

public class LocalWSMain {

    private LocalWSGameServer server;

    public void run(String[] args, Supplier<SettingsReader> gameSettingsSupplier, BiFunction<Dice, SettingsReader, AbstractGameType> gameTypeFunction) {
        server = new LocalWSGameServer();

        server.print("Please run this stuff with VM options:\n" +
                "\t\t-Dsettings={'ROUNDS_ENABLED':false, ...}\n" +
                "\t\t-Drandom=SEED_STRING\n");

        String settingsString = System.getProperty("settings", "{}");
        String randomSeed = System.getProperty("random", null);

        Dice dice = getDice(randomSeed);

        JSONObject settings = new JSONObject(settingsString);

        SettingsReader gameSettings = gameSettingsSupplier.get()
                .update(settings);

        if (!contains(settings, ROUNDS_ENABLED)) {
            String json = "{\n" +
                    "  'ROUNDS_ENABLED':false\n" +
                    "}\n";
            server.print("Simple mode! Hardcoded: \n" + json);
            gameSettings.update(new JSONObject(json));
        }

        server.print("Current settings:\n" +
                JsonUtils.prettyPrint(gameSettings.asJson()));

        AbstractGameType gameType = gameTypeFunction.apply(dice, gameSettings);
        server.run(gameType);
    }

    private boolean contains(JSONObject settings, SettingsReader.Key key) {
        return settings.has(SettingsReader.keyToName(RoundSettings.allRoundsKeys(), key.key()));
    }

    private Dice getDice(String randomSeed) {
        if (StringUtils.isEmpty(randomSeed)) {
            return new RandomDice();
        } else {
            DiceGenerator dice = new DiceGenerator(server.settings().out());
            dice.printDice(false);
            dice.printConversions(false);
            return dice.getDice(randomSeed, 100, 10000);
        }
    }

}
