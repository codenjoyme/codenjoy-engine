package com.codenjoy.dojo.client.generator;

public class Go implements Template {

    @Override
    public String header() {
        return "package %s\n" +
                "\n" +
                "var Elements = map[string]rune{\n";
    }

    @Override
    public String line() {
        return "    \"%s\": '%s',";
    }

    @Override
    public String info() {
        return "// %s\n";
    }

    @Override
    public String footer() {
        return "}\n";
    }
}
