package com.codenjoy.dojo.services.generator.manual;

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

import java.util.Arrays;
import java.util.List;

public class CodenjoyManual extends GameManualGenerator {

    private static final String CODENJOY_MANUAL_MD = "codenjoy-manual";
    private static final String GLOBAL_SOURCES = "engine/src/main/resources/manuals/";
    private static final String GAME_SOURCES = "{$game}/src/main/webapp/resources/{$game}/help/";

    public CodenjoyManual(String game, String language, String basePath) {
        this(game, language, basePath, GLOBAL_SOURCES, GAME_SOURCES);
    }

    public CodenjoyManual(String game, String language, String basePath, String globalSources, String gameSources) {
        super(game, language, basePath, globalSources, gameSources);
    }

    @Override
    protected List<String> getManualParts() {
        return Arrays.asList("header.md",
                "codenjoy_intro.md",
                "game-about.md",
                "codenjoy_how-connect.md",
                "message-format.md",
                "field.md",
                "codenjoy-board-link.md",
                "elements.md",
                "what-to-do.md",
                "ask-sensei.md",
                "client-and-api.md",
                "faq.md",
                "how-to-host.md"
                );
    }

    @Override
    protected String getManualType() {
        return CODENJOY_MANUAL_MD;
    }
}
