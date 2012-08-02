grammar SqlGrammar;

/*
    This is an implementation of a subset of SQL92. It parses the input file
    and returns a tree representing the parsed statement
*/


@header {
package org.espresso.grammar;
import org.espresso.token.*;
import static org.espresso.token.SqlComparisonOperator.*;
import static org.espresso.token.SqlBooleanOperator.*;
import static org.espresso.token.SqlArithmeticOperator.*;
}

@lexer::header {
package org.espresso.grammar;
}

DESCRIBE_ : ('D'|'d')('E'|'e')('S'|'s')('C'|'c')('R'|'r')('I'|'i')('B'|'b')('E'|'e') ;
CANCEL_ : ('C'|'c')('A'|'a')('N'|'n')('C'|'c')('E'|'e')('L'|'l') ;
SELECT_ : ('S'|'s')('E'|'e')('L'|'l')('E'|'e')('C'|'c')('T'|'t') ;
FROM_ : ('F'|'f')('R'|'r')('O'|'o')('M'|'m') ;
WHERE_ : ('W'|'w')('H'|'h')('E'|'e')('R'|'r')('E'|'e') ;
OR_ : ('O'|'o')('R'|'r') ;
AND_ : ('A'|'a')('N'|'n')('D'|'d') ;
NOT_ : ('N'|'n')('O'|'o')('T'|'t') ;
BETWEEN_ : ('B'|'b')('E'|'e')('T'|'t')('W'|'w')('E'|'e')('E'|'e')('N'|'n') ;
IN_ : ('I'|'i')('N'|'n') ;
LIKE_ : ('L'|'l')('I'|'i')('K'|'k')('E'|'e') ;
IS_ : ('I'|'i')('S'|'s') ;
NULL_ : ('N'|'n')('U'|'u')('L'|'l')('L'|'l') ;

eval returns [SqlStatement statement]
    :   (   select = selectStatement {
                    $statement = select;
                }
        |   describe = describeStatement {
                    $statement = describe;
                }
        |   cancel = cancelStatement {
                    $statement = cancel;
                }
        )
        ';'?
    ;

describeStatement returns [SqlDescribe describeStatement]
    :   DESCRIBE_
        tableName = Identifier {
                $describeStatement = new SqlDescribe(tableName.getText());
            }
    ;

cancelStatement returns [SqlCancel cancelStatement]
    :   CANCEL_
        queryId = Identifier {
                $cancelStatement = new SqlCancel(queryId.getText());
            }
    ;

selectStatement returns [SqlSelect selectStatement]
    :   SELECT_
        '*'
        FROM_
        tableName = Identifier {}
        WHERE_
        sCond = searchCondition {
                $selectStatement = new SqlSelect(tableName.getText(), sCond);
            }
    ;

searchCondition returns [SqlExpressionNode sCond]
    :   lt1 = logicalTerm {
                $sCond = lt1;
            }
        (   OR_
            lt2 = logicalTerm {
                    if ($sCond == lt1) {
                        SqlExpression temp = new SqlBooleanExpression(OR);
                        temp.addOperand($sCond);
                        $sCond = temp;
                    }
                    ((SqlExpression)$sCond).addOperand(lt2);
                }
        )*
    ;

logicalTerm returns [SqlExpressionNode lTerm]
    :   lf1 = logicalFactor {
                $lTerm = lf1;
            }
        ( AND_
            lf2 = logicalFactor {
                if (lTerm == lf1) {
                    SqlExpression temp = new SqlBooleanExpression(AND);
                    temp.addOperand($lTerm);
                    $lTerm= temp;
                }
                ((SqlExpression)$lTerm).addOperand(lf2);
            }
        )*
    ;

logicalFactor returns [SqlExpressionNode lFactor]
    : (     NOT_ {
                    $lFactor = new SqlBooleanExpression(NOT);
                }
      )?
      pred = predicate {
                    if (null == lFactor)
                        $lFactor = pred;
                    else
                        ((SqlExpression)$lFactor).addOperand(pred);
                }
    ;

predicate returns [SqlExpressionNode lFactor]
    :   b1 = between {
                $lFactor = b1;
            }
    |   il1 = inList {
                $lFactor = il1;
            }
    |   l1 = like {
                $lFactor = l1;
            }
    |   c1 = comparison {
                $lFactor = c1;
            }
    |   n1 = nullPredicate {
                $lFactor = n1;
            }
    ;

between returns [SqlExpressionNode bw]
    :   col = Identifier {
                $bw = new SqlBetweenExpression();
                ((SqlExpression)$bw).addOperand(new SqlColumn($col.text));
            }
        BETWEEN_
        e1 = expression {
                ((SqlExpression)$bw).addOperand(e1);
            }
        AND_
        e2 = expression {
                ((SqlExpression)$bw).addOperand(e2);
            }
    ;

inList returns [SqlExpressionNode inL]
    :   col = Identifier {
                $inL = new SqlInExpression(new SqlColumn($col.text));
            }
        IN_
        '('
        exp = expression {
                ((SqlExpression)$inL).addOperand(exp);
            }
        ( ','   exp = expression {
                        ((SqlExpression)$inL).addOperand(exp);
                    }
        )*
        ')'
    ;

