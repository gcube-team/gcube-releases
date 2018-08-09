package org.gcube.data.analysis.tabulardata.expression.logical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MultivaluedExpression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValueIsIn extends BinaryExpression implements LogicalExpression{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6302543604524650146L;

	@SuppressWarnings("unused")
	private ValueIsIn() {}

	public ValueIsIn(Expression leftArgument, MultivaluedExpression rightArgument) {
		super(leftArgument, (Expression) rightArgument);
	}

	@Override
	public Operator getOperator() {
		return Operator.IN;
	}
	
	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}
}

