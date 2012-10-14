/*
 * Copyright 2012 Espresso Team
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

import org.espresso.eval.Evaluator;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;

import static org.apache.bcel.Constants.ACC_PUBLIC;
import static org.apache.bcel.generic.Type.BOOLEAN;
import static org.apache.bcel.generic.Type.OBJECT;

/**
 * Demonstrates the concept of creating a class that implements an interface on the
 * fly using the BCEL library.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class CodeGen {
    public static void main(String... args) throws IllegalAccessException, InstantiationException {
        System.out.println(getEvaluator().matches(null));
    }

    /**
     * Creates an instance of the following class on the fly:
     *
     * <code>
     *     public class EvaluatorTest implements Evaluator {
     *         @Override
     *         public boolean matches(Object row) {
     *             System.out.println("Worked!");
     *         }
     *     }
     * </code>
     * @return an object that is an instance of the class above
     * @throws InstantiationException propagated from the new ClassLoader below
     * @throws IllegalAccessException propagated from the new ClassLoader below
     */
    private static Evaluator getEvaluator() throws InstantiationException, IllegalAccessException {


        // All constants referred to by our class, including fields, methods, and strings.
        final ConstantPoolGen constPoolGen = new ConstantPoolGen();
        final int systemOut = constPoolGen.addFieldref("java/lang/System", "out", "Ljava/io/PrintStream;");
        final int println = constPoolGen.addMethodref("java.io.PrintStream", "println", "(Ljava/lang/String;)V");
        final int test = constPoolGen.addString("Worked!");

        // The class generator - Evaluator test, extending Object, implementing the Evaluator
        // interface, and using the constantPoolGenerator we just created
        final ClassGen classGen = new ClassGen("EvaluatorTest", "java/lang/Object", "EvaluatorTest.java",
                ACC_PUBLIC, new String[] {"org.espresso.eval.Evaluator"}, constPoolGen);

        // List of instructions that call System.out.println("Worked!") and return false
        final InstructionList instructionList = new InstructionList();
        // Call to println
        instructionList.append(new GETSTATIC(systemOut));
        instructionList.append(new LDC(test));
        instructionList.append(new INVOKEVIRTUAL(println));
        // Push false into the stack and return
        instructionList.append(new ICONST(0));
        instructionList.append(new IRETURN());

        // Create the "matches" method matching the Evaluator.matches declaration
        final MethodGen methodGen = new MethodGen(ACC_PUBLIC, BOOLEAN, new Type[]{OBJECT},
                null, "matches", "EvaluatorTest", instructionList, constPoolGen);
        methodGen.setMaxLocals();
        methodGen.setMaxStack();
        // Adds the method to the class
        classGen.addMethod(methodGen.getMethod());

        // Create the constructor
        classGen.addEmptyConstructor(ACC_PUBLIC);

        // Done, print the class definition so we can sanity check what we just did
        final JavaClass javaClass = classGen.getJavaClass();
        System.out.println(javaClass);

        // Get the bytecode representing the class
        final byte[] bytecode = javaClass.getBytes();

        // Create a special class loader that knows only how to do one thing: create an instance
        // of the class we just created. First, define the class, then create a new instance.
        // Since we know our class implements the Evalator interface, we can safely cast it.
        return new ClassLoader() {
            public final Evaluator getEvaluator(final byte[] bytecode) throws IllegalAccessException, InstantiationException {
                return (Evaluator) (defineClass("EvaluatorTest", bytecode, 0, bytecode.length)).newInstance();
            }
        }.getEvaluator(bytecode);

    }

}
