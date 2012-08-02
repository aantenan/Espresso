package org.espresso;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Contains the two references to perform a function call - the target object
 * and a reference to the method.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 *         Date: 11/17/11
 *         Time: 2:21 PM
 */
public class FunctionExtension {
    final Object target;
    final Method method;

    public FunctionExtension(final Object target, final Method method) {
        this.target = target;
        this.method = method;
    }

    public Object invoke(final Object... parameters)
            throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, parameters);
    }

    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }
}
