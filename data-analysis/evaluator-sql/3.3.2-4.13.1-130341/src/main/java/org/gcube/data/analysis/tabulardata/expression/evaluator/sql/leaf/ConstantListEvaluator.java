package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.leaf;

import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.leaf.ConstantList;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

public class ConstantListEvaluator extends BaseExpressionEvaluator<ConstantList> implements Evaluator<String> {

	private SQLExpressionEvaluatorFactory factory;

	public ConstantListEvaluator(ConstantList expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}
	
	@Override
	public String evaluate() throws EvaluatorException {
		StringBuilder result=new StringBuilder("(");
		for(TDTypeValue value: expression.getArguments())
			result.append(factory.getEvaluator(value).evaluate()+",");
		result.deleteCharAt(result.lastIndexOf(","));
		result.append(")");
		return result.toString();
	}
	
}
