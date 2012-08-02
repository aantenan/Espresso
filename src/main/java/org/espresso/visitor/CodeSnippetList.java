package org.espresso.visitor;

import org.apache.bcel.generic.InstructionList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates a list of code snippets. This is a convenience class.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class CodeSnippetList implements Iterable<CodeSnippet> {
    private final List<CodeSnippet> snippets = new ArrayList<CodeSnippet>();

    public void appendSnippet(final CodeSnippet snippet) {
        snippets.add(snippet);
    }

    public InstructionList asInstructionList() {
        final InstructionList instructionList = new InstructionList();
        for (final CodeSnippet snippet : snippets)
            instructionList.append(snippet.getCode());
        return instructionList;
    }
    
    public CodeSnippet getSnippetAt(final int index) {
        return snippets.get(index);
    }

    @Override
    public Iterator<CodeSnippet> iterator() {
        return snippets.iterator();
    }
}
