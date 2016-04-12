package org.gcube.data.analysis.tabulardata.expression.evaluator.description.leaf;

import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.leaf.ColumnReferencePlaceholder;

public class PlaceholderEvaluator extends BaseExpressionEvaluator<ColumnReferencePlaceholder> implements Evaluator<String>{

	public PlaceholderEvaluator(ColumnReferencePlaceholder expression) {
		super(expression);	
	}

	public String evaluate() throws EvaluatorException {		
		return "("+expression.getDatatype().getName()+")"+"%"+expression.getId();
	}
	
}
