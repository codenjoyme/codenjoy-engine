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


import com.codenjoy.dojo.services.info.Information;
import com.codenjoy.dojo.services.info.ScoresCollector;
import com.codenjoy.dojo.services.lock.LockedGame;
import com.codenjoy.dojo.services.multiplayer.*;
import com.codenjoy.dojo.services.nullobj.NullDeal;
import com.codenjoy.dojo.utils.Strings;
import com.google.common.collect.Multimap;
import lombok.experimental.FieldNameConstants;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@FieldNameConstants
// TODO It's a good idea to move this logic to engine
//      and make all integration tests of the game such
//      that they use real logic, not emulation.
//      This way there will be fewer hallucinations after
//      moving from well tested logic by unit tests to
//      integration. After all, right now even SmokeTest
//      creates the illusion of workability.
public class Deals implements Iterable<Deal> {

    public static final boolean ALL = true;
    public static final boolean ACTIVE = !ALL;

    private List<Deal> all = new LinkedList<>();

    private List<Consumer<Deal>> onAdd = new LinkedList<>();
    private List<Consumer<Deal>> onRemove = new LinkedList<>();
    private List<Consumer<Deal>> onField = new LinkedList<>();
    private Function<EventListener, EventListener> onListener;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private Spreader spreader;

    public Deals(Spreader spreader) {
        this.spreader = spreader;
    }

    public void onAdd(Consumer<Deal> consumer) {
        on(onAdd, consumer);
    }

    private void on(List<Consumer<Deal>> on, Consumer<Deal> consumer) {
        if (consumer == null) {
            on.clear();
        } else {
            on.add(consumer);
        }
    }

    public void onRemove(Consumer<Deal> consumer) {
        on(onRemove, consumer);
    }

    public void onField(Consumer<Deal> consumer) {
        on(onField, consumer);
    }

    public void onListener(Function<EventListener, EventListener> function) {
        this.onListener = function;
    }

    public void init(ReadWriteLock lock) {
        this.lock = lock;
    }

    public void remove(String id, Sweeper sweeper) {
        int index = all.indexOf(new Player(id));
        if (index == -1) return;
        Deal deal = all.get(index);

        removeInRoom(deal, sweeper);
        all.remove(index);

        process(deal, onRemove);
        deal.remove();
        deal.setChat(null);
    }

    private void removeInRoom(Deal deal, Sweeper sweeper) {
        sweeper.of(deal.getType());

        List<Deal> alone = spreader.remove(deal, sweeper);

        if (sweeper.isResetOther()) {
            alone.forEach(otherDeal -> play(otherDeal, otherDeal.getGame().getSave()));
        }
    }

    // TODO по хорошему тут тоже надо optional
    public Deal get(String id) {
        return get(deal -> Objects.equals(id, deal.getPlayerId()))
                .orElse(NullDeal.INSTANCE);
    }

    public Optional<Deal> get(Predicate<Deal> filter) {
        return all.stream()
                .filter(filter)
                .findFirst();
    }

    private void play(Deal deal, JSONObject save) {
        Game game = deal.getGame();
        String room = deal.getRoom();
        GameType gameType = deal.getGameType();

        MultiplayerType type = deal.getType();
        int roomSize = type.loadProgress(game, save);
        LevelProgress progress = game.getProgress();
        int level = progress.getCurrent();

        GameField field = spreader.fieldFor(deal,
                room,
                type,
                roomSize,
                level,
                () -> {
                    game.getPlayer().setProgress(progress);
                    return gameType.createGame(level, gameType.getSettings());
                });

        game.on(field);
        process(deal, onField);
        game.newGame();

        if (save != null && !save.keySet().isEmpty()) {
            game.loadSave(save);
        }
    }

    public Deal add(Player player, String room, PlayerSave save) {
        Single single = buildSingle(player, save);

        Game game = new LockedGame(lock).wrap(single);

        Deal deal = create(player, room, game);
        process(deal, onAdd);
        all.add(deal);

        play(deal, parseSave(save));

        return deal;
    }

