package org.gcube.data.analysis.tabulardata.expression.composite.comparable;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
@XmlRootElement
public class NotLess extends BinaryExpression implements ComparableExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8137997205434693407L;

	@SuppressWarnings("unused")
	private NotLess() {}

	public NotLess(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}

	@Override
	public Operator getOperator() {		
		return Operator.EQUALS;
	}
	
	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}
}
