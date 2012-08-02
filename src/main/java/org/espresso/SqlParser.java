package org.espresso;

import org.espresso.grammar.SqlGrammarLexer;
import org.espresso.grammar.SqlGrammarParser;
import org.espresso.token.SqlStatement;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

/**
 * Convenient interface to a SQL Parser. This class holds no state - it simply parses a string
 * representing a SQL statement and returns the corresponding tree, throwing a {@code SQLException}
 * in case of errors.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public final class SqlParser {
    /**
     * Parses the given SQL Statement
     *
     * @param sqlStatement The statement to be parsed
     *
     * @return the tree representing the SQL statement
     *
     * @throws java.sql.SQLException in case of parsing errors
     */
    public static SqlStatement parse(final String sqlStatement)
            throws SQLException {
        final ANTLRStringStream in = new ANTLRStringStream(sqlStatement);
        final SqlGrammarLexer lexer = new SqlGrammarLexer(in);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final SqlGrammarParser parser = new SqlGrammarParser(tokens);
        try {
            return parser.eval();
        } catch (final RecognitionException e) {
            throw new SQLSyntaxErrorException(e);
        }
    }

    private SqlParser() {
    }
}
