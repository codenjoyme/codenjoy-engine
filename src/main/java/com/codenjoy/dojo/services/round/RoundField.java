package com.codenjoy.dojo.services.round;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2020 Codenjoy
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

import com.codenjoy.dojo.services.Tickable;

import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class RoundField<P extends RoundGamePlayer<? extends RoundPlayerHero, ? extends RoundGameField>> implements RoundGameField<P>, Tickable {

    private static final boolean FIRE_EVENTS = true;
    private static final boolean DONT_FIRE_EVENTS = !FIRE_EVENTS;

    private Round round;
    private List<P> inactive;

    private Object startRoundEvent;
    private Object loseEvent;

    public RoundField() {
        // do nothing, for testing only
    }

    public RoundField(Object startRoundEvent, Object winEvent, Object loseEvent, RoundSettings settings) {
        this.round = RoundFactory.get(settings);
        round.init(this, winEvent);

        this.startRoundEvent = startRoundEvent;
        this.loseEvent = loseEvent;

        inactive = new LinkedList<>();
    }

    /**
     * @return Доступ к игрокам на поле.
     */
    protected abstract List<P> players();

    /**
     * Если раунд начался, то к этому методу доходит сигнал из
     * {@link #tick()}, в противном случае нет.
     */
    protected abstract void tickField();

    /**
     * Перед каждым тиком поле может захотеть почистить некоторые
     * устаревшие за время с прошлого тика артефакты. Актуально
     * например для "умирающих" объектов, которые пропадают на 2й тик.
     */
    protected void cleanStuff() {
        // clean all temporary stuff before next tick
    }

    /**
     * После вручения призов победителю полю может понадобиться
     * провести какую-то работу. Тут это можно сделать.
     */
    protected void setNewObjects() {
        // add new object after rewarding winner
    }

    /**
     * После добавления игрока поле может захотеть сделать
     * некоторые подготовительные действия.
     * @param player Добавляемый на поле игрок.
     */
    protected abstract void onAdd(P player);

    /**
     * После удаления игрока поле может захотеть сделать
     * некоторые подготовительные действия.
     * @param player Удаляемый из поля игрок.
     */
    protected abstract void onRemove(P player);

    /**
     * Сердце игры. Метод "тикается" фреймворком каждую секунду.
     */
    @Override
    public void tick() {
        cleanStuff();

        boolean skip = round.tick();
        if (skip) {
            return;
        }

        tickField();

        rewardTheWinnerIfNeeded(this::setNewObjects);
    }

    @Override
    public List<P> aliveActive() {
        return players().stream()
                .filter(p -> p.isAlive() && p.isActive())
                .collect(toList());
    }

    @Override
    public void start(int round) {
        players().forEach(p -> p.start(round, startRoundEvent));
    }

    @Override
    public void print(String message) {
        players().forEach(player -> player.printMessage(message));
    }

    @Override
    public int score(P player) {
        return player.getHero().scores();
    }

    @Override
    public void oneMoreDead(P player) {
        if (round instanceof NullRound) {
            player.die(false, loseEvent);
        } else {
            player.die(round.isMatchOver(), loseEvent);
            inactive.add(player);
        }
    }

    @Override
    public void reset(P player) {
        if (round.isMatchOver()) {
            player.getHero().setAlive(false);
            player.leaveBoard();
        } else {
            newGame(player);
        }
    }

    public void rewardTheWinnerIfNeeded(Runnable runnable) {
        if (inactive.isEmpty()) {
            return;
        }

        inactive.clear();
        round.rewardTheWinner();
        runnable.run();
    }

    @Override
    public void newGame(P player) {
        if (inactive.contains(player)) {
            inactive.remove(player);
        }

        if (players().contains(player)) {
            remove(player, DONT_FIRE_EVENTS);
        }

        players().add(player);
        onAdd(player);
    }

    /**
     * Иногда ведущий игры хочет обнулить состояние
     * на поле - этот метод ему в помощь.
     */
    @Override
    public void clearScore() {
        round.clear();
        inactive.clear();

        resetAllPlayers();
    }

    public void resetAllPlayers() {
        new LinkedList<>(players()).forEach(this::newGame);
    }

    @Override
    public void remove(P player) {
        remove(player, FIRE_EVENTS);
    }

    private void remove(P player, boolean fireEvents) {
        if (!players().contains(player)) {
            return;
        }

        if (!players().remove(player)) {
            return;
        }

        // кто уходит из игры не лишает коллег очков за победу,
        // но только если он был жив к этому моменту
        RoundPlayerHero hero = player.getHero();
        if (fireEvents && hero.isActiveAndAlive()) {
            hero.die();
            rewardTheWinnerIfNeeded(() -> {});
        }

        onRemove(player);

        return;
    }
}