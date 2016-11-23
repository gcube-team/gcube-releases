package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool;

import java.util.Iterator;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public abstract class BaseCompositeExpressionEvaluator<T extends CompositeExpression> extends BaseExpressionEvaluator<T> implements Evaluator<String>{
	
	
	SQLExpressionEvaluatorFactory evaluatorFactory;

	public BaseCompositeExpressionEvaluator(SQLExpressionEvaluatorFactory evaluatorFactory, T expression) {
		super(expression);
		this.evaluatorFactory = evaluatorFactory;
	}
	
	protected Iterator<Expression> childExpressionsIterator;

	
	public String evaluate() {
		StringBuilder result = new StringBuilder("(");
		childExpressionsIterator = getChildren();
		addPrefixIfNeeded(result);
		while (childExpressionsIterator.hasNext()) {
			addExpressionEvaluationToResult(result);
	
		}
		addSuffixIfNeeded(result);
//		addEnclosingParenthesis(result);
		result.append(")");
		return result.toString();
	}

//	private void addEnclosingParenthesis(StringBuilder result) {
//		result.insert(0, '(');
//		result.append(')');
//	}

	protected abstract Iterator<Expression> getChildren();

	private void addExpressionEvaluationToResult(StringBuilder result) {		
		Evaluator<String> evaluator = getEvaluator(childExpressionsIterator.next());
		if(encloseArgumentWithParenthesis())result.append("("+evaluator.evaluate()+")");
		else result.append(evaluator.evaluate());
		addMiddleOperatorIfNeeded(result);
	}

	protected abstract void addSuffixIfNeeded(StringBuilder result);
	protected abstract void addPrefixIfNeeded(StringBuilder result);
	protected abstract void addMiddleOperatorIfNeeded(StringBuilder result);
	
	private Evaluator<String> getEvaluator(Expression expression) {
		return evaluatorFactory.getEvaluator(expression);
	}

	
	protected final String getOperatorSymbol(Operator op){
		switch(op){
			// ARITHMETIC
			case ADDITION : return "+";
			case SUBTRACTION : return "-";
			case MULTIPLICATION : return "*";
			case DIVISION : return "/";
			case MODULUS : return "mod";
			case EXPONENTIATION : return "^";
			
			case CASE : return "CASE";
			
			// COMPARISON
			case EQUALS : return "=";
			case GREATER : return ">";
			case LESSER : return "<";
			case GREATER_OR_EQUALS: return ">=";
			case LESSER_OR_EQUALS : return "<=";
			case NOT_EQUALS : return "!=";
			case NOT_GREATER : return "!>";
			case NOT_LESSER : return "!<";
			
			// LOGICAL
			
//			case ALL : return "ALL";
			case AND : return "AND";
//			case ANY : return "ANY";
			case BETWEEN : return "BETWEEN";
//			case EXISTS : return "EXISTS";
			case IN : return "IN";
//			case LIKE : return "LIKE";
			case NOT : return "NOT";
			case OR : return "OR";
			case IS_NULL : return "IS NULL";
//			case UNIQUE : return "UNIQUE";
			case IS_NOT_NULL : return "IS NOT NULL";
			
			// STRING
			
			case BEGINS_WITH : return "BEGINS WITH";
			case ENDS_WITH : return "ENDS WITH ";
			case CONTAINS : return "CONTAINS";
			case MATCH_REGEX_SQL : return "SIMILAR TO";
			case MATCH_REGEX_POSIX : return "~";
			case TRIM : return "trim";
			case LENGTH : return "length";
			case UPPER : return "upper";
			case LOWER : return "lower";
			case CONCAT: return "||";
			case MD5 : return "md5";
			case SOUNDEX : return "soundex";
			case SIMILARITY : return "difference";
			case LEVENSHTEIN : return "levenshtein";
			
			// AGGREGATE 
			
			case AVG : return "avg";
			case COUNT : return "count";			
			case MAX : return "max";
			case MIN : return "min";
			case SUM : return "sum";
			case ST_EXTENT : return "ST_Extent";
			
			
			default : throw new EvaluatorException("Operator "+op+" not supported");
		}
	}
	
	protected boolean encloseArgumentWithParenthesis(){
		return false;
	}
}
