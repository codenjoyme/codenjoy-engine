package com.codenjoy.dojo.services.annotations;

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

import java.lang.annotation.*;

/**
 * The mark on the method says that the shit
 * in it is the reason for the performance
 * optimization. This method was most likely
 * once beautiful, but because of the criticality
 * it was optimized, then it looks like this.
 * Refactoring in this method is only possible
 * with benchmarking performance tests.
 *
 * Thank you for understanding.
 *
 * P.S. If suddenly you can find a more
 * optimal solution, it will be great!
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PerformanceOptimized {
}
