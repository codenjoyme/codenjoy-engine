package com.codenjoy.dojo.client.generator.language;

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

import com.codenjoy.dojo.client.generator.Template;

public class Java implements Template {

    @Override
    public String header() {
        return "package com.codenjoy.dojo.games.${game};\n" +
                "\n" +
                "/*-\n" +
                " * #%L\n" +
                " * Codenjoy - it's a dojo-like platform from developers to developers.\n" +
                " * %%\n" +
                " * Copyright (C) 2021 Codenjoy\n" +
                " * %%\n" +
                " * This program is free software: you can redistribute it and/or modify\n" +
                " * it under the terms of the GNU General Public License as\n" +
                " * published by the Free Software Foundation, either version 3 of the\n" +
                " * License, or (at your option) any later version.\n" +
                " * \n" +
                " * This program is distributed in the hope that it will be useful,\n" +
                " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                " * GNU General Public License for more details.\n" +
                " * \n" +
                " * You should have received a copy of the GNU General Public\n" +
                " * License along with this program.  If not, see\n" +
                " * <http://www.gnu.org/licenses/gpl-3.0.html>.\n" +
                " * #L%\n" +
                " */\n" +
                "\n" +
                "\n" +
                "import com.codenjoy.dojo.services.printer.CharElement;\n" +
                "\n" +
                "public enum Element implements CharElement {\n";
    }

    @Override
    public String line(boolean subrepo) {
        return "    ${element}('${char}'),\n";
    }

    @Override
    public String lastDelimiter() {
        return ";";
    }

    @Override
    public String comment() {
        return "        // ";
    }

    @Override
    public String footer() {
        return "\n" +
                "\n" +
                "    private final char ch;\n" +
                "\n" +
                "    Element(char ch) {\n" +
                "        this.ch = ch;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public char ch() {\n" +
                "        return ch;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public String toString() {\n" +
                "        return String.valueOf(ch);\n" +
                "    }\n" +
                "\n" +
                "    public static Element valueOf(char ch) {\n" +
                "        for (Element el : Element.values()) {\n" +
                "            if (el.ch == ch) {\n" +
                "                return el;\n" +
                "            }\n" +
                "        }\n" +
                "        throw new IllegalArgumentException(\"No such element for \" + ch);\n" +
                "    }\n" +
                "}\n";
    }
}
