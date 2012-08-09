/*
 * Copyright 2012 Alberto Antenangeli
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private final Object target;
    private final Method method;

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
