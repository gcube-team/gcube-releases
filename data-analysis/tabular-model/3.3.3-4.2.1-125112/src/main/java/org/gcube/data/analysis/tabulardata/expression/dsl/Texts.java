package org.gcube.data.analysis.tabulardata.expression.dsl;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.RepeatText;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchPosixRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;

public class Texts {

	public static Expression regexprReplace(Expression toCheckText, String regexp,
			String replacing,boolean caseSensitive){
		return new TextReplaceMatchingRegex(toCheckText, new TDText(regexp), new TDText(replacing), caseSensitive);
	}
	
	public static Expression regexprReplace(Expression toCheckText, String regexp,
			String replacing){
		return new TextReplaceMatchingRegex(toCheckText, new TDText(regexp), new TDText(replacing));
	}
		
	public static Expression repeat(Expression valueToRepeat, Expression timesToRepeat){
		return new RepeatText(valueToRepeat, timesToRepeat);
	}
		
	public static Expression substring(Expression sourceString, Expression regexp){
		return new SubstringByRegex(sourceString, regexp);
	}
	
	public static Expression substring(Expression sourceString, Expression startingIndex, Expression finalIndex){
		return new SubstringByIndex(sourceString, startingIndex, finalIndex);
	}
	
	public static Expression matchesSQL(Expression value, Expression regexp){
		return new TextMatchSQLRegexp(value, regexp);
	}
	
	public static Expression matchesPosix(Expression value, Expression regexp){
		return new TextMatchPosixRegexp(value, regexp);
	}
	
	public static Concat concat(Expression firstArgument, Expression secondArgumnet){
		return new Concat(firstArgument, secondArgumnet);
	}
	
	public static Length length(Expression expression){
		return new Length(expression);
	}
	
}