    private static void process(Deal deal, List<Consumer<Deal>> on) {
        on.forEach(consumer -> consumer.accept(deal));
    }

    /**
     * Do not inline this method, it will be overridden in tests.
     */
    public Deal create(Player player, String room, Game game) {
        return new Deal(player, game, room);
    }

    private Single buildSingle(Player player, PlayerSave save) {
        if (save != null && save != PlayerSave.NULL) {
            player.setTeamId(save.getTeamId());
        }
        GameType gameType = player.getGameType();
        GamePlayer gamePlayer = gameType.createPlayer(wrap(player.getInfo()),
                player.getTeamId(), player.getId(), gameType.getSettings());
        return new Single(gamePlayer,
                gameType.getPrinterFactory(),
                gameType.getMultiplayerType(gameType.getSettings()));
    }

    private EventListener wrap(EventListener listener) {
        return onListener == null
                ? listener
                : onListener.apply(listener);
    }

    private JSONObject parseSave(PlayerSave save) {
        if (save == null || PlayerSave.isSaveNull(save.getSave())) {
            return new JSONObject();
        }
        return new JSONObject(save.getSave());
    }

    public boolean isEmpty() {
        return all.isEmpty();
    }

    @Override
    public Iterator<Deal> iterator() {
        return all.iterator();
    }

    public List<Player> players() {
        return all.stream()
                .map(Deal::getPlayer)
                .collect(toList());
    }

    public int size() {
        return all.size();
    }

    public void clear() {
        players().forEach(player -> remove(player.getId(), Sweeper.off()));
        spreader.rooms().clear(); // могут быть комнаты потеряшки, их надо удалить так же
    }

    public List<Deal> getAll(Predicate<Deal> predicate) {
        return all.stream()
                .filter(predicate)
                .collect(toList());
    }

    public static Predicate<Deal> withType(String gameType) {
        return deal -> deal.getPlayer().getGame().equals(gameType);
    }

    public static Predicate<Deal> withAll() {
        return deal -> ALL;
    }

    public static Predicate<Deal> withRoom(String room) {
        return deal -> deal.getRoom() != null && deal.getRoom().equals(room);
    }

    public static Predicate<Deal> exclude(List<String> ids) {
        return deal -> !ids.contains(deal.getPlayerId());
    }

