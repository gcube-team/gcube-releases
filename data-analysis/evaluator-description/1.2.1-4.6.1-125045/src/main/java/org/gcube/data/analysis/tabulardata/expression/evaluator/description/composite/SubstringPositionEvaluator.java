package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public class SubstringPositionEvaluator extends BaseExpressionEvaluator<SubstringPosition> implements Evaluator<String>{

	private DescriptionExpressionEvaluatorFactory factory;
	
		
	public SubstringPositionEvaluator(SubstringPosition expression,
			DescriptionExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}



	@Override
	public String evaluate() throws EvaluatorException {
		return String.format("(Position of %s in %s))", 
				factory.getEvaluator(expression.getRightArgument()).evaluate(),
				factory.getEvaluator(expression.getLeftArgument()).evaluate());
	}
	
}
