grammar SPQL;


@lexer::header {
package org.gcube.dataaccess.spql;
import org.gcube.dataaccess.spql.model.error.SyntaxError;
}


@lexer::members {

    @Override
      public void emitErrorMessage(String msg) {
      throw new SyntaxError(msg);
  }
}

@header {
package org.gcube.dataaccess.spql;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.gcube.dataaccess.spql.model.*;
import org.gcube.dataaccess.spql.model.where.*;
import org.gcube.dataaccess.spql.model.having.*;
import org.gcube.dataaccess.spql.model.ret.*;
import org.gcube.dataaccess.spql.model.error.SyntaxError;
import static org.gcube.dataaccess.spql.model.where.ConditionParameter.*;
import static org.gcube.dataaccess.spql.model.RelationalOperator.*;
import static org.gcube.dataaccess.spql.model.TermType.*;

}

@parser::members {
   /* @Override
    public void reportError(RecognitionException e) throws RecognitionException {
        throw e;
    }*/
    @Override
      public void emitErrorMessage(String msg) {
      throw new SyntaxError(msg);
  }

    
    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException
    {
      throw e;
    }
    
    //disable re-sync
    @Override
  protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException
  {
    throw new MismatchedTokenException(ttype, input);
  }
  
  protected void mismatch(IntStream input, int ttype, BitSet follow) throws RecognitionException
  {
    throw new MismatchedTokenException(ttype, input);
   }
  
  
  @Override
  public void recover(IntStream input, RecognitionException re) {
    throw new SyntaxError(re.getMessage());
  }
}
// Alter code generation so catch-clauses get replace with
// this action.
@rulecatch {
  catch (RecognitionException exception) {
    throw exception;
  }
}


query returns [Query result] : {$result = new Query();}
      SEARCHBY t=terms {$result.setTerms($t.terms);} 
      (IN d=datasources {$result.setDatasources($d.datasources);})? 
      (WHERE we=whereExpression {$result.setWhereExpression(new WhereExpression($we.conditions));})? 
      (RETURN rt=returnExpression {$result.setReturnType($rt.returnType);})?
      (HAVING he=havingExpression {$result.setHavingExpression($he.expression);})?
      EOF
      ;
      
terms returns [List<Term> terms] : {$terms = new ArrayList<Term>();}
      t=term {$terms.add($t.term);} (',' t=term {$terms.add($t.term);})*;
      
term returns [Term term]: t=scientificTerms {$term = t;} | t=commonNameTerms {$term = t;};

scientificTerms returns [Term term]: {$term = new Term(SCIENTIFIC_NAME);} 
('ScientificName'|'SN') w=words {$term.setWords($w.words);} (u=unfoldClause {$term.setUnfoldClause($u.clause);})? (e=expandClause {$term.setExpandClause($e.clause);})?; 
  
commonNameTerms returns [Term term]: {$term = new Term(COMMON_NAME);} 
('CommonName'|'CN') w=words {$term.setWords($w.words);} (r=resolveClause {$term.setResolveClause($r.clause);}) (e=expandClause {$term.setExpandClause($e.clause);})?;

words returns [List<String> words] : {$words = new ArrayList();}
  w=word {$words.add($w.word);} (',' w=word {$words.add($w.word);})*;
  
word returns [String word] : STRING {$word = $STRING.text.substring(1,$STRING.text.length()-1);};

unfoldClause returns [UnfoldClause clause]: {$clause = new UnfoldClause();} 
        UNFOLD WITH d=datasource {$clause.setDatasource($d.datasource);}; 

expandClause returns [ExpandClause clause]: {$clause = new ExpandClause();} 
              EXPAND (WITH d=datasources {$clause.setDatasources($d.datasources);})?;
  
resolveClause returns [ResolveClause clause]: {$clause = new ResolveClause();} 
              RESOLVE (WITH d=datasources {$clause.setDatasources($d.datasources);})?;

datasources returns [List<String> datasources] :  {$datasources = new ArrayList();} 
            d=datasource {$datasources.add($d.datasource);} (',' d=datasource {$datasources.add($d.datasource);})*;
 
datasource returns [String datasource]: ID {$datasource = $ID.text;};

rank returns [String rank]: ID {$rank = $ID.text;};
      
whereExpression returns [List<Condition> conditions]: {$conditions = new ArrayList<Condition>();} 
           left=wexpression {$conditions.add($left.condition);} (AND right = wexpression {$conditions.add($right.condition);})*;
wexpression returns [Condition condition]:  (bc=coordinateCondition {$condition = $bc.condition;} | dc=eventDateCondition {$condition = $dc.condition;});

