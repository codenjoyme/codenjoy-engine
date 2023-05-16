package com.codenjoy.dojo.utils.gametest;

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

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.dice.NumbersDice;
import com.codenjoy.dojo.services.field.AbstractLevel;
import com.codenjoy.dojo.services.lock.LockedJoystick;
import com.codenjoy.dojo.services.multiplayer.FieldService;
import com.codenjoy.dojo.services.multiplayer.LevelProgress;
import com.codenjoy.dojo.services.multiplayer.Spreader;
import com.codenjoy.dojo.services.multiplayer.Sweeper;
import com.codenjoy.dojo.services.room.RoomService;
import com.codenjoy.dojo.services.round.RoundField;
import com.codenjoy.dojo.services.round.RoundGamePlayer;
import com.codenjoy.dojo.services.round.RoundPlayerHero;
import com.codenjoy.dojo.services.settings.AllSettings;
import com.codenjoy.dojo.utils.events.EventsListenersAssert;
import com.codenjoy.dojo.utils.smart.SmartAssert;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import static com.codenjoy.dojo.services.Deals.withRoom;
import static com.codenjoy.dojo.utils.TestUtils.collectHeroesData;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class NewAbstractBaseGameTest
        <P extends RoundGamePlayer,
        F extends RoundField,
        S extends AllSettings,
        L extends AbstractLevel,
        H extends RoundPlayerHero> {

    private Deals all;
    private NumbersDice dice;
    private RoomService rooms;

    private List<EventListener> listeners;
    private EventsListenersAssert events;
    private L level;
    private S settings;
    private String room;
    private GameType gameType;

    /**
     * Метод необходимо аннотировать @Before в наследнике.
     */
    public void setup() {
        listeners = new LinkedList<>();
        events = new EventsListenersAssert(() -> listeners, eventClass());

        rooms = new RoomService();
        FieldService fields = new FieldService(0);
        Spreader spreader = new Spreader(fields);

        all = new Deals(spreader, rooms);
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        all.init(lock);
        all.onAdd(deal -> {});
        all.onRemove(deal -> {});
        all.onListener(listener -> {
            EventListener result = spy(listener);
            listeners.add(result);
            return result;
        });

        dice = new NumbersDice();

        room = "room";
        GameType original = spy(gameType());
        when(original.getDice()).thenReturn(dice);

        gameType = rooms.create(room, original);
        settings = setupSettings((S) rooms.settings(room));
    }

    /**
     * Метод необходимо аннотировать @After в наследнике.
     */
    public void after() {
        verifyAllEvents("");
        SmartAssert.checkResult();
    }

    /**
     * @return Класс представляющий Event в игре.
     */
    protected abstract Class<?> eventClass();

    /**
     * @return Класс представляющий GameType в игре.
     */
    protected abstract GameType gameType();

    /**
     * @return Конструктор для создания Level в игре.
     */
    protected abstract Function<String, L> createLevel();

    /**
     * @return Объект Settings с базовыми настройками для тестов.
     */
    protected abstract S setupSettings(S settings);

    /**
     * @return true - если мы хотим генерировать hero из level map для удобства тестировангия,
     *         false - если мы хотим генерировать hero только по координатам (ближе к реальности).
     */
    protected boolean manualHero() {
        return false;
    }

    public void dice(Integer... next) {
        if (next.length == 0) return;
        dice.will(next);
    }

    /**
     * Генерирует Filed настроенный так, как указано на карте maps[0].
     * Карты заносятся в Settings.
     * @param maps Карты на основе которых будет сгенерирована игра.
     */
    public void givenFl(String... maps) {
        int levelNumber = LevelProgress.levelsStartsFrom1;
        settings.setLevelMaps(levelNumber, maps);
        level = (L) settings.level(levelNumber, dice, createLevel());
        List<H> heroes = (List<H>) level.heroes();

        beforeCreateField();

        all.onField(deal -> {
            if (heroes.isEmpty()) {
                return;
            }
            H hero = heroes.get(index());
            if (manualHero()) {
                // get whole hero ant put it on field
                deal.getGame().getPlayer().setHero(hero);
                // then will call field.newGame(player) and process hero as is
            } else {
                // take care of the hero's initial position
                dice.will(hero.getX(), hero.getY());
                // then will call field.newGame(player) and finding place for hero with dice
            }
        });

        for (H hero : heroes) {
            givenPlayer();
        }

        // disable hero generation from the level for during hero
        // regeneration on the new field, when player.shouldLeave() = true
        all.onField(null);

        afterCreateField();
    }

    public void givenPlayer() {
        long now = Calendar.getInstance().getTimeInMillis();
        all.deal(PlayerSave.NULL, room, "player" + index(), "callbackUrl", gameType, now);
    }

    private int index() {
        return deals().size() - 1;
    }

    private List<Deal> deals() {
        return all.getAll(withRoom(room));
    }

    /**
     * Метод служит предварительной настройке окружения перед
     * созданием Field. Настройки подтюнить или что-то в level
     * из которого будет создаваться Field.
     */
    protected void beforeCreateField() {
        // settings / level pre-processing
    }

    /**
     * Метод служит постобработке окружения после
     * создания Field. Настройки подтюнить или что-то в самой Field.
     */
    protected void afterCreateField() {
        // settings / field post-processing
    }

    public void tick() {
        all.tick();
    }

    public void ticks(int fromInclusive, int tillExclusive) {
        for (int tick = fromInclusive; tick < tillExclusive; tick++) {
            tick();
        }
    }

    // basic asserts

    public void assertF(String expected) {
        assertF(expected, 0);
    }

    /**
     * Проверяет одну борду с заданным индексом.
     * @param expected Ожидаемое значение.
     * @param index Индекс игрока.
     */
    public void assertF(String expected, int index) {
        assertEquals(expected, game(index).getBoardAsString(true));
    }

    /**
     * Проверяет все борды сразу.
     * @param expected Ожидаемое значение.
     */
    public void assertA(String expected) {
        assertEquals(expected,
                EventsListenersAssert.collectAll(listeners(), index -> {
                    Object actual = game(index).getBoardAsString(true);
                    return String.format("game(%s)\n%s\n", index, actual);
                }));
    }

    public void verifyAllEvents(String expected) {
        assertEquals(expected, events().getEvents());
    }

    public void assertScores(boolean skipDefault, String expected) {
        assertEquals(expected,
                collectHeroesData(players(), "scores", skipDefault));
    }

    public void assertScores(String expected) {
        assertScores(true, expected);
    }

    public void assertEquals(String message, Object expected, Object actual) {
        SmartAssert.assertEquals(message, expected, actual);
    }

    public void assertEquals(Object expected, Object actual) {
        SmartAssert.assertEquals(expected, actual);
    }

    // protected getters

    protected S settings() {
        return settings;
    }

    protected NumbersDice dice() {
        return dice;
    }

    protected List<EventListener> listeners() {
        return listeners;
    }

    protected F field() {
        return (F) deal().getField();
    }

    protected L level() {
        return level;
    }

    protected EventsListenersAssert events() {
        return events;
    }

    // public getters

    public Deal deal() {
        return deal(0);
    }

    public Deal deal(int index) {
        return deals().get(index);
    }

    public Game game() {
        return game(0);
    }

    public Game game(int index) {
        return deal(index).getGame();
    }

    public Joystick joystick() {
        return joystick(0);
    }

    public Joystick joystick(int index) {
        return ((LockedJoystick) game(index).getJoystick()).getWrapped();
    }

    public EventListener listener() {
        return listener(0);
    }

    public EventListener listener(int index) {
        return deal(index).getPlayer().getInfo();
    }

    public H hero() {
        return hero(0);
    }

    public H hero(int index) {
        return (H) player(index).getHero();
    }

    public List<H> heroes() {
        return players().stream()
                .map(player -> (H)player.getHero())
                .collect(toList());
    }

    public P player() {
        return player(0);
    }

    public P player(int index) {
        return (P) deal(index).getGame().getPlayer();
    }

    protected List<P> players() {
        return deals().stream()
                .map(deal -> (P)deal.getGame().getPlayer())
                .collect(toList());
    }

    // other stuff

    public void assertHeroDie() {
        assertHeroAlive(0, false);
    }

    public void assertHeroAlive() {
        assertHeroAlive(0, true);
    }

    public void assertHeroAlive(int index, boolean alive) {
        assertEquals(!alive, game(index).isGameOver());
    }

    public void assertHeroActive(boolean active) {
        assertHeroActive(0, active);
    }

    public void assertHeroActive(int index, boolean active) {
        assertEquals(active, hero(index).isActive());
    }

    public void assertHeroStatus(String expected) {
        assertEquals(expected,
                "active:\n" +
                collectHeroesData(players(), "isActive", false) +
                "\nalive\n" +
                collectHeroesData(players(), "isAlive", false));
    }

    public void remove(int index) {
       all.remove(deal(index).getPlayerId(), Sweeper.on());
       listeners.remove(index);
    }
}