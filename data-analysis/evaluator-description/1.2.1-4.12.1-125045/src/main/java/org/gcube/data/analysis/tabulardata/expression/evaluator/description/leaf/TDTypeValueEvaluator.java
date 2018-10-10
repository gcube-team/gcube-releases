package org.gcube.data.analysis.tabulardata.expression.evaluator.description.leaf;

import java.text.SimpleDateFormat;

import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

public class TDTypeValueEvaluator extends BaseExpressionEvaluator<TDTypeValue> implements Evaluator<String> {

	private static SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	
	public TDTypeValueEvaluator(TDTypeValue expression) {
		super(expression);
	}
	
	public String evaluate() throws EvaluatorException {
		if(expression instanceof TDBoolean) return ((TDBoolean)expression).getValue().toString();
		if(expression instanceof TDDate) return formatter.format(((TDDate)expression).getValue());
		if(expression instanceof TDInteger) return ((TDInteger)expression).getValue().toString();
		if(expression instanceof TDNumeric) return ((TDNumeric)expression).getValue().toString();
		if(expression instanceof TDText) return "'"+((TDText)expression).getValue().toString()+"'";
		if(expression instanceof TDGeometry) return ((TDGeometry)expression).getValue();
		throw new EvaluatorException("Type "+expression.getClass()+" not supported");
	}
	
}
