grammar SPQL;


@lexer::header {
package org.gcube.dataaccess.spql;
import org.gcube.dataaccess.spql.model.error.SyntaxError;
}


@lexer::members {
/*
    @Override
     public void emitErrorMessage(String msg) {
      throw new SyntaxError(msg);
  }*/
}

@parser::header {
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
/*    @Override
    public void reportError(RecognitionException e) throws RecognitionException {
        throw e;
    }
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
  }*/
}
// Alter code generation so catch-clauses get replace with
// this action.
@rulecatch {
  catch (RecognitionException exception) {
    throw exception;
  }
}


query returns [Query result] : {$result = new Query();}
      SEARCHBY t=termsRule {$result.setTerms($t.terms);} 
      (IN d=datasourcesRule {$result.setDatasources($d.datasources);})? 
      (WHERE we=whereExpression {$result.setWhereExpression(new WhereExpression($we.conditions));})? 
      (RETURN rt=returnExpression {$result.setReturnType($rt.returnType);})?
      (HAVING he=havingExpression {$result.setHavingExpression($he.expression);})?
      EOF
      ;
      
termsRule returns [List<Term> terms] : {$terms = new ArrayList<Term>();}
      t=termRule {$terms.add($t.term);} (',' t=termRule {$terms.add($t.term);})*;
      
termRule returns [Term term]: scientificTerms {$term = scientificTerms;} | commonNameTerms {$term = commonNameTerms;};

scientificTerms returns [Term term]: {$term = new Term(SCIENTIFIC_NAME);} 
('ScientificName'|'SN') w=wordsRule {$term.setWords($w.words);} (u=unfoldClause {$term.setUnfoldClause($u.clause);})? (e=expandClause {$term.setExpandClause($e.clause);})?; 
  
commonNameTerms returns [Term term]: {$term = new Term(COMMON_NAME);} 
('CommonName'|'CN') w=wordsRule {$term.setWords($w.words);} (r=resolveClause {$term.setResolveClause($r.clause);}) (e=expandClause {$term.setExpandClause($e.clause);})?;

wordsRule returns [List<String> words] : {$words = new ArrayList();}
  w=wordRule {$words.add($w.word);} (',' w=wordRule {$words.add($w.word);})*;
  
wordRule returns [String word] : STRING {$word = $STRING.text.substring(1,$STRING.text.length()-1);};

unfoldClause returns [UnfoldClause clause]: {$clause = new UnfoldClause();} 
        UNFOLD WITH d=datasourceRule {$clause.setDatasource($d.datasource);}; 

expandClause returns [ExpandClause clause]: {$clause = new ExpandClause();} 
              EXPAND (WITH d=datasourcesRule {$clause.setDatasources($d.datasources);})?;
  
resolveClause returns [ResolveClause clause]: {$clause = new ResolveClause();} 
              RESOLVE (WITH d=datasourcesRule {$clause.setDatasources($d.datasources);})?;

datasourcesRule returns [List<String> datasources] :  {$datasources = new ArrayList();} 
            d=datasourceRule {$datasources.add($d.datasource);} (',' d=datasourceRule {$datasources.add($d.datasource);})*;
 
datasourceRule returns [String datasource]: ID {$datasource = $ID.text;};

rankRule returns [String rank]: ID {$rank = $ID.text;};
      
whereExpression returns [List<Condition> conditions]: {$conditions = new ArrayList<Condition>();} 
           left=wexpression {$conditions.add($left.condition);} (AND right = wexpression {$conditions.add($right.condition);})*;
wexpression returns [Condition condition]:  (bc=coordinateCondition {$condition = $bc.condition;} | dc=eventDateCondition {$condition = $dc.condition;});

coordinateCondition returns [Condition condition]: 'coordinate' o=relationalOperator c=coordinateRule {$condition = new Condition(COORDINATE,$o.operator,$c.coordinate);};
 
eventDateCondition returns [Condition condition]: 'eventDate' o=relationalOperator d=dateRule {$condition = new Condition(EVENT_DATE,$o.operator,$d.date);};

relationalOperator returns [RelationalOperator operator]: 
            '<' {$operator=LT;} | 
            '<=' {$operator=LE;} | 
            '==' {$operator=EQ;} | 
            '>' {$operator=GT;} | 
            '>=' {$operator=GE;} ;
 
dateRule returns [ParserDate date] : STRING {$date = new ParserDate($STRING.text.substring(1,$STRING.text.length()-1));};

coordinateRule returns [ParserCoordinate coordinate]: lat=number ',' lon=number {$coordinate = new ParserCoordinate($lat.text, $lon.text);};

returnExpression returns [ReturnType returnType]: 
            PRODUCT {$returnType = ReturnType.PRODUCT;}| 
            OCCURRENCE {$returnType = ReturnType.OCCURRENCE;}| 
            TAXON {$returnType = ReturnType.TAXON;};
 
havingExpression returns [HavingExpression expression] : e=expressionRule {$expression = new HavingExpression($e.text);};

number
 :    unary_operator? unsigned_number
 ;
 
 unary_operator
 :    '+'
 |    '-'
 ;
 
unsigned_number
 :    INT
 |    FLOAT
 ;

expressionRule  
    :   conditionalAndExpression
        ('||' conditionalAndExpression
        )*
    ;
        
parExpression 
    :   '(' expressionRule ')'
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
    :   ('//' ~('\n'|'\r')* '\r'? '\n' 
    |   '/*' .*? '*/' ) -> skip
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) -> skip
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
