package org.gcube.data.analysis.tabulardata.expression.evaluator.description.leaf;

import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.NamesRetriever;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;

public class ColumnReferenceEvaluator extends BaseExpressionEvaluator<ColumnReference> implements Evaluator<String> {

	private NamesRetriever namesRetriever;

	public ColumnReferenceEvaluator(ColumnReference expression,
			NamesRetriever namesRetriever) {
		super(expression);
		this.namesRetriever = namesRetriever;
	}
	
	public String evaluate() throws EvaluatorException {
		String prefix="";
		if(expression.getType()!=null) prefix="("+expression.getType().getName()+")"; 
		return prefix+namesRetriever.getTableName(expression.getTableId())+"."+namesRetriever.getColumnName(expression);
	}
	
}
