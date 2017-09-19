package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.leaf;

import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.ReferenceResolver;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;

public class ColumnReferenceEvaluator extends BaseExpressionEvaluator<ColumnReference> implements Evaluator<String> {

	private ReferenceResolver resolver;

	public ColumnReferenceEvaluator(ColumnReference expression,
			ReferenceResolver referenceResolver) {
		super(expression);
		this.resolver = referenceResolver;
	}
	
	public String evaluate() throws EvaluatorException {
		return resolver.getTable(expression).getName()+"."+resolver.getColumn(expression).getName();
	}
	
}
