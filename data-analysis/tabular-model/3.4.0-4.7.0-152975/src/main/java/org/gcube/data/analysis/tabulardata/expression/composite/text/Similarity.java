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
public class Similarity extends BaseTextExpression{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5526992095410553910L;

	@SuppressWarnings("unused")
	private Similarity() {		
	}


	public Similarity(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}



	@Override
	public Operator getOperator() {
		return Operator.SIMILARITY;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new IntegerType();
	}
}
