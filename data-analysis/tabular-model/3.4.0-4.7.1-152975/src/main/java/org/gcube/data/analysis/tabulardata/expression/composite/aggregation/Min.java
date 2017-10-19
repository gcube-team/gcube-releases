package org.gcube.data.analysis.tabulardata.expression.composite.aggregation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Min extends UnaryExpression implements AggregationExpression {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3559287890015938843L;

	
	@SuppressWarnings("unused")
	private Min() {}
	
	
	
	
	public Min(Expression argument) {
		super(argument);
		// TODO Auto-generated constructor stub
	}




	@Override
	public Operator getOperator() {
		return Operator.MIN;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return getArgument().getReturnedDataType();
	}

}
