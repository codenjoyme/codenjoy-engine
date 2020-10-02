package com.codenjoy.dojo.services.multiplayer;

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


import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.multiplayer.types.*;
import org.json.JSONObject;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Определяет тип многопользовательский игры.
 * Этот функционал реализует сервис многопользовательской игры {@see MultiplayerService}
 */
public class MultiplayerType {

    /**
     * Если комната игры одноразовая, то этот флаг стоит в true
     */
    public final static boolean DISPOSABLE = true;

    /**
     * Каждый игрок на своем отдельном поле.
     * Удаляется игрок - удаляется поле.
     * Создается игрок - создается новое поле.
     */
    public static final MultiplayerType SINGLE = new SingleType();

    /**
     * Все игроки на одном поле без ограничений по количеству.
     * Удалятеся игрок - поле остается с другими игроками.
     * Последний игрок удаляется - поле тоже удаляется. TODO ##1 можно поменять тут
     * Каждый новый игрок создается на этом же поле.
     */
    public static final MultiplayerType MULTIPLE = new MultipleType();

    /**
     * На поле только заданное количество игроков.
     * Что будет при добавлении/удалении игроков зависит от флага disposable.
     * Этот флаг говорит о том одноразовое поле (true) или нет (false).
     * Для true (одноразовое):
     *    Игроки будут добавляться до тех пор пока поле не заполнится.
     *    Удаленные игроки не влияют на уровень заполненности поля -
     *    если на поле вместимостью 4 побывало уже 4 участника, то не важно
     *    сколько сейчас на нем игроков больше новых на него пустит.
     *    Все новый игроки сверх вместимости поля будут добавляться на новое поле.
     * Для false (многоразовое):
     *    Игроки будут добавляться до тех пор пока поле не заполнится.
     *    Но, в отличие от прошлого случая (одноразовая борда), берется во внимание
     *    только текущее количество игроков на поле, если оно меньше вместимости -
     *    новый игрок добавится на это поле, иначе для него создастся новое поле
     * В любом случае если игроков было несколько и они все ушли, оставив одного на поле -
     *    для него создастся новое поле и к нему можно будет добавляться.
     */
    public static final BiFunction<Integer, Boolean, MultiplayerType> TEAM = TeamType::new;

    /**
     * По двое игроков за раз на одном поле.
     * Для третьего игрока создается следующее поле.
     * Если какой-то игрок выбыл из игры, то на его место уже не может
     * зарегистрироваться другой желающий - комната одноразовая.
     * Если на поле остается один последний игрок (после того как вышли),
     * то он так же выходит из этого поля и оно удаляется.
     * По сути поведение такое же как для TEAM(2, DISPOSABLE)
     * TODO Если один из игроков закончил, он попадает в лобби и ждет пока в нем не появится игрок, с которым он еще не играл.
     * TODO Игра находится на паузе, пока не соберется заданное количество игроков.
     */
    public static final MultiplayerType TOURNAMENT = new TournamentType();

    /**
     * Трое игроков за раз на одном поле.
     * Для четвертого будет создано отдельное поле.
     * Если какой-то игрок выбыл из игры, то на его место уже не может
     * зарегистрироваться другой желающий - комната одноразовая.
     * Если на поле остается один последний игрок (после того как вышли),
     * то он так же выходит из этого поля и оно удаляется.
     * По сути поведение такое же как для TEAM(3, DISPOSABLE)
     * TODO Если один из игроков закончил, он попадает в лобби и ждет пока в нем не появятся двое других игроков, с которыми он еще не играл.
     * TODO Игра находится на паузе, пока не соберется заданное количество игроков.
     */
    public static final MultiplayerType TRIPLE = new TripleType();

    /**
     * Четверо игроков за раз на одном поле.
     * Для пятого игрока уже создается новое поле.
     * Если какой-то игрок выбыл из игры, то на его место уже не может
     * зарегистрироваться другой желающий - комната одноразовая.
     * Если на поле остается один последний игрок (после того как вышли),
     * то он так же выходит из этого поля и оно удаляется.
     * По сути поведение такое же как для TEAM(4, DISPOSABLE)
     * TODO Если один из игроков закончил, он попадает в лобби и ждет пока в нем не появятся трое других игроков, с которыми он еще не играл.
     * TODO Игра находится на паузе, пока не соберется заданное количество игроков.
     */
    public static MultiplayerType QUADRO = new QuadroType();

    /**
     * Игроки играют каждый на своем уровне (single) заданное количество уровней,
     * затем они все переходят на одну и ту же карту (multiple)
     * и она зацикливается для них навсегда.
     * Переходить между уровнями вполне можно, тогда single команты пересоздаются
     * всякий раз когда на них заходят, а multiple остается общей для всех.
     */
    public static final Function<Integer, MultiplayerType> TRAINING = TrainingType::new;

    protected int roomSize;
    protected int levelsCount;
    protected boolean disposable;

    protected MultiplayerType(int roomSize, boolean disposable) {
        this(roomSize, 1, disposable);
    }

    public MultiplayerType(int roomSize, int levelsCount, boolean disposable) {
        this.roomSize = roomSize;
        this.levelsCount = levelsCount;
        this.disposable = disposable;
    }

    public boolean isDisposable() {
        return disposable;
    }

    public boolean isSingle() {
        return this instanceof SingleType;
    }

    public boolean isMultiple() {
        return this instanceof MultipleType;
    }

    public boolean isTriple() {
        return this instanceof TripleType;
    }

    public boolean isTournament() {
        return this instanceof TournamentType;
    }

    public boolean isQuadro() {
        return this instanceof QuadroType;
    }

    public boolean isTeam() {
        return this instanceof TeamType;
    }

    public boolean isSingleplayer() {
        return isSingle();
    }

    public boolean isMultiplayer() {
        return !isSingle();
    }

    public boolean isTraining() {
        return this instanceof TrainingType;
    }

    public boolean isLastLevel(int levelNumber) {
        return levelNumber == levelsCount;
    }

    public String getType() {
        return this.getClass().getSimpleName().toLowerCase().replace("type", "");
    }

    public int loadProgress(Game game, JSONObject save) {
        int roomSize = getRoomSize();
        game.setProgress(progress());
        return roomSize;
    }

    public int getRoomSize(LevelProgress progress) {
        return roomSize;
    }

    public int getRoomSize() {
        return getRoomSize(null);
    }

    public int getLevelsCount() {
        return levelsCount;
    }

    /**
     * Постобработка борды после прорисовки.
     * Некоторые типы уровней могут захотеть добавить туда информацию, скажем про уровень.
     * @param board подготовленная игрой борда (String или JSONObject)
     * @param single вся информация о игре
     * @return измененная борда, отправляемая клиенту
     */
    public Object postProcessBoard(Object board, Single single) {
        return board;
    }

    /**
     * Постобработка сейва игры после создания.
     * Некоторые типы уровней могут захотеть добавить туда информацию, скажем про уровень.
     * @param save подготовленный игрой сейв
     * @param single вся информация о игре
     * @return измененный сейв игры, который затем уйдет на сохранение
     */
    public JSONObject postProcessSave(JSONObject save, Single single) {
        if (save == null) {
            return new JSONObject();
        }
        return save;
    }

    /**
     * Прогресс содержит в себе информацию об уровнях,
     * а она зависит от MultiplayerType
     * @return объект прогресса на основе типа
     */
    public LevelProgress progress() {
        return new LevelProgress();
    }
}
