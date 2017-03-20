package org.gcube.data.analysis.tabulardata.expression.composite.comparable;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
@XmlRootElement
public class LessOrEquals extends BinaryExpression implements ComparableExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5679425419431246170L;

	@SuppressWarnings("unused")
	private LessOrEquals() {}

	public LessOrEquals(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}

	@Override
	public Operator getOperator() {		
		return Operator.LESSER_OR_EQUALS;
	}
	
	@Override
	public DataType getReturnedDataType() {		
		return new BooleanType();
	}
}