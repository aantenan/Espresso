package org.espresso.extension;

/**
 * Extension to support a "not(<boolean expression>)" style function. It gets automatically added to
 * the engine, unless an alternative extension with the same name is provided.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public final class NotExtension {
    @SqlExtension
    public boolean NOT(final boolean expression) {
        return !expression;
    }
}
