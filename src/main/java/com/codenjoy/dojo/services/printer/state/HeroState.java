package com.codenjoy.dojo.services.printer.state;

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

import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;
import com.codenjoy.dojo.services.printer.TeamElement;
import com.codenjoy.dojo.services.round.RoundGamePlayer;
import com.codenjoy.dojo.services.round.RoundPlayerHero;

import java.util.List;

import static com.codenjoy.dojo.services.printer.state.StateUtils.filter;

public interface HeroState<E extends TeamElement, H extends HeroState<E, H, P>, P extends RoundGamePlayer<? extends RoundPlayerHero, ? extends GameField>> {

    E beforeState(Object... alsoAtPoint);

    default E middleState(E state, List<H> heroes, Object[] alsoAtPoint) {
        return state;
    }

    default E afterState(E state) {
        return state;
    }

    default E state(P player, Object... alsoAtPoint) {
        boolean myHero = StateUtils.containsMyHero(player, (PlayerHero) this, alsoAtPoint, (State) player.getHero());
        H hero = (H) (myHero ? player.getHero() : this);

        E state = hero.beforeState(alsoAtPoint);

        if (!myHero) {
            List<H> heroes = (List) filter(alsoAtPoint, this.getClass());

            state = hero.middleState(state, heroes, alsoAtPoint);

            if (state == null) {
                return null;
            }

            state = (E) (player.allFromMyTeam((List) heroes)
                    ? state.otherHero()
                    : state.enemyHero());
        }

        return hero.afterState(state);
    }
}