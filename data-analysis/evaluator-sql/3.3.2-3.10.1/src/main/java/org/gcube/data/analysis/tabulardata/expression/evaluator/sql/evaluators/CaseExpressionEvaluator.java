package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators;

import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case.WhenConstruct;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class CaseExpressionEvaluator extends BaseExpressionEvaluator<Case> implements Evaluator<String>{

	private SQLExpressionEvaluatorFactory factory;
	
	

	public CaseExpressionEvaluator(SQLExpressionEvaluatorFactory factory,
			Case expression) {
		super(expression);
		this.factory = factory;
	}



	@Override
	public String evaluate() throws EvaluatorException {
		
		StringBuilder evaluated = new StringBuilder("(CASE ");
		
		for (WhenConstruct whenConstruct : this.expression.getWhenConstructs()){
			evaluated.append(" WHEN ")
				.append(factory.getEvaluator(whenConstruct.getWhen()).evaluate())
				.append(" THEN ")
				.append(factory.getEvaluator(whenConstruct.getThen()).evaluate());
			
		}
		evaluated.append(" END)");
		return evaluated.toString();
		
	}
	
}
