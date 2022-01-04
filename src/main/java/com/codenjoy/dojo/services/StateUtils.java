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

import com.codenjoy.dojo.services.field.Multimap;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// TODO перенести в utils пакет
public class StateUtils {

	public static <T> List<T> filter(Object[] array, Class<T> clazz) {
		List<T> result = new LinkedList<>();
		for (int index = 0; index < array.length; index++) {
			Object object = array[index];
			if (object != null && clazz.isAssignableFrom(object.getClass())) {
				result.add((T) object);
			}
		}
		return result;
	}

	public static <T> T filterOne(Object[] array, Class<T> clazz) {
		for (int index = 0; index < array.length; index++) {
			Object object = array[index];
			if (object != null && clazz.isAssignableFrom(object.getClass())) {
				return (T) object;
			}
		}
		return null;
	}

	public static boolean itsMe(GamePlayer player, PlayerHero hero,
								Object[] alsoAtPoint, State item)
	{
		return player.getHero() == hero
				|| Arrays.asList(alsoAtPoint).contains(item);
	}
}
