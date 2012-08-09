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
package org.espresso.visitor;

import org.apache.bcel.generic.InstructionList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates a list of code snippets. This is a convenience class.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
class CodeSnippetList implements Iterable<CodeSnippet> {
    private final List<CodeSnippet> snippets = new ArrayList<CodeSnippet>();

    /**
     * Appends a code snippet to this list.
     * @param snippet the snippet to append
     */
    public void append(final CodeSnippet snippet) {
        snippets.add(snippet);
    }

    /**
     * Converts this list of snippets to an InstructionList
     * @return the instruction list
     */
    public InstructionList asInstructionList() {
        final InstructionList instructionList = new InstructionList();
        for (final CodeSnippet snippet : snippets)
            instructionList.append(snippet.getCode());
        return instructionList;
    }

    /**
     * Converts this list of snippet to a consolidated code snippet
     * @param type what type is left on the top of stack after consolidation
     * @param clazz optional class when the TOS is an object
     * @return the consolidated snippet
     */
    public CodeSnippet asSnippet(final JvmType type, final Class clazz) {
        final CodeSnippet newSnippet = new CodeSnippet(type, clazz);
        for (final CodeSnippet snippet : snippets)
            newSnippet.append(snippet);
        return newSnippet;
    }

    /**
     * Access to snippet at a given position
     * @param index the position (starting with 0)
     * @return the corresponding snippet
     */
    public CodeSnippet getSnippetAt(final int index) {
        return snippets.get(index);
    }

    /**
     * So we can iterate over the snippets.
     * @return an iterator over the list of snippets
     */
    @Override
    public Iterator<CodeSnippet> iterator() {
        return snippets.iterator();
    }
}
