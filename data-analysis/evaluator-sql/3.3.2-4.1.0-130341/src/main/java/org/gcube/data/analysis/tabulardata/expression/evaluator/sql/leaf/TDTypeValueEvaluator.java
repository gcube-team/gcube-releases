package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.leaf;

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
import org.gcube.data.analysis.tabulardata.model.mapping.SQLModelMapper;

public class TDTypeValueEvaluator extends BaseExpressionEvaluator<TDTypeValue> implements Evaluator<String> {

	private SQLModelMapper mapper;
	
	public TDTypeValueEvaluator(TDTypeValue expression, SQLModelMapper mapper) {
		super(expression);
		this.mapper=mapper;
	}

	@Override
	public String evaluate() throws EvaluatorException {		
		if(expression instanceof TDBoolean) return mapper.translateModelValueToSQL((TDBoolean)expression);
		if(expression instanceof TDDate) return mapper.translateModelValueToSQL((TDDate)expression);
		if(expression instanceof TDInteger) return mapper.translateModelValueToSQL((TDInteger)expression);
		if(expression instanceof TDNumeric) return mapper.translateModelValueToSQL((TDNumeric)expression);
		if(expression instanceof TDText) return mapper.translateModelValueToSQL((TDText)expression);
		if(expression instanceof TDGeometry) return mapper.translateModelValueToSQL((TDGeometry)expression);
		throw new EvaluatorException("Type "+expression.getClass()+" not supported");
	}
	
}
