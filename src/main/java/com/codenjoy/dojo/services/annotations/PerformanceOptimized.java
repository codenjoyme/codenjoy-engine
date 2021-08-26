package com.codenjoy.dojo.services.annotations;

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
