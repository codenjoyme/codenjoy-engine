package com.codenjoy.dojo.services.field;

import com.codenjoy.dojo.services.Point;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class NullMultimap extends Multimap<Object, Object> {

    public static final Multimap INSTANCE = new NullMultimap();

    @Override
    public List get(Object key) {
        return Arrays.asList();
    }

    @Override
    public boolean contains(Object key) {
        return false;
    }

    @Override
    public boolean remove(Object key, Point pt) {
        return false;
    }

    @Override
    public boolean remove(Object key, Predicate predicate) {
        return false;
    }

    @Override
    public Object ifPresent(Object key, Object defaultValue, Function function) {
        return defaultValue;
    }

    @Override
    public void removeKey(Object key) {
        // do nothing
    }

    @Override
    public void forEach(Consumer action) {
        // do nothing
    }

    @Override
    public String toString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean removeAllExact(Object key, Object value) {
        return false;
    }

    @Override
    public void clear(Object key) {
        // do nothing
    }

    @Override
    public Set keys() {
        return Sets.newHashSet();
    }

    @Override
    public List allValues() {
        return Arrays.asList();
    }
}
