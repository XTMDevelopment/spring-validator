package id.xtramile.validator.annotation.common;

import java.lang.annotation.*;

/**
 * Supplies a human-readable display name for a field in validation messages.
 * <p>
 * When present on a DTO field, the friendly message resolver prefers this value
 * over the Java property name.
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldName {

    /**
     * The display name shown in validation messages.
     *
     * @return the field display name
     */
    String value();
}
