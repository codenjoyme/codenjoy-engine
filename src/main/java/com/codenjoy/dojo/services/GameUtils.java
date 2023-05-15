package com.codenjoy.dojo.services;

public class GameUtils {

    // TODO кажется это старый код комнат, его можно убрать после окончательной имплементации комнат
    public static final String ROOMS_SEPARATOR = "-";

    public static String removeNumbers(String game) {
        return game.split(ROOMS_SEPARATOR)[0];
    }
}