like returns [SqlExpressionNode lk]
    :   col = Identifier {
                $lk = new SqlLikeExpression();
                ((SqlExpression)$lk).addOperand(new SqlColumn($col.text));
            }
        LIKE_
        str = String {
                SqlString string = new SqlString($str.text);
                ((SqlExpression)$lk).addOperand(string);
            }
    ;

nullPredicate returns [SqlExpressionNode nullPred]
    :   col = Identifier {
                boolean isNull = true;
            }
        IS_
        ( NOT_ {
                isNull = false;
            }
        )?
        NULL_ {
                $nullPred = new SqlIsNullExpression(new SqlColumn($col.text), isNull);
            }
    ;

comparison returns [SqlExpressionNode comp]
    :   e1 = expression {
                    $comp = e1;
                }
        ( '>'   e2=expression {
                        SqlExpression temp = new SqlComparisonExpression(GT);
                        temp.addOperand(e1);
                        temp.addOperand(e2);
                        $comp = temp;
                    }
        | '<'   e2 = expression {
                        SqlExpression temp = new SqlComparisonExpression(LT);
                        temp.addOperand(e1);
                        temp.addOperand(e2);
                        $comp = temp;
                    }
        | '>='  e2 = expression {
                        SqlExpression temp = new SqlComparisonExpression(GE);
                        temp.addOperand(e1);
                        temp.addOperand(e2);
                        $comp = temp;
                    }
        | '<='  e2 = expression {
                        SqlExpression temp = new SqlComparisonExpression(LE);
                        temp.addOperand(e1);
                        temp.addOperand(e2);
                        $comp = temp;
                    }
        | '='   e2 = expression {
                        SqlExpression temp = new SqlComparisonExpression(EQ);
                        temp.addOperand(e1);
                        temp.addOperand(e2);
                        $comp = temp;
                    }
        | '<>'  e2 = expression {
                        SqlExpression temp = new SqlComparisonExpression(NE);
                        temp.addOperand(e1);
                        temp.addOperand(e2);
                        $comp = temp;
                    }
        | '!='  e2 = expression {
                        SqlExpression temp = new SqlComparisonExpression(NE);
                        temp.addOperand(e1);
                        temp.addOperand(e2);
                        $comp = temp;
                    }
        )?
    ;

expression returns [SqlExpressionNode exp]
    :   t1 = term {
                    $exp =  t1;
                }
         ( '+'  t2 = term {
                        SqlExpression temp = new SqlArithmeticExpression(PLUS);
                        temp.addOperand($exp);
                        temp.addOperand(t2);
                        $exp = temp;
                    }
         | '-'  t2 = term {
                        SqlExpression temp = new SqlArithmeticExpression(MINUS);
                        temp.addOperand($exp);
                        temp.addOperand(t2);
                        $exp = temp;
                    }
         )*
    ;

term returns [SqlExpressionNode trm]
    :   a1 = atomicExp {
                    $trm = a1;
                }
         ( '*'  a2 = atomicExp {
                        SqlExpression temp = new SqlArithmeticExpression(TIMES);
                        temp.addOperand($trm);
                        temp.addOperand(a2);
                        $trm = temp;
                    }
         | '/'  a2 = atomicExp {
                        SqlExpression temp = new SqlArithmeticExpression(DIV);
                        temp.addOperand($trm);
                        temp.addOperand(a2);
                        $trm = temp;
                    }
         )*
    ;



atomicExp returns [SqlExpressionNode ae]
    :   number = Number {
                $ae = new SqlNumber($number.text);
            }
    |   identifier = Identifier {
                $ae = new SqlColumn($identifier.text);
            }
        ( '('   pl = parameterList {
                        $ae = new SqlFunction($identifier.text);
                        ((SqlFunction)$ae).setParameters(pl);
                    }
          ')'
        )?
    |   date = Date {
                $ae = new SqlDate($date.text);
            }
    |   string = String {
                $ae = new SqlString($string.text);
            }
    |   '(' sc=searchCondition ')' {
                $ae = sc;
            }
    ;

Number
    :   ('0'..'9')+ ('.' ('0'..'9')+)?
    ;

Identifier
    :   ('A'..'Z'|'a'..'z')('A'..'Z'|'a'..'z'|'0'..'9'|'_'|'.')*
    ;

Date
    :   (   '\'' ('0'..'9')('0'..'9')? ('/'|'-') ('0'..'9')('0'..'9')? ('/'|'-') ('0'..'9')('0'..'9')('0'..'9')('0'..'9') '\''
        |   '\'' ('0'..'9')('0'..'9')('0'..'9')('0'..'9') ('/'|'-') ('0'..'9')('0'..'9')? ('/'|'-') ('0'..'9')('0'..'9')? '\''
        )
    ;


parameterList returns [List<SqlExpressionNode> params]
    :   { params = new ArrayList<SqlExpressionNode>(); }
        (   exp = expression {
                    $params.add(exp);
                }
            ( ','   exp = expression {
                            $params.add(exp);
                        }
            )*
        )?
    ;

String
    :   '\'' ( ~'\'' )* '\''
    ;

WS
    :   (' ' | '\t' | '\r'| '\n') {$channel=HIDDEN;}
    ;
