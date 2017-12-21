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
public class LessThan extends BinaryExpression implements ComparableExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6076228636339619364L;


	@SuppressWarnings("unused")
	private LessThan() {}

	public LessThan(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}

	@Override
	public Operator getOperator() {		
		return Operator.LESSER;
	}
	
	
	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}
}
