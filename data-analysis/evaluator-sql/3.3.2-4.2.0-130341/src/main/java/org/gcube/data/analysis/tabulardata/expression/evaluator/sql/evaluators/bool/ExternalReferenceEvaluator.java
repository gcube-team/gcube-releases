package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool;

import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class ExternalReferenceEvaluator extends BaseExpressionEvaluator<ExternalReferenceExpression> implements Evaluator<String> {
	
	private SQLExpressionEvaluatorFactory factory;

	String evaluatedSelectionArgument=null;
	
	public ExternalReferenceEvaluator(ExternalReferenceExpression expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}
	
	@Override
	public String evaluate() throws EvaluatorException {
		StringBuilder toReturn=new StringBuilder("(");
		toReturn.append("SELECT ");				
		
		
		
		toReturn.append(getColumnReferences());
		toReturn.append(" FROM ");
		toReturn.append(getTableReferences());
		toReturn.append(" WHERE ");
		toReturn.append(factory.getEvaluator(expression.getExternalCondition()).evaluate());
		toReturn.append(")");
		return toReturn.toString();
	}
	
	
	private String getColumnReferences(){
		if(evaluatedSelectionArgument==null) evaluatedSelectionArgument=factory.getEvaluator(expression.getSelectArgument()).evaluate();
		return (evaluatedSelectionArgument.contains(".")?evaluatedSelectionArgument.substring(evaluatedSelectionArgument.indexOf('.')+1):evaluatedSelectionArgument);		
	}
	
	private String getTableReferences(){
		if(evaluatedSelectionArgument==null) evaluatedSelectionArgument=factory.getEvaluator(expression.getSelectArgument()).evaluate();
		return (evaluatedSelectionArgument.contains(".")?evaluatedSelectionArgument.substring(0,evaluatedSelectionArgument.indexOf('.')):evaluatedSelectionArgument);
	}
	
}
