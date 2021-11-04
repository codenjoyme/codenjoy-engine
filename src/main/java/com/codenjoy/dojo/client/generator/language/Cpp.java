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

public class Cpp implements Template {

    @Override
    public String header() {
        return "//include \"Element.h\"\n" +
                "\n" +
                "Element::Element(Char el) {\n" +
                "    elem.first = valueOf(el);\n" +
                "    elem.second = el;\n" +
                "}\n" +
                "\n" +
                "Element::Element(String name) {\n" +
                "    elem.second = Elements.at(name);\n" +
                "    elem.first = name;\n" +
                "}\n" +
                "\n" +
                "Char Element::ch() {\n" +
                "    return elem.second;\n" +
                "}\n" +
                "\n" +
                "String Element::name() {\n" +
                "    return elem.first;\n" +
                "}\n" +
                "\n" +
                "Char Element::getChar() const {\n" +
                "    return elem.second;\n" +
                "}\n" +
                "\n" +
                "String Element::valueOf(Char ch) const {\n" +
                "    for (auto i : Elements) {\n" +
                "        if (i.second == ch) return i.first;\n" +
                "    }\n" +
                "    throw std::invalid_argument(\"Element::valueOf(Char ch): No such Element for \" + ch);\n" +
                "}\n" +
                "\n" +
                "bool Element::operator==(const Element& el) const {\n" +
                "    return elem == el.elem;\n" +
                "}\n" +
                "\n" +
                "ElementMap Element::initialiseElements() {\n" +
                "    ElementMap mapOfElements;\n";
    }

    @Override
    public String line() {
        return "    mapOfElements[LL(\"%s\")] = LL('%s');";
    }

    @Override
    public String comment() {
        return "// ";
    }

    @Override
    public String footer() {
        return "\n" +
                "    return mapOfElements;\n" +
                "};\n" +
                "\n" +
                "const ElementMap Element::Elements = Element::initialiseElements();\n";
    }
}