    /**
     * @return Возвращает уникальные (недублирующиеся) GameType в которые сейчас играют.
     */
    public List<GameType> getGameTypes() {
        return all.stream()
                .map(Deal::getGameType)
                .map(RoomGameType::unwrap)
                .filter(distinctByKey(GameType::name))
                .collect(toList());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public void tick(Predicate<Deal> filter) {
        List<Deal> active = getAll(filter);

        // по всем джойстикам отправили сообщения играм
        active.forEach(Deal::quietTick);

        // если в TRAINING кто-то isWin то мы его относим на следующий уровень
        // если в DISPOSABLE уровнях кто-то shouldLeave то мы его перезагружаем - от этого он появится на другом поле
        // а для всех остальных, кто уже isGameOver - создаем новые игры на том же поле
        for (Deal deal : active) {
            Game game = deal.getGame();
            String id = deal.getPlayerId();

            MultiplayerType type = deal.getType();
            if (game.isGameOver()) {
                quiet(() -> {

                    if (type.isLevels() && game.isWin()) {
                        if (setLevel(id, LevelProgress.goNext(game.getSave()))) {
                            return;
                        }
                    }

                    if (type.isDisposable() && game.shouldLeave()) {
                        reload(deal, Sweeper.on());
                        return;
                    }

                    game.newGame();
                });
            }
        }

        // собираем все уникальные борды
        // независимо от типа игры нам нужно тикнуть все,
        //      но только те, которые не DISPOSABLE и одновременно
        //      недоукомплектованы игроками,
        //      а так же комнаты которых активны
        // TODO разобраться почему случается что deal.getField() == null при переключении уровней icancode
        active.stream()
                .filter(deal -> deal.getField() != null)
                .map(Deal::getField)
                .distinct()
                .filter(spreader::isRoomStaffed)
                .forEach(GameField::quietTick);

        // ну и тикаем все GameRunner мало ли кому надо на это подписаться
        getGameTypes().forEach(GameType::quietTick);
    }

    public void reload(Deal deal, Sweeper sweeper) {
        reload(deal, null, sweeper);
    }

    private void reload(Deal deal, JSONObject save, Sweeper sweeper) {
        reload(deal, null, save, sweeper);
    }

    private void reload(Deal deal, String room, JSONObject save, Sweeper sweeper) {
        if (save == null) {
            save = deal.getGame().getSave();
        }
        removeInRoom(deal, sweeper);
        if (room != null) {
            deal.setRoom(room);
        }
        play(deal, save);
    }

    // переводим всех игроков на новые борды
    // при этом если надо перемешиваем их
    public void reloadAll(boolean shuffle) {
        reloadAll(shuffle, withAll());
    }

    public void reloadAll(boolean shuffle, Predicate<Deal> filter) {
        List<Deal> games = getAll(filter);

        if (shuffle) {
            Collections.shuffle(games);
        }

        games.forEach(deal -> spreader.remove(deal, Sweeper.off()));
        games.forEach(deal -> reload(deal, Sweeper.off()));
    }

    private void quiet(Runnable runnable) {
        ((Tickable) runnable::run).quietTick();
    }

    public List<Player> getPlayersByGame(String game) {
        return all.stream()
                .map(Deal::getPlayer)
                .filter(player -> player.getGame().equals(game))
                .collect(toList());
    }

    public List<Player> getPlayersByRoom(String room) {
        return all.stream()
                .map(Deal::getPlayer)
                .filter(player -> player.getRoom().equals(room))
                .collect(toList());
    }

    public LevelProgress getLevel(String id) {
        return get(id).levelFromSave();
    }

    public boolean changeLevel(String id, int level) {
        Deal deal = get(id);
        LevelProgress progress = deal.levelFromSave();
        if (progress.canChange(level)) {
            progress.change(level);
            reload(deal, progress.saveTo(new JSONObject()), Sweeper.on());
            deal.fireOnLevelChanged();
            return true;
        } else {
            return false;
        }
    }

    public boolean setLevel(String id, JSONObject save) {
        if (save == null) {
            return false;
        }
        Deal deal = get(id);
        reload(deal, save, Sweeper.on());
        deal.fireOnLevelChanged();
        return true;
    }

    protected void setTeam(String id, int teamId) {
        Deal deal = get(id);

        deal.setTeamId(teamId);

        reload(deal, Sweeper.on());
    }

    public void changeRoom(String id, String gameName, String newRoom) {
        if (Strings.isEmpty(newRoom) || Strings.isEmpty(gameName)) {
            return;
        }
        Deal deal = get(id);
        if (!deal.getPlayer().getGame().equals(gameName)) {
            return;
        }
        reload(deal, newRoom, null, Sweeper.on());
    }

    public Deal get(int index) {
        return all.get(index);
    }

    public List<Deal> all() {
        return all;
    }

    public Stream<Deal> stream() {
        return all.stream();
    }

    public Multimap<String, GameRoom> rooms() {
        return spreader.rooms();
    }

    public Deal deal(PlayerSave save, String room, String id, String callbackUrl, GameType gameType, long now) {
        remove(id, Sweeper.on());

        PlayerScores playerScores = gameType.getPlayerScores(save.getScore(), gameType.getSettings());
        Information listener = new ScoresCollector(id, playerScores);

        Player player = new Player(id, callbackUrl, gameType, playerScores, listener);
        player.setLastResponse(now);
        player.setRoom(room);

        return add(player, room, save);
    }
}
