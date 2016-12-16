package org.gcube.data.analysis.tabulardata.expression.composite.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Levenshtein extends BaseTextExpression{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9092423307003757878L;

	
	@SuppressWarnings("unused")
	private Levenshtein() {		
	}


	public Levenshtein(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}



	@Override
	public Operator getOperator() {
		return Operator.LEVENSHTEIN;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new IntegerType();
	}
}
