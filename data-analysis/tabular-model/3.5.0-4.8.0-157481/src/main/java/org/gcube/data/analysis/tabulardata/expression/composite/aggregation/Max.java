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
public class Max extends UnaryExpression implements AggregationExpression {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2649125888588892833L;

	@SuppressWarnings("unused")
	private Max() {
	}

	
	
	public Max(Expression argument) {
		super(argument);
	}



	@Override
	public Operator getOperator() {
		return Operator.MAX;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return getArgument().getReturnedDataType();
	}

}
