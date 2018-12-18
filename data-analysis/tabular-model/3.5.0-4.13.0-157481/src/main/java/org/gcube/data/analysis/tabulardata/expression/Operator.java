package org.gcube.data.analysis.tabulardata.expression;

import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Count;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Max;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Min;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.ST_Extent;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Sum;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Addition;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Division;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Exponentiation;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Modulus;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Multiplication;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Subtraction;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotGreater;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.NotLess;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Levenshtein;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Lower;
import org.gcube.data.analysis.tabulardata.expression.composite.text.RepeatText;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Similarity;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Soundex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextEndsWith;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchPosixRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Trim;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Upper;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Not;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.expression.logical.ValueIsIn;

public enum Operator {

	
	
	// Arithmetic
	ADDITION (Addition.class,"Addition","+"),
	SUBTRACTION(Subtraction.class,"Subtraction","-"),
	MULTIPLICATION(Multiplication.class,"Multiplication","*"),
	DIVISION(Division.class,"Division","/"),
	MODULUS(Modulus.class,"Modulus","mod"),
	EXPONENTIATION(Exponentiation.class, "exponentiation","^"),
	
	//COMPARISON
	
	EQUALS(Equals.class,"Equals","="),
	GREATER(GreaterThan.class,"Greater than",">"),
	LESSER(LessThan.class,"Less than","<"),

	GREATER_OR_EQUALS(GreaterOrEquals.class,"Greater or equals",">="),
	LESSER_OR_EQUALS(LessOrEquals.class,"Less or equals","<="),
	
	NOT_EQUALS(NotEquals.class,"Not Equals","!="),
	NOT_GREATER(NotGreater.class,"Not Greater","!>"),
	NOT_LESSER(NotLess.class,"Not Less","!<"),
	
	//CONDITIONAL
	CASE(Case.class, "Case", "Case"),
	
	//LOGICAL
	
	
//	ALL(A.class,"All","ALL"),
	AND(And.class,"And","&"),
//	ANY,
	BETWEEN(Between.class,"Between","[..]"),
//	EXISTS(Ex),
	IN(ValueIsIn.class,"In","IN"),
//	LIKE,
	NOT(Not.class,"Not","!"),
	OR(Or.class,"Or","|"),
	IS_NULL(IsNull.class,"Is Null","Is Null"),
//	UNIQUE,
	IS_NOT_NULL(IsNotNull.class,"Is Not Null","Is Not Null"),
	
	//STRING
	
	BEGINS_WITH(TextBeginsWith.class,"Begins with","XXX*"),
	ENDS_WITH(TextEndsWith.class,"Ends with","*XXX"),
	CONTAINS(TextContains.class,"Contains","*XXX*"),
	MATCH_REGEX_SQL(TextMatchSQLRegexp.class,"Match Regex SQL","Match_Regex SQL"),
	MATCH_REGEX_POSIX(TextMatchPosixRegexp.class,"Match Regex Posix","Match_Regex Posix"),
	REPLACE_REGEX(TextReplaceMatchingRegex.class,"Replace By Regex","Replace By Regex"),
	REPEAT_TEXT(RepeatText.class,"Repeat Text","Repeat Text"),
	SUBSTRING_BY_REGEX(SubstringByRegex.class,"Substring By Regex","Substring By Regex"),
	SUBSTRING_BY_INDEX(SubstringByIndex.class,"Substring By Index","Substring By Index"),
	SUBSTRING_POSITION(SubstringPosition.class,"Substring Position","Pos"),
	TRIM(Trim.class,"Trim","Trim"),
	LENGTH(Length.class,"Length","Length"),
	UPPER(Upper.class,"Uppercase","UpCase"),
	LOWER(Lower.class,"Lowercase","LowCase"),
	CONCAT(Concat.class,"Concat","||"),
	MD5(org.gcube.data.analysis.tabulardata.expression.composite.text.MD5.class,"MD5","MD5"),
	SOUNDEX(Soundex.class,"Soundex","SND"),
	SIMILARITY(Similarity.class,"Similarity","SML"),
	LEVENSHTEIN(Levenshtein.class,"Levenshtein","Lev"),
	
	// TODO

	
	//COMPLEX
	
	SELECT_IN(ExternalReferenceExpression.class,"Select IN","Select IN"),
	CAST(Cast.class,"Cast","Cast"),
	// AGGREGATION 
	
	AVG(Avg.class,"Average","AVG"),
	COUNT(Count.class,"Count","Count"),
	
	MAX(Max.class,"Max","Max"),
	MIN(Min.class,"Min","Min"),
	SUM(Sum.class,"Sum","Sum"),
	
	ST_EXTENT(ST_Extent.class,"ST_Extent","ST_Extent");
	
	
	
	
	
	private Class<? extends CompositeExpression> implementingClass;
	private String label;
	private String symbol;
	Operator(Class<? extends CompositeExpression> implementingClass,
			String label, String symbol) {
		this.implementingClass = implementingClass;
		this.label = label;
		this.symbol = symbol;
	}
	
	public static final Operator getByExpressionClass(Class<? extends CompositeExpression> clazz){
		for(Operator op:values()){
			if(op.getImplementingClass().equals(clazz)) return op;
		}
		throw new RuntimeException("Unable to find an operator for class "+clazz);
	}

	public Class<? extends CompositeExpression> getImplementingClass() {
		return implementingClass;
	}

	public String getLabel() {
		return label;
	}

	public String getSymbol() {
		return symbol;
	}
	
}
