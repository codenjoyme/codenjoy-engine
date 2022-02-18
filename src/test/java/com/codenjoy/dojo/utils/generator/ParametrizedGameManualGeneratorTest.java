package com.codenjoy.dojo.utils.generator;

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

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParametrizedGameManualGeneratorTest extends AbstractManualGeneratorTest {

    private String language;
    private String game;
    private String manualType;

    public ParametrizedGameManualGeneratorTest(String game, String manualType, String language) {
        this.language = language;
        this.game = game;
        this.manualType = manualType;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"mollymage", "codenjoy", "en"},
                {"mollymage", "codenjoy", "ru"},
                {"clifford", "codenjoy", "en"},
                {"clifford", "codenjoy", "ru"},
        });
    }

    @Before
    public void setUp() {
        base = StringUtils.EMPTY;
        actual = StringUtils.EMPTY;
        expected = StringUtils.EMPTY;
    }

    private void prepareCorrectSources() {
        base = new File("target/test-classes/generator/correct").getAbsolutePath() + "/";
        actual = base + RELATIVE_PATH_TO_GAME_SOURCES.replace("{$game}", game) + manualType + "-" + language + ".md";
        expected = new File("src/test/resources/generator/correct/games/" + game).getAbsolutePath() + "/expected-" + language + ".md";

        generator = getGenerator(game, language, manualType);

        delete(actual);
        logState();
    }

    @Test
    @Ignore
    @SneakyThrows
    public void shouldGenerate_ENManual() {
        // when all source files present
        prepareCorrectSources();
        //then should generate target file correct
        generator.generate();
        Assert.assertEquals("The files differ!",
                load(expected), load(actual));
    }
}
