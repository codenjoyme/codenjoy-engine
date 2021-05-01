package com.codenjoy.dojo.services.log;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

public class DebugService extends Suspendable {

    private List<String> filter;

    public DebugService(boolean active, List<String> filter) {
        this.active = active;
        this.filter = filter;
    }

    public void setDebugEnable(boolean active) {
        super.setActive(active);
    }

    @Override
    public void pause() {
        changePackageLoggingLevels(Level.INFO);
    }

    @Override
    public boolean isWorking() {
        return loggers()
                .map(Logger::getLevel)
                .anyMatch(Level.DEBUG::equals);
    }

    private Stream<Logger> loggers() {
        return filter.stream()
                .map(LoggerFactory::getLogger)
                .map(Logger.class::cast);
    }

    @Override
    public void resume() {
        changePackageLoggingLevels(Level.DEBUG);
    }

    private void changePackageLoggingLevels(Level level) {
        loggers().forEach(logger -> logger.setLevel(level));
    }
}