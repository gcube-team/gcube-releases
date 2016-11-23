package org.gcube.data.analysis.tabulardata.expression.composite.comparable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NotEquals extends BinaryExpression implements ComparableExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5563511567272417109L;

	@SuppressWarnings("unused")
	private NotEquals() {}

	public NotEquals(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}

	@Override
	public Operator getOperator() {		
		return Operator.NOT_EQUALS;
	}

	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}
	
}
