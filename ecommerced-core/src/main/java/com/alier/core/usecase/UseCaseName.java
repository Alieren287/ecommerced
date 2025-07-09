package com.alier.core.usecase;

import java.lang.annotation.*;

/**
 * Annotation to provide a meaningful name for use cases in logging
 * This helps identify use cases in logs with business-friendly names
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseCaseName {

    /**
     * The business-friendly name for the use case
     * This will be used in logging instead of the class name
     *
     * @return the display name for the use case
     */
    String value();

    /**
     * Optional description of what the use case does
     * This can be used for documentation purposes
     *
     * @return the description of the use case
     */
    String description() default "";
} 