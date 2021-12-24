package com.codenjoy.dojo.services.event.cases;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2021 Codenjoy
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

import com.codenjoy.dojo.services.event.EventObject;
import lombok.ToString;

@ToString
public class MultiValuesEvent implements EventObject<MultiValuesEvent.Type, MultiValuesEvent> {

    private Type type;
    private Integer value1;
    private Integer value2;

    public MultiValuesEvent(Type type, Integer value1, Integer value2) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
    }

    public enum Type {
        TYPE1,
        TYPE2;
    }

    @Override
    public Type type() {
        return type;
    }
}
