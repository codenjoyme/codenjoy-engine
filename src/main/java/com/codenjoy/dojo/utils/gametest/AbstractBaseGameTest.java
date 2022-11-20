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

import com.codenjoy.dojo.games.clifford.Element;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.dice.MockDice;
import com.codenjoy.dojo.services.field.AbstractLevel;
import com.codenjoy.dojo.services.multiplayer.LevelProgress;
import com.codenjoy.dojo.services.multiplayer.TriFunction;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.services.round.RoundField;
import com.codenjoy.dojo.services.round.RoundGamePlayer;
import com.codenjoy.dojo.services.round.RoundPlayerHero;
import com.codenjoy.dojo.services.settings.AllSettings;
import com.codenjoy.dojo.utils.events.EventsListenersAssert;
import com.codenjoy.dojo.utils.smart.SmartAssert;
import com.codenjoy.dojo.utils.whatsnext.WhatsNextUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.utils.TestUtils.asArray;
import static com.codenjoy.dojo.utils.TestUtils.collectHeroesData;
import static com.codenjoy.dojo.utils.core.MockitoJunitTesting.testing;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public abstract class AbstractBaseGameTest
        <P extends RoundGamePlayer,
        F extends RoundField,
        S extends AllSettings,
        L extends AbstractLevel,
        H extends RoundPlayerHero> {

    private List<EventListener> listeners;
    private List<P> players;
    private List<Game> games;
    private MockDice dice;
    private PrinterFactory<Element, P> printer;
    private F field;
    private S settings;
    private EventsListenersAssert events;
    private L level;

    /**
     * Метод необходимо аннотировать @Before в наследнике.
     */
    public void setup() {
        listeners = new LinkedList<>();
        players = new LinkedList<>();
        games = new LinkedList<>();
        dice = new MockDice();
        printer = new PrinterFactoryImpl<>();
        settings = setupSettings();
        events = new EventsListenersAssert(() -> listeners, eventClass());
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
     * @return Конструктор для создания Field в игре.
     */
    protected abstract TriFunction<Dice, L, S, F> createField();

    /**
     * @return Конструктор для создания Level в игре.
     */
    protected abstract Function<String, L> createLevel();

    /**
     * @return Конструктор для создания Player в игре.
     */
    protected abstract BiFunction<EventListener,S,P> createPlayer();

    /**
     * @return Объект Settings с базовыми настройками для тестов.
     */
    protected abstract S setupSettings();

    public void dice(Integer... next) {
        if (next.length == 0) return;
        dice.then(next);
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

        beforeCreateField();

        field = createField().apply(dice, null, settings);
        field.load(level.map(), (Function<H, P>) this::givenPlayer);

        afterCreateField();
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

    protected P givenPlayer(H hero) {
        P player = newPlayer();

        player.setHero(hero);
        newGame(player);
        player.getHero().manual(false);

        return player;
    }

    private void newGame(P player) {
        games.add(WhatsNextUtils.newGame(player, printer, field));
    }

    private P newPlayer() {
        P player = createPlayer().apply(newEventListener(), settings);
        players.add(player);
        return player;
    }

    private EventListener newEventListener() {
        EventListener listener = testing().mock(EventListener.class);
        listeners.add(listener);
        return listener;
    }

    /**
     * Создает Player в заданной координате. Используется в случае,
     * если мы хотим где-то в тесте после givenFl создать еще одного героя.
     * @param pt Координата где будет новый герой.
     * @return Созданный Player.
     */
    public P givenPlayer(Point pt) {
        P player = newPlayer();

        dice(asArray(asList(pt)));
        newGame(player);

        return player;
    }

    public void tick() {
        field.tick();
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
        assertEquals(expected, game(index).getBoardAsString());
    }

    /**
     * Проверяет все борды сразу.
     * @param expected Ожидаемое значение.
     */
    public void assertA(String expected) {
        assertEquals(expected,
                EventsListenersAssert.collectAll(games, index -> {
                    Object actual = game(index).getBoardAsString();
                    return String.format("game(%s)\n%s\n", index, actual);
                }));
    }

    public void verifyAllEvents(String expected) {
        assertEquals(expected, events().getEvents());
    }

    public void assertScores(String expected) {
        assertEquals(expected,
                collectHeroesData(players, "scores", true));
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

    protected MockDice dice() {
        return dice;
    }

    protected List<EventListener> listeners() {
        return listeners;
    }

    protected F field() {
        return field;
    }

    protected L level() {
        return level;
    }

    protected EventsListenersAssert events() {
        return events;
    }

    // public getters

    public Game game() {
        return games.get(0);
    }

    public Game game(int index) {
        return games.get(index);
    }

    public EventListener listener() {
        return listeners.get(0);
    }

    public EventListener listener(int index) {
        return listeners.get(index);
    }

    public H hero() {
        return hero(0);
    }

    public H hero(int index) {
        return (H) player(index).getHero();
    }

    protected List<H> heroes() {
        return players.stream()
                .map(RoundGamePlayer::getHero)
                .map(it -> (H)it)
                .collect(toList());
    }

    public P player() {
        return player(0);
    }

    public P player(int index) {
        return players.get(index);
    }

    protected List<P> players() {
        return players();
    }

    // other stuff

    public void assertHeroDie() {
        assertEquals(true, game().isGameOver());
    }

    public void assertHeroAlive() {
        assertEquals(false, game().isGameOver());
    }

    public void remove(int index) {
        field.remove(players.get(index));
        players.remove(index);
        listeners.remove(index);
    }

    public void removeAllDied() {
        players().forEach(player -> {
            if (!player.isAlive()) {
                field().remove(player);
            }
        });
    }
}
