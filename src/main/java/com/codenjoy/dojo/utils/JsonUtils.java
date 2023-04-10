package com.codenjoy.dojo.utils;

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


import com.cedarsoftware.util.io.JsonWriter;
import com.codenjoy.dojo.client.Utils;
import lombok.experimental.UtilityClass;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.SortedJSONArray;
import org.json.SortedJSONObject;

import java.util.Collection;

@UtilityClass
public class JsonUtils {

    public static String cleanSorted(Object object) {
        String json = toStringSorted(object);
        return clean(json);
    }

    public static String prettyPrint(Object object) {
        String json = toStringSorted(object);
        return clean(JsonWriter.formatJson(json));
    }

    // TODO inline this method
    public static String clean(String json) {
        return Utils.clean(json);
    }

    // TODO почему-то этот малый слетает в MVN при билде из консоли для символов борды expansion
    public static String prettyPrint(String jsonString) {
        return clean(asString(jsonString));
    }

    public static String asString(String jsonString) {
        String json = toStringSorted(jsonString);
        return JsonWriter.formatJson(json);
    }

    public static String toStringSorted(String jsonString) {
        return new SortedJSONObject(jsonString).toString();
    }

    public static String toStringSorted(Object object) {
        if (object instanceof Collection) {
            return new SortedJSONArray((Collection) object).toString();
        } else if (object instanceof JSONArray) {
            return object.toString();
        } else if (object instanceof JSONObject) {
            return new SortedJSONObject(object.toString()).toString();
        } else {
            return new SortedJSONObject(object).toString();
        }
    }
}
