package com.codenjoy.dojo.client.generator.language;

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

import com.codenjoy.dojo.client.generator.Template;

public class Php implements Template {

    @Override
    public String header() {
        return "<?php\n" +
                "\n" +
                "namespace %S;\n" +
                "\n" +
                "abstract class Element\n" +
                "{\n" +
                "    static array $elements = array(\n";
    }

    @Override
    public String line() {
        return "        \"%s\" => '%s',\n";
    }

    @Override
    public String comment() {
        return "# ";
    }

    @Override
    public String footer() {
        return "    );\n" +
                "}\n";
    }
}