coordinateCondition returns [Condition condition]: 'coordinate' o=relationalOperator c=coordinate {$condition = new Condition(COORDINATE,$o.operator,$c.coordinate);};
 
eventDateCondition returns [Condition condition]: 'eventDate' o=relationalOperator d=date {$condition = new Condition(EVENT_DATE,$o.operator,$d.date);};

relationalOperator returns [RelationalOperator operator]: 
            '<' {$operator=LT;} | 
            '<=' {$operator=LE;} | 
            '==' {$operator=EQ;} | 
            '>' {$operator=GT;} | 
            '>=' {$operator=GE;} ;
 
date returns [ParserDate date] : STRING {$date = new ParserDate($STRING.text.substring(1,$STRING.text.length()-1));};

coordinate returns [ParserCoordinate coordinate]: lat=FLOAT ',' lon=FLOAT {$coordinate = new ParserCoordinate($lat.text, $lon.text);};

returnExpression returns [ReturnType returnType]: 
            PRODUCT {$returnType = ReturnType.PRODUCT;}| 
            OCCURRENCE {$returnType = ReturnType.OCCURRENCE;}| 
            TAXON {$returnType = ReturnType.TAXON;};
 
havingExpression returns [HavingExpression expression] : e=expression {$expression = new HavingExpression($e.text);};

expression  
    :   conditionalAndExpression
        ('||' conditionalAndExpression
        )*
    ;
        
parExpression 
    :   '(' expression ')'
    ;

conditionalAndExpression 
    :   equalityExpression 
        ('&&' equalityExpression
        )*
    ;

equalityExpression 
    :   relationalExpression
        (   
            (   '==' 
            |   '!=' 
            )
            relationalExpression
        )*
    ;

relationalExpression  
    :   additiveExpression 
        (relationalOp additiveExpression
        )*
    ;

relationalOp 
    :    '<' '=' 
    |    '>' '=' 
    |   '<' 
    |   '>' 
    ;

additiveExpression 
    :   multiplicativeExpression
        (  
            (   '+'
            |   '-'
            )
            multiplicativeExpression
         )*
    ;

multiplicativeExpression 
    :
        unaryExpression
        (   
            (   '*'
            |   '/'
            |   '%'
            )
            unaryExpression
        )*
    ;

/**
 * NOTE: for '+' and '-', if the next token is int or long interal, then it's not a unary expression.
 *       it's a literal with signed value. INTLTERAL AND LONG LITERAL are added here for this.
 */
unaryExpression
    :   '+'  unaryExpression
    |   '-' unaryExpression
    |   '++' unaryExpression
    |   '--' unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus 
    :   '!' unaryExpression
    |   primary
    ;

/**
 * have to use scope here, parameter passing isn't well supported in antlr.
 */
primary:  ( parExpression
    |   calls
    |   xpath_function
    |   exl_function
    |   lucio_function
    |   literal)
    ;

calls: ID ('.' ID)*;

xpath_function: XPATH '(' STRING_DOUBLE_QUOTE ')';
exl_function: EXL '(' STRING_DOUBLE_QUOTE ')';
lucio_function: LUCIO '(' STRING_DOUBLE_QUOTE ')';

literal  
    :   INT
    |   FLOAT
    |   STRING_DOUBLE_QUOTE
    |   STRING
    |   TRUE
    |   FALSE
    |   NULL
    ;

LUCIO: 'LUCIO' | 'lucio';
EXL: 'EXL' | 'exl';
XPATH: 'XPATH' | 'xpath';
SEARCHBY: 'SEARCH BY' | 'search by';
EXPAND: 'EXPAND' | 'expande';
RESOLVE: 'RESOLVE' | 'resolve';
UNFOLD: 'UNFOLD' | 'unfold';
WITH: 'WITH' | 'with';
IN : 'IN' | 'in';
WHERE : 'WHERE' | 'where';
RETURN : 'RETURN' | 'return';
AND : 'AND' | 'and';
IS : 'IS' | 'is';
AS : 'AS' | 'as';
HAVING : 'HAVING' | 'having';
PRODUCT: 'Product' | 'PRODUCT' | 'product';
OCCURRENCE: 'Occurrence' | 'OCCURRENCE' | 'occurrence';
TAXON: 'Taxon' | 'TAXON' | 'taxon';


TRUE
    :   'true' | 'TRUE'
    ;

FALSE
    :   'false' | 'FALSE'
    ;

NULL
    :   'null'  | 'NULL'
    ;

ID  : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT : '0'..'9'+ ;


FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING
    :  '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\''
    ;
    
STRING_DOUBLE_QUOTE
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;
      

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
