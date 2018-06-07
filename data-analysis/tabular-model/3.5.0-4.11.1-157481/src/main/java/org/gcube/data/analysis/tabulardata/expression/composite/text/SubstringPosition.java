package org.gcube.data.analysis.tabulardata.expression.composite.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SubstringPosition extends BaseTextExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1666992192781983274L;

	@Override
	public Operator getOperator() {
		return Operator.SUBSTRING_POSITION;
	}
	
	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {		
		return new IntegerType();
	}
	
	@SuppressWarnings("unused")
	private SubstringPosition() {
	}

	public SubstringPosition(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);
	}
	
	
}
