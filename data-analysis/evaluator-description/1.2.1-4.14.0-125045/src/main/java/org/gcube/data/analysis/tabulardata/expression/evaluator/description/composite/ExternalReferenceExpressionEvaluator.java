package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import org.gcube.data.analysis.tabulardata.expression.composite.ExternalReferenceExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public class ExternalReferenceExpressionEvaluator extends BaseExpressionEvaluator<ExternalReferenceExpression> implements Evaluator<String> {

	private DescriptionExpressionEvaluatorFactory factory;

	public ExternalReferenceExpressionEvaluator(
			ExternalReferenceExpression expression,
			DescriptionExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}
	
	@Override
	public String evaluate() throws EvaluatorException {
		StringBuilder toReturn=new StringBuilder("(");
		toReturn.append("SELECT ");
		String selectionString=factory.getEvaluator(expression.getSelectArgument()).evaluate();		
		toReturn.append(selectionString);
		toReturn.append(" WHERE ");
		toReturn.append(factory.getEvaluator(expression.getExternalCondition()).evaluate());
		toReturn.append(")");
		return toReturn.toString();
	}
	
}
