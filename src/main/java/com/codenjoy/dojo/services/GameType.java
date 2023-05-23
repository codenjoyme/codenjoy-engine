package com.codenjoy.dojo.services;

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


import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.LevelProgress;
import com.codenjoy.dojo.services.multiplayer.MultiplayerType;
import com.codenjoy.dojo.services.printer.CharElement;
import com.codenjoy.dojo.services.printer.GraphicPrinter;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;

/**
 * Это интерфейс указывает на тип игры. Стоит его реализовать -
 * как на админке (http://localhost:8080/codenjoy-contest/admin)
 * будет возможность переключиться на твою игру.
 */
public interface GameType<T extends Settings> extends Tickable {

    /**
     * @param score Значения очков перед началом игры
     *         (используется например при загрузке игры из save).
     * @param settings Настройки по умолчанию связанные с текущей комнатой.
     * @return объект, который умеет в зависимости от типа события
     *         на карте подчитывать очки игроков.
     */
    PlayerScores getPlayerScores(Object score, T settings);

    /**
     * Так фреймворк будет стартовать новую игру для каждого пользователя.
     * @param level уровень игры (опциональное поле, обычно начинается с 1
     *          {@link LevelProgress#levelsStartsFrom1}).
     * @param settings Настройки по умолчанию связанные с текущей комнатой.
     * @return Экземпляр игры пользователя.
     */
    GameField createGame(int level, T settings);

    /**
     * @param settings Настройки по умолчанию связанные с текущей комнатой.
     * @return Размер поля. Важно, чтобы у всех пользователей
     *         были одинаковые по размеру поля.
     */
    Parameter<Integer> getBoardSize(T settings);

    /**
     * @return Имя игры.
     */
    String name();

    /**
     * @return Список элементов, отображаемых на поле.
     * Смотри класс {@link com.codenjoy.dojo.games.sample.Element} конкретной игры.
     */
    CharElement[] getPlots();

    /**
     * @return Настройки игры по умолчанию. Эти настройки будут использоваться
     * для каждой отдельной комнаты и смогут меняться независимо.
     *
     * Настройки представлены несколькими интерфейсами и классами
     * {@link com.codenjoy.dojo.services.settings.SettingsReader},
     * {@link Settings}, {@link Parameter} и др.
     */
    T getSettings();

    /**
     * @return Каждая игра может (null - если это не так) предоставить
     *      своего AI который будет развлекать новопришедших игроков.
     */
    Class<? extends Solver> getAI();

    /**
     * @return Объект, который предоставляет игроку api для игры.
     */
    Class<? extends ClientBoard> getBoard();

    /**
     * Если подложить в 'src\main\resources\<GAME>\version.properties'
     * игры строчку '${project.version}_${build.time}'
     * то ее потом можно будет прочитать с помощью
     * VersionReader.getCurrentVersion();
     *
     * @return Версия игры.
     */
    String getVersion();

    /**
     * @param settings Настройки по умолчанию связанные с текущей комнатой.
     *
     * @return Возвращает тип мультиплеера этой игры.
     */
    MultiplayerType getMultiplayerType(T settings);

    /**
     * Метод для создания игрового пользователя внутри игры.
     * @param listener Через этот интерфейс фреймворк будет
     *                 слушать какие ивенты возникают в твоей игре.
     * @param settings Настройки по умолчанию связанные с текущей комнатой.
     * @param teamId Команда в которой будет играть игрок.
     *               При смене команды игра пересоздается.
     * @param playerId идентификатор игрока, зарегистрировавшегося на сервере.
     * @return Объект игрока.
     */
    GamePlayer createPlayer(EventListener listener, int teamId, String playerId, T settings);

    /**
     * @return Объект реализующий функцию random. Ты можешь переопределить алгоритм,
     *          например, для тестовых целей.
     */
    Dice getDice();

    /**
     * @return Возвращает фабрику принтеров, которая создаст
     * в нужный момент принтер {@link Printer}, который возьмет
     * на себя представление борды в виде строчки.
     * Если хочешь использовать другой принтер - используй
     * {@link PrinterFactory#get(GraphicPrinter)}}
     */
    PrinterFactory getPrinterFactory();

    /**
     * @return true если игра находится в режиме тестирования и ее не надо включать.
     */
    default boolean isTesting() {
        return false;
    }
}



















