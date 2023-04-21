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


import com.codenjoy.dojo.client.Closeable;
import com.codenjoy.dojo.services.hero.HeroData;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import org.json.JSONObject;

public interface Game extends Closeable, Progressive {

    /**
     * @return Джойстик для управления ботом игрока.
     */
    Joystick getJoystick();

    /**
     * @return True - если герой убит.
     */
    boolean isGameOver();

    /**
     * @return True - если герой прошел уровень. TODO ##2 работает пока только с multiplayerType.isTraining()
     */
    boolean isWin();

    /**
     * @return True - если герой должен покинуть эту комнату (проиграл матч).
     *          Работает только с multiplayerType.isDisposable().
     */
    boolean shouldLeave();

    /**
     * Если герой убит, то в следеющий тик фреймворк
     * дернет за этот метод, чтобы создать новую игру для игрока.
     * То же происходит при регистрации нового игрока.
     */
    void newGame();

    /**
     * Если игра имеет состояние и может быть сохранена,
     * то она может и загрузиться из этого состояния с помощью данного метода.
     * Этот метод вызывается сразу после создания игры методом {@link #newGame()}
     * @param save Загружаемый save.
     */
    void loadSave(JSONObject save);

    /**
     * @return true - если игра отрисовывается двумя разными состояниями для ws-клиента
     *         играющего и для прорисовки в браузере для наблюдающего. Это может
     *         потребоваться, когда мы хотим скрыть какие-то данные от игрока, но при
     *         этом продемонстрировать их в браузере.
     */
    default boolean sameBoard() {
        return true;
    }

    /**
     * Board =
     * "******" +
     * "*    *" +
     * "* ☺  *" +
     * "*    *" +
     * "*    *" +
     * "******";
     * Board может быть представлена в виде json объекта или любым другим представлением.
     *
     * @param parameters Обычно всего не используется, но если Game#sameBoard() = false, то:
     *                   parameters[0] = true, если нужно получить полное состояние доски
     *                   для отрисовки в браузере (default значение).
     *                   parameters[0] = false, если нужно получить состояние доски
     *                   для игры на ws-клиенте игрока (мы не хотим ему все данные).
     * @return строковое (или {@link JSONObject}) представление квадратной доски.
     */
    Object getBoardAsString(Object... parameters);

    /**
     * Если вдруг пользователь передумает играть и уйдет,
     * от при выходе из игры фреймворк дернет этот метод.
     * Мало ли, вдруг ты хранишь всех игроков у себя
     * (актуально для игры типа много игроков на одном поле).
     */
    void close();

    /**
     * Иногда ведущий игры сбрасывает очки участников -
     * в этом случае фреймворк дернет за этот метод.
     */
    void clearScore();

    /**
     * @return Полезная для клиента информация об игроке.
     */
    HeroData getHero();

    /**
     * @return Если игра сохраняется, то у нее должно быть
     * состояние (иначе верни null).
     *
     */
    JSONObject getSave();

    /**
     * @return Возвращает игрока играющего в эту игру.
     */
    GamePlayer getPlayer();

    /**
     * @return Возвращает борду игры
     */
    GameField getField();

    /**
     * указывает, что плеер хочет играть в эту игру.
     * @param field Поле на котором будет играть игрок.
     */
    void on(GameField field);

}
