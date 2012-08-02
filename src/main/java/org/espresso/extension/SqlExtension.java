package org.espresso.extension;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks methods for automatic use as SQL extension functions.  If the last parameter of the method
 * is the cache element type, the method receives the current object as that parameter.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface SqlExtension {
}
