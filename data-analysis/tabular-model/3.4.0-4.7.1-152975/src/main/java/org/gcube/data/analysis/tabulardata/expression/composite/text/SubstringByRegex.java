package org.gcube.data.analysis.tabulardata.expression.composite.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SubstringByRegex extends BaseTextExpression {

	

		
	@SuppressWarnings("unused")
	private SubstringByRegex() {
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3688450547658449242L;

	public SubstringByRegex(Expression sourceString, Expression regexp) {
		super(sourceString, regexp);
	}

	@Override
	public Operator getOperator() {
		return Operator.SUBSTRING_BY_REGEX;
	}
	
	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new TextType();
	}


	
	
}
