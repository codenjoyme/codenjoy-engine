package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;
import lombok.experimental.UtilityClass;

import java.util.Iterator;
import java.util.List;

@UtilityClass
public class Utils {

    public static boolean removeAllExact(List list, Point element) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        boolean result = false;
        Iterator<Point> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == element) {
                iterator.remove();
                result = true;
            }
        }
        return result;
    }
}
